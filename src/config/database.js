"use strict";
const mongoose = require("mongoose");

const DB_URI = process.env.MONGODB_URI || "mongodb://localhost:27017/airbnb";

const connectDB = async () => {
  try {
    await mongoose.connect(DB_URI);
    console.log("MongoDB connected successfully");
  } catch (err) {
    console.error("MongoDB connection error:", err);
    process.exit(1);
  }
};

module.exports = { connectDB, DB_URI };