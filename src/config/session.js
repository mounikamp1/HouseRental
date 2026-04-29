"use strict";
const session = require("express-session");
const MongoDBStore = require("connect-mongodb-session")(session);
const { DB_URI } = require("./database");

const store = new MongoDBStore({ uri: DB_URI, collection: "sessions" });

store.on("error", (err) => console.error("Session store error:", err));

const sessionMiddleware = session({
  secret: process.env.SESSION_SECRET || "airbnb-secret-key",
  resave: false,
  saveUninitialized: false,
  store: store,
  cookie: {
    maxAge: 1000 * 60 * 60 * 24, // 1 day
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
  },
});

module.exports = sessionMiddleware;