const axios = require("axios");

// Java Booking Microservice Configuration
const BOOKING_SERVICE_URL =
  process.env.BOOKING_SERVICE_URL || "http://localhost:8080/api/bookings";

// ============================================================
// STRANGLER FIG CIRCUIT BREAKER
// Node.js acts as the "old system" that knows when to delegate
// to the Java microservice (new system). If Java is down, the
// circuit opens and requests fall back gracefully — the user
// experience degrades gracefully rather than crashing.
//
// States: CLOSED (normal) → OPEN (tripped) → HALF_OPEN (probe)
// ============================================================
const CircuitBreaker = {
  state: "CLOSED",        // CLOSED | OPEN | HALF_OPEN
  failureCount: 0,
  failureThreshold: 3,    // trip after 3 consecutive failures
  successThreshold: 1,    // recover after 1 success in HALF_OPEN
  retryAfterMs: 30000,    // wait 30s before probing again
  lastFailureTime: null,

  isOpen() {
    if (this.state === "OPEN") {
      const elapsed = Date.now() - this.lastFailureTime;
      if (elapsed >= this.retryAfterMs) {
        this.state = "HALF_OPEN";
        console.log("[CircuitBreaker] → HALF_OPEN: probing booking service");
        return false; // allow probe request through
      }
      return true;
    }
    return false;
  },

  recordSuccess() {
    this.failureCount = 0;
    if (this.state === "HALF_OPEN") {
      this.state = "CLOSED";
      console.log("[CircuitBreaker] → CLOSED: booking service recovered");
    }
  },

  recordFailure() {
    this.failureCount++;
    this.lastFailureTime = Date.now();
    if (this.state === "HALF_OPEN" || this.failureCount >= this.failureThreshold) {
      this.state = "OPEN";
      console.warn(`[CircuitBreaker] → OPEN after ${this.failureCount} failures`);
    }
  },

  getStatus() {
    return {
      state: this.state,
      failureCount: this.failureCount,
      lastFailureTime: this.lastFailureTime,
    };
  },
};

// ============================================================
// RETRY WITH EXPONENTIAL BACKOFF
// ============================================================
async function withRetry(fn, maxAttempts = 2, baseDelayMs = 300) {
  let lastError;
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try {
      const result = await fn();
      CircuitBreaker.recordSuccess();
      return result;
    } catch (err) {
      lastError = err;
      const isServerError = err.response && err.response.status >= 500;
      const isNetworkError = !err.response;
      if ((isServerError || isNetworkError) && attempt < maxAttempts) {
        const delay = baseDelayMs * Math.pow(2, attempt - 1);
        console.warn(`[BookingService] Attempt ${attempt} failed. Retrying in ${delay}ms...`);
        await new Promise((r) => setTimeout(r, delay));
      } else {
        break;
      }
    }
  }
  CircuitBreaker.recordFailure();
  throw lastError;
}

// Wrap every outbound call with circuit-breaker + retry
async function callService(fn) {
  if (CircuitBreaker.isOpen()) {
    return {
      success: false,
      error: "Booking service is temporarily unavailable (circuit open). Please try again shortly.",
      status: 503,
      circuitBreaker: CircuitBreaker.getStatus(),
    };
  }
  try {
    return await withRetry(fn);
  } catch (error) {
    return BookingService.handleError(error);
  }
}

/**
 * Booking Service - Node.js facade over Java Spring Boot microservice.
 *
 * Implements the Strangler Fig pattern: this file is the "seam" between
 * the Node.js monolith and the extracted Java booking domain service.
 * The circuit breaker ensures the monolith degrades gracefully while
 * the new service stabilises — a key senior operations concern.
 */
class BookingService {
  static async createBooking(bookingData) {
    return callService(() =>
      axios
        .post(BOOKING_SERVICE_URL, bookingData, {
          headers: { "Content-Type": "application/json" },
          timeout: 10000,
        })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async getAllBookings() {
    return callService(() =>
      axios.get(BOOKING_SERVICE_URL, { timeout: 5000 }).then((r) => ({ success: true, data: r.data }))
    );
  }

  static async getBookingById(bookingId) {
    return callService(() =>
      axios.get(`${BOOKING_SERVICE_URL}/${bookingId}`, { timeout: 5000 }).then((r) => ({ success: true, data: r.data }))
    );
  }

  static async getBookingsByUserId(userId) {
    return callService(() =>
      axios
        .get(`${BOOKING_SERVICE_URL}/user/${userId}`, { timeout: 5000 })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async getUpcomingBookings(userId) {
    return callService(() =>
      axios
        .get(`${BOOKING_SERVICE_URL}/user/${userId}/upcoming`, { timeout: 5000 })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async getPastBookings(userId) {
    return callService(() =>
      axios
        .get(`${BOOKING_SERVICE_URL}/user/${userId}/past`, { timeout: 5000 })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async getBookingsByHomeId(homeId) {
    return callService(() =>
      axios
        .get(`${BOOKING_SERVICE_URL}/home/${homeId}`, { timeout: 5000 })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async checkAvailability(homeId, checkIn, checkOut) {
    return callService(() =>
      axios
        .post(
          `${BOOKING_SERVICE_URL}/availability`,
          { homeId, checkIn, checkOut },
          { headers: { "Content-Type": "application/json" }, timeout: 5000 }
        )
        .then((r) => ({ success: true, available: r.data }))
    );
  }

  static async confirmBooking(bookingId) {
    return callService(() =>
      axios
        .patch(`${BOOKING_SERVICE_URL}/${bookingId}/confirm`, {}, { timeout: 5000 })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async cancelBooking(bookingId) {
    return callService(() =>
      axios
        .patch(`${BOOKING_SERVICE_URL}/${bookingId}/cancel`, {}, { timeout: 5000 })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async updateBookingStatus(bookingId, status) {
    return callService(() =>
      axios
        .patch(`${BOOKING_SERVICE_URL}/${bookingId}/status`, null, {
          params: { status },
          timeout: 5000,
        })
        .then((r) => ({ success: true, data: r.data }))
    );
  }

  static async deleteBooking(bookingId) {
    return callService(() =>
      axios
        .delete(`${BOOKING_SERVICE_URL}/${bookingId}`, { timeout: 5000 })
        .then(() => ({ success: true, message: "Booking deleted successfully" }))
    );
  }

  static async checkServiceHealth() {
    // Health check bypasses circuit breaker deliberately — used for monitoring
    try {
      const response = await axios.get("http://localhost:8080/actuator/health", { timeout: 3000 });
      return { success: true, data: response.data, circuitBreaker: CircuitBreaker.getStatus() };
    } catch {
      return {
        success: false,
        error: "Booking service is unavailable",
        circuitBreaker: CircuitBreaker.getStatus(),
      };
    }
  }

  static handleError(error) {
    if (error.response) {
      return {
        success: false,
        error: error.response.data?.message || error.response.data?.error || "Request failed",
        status: error.response.status,
        details: error.response.data,
      };
    } else if (error.request) {
      return {
        success: false,
        error: "Booking service is unavailable. Please try again later.",
        status: 503,
      };
    } else {
      return {
        success: false,
        error: error.message || "An unexpected error occurred",
        status: 500,
      };
    }
  }
}

module.exports = BookingService;