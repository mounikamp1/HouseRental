"use strict";
const express = require("express");
const router = express.Router();
const analyticsController = require("../controllers/analyticsController");
const { requireAuth, requireHost } = require("../middleware/auth");

// All analytics routes: must be logged in AND be a host
router.use(requireAuth, requireHost);

router.get("/dashboard",               analyticsController.getDashboard);
router.get("/api/stats",               analyticsController.getStats);
router.get("/api/property-performance", analyticsController.getPropertyPerformance);

module.exports = router;