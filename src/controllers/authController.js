const { check, validationResult } = require("express-validator");
const User = require("../models/user");
const bcrypt = require("bcryptjs");

exports.getLogin = (req, res, next) => {
  res.render("auth/login", {
    pageTitle: "Login",
    currentPage: "login",
    isLoggedIn: false,
    errors: [],
    oldInput: {
      email: "",
      password: "",
    },
    user: {},
  });
};

exports.getSignup = (req, res, next) => {
  res.render("auth/signup", {
    pageTitle: "Signup",
    currentPage: "signup",
    isLoggedIn: false,
    errors: [],
    oldInput: {
      firstName: "",
      lastName: "",
      email: "",
      userType: "",
      terms: "",
    },
    user: {},
  });
};

exports.postSignup = [
  check("firstName")
    .trim()
    .isLength({ min: 2 })
    .withMessage("First name must be at least 2 characters long.")
    .matches(/^[A-Za-z]+$/)
    .withMessage("First name must contain only alphabetic characters."),

  check("lastName")
    .matches(/^[A-Za-z]*$/)
    .withMessage("Last name must contain only alphabetic characters."),

  check("email")
    .isEmail()
    .withMessage("Please enter a valid email address.")
    .normalizeEmail(),

  check("password")
    .isLength({ min: 8 })
    .withMessage("Password must be at least 8 characters long.")
    .matches(/[a-z]/)
    .withMessage("Password must contain at least one lowercase letter.")
    .matches(/[A-Z]/)
    .withMessage("Password must contain at least one uppercase letter.")
    .matches(/[0-9]/)
    .withMessage("Password must contain at least one number.")
    .matches(/[!@&]/)
    .withMessage("Password must contain at least one special character.")
    .trim(),

  check("confirmPassword")
    .trim()
    .custom((value, { req }) => {
      if (value !== req.body.password) {
        throw new Error("Passwords do not match.");
      }
      return true;
    }),

  check("userType")
    .notEmpty()
    .withMessage("Please select a user type.")
    .isIn(["guest", "host"])
    .withMessage("Invalid user type selected."),

  check("terms")
    .equals("on")
    .withMessage("You must accept the terms and conditions.")
    .custom((value, { req }) => {
      if (value !== "on") {
        throw new Error("You must accept the terms and conditions.");
      }
      return true;
    }),

  // Middleware to handle the request after validation
  (req, res, next) => {
    const { firstName, lastName, email, password, userType, terms } = req.body;
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(422).render("auth/signup", {
        pageTitle: "Signup",
        currentPage: "signup",
        isLoggedIn: false,
        errors: errors.array().map((err) => err.msg),
        oldInput: { firstName, lastName, email, password, userType, terms },
        user: {},
      });
    }

    bcrypt
      .hash(password, 12)
      .then((hashedPassword) => {
        const newUser = new User({
          firstName,
          lastName,
          email,
          password: hashedPassword,
          userType,
        });
        console.log("New User Registered:", newUser);
        return newUser.save();
      })
      .then(() => {
        res.redirect("/login");
      })
      .catch((err) => {
        return res.status(422).render("auth/signup", {
          pageTitle: "Signup",
          currentPage: "signup",
          isLoggedIn: false,
          errors: [err.message],
          oldInput: { firstName, lastName, email, password, userType, terms },
        });
      });
    // res.cookie("isLoggedIn", true);
    //req.session.isLoggedIn = true;
    //req.isLoggedIn = true;
    // res.redirect("/login");
  },
];

exports.postLogin = async (req, res, next) => {
  const { email, password } = req.body;
  const user = await User.findOne({ email });
  if (!user) {
    console.log("LOGIN ERROR — SENDING:", {
      errors: ["Invalid email or password"],
      oldInput: { email },
    });
    return res.status(422).render("auth/login", {
      pageTitle: "Login",
      currentPage: "login",
      isLoggedIn: false,
      errors: ["User does not exist. Please sign up first."],
      oldInput: { email, password: "" },
      user: {},
    });
  }

  const doMatch = await bcrypt.compare(password, user.password);
  if (!doMatch) {
    console.log("LOGIN ERROR — SENDING:", {
      errors: ["Invalid email or password"],
      oldInput: { email },
    });
    return res.status(422).render("auth/login", {
      pageTitle: "Login",
      currentPage: "login",
      isLoggedIn: false,
      errors: ["Invalid password. Please try again."],
      oldInput: { email, password: "" },
    });
  }
  // res.cookie("isLoggedIn", true);
  req.session.isLoggedIn = true;
  req.session.user = user;
  await req.session.save();
  //req.isLoggedIn = true;
  res.redirect("/");
};

exports.postLogout = (req, res, next) => {
  // res.cookie("isLoggedIn", false);
  req.session.destroy(() => {
    res.redirect("/login");
  });
};
