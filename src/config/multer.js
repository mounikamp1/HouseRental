"use strict";
const multer = require("multer");
const path = require("path");

const storage = multer.diskStorage({
  destination: (_req, _file, cb) => cb(null, "uploads/"),
  filename: (_req, file, cb) => {
    const rand = Math.random().toString(36).substring(2, 12);
    cb(null, rand + "-" + file.originalname);
  },
});

const fileFilter = (_req, file, cb) => {
  const allowed = ["image/png", "image/jpg", "image/jpeg"];
  cb(null, allowed.includes(file.mimetype));
};

const upload = multer({ storage, fileFilter });

module.exports = upload;