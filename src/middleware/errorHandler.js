"use strict";

const pageNotFound = (req, res, _next) => {
  res.status(404).render("404", {
    pageTitle: "Page Not Found",
    currentPage: "404",
    isLoggedIn: req.isLoggedIn,
    user: req.session ? req.session.user : null,
  });
};

const globalErrorHandler = (err, req, res, _next) => {
  console.error("Unhandled error:", err);
  res.status(err.status || 500).render("404", {
    pageTitle: "Something went wrong",
    currentPage: "error",
    isLoggedIn: req.isLoggedIn,
    user: req.session ? req.session.user : null,
  });
};

module.exports = { pageNotFound, globalErrorHandler };