//core module
const path = require("path");
//external modules
const express = require("express");
const session = require("express-session");
const MongoDBStore = require("connect-mongodb-session")(session);
const DB_PATH =
  "***REMOVED***/airbnb?appName=CompleteCoding";
//Local modules
const storeRouter = require("./routers/storeRouter");
const hostRouter = require("./routers/hostRouter");
const authRouter = require("./routers/authRouter");
const rootDIR = require("./util/pathUtil");
const errorsController = require("./controllers/errors");
//const { mongoConnect } = require("./util/databaseUtil");
const { default: mongoose } = require("mongoose");

const app = express();

app.set("view engine", "ejs");
app.set("views", "views");

const store = new MongoDBStore({
  uri: DB_PATH,
  collection: "sessions",
});

app.use(express.urlencoded()); //middleware to parse form data
app.use(
  session({
    secret: "Airbnb Webpage",
    resave: false,
    saveUnintialized: true,
    store: store,
  })
);
app.use((req, res, next) => {
  // req.isLoggedIn = req.get("Cookie")
  //   ? req.get("Cookie").split("=")[1] === "true"
  //   : false;
  req.isLoggedIn = req.session.isLoggedIn;
  next();
});

app.use(authRouter);
app.use(storeRouter);
app.use("/host", (req, res, next) => {
  if (!req.isLoggedIn) {
    return res.redirect("/login");
  }
  next();
});
app.use("/host", hostRouter);

app.use(express.static(path.join(rootDIR, "public")));

app.use(errorsController.pageNotFound);

const PORT = 3000;
// mongoConnect(() => {
//   app.listen(PORT, () => {
//     console.log(`Server is running on port ${PORT}`);
//   });
// });

mongoose
  .connect(DB_PATH)
  .then(() => {
    console.log("Connected to Mongo");
    app.listen(PORT, () => {
      console.log(`Server is running on port ${PORT}`);
    });
  })
  .catch((err) => {
    console.log("Error while connecting to mongo:", err);
  });
