"use strict";
const express = require("express");
const router = express.Router();
const bookingController = require("../controllers/bookingController");
const { requireAuth } = require("../middleware/auth");

// All booking routes require the user to be logged in
router.use(requireAuth);

// Pages
router.get("/book/:homeId",            bookingController.getBookingForm);
router.get("/bookings",                bookingController.getMyBookings);
router.get("/bookings/:bookingId",     bookingController.getBookingDetails);

// AJAX endpoints (called from browser-side JS in booking-form.ejs / booking-details.ejs)
router.post("/booking/availability",   bookingController.checkAvailability);
router.post("/booking/create",         bookingController.createBooking);
router.post("/booking/cancel/:bookingId", bookingController.cancelBooking);

module.exports = router;