"use strict";
const express = require("express");
const storeRouter = express.Router();
const storeController = require("../controllers/storeController");
const { requireAuth } = require("../middleware/auth");

storeRouter.get("/",           storeController.getIndex);
storeRouter.get("/homes",      storeController.getHomes);
storeRouter.get("/homes/:homeId", storeController.getHomeDetails);

// Auth-protected store routes
storeRouter.get("/favourites",                      requireAuth, storeController.getFavouriteList);
storeRouter.post("/favourites",                     requireAuth, storeController.postAddToFavourites);
storeRouter.post("/favourites/delete/:homeId",      requireAuth, storeController.postRemoveFromFavourites);
storeRouter.get("/rules/:homeId",                   requireAuth, storeController.getHouseRules);

module.exports = storeRouter;