// // const mysql = require("mysql2");

// // const pool = mysql.createPool({
// //   host: "localhost",
// //   user: "root",
// //   password: "root",
// //   database: "airbnb",
// // });
// // module.exports = pool.promise();
// const mongo = require("mongodb");

// const MongoClient = mongo.MongoClient;
// const MONGO_URL =
//   "***REMOVED***";

// let _db;

// const mongoConnect = (callback) => {
//   MongoClient.connect(MONGO_URL)
//     .then((client) => {
//       callback();
//       _db = client.db("airbnb");
//     })
//     .catch((err) => {
//       console.log("Error While Connecting to Mongo: ", err);
//     });
// };

// const getDB = () => {
//   if (!_db) {
//     throw new Error("Mongo not connected");
//   }
//   return _db;
// };

// exports.mongoConnect = mongoConnect;
// exports.getDB = getDB;
