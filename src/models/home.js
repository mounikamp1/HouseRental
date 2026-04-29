// //core modules
// //The below commented  lines are related to file operations
// // const fs = require("fs");
// // const path = require("path");
// // const rootDir = require("../util/pathUtil");
// // const Favourite = require("./favourite");

// // const homeDataPath = path.join(rootDir, "data", "homes.json");
// const { ObjectId } = require("mongodb");
// //const { getDB } = require("../util/databaseUtil");

// module.exports = class Home {
//   constructor(
//     houseName,
//     address,
//     location,
//     price,
//     rating,
//     photo,
//     description,
//     _id
//   ) {
//     this.houseName = houseName;
//     this.address = address;
//     this.location = location;
//     this.price = price;
//     this.rating = rating;
//     this.photo = photo;
//     this.description = description;
//     if (_id) {
//       this._id = _id;
//     }
//   }
//   // save() {
//   //   Home.find((registeredHomes) => {
//   //     if (this.id) {
//   //       // edit home case
//   //       registeredHomes = registeredHomes.map((home) =>
//   //         home._id === this.id ? this : home
//   //       );
//   //     } else {
//   //       // add home case
//   //       this.id = Math.random().toString();
//   //       registeredHomes.push(this);
//   //     }

//   //     fs.writeFile(homeDataPath, JSON.stringify(registeredHomes), (error) => {
//   //       console.log("File Writing Concluded", error);
//   //     });
//   //   });
//   // }
//   // static find(callback) {
//   //   fs.readFile(homeDataPath, (err, data) => {
//   //     callback(!err ? JSON.parse(data) : []);
//   //   });
//   // }

//   // static findById(homeId, callback) {
//   //   this.find((homes) => {
//   //     const homeFound = homes.find((home) => home._id === homeId);
//   //     callback(homeFound);
//   //   });
//   // }
//   // static deleteById(homeId, callback) {
//   //   this.find((homes) => {
//   //     homes = homes.filter((home) => home._id !== homeId);
//   //     fs.writeFile(homeDataPath, JSON.stringify(homes), (error) => {
//   //       Favourite.deleteById(homeId, callback);
//   //     });
//   //   });
//   // }
//   //   save() {
//   //     //update
//   //     if (this.id) {
//   //       return db.execute(
//   //         "UPDATE homes SET houseName=?,address=?,location=?,price=?,rating=?,photo=?,description=? WHERE id=?",
//   //         [
//   //           this.houseName,
//   //           this.address,
//   //           this.location,
//   //           this.price,
//   //           this.rating,
//   //           this.photo,
//   //           this.description,
//   //           this.id,
//   //         ]
//   //       );
//   //     } else {
//   //       //add
//   //       return db.execute(
//   //         "INSERT INTO homes (houseName,address,location,price,rating,photo,description) VALUES(?,?,?,?,?,?,?)",
//   //         [
//   //           this.houseName,
//   //           this.address,
//   //           this.location,
//   //           this.price,
//   //           this.rating,
//   //           this.photo,
//   //           this.description,
//   //         ]
//   //       );
//   //     }
//   //   }
//   //   static find() {
//   //     return db.execute("SELECT * FROM homes");
//   //   }

//   //   static findById(id) {
//   //     return db.execute("SELECT * FROM homes WHERE id=?", [id]);
//   //   }
//   //   static deleteById(id) {
//   //     return db.execute("DELETE FROM homes WHERE id=?", [id]);
//   //   }
//   // };
//   save() {
//     const db = getDB();
//     if (this._id) {
//       //Update
//       const updateFields = {
//         houseName: this.houseName,
//         address: this.address,
//         location: this.location,
//         price: this.price,
//         rating: this.rating,
//         photo: this.photo,
//         description: this.description,
//       };
//       return db
//         .collection("homes")
//         .updateOne(
//           { _id: new ObjectId(String(this._id)) },
//           { $set: updateFields }
//         );
//     } else {
//       // Insert
//       return db.collection("homes").insertOne(this);
//     }
//   }

//   static find() {
//     const db = getDB();
//     return db.collection("homes").find().toArray();
//   }
//   static findById(homeId) {
//     const db = getDB();
//     return db
//       .collection("homes")
//       .find({ _id: new ObjectId(String(homeId)) })
//       .next();
//   }
//   static deleteById(homeId) {
//     const db = getDB();
//     return db
//       .collection("homes")
//       .deleteOne({ _id: new ObjectId(String(homeId)) });
//   }
// };
// const { ObjectId } = require("mongodb");
const mongoose = require("mongoose");
//const favourite = require("./favourite");
const homeSchema = mongoose.Schema({
  houseName: { type: String, required: true },
  address: { type: String, required: true },
  location: { type: String, required: true },
  price: { type: Number, required: true },
  rating: { type: Number, required: true },
  photo: String,
  description: String,
});

// homeSchema.pre("findOneAndDelete", async function (next) {
//   console.log("Aim to prehook while deleting a home");
//   const homeId = this.getQuery()._id;
//   await favourite.deleteMany({ houseId: homeId });
//   next();
// });
module.exports = mongoose.model("Home", homeSchema);
