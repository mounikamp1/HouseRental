"use strict";
require("dotenv").config();

const path = require("path");
const express = require("express");

// Config
const { connectDB } = require("./src/config/database");
const sessionMiddleware = require("./src/config/session");
const upload = require("./src/config/multer");

// Middleware
const { attachUser, requireAuth } = require("./src/middleware/auth");
const { pageNotFound, globalErrorHandler } = require("./src/middleware/errorHandler");

// Routes
const authRoutes     = require("./src/routes/authRoutes");
const storeRoutes    = require("./src/routes/storeRoutes");
const hostRoutes     = require("./src/routes/hostRoutes");
const bookingRoutes  = require("./src/routes/bookingRoutes");
const analyticsRoutes = require("./src/routes/analyticsRoutes");

const app = express();

// ─── View Engine ──────────────────────────────────────────────────────────────
app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));

// ─── Static Assets ────────────────────────────────────────────────────────────
app.use(express.static(path.join(__dirname, "public")));
app.use("/uploads", express.static(path.join(__dirname, "uploads")));

// ─── Body Parsers ─────────────────────────────────────────────────────────────
app.use(express.urlencoded({ extended: true }));
app.use(express.json());              // required for AJAX booking endpoints
app.use(upload.single("photo"));

// ─── Session ─────────────────────────────────────────────────────────────────
app.use(sessionMiddleware);

// ─── Auth Context ─────────────────────────────────────────────────────────────
app.use(attachUser);

// ─── Routes ──────────────────────────────────────────────────────────────────
app.use(authRoutes);
app.use(storeRoutes);
app.use(bookingRoutes);                      // /book/:homeId, /bookings, /booking/*
app.use("/host",      requireAuth, hostRoutes);
app.use("/analytics", analyticsRoutes);      // guards are inside analyticsRoutes

// ─── Error Handling ───────────────────────────────────────────────────────────
app.use(pageNotFound);
app.use(globalErrorHandler);

// ─── Start Server ─────────────────────────────────────────────────────────────
const PORT = process.env.PORT || 5000;

connectDB().then(() => {
  app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
});

module.exports = app;