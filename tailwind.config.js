/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./views/**/*.ejs", // ✅ watches all ejs files in all folders inside /views
    "./views/**/*.html", // ✅ if you ever use .html too
    "./public/**/*.css", // ✅ optional, keeps CSS files in scope
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};
