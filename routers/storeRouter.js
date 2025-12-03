//External Module
const express = require("express");
const storeRouter = express.Router();
//Local module

const storeController = require("../controllers/storeController");

storeRouter.get("/", storeController.getIndex);
storeRouter.get("/homes", storeController.getHomes);
storeRouter.get("/bookings", storeController.getBookings);
storeRouter.get("/favourites", storeController.getFavouriteList);
storeRouter.get("/homes/:homeId", storeController.getHomeDetails);
storeRouter.post("/favourites", storeController.postAddToFavourites);
storeRouter.post(
  "/favourites/delete/:homeId",
  storeController.postRemoveFromFavourites
);
storeRouter.get("/rules/:homeId", storeController.getHouseRules);

module.exports = storeRouter;
