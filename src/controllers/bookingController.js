"use strict";
const BookingService = require("../services/bookingService");
const Home = require("../models/home");

// GET /book/:homeId  — show booking form
exports.getBookingForm = async (req, res, next) => {
  try {
    const home = await Home.findById(req.params.homeId);
    if (!home) return res.redirect("/homes");

    res.render("store/booking-form", {
      pageTitle: `Book ${home.houseName}`,
      currentPage: "bookings",
      isLoggedIn: req.isLoggedIn,
      user: req.session.user,
      home,
      errors: [],
    });
  } catch (err) {
    next(err);
  }
};

// GET /bookings  — list user's bookings
exports.getMyBookings = async (req, res, next) => {
  try {
    const userId = req.session.user._id.toString();
    const result = await BookingService.getBookingsByUserId(userId);
    const bookings = result.success ? result.data : [];

    res.render("store/bookings", {
      pageTitle: "My Bookings",
      currentPage: "bookings",
      isLoggedIn: req.isLoggedIn,
      user: req.session.user,
      bookings,
      serviceError: result.success ? null : result.error,
    });
  } catch (err) {
    next(err);
  }
};

// GET /bookings/:bookingId  — booking detail page
exports.getBookingDetails = async (req, res, next) => {
  try {
    const result = await BookingService.getBookingById(req.params.bookingId);
    if (!result.success) {
      return res.status(result.status || 404).render("404", {
        pageTitle: "Booking Not Found",
        currentPage: "404",
        isLoggedIn: req.isLoggedIn,
        user: req.session.user,
      });
    }

    const booking = result.data;
    // Optionally enrich with home data
    let home = null;
    try { home = await Home.findById(booking.homeId); } catch (_) {}

    res.render("store/booking-details", {
      pageTitle: "Booking Details",
      currentPage: "bookings",
      isLoggedIn: req.isLoggedIn,
      user: req.session.user,
      booking,
      home,
    });
  } catch (err) {
    next(err);
  }
};

// POST /booking/availability  — AJAX availability check
exports.checkAvailability = async (req, res, next) => {
  try {
    const { homeId, checkIn, checkOut } = req.body;
    if (!homeId || !checkIn || !checkOut) {
      return res.status(400).json({ success: false, error: "homeId, checkIn and checkOut are required" });
    }

    const result = await BookingService.checkAvailability(homeId, checkIn, checkOut);
    res.json(result);
  } catch (err) {
    res.status(500).json({ success: false, error: "Unable to check availability" });
  }
};

// POST /booking/create  — AJAX booking creation
exports.createBooking = async (req, res, next) => {
  try {
    const sessionUser = req.session.user;
    const { homeId, checkIn, checkOut, guests, pricePerNight, specialRequests } = req.body;

    // Build the payload the Java microservice expects (BookingRequest record)
    const bookingPayload = {
      userId: sessionUser._id.toString(),
      homeId,
      checkIn,
      checkOut,
      guests: parseInt(guests, 10),
      pricePerNight: parseFloat(pricePerNight),
      guestName: `${sessionUser.firstName} ${sessionUser.lastName}`.trim(),
      guestEmail: sessionUser.email,
      guestPhone: sessionUser.phone || null,
      specialRequests: specialRequests || null,
    };

    const result = await BookingService.createBooking(bookingPayload);
    if (result.success) {
      return res.json({ success: true, bookingId: result.data.id });
    }
    res.status(result.status || 400).json({ success: false, error: result.error });
  } catch (err) {
    res.status(500).json({ success: false, error: "Failed to create booking" });
  }
};

// POST /booking/cancel/:bookingId  — AJAX cancel
exports.cancelBooking = async (req, res, next) => {
  try {
    const result = await BookingService.cancelBooking(req.params.bookingId);
    if (result.success) {
      return res.json({ success: true });
    }
    res.status(result.status || 400).json({ success: false, error: result.error });
  } catch (err) {
    res.status(500).json({ success: false, error: "Failed to cancel booking" });
  }
};