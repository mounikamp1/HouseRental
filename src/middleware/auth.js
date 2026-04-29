"use strict";

/**
 * Attach session user to every request.
 */
const attachUser = (req, res, next) => {
  req.isLoggedIn = req.session.isLoggedIn || false;
  next();
};

/**
 * Guard routes that require authentication.
 */
const requireAuth = (req, res, next) => {
  if (!req.isLoggedIn) {
    return res.redirect("/login");
  }
  next();
};

/**
 * Guard routes that require host role.
 */
const requireHost = (req, res, next) => {
  if (!req.session.user || req.session.user.userType !== "host") {
    return res.status(403).render("404", {
      pageTitle: "Access Denied",
      currentPage: "404",
      isLoggedIn: req.isLoggedIn,
      user: req.session.user,
    });
  }
  next();
};

module.exports = { attachUser, requireAuth, requireHost };