// //core modules
// // const fs = require("fs");
// // const path = require("path");
// // const rootDir = require("../util/pathUtil");

// //const { getDB } = require("../util/databaseUtil");

// // const favouriteDataPath = path.join(rootDir, "data", "favourite.json");

// module.exports = class Favourite {
//   // static addToFavourite(id, callback) {
//   //   Favourite.getFavourites((favourites) => {
//   //     if (favourites.includes(id)) {
//   //       callback("Home already in favourites");
//   //     } else {
//   //       favourites.push(id);
//   //       fs.writeFile(favouriteDataPath, JSON.stringify(favourites), callback);
//   //     }
//   //   });
//   // }
//   // static getFavourites(callback) {
//   //   fs.readFile(favouriteDataPath, (err, data) => {
//   //     callback(!err ? JSON.parse(data) : []);
//   //   });
//   // }
//   // static deleteById(delHomeId, callback) {
//   //   Favourite.getFavourites((homeIds) => {
//   //     homeIds = homeIds.filter((homeId) => delHomeId !== homeId);
//   //     fs.writeFile(favouriteDataPath, JSON.stringify(homeIds), callback);
//   //   });
//   // }

//   constructor(houseId) {
//     this.houseId = houseId;
//   }
//   save() {
//     const db = getDB();
//     return db
//       .collection("favourites")
//       .findOne({ houseId: this.houseId })
//       .then((existingFav) => {
//         if (!existingFav) {
//           return db.collection("favourites").insertOne(this);
//         }
//         return Promise.resolve();
//       });
//   }

//   static getFavourites() {
//     const db = getDB();
//     return db.collection("favourites").find().toArray();
//   }
//   static deleteById(delHomeId) {
//     const db = getDB();
//     return db.collection("favourites").deleteOne({ houseId: delHomeId });
//   }
// };
// const mongoose = require("mongoose");

// const favouriteSchema = new mongoose.Schema({
//   houseId: {
//     type: mongoose.Schema.Types.ObjectId,
//     ref: "Home",
//     requried: true,
//     unique: true,
//   },
// });

// module.exports = mongoose.model("Favourite", favouriteSchema);
