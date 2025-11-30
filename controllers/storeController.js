const Favourite = require("../models/favourite");
const Home = require("../models/home");

exports.getIndex = (req, res, next) => {
  console.log("Session value:", req.session);
  Home.find().then((registeredHomes) => {
    res.render("store/index", {
      registeredHomes: registeredHomes,
      pageTitle: "airbnb Home",
      currentPage: "index",
      isLoggedIn: req.isLoggedIn,
    });
  });
};
exports.getHomes = (req, res, next) => {
  Home.find().then((registeredHomes) => {
    res.render("store/home-list", {
      registeredHomes: registeredHomes,
      pageTitle: "Homes List",
      currentPage: "Home",
      isLoggedIn: req.isLoggedIn,
    });
  });
};

exports.getBookings = (req, res, next) => {
  res.render("store/bookings", {
    pageTitle: "My Bookings",
    currentPage: "bookings",
    isLoggedIn: req.isLoggedIn,
  });
};

exports.getFavouriteList = (req, res, next) => {
  Favourite.find()
    .populate("houseId")
    .then((favourites) => {
      const favouriteHomes = favourites.map((fav) => fav.houseId);
      res.render("store/favourite-list", {
        favouriteHomes: favouriteHomes,
        pageTitle: "Favourite List",
        currentPage: "favourite-list",
        isLoggedIn: req.isLoggedIn,
      });
    });
};

exports.postAddToFavourites = (req, res, next) => {
  const homeId = req.body.id;
  Favourite.findOne({ houseId: homeId })
    .then((fav) => {
      if (fav) {
        console.log("Already marked as Favourite");
      } else {
        fav = new Favourite({ houseId: homeId });
        fav.save().then((result) => {
          console.log("Fav Added:", result);
        });
      }
      res.redirect("/favourites");
    })
    .catch((err) => {
      console.log("Error While marking favourites:", err);
    });

  // const fav = new Favourite(homeId);
  // fav
  //   .save()
  //   .then((result) => {
  //     console.log("Fav Added:", result);
  //   })
  //   .catch((err) => {
  //     console.log("Error while adding to favourites :", err);
  //   })
  //   .finally(() => {
  //     res.redirect("/favourites");
  //   });
};
exports.postRemoveFromFavourites = (req, res, next) => {
  const homeId = req.params.homeId;
  Favourite.findOneAndDelete({ houseId: homeId })
    .then((result) => {
      console.log("Fav Removed:", result);
    })
    .catch((err) => {
      console.log("Error while removing to favourites :", err);
    })
    .finally(() => {
      res.redirect("/favourites");
    });
};

exports.getHomeDetails = (req, res, next) => {
  const homeId = req.params.homeId;
  Home.findById(homeId).then((home) => {
    if (!home) {
      console.log("Home not found");
      res.redirect("/homes");
    } else {
      res.render("store/home-details", {
        home: home,
        pageTitle: "Home Details",
        currentPage: "Home",
        isLoggedIn: req.isLoggedIn,
      });
    }
  });
};
