# 🏠 AirBnb Clone - House Rental Platform

A modern, full-featured house rental platform built with **Node.js**, **Express**, and **Tailwind CSS**. This application allows users to browse, book, and manage properties with a beautiful and responsive user interface.

## ✨ Features

### 🏘️ For Guests

- **Browse Properties** - Explore available homes with detailed information and ratings
- **Property Details** - View comprehensive property information, location, pricing, and amenities
- **Favorites** - Save favorite properties for later reference
- **Booking System** - Reserve properties with ease
- **Download House Rules** - Get property guidelines in multiple formats (PDF, Word, Excel)
- **Responsive Design** - Seamless experience on mobile, tablet, and desktop

### 🔑 For Hosts

- **List Properties** - Add new properties to the platform with photos and details
- **Manage Listings** - Edit and delete property information
- **Property Dashboard** - View all hosted properties in a clean dashboard
- **Download Options** - Export house rules in multiple formats

### 👤 User Account

- **Secure Authentication** - Register and login with encrypted passwords (bcryptjs)
- **Role Selection** - Choose between Guest or Host accounts
- **Session Management** - Persistent sessions with MongoDB store
- **Error Handling** - Clear error messages for form validation

## 🛠️ Tech Stack

| Component               | Technology                      | Version |
| ----------------------- | ------------------------------- | ------- |
| **Runtime**             | Node.js                         | v14+    |
| **Server Framework**    | Express.js                      | ^5.1.0  |
| **Database**            | MongoDB with Mongoose           | ^8.19.3 |
| **Frontend**            | EJS Templating                  | ^3.1.10 |
| **Styling**             | Tailwind CSS                    | ^3.4.18 |
| **Security**            | bcryptjs                        | ^3.0.3  |
| **Sessions**            | express-session + MongoDB Store | ^1.18.2 |
| **File Upload**         | Multer                          | ^2.0.2  |
| **Validation**          | express-validator               | ^7.3.1  |
| **Body Parser**         | body-parser                     | ^2.2.0  |
| **Dev Tool - Server**   | Nodemon                         | ^3.1.10 |
| **Dev Tool - Parallel** | Concurrently                    | ^9.2.1  |
| **CSS Processing**      | PostCSS + Autoprefixer          | ^8.5.6  |

## 📦 Project Structure

```
airbnb/
├── controllers/              # Business logic
│   ├── authController.js    # Authentication & user management
│   ├── hostController.js    # Host property management
│   ├── storeController.js   # Guest browsing & booking
│   └── errors.js            # Error handling
├── models/                  # Database schemas
│   ├── home.js             # Property model
│   └── favourite.js        # Favorite properties model
├── routers/                # API routes
│   ├── authRouter.js       # Auth routes (/login, /signup)
│   ├── hostRouter.js       # Host routes (/host/*)
│   └── storeRouter.js      # Store routes (/homes, /favorites)
├── views/                  # EJS templates
│   ├── auth/               # Login & signup pages
│   │   ├── login.ejs
│   │   └── signup.ejs
│   ├── host/               # Host management pages
│   │   ├── edit-home.ejs
│   │   └── host-home-list.ejs
│   ├── store/              # Guest browsing pages
│   │   ├── home-details.ejs
│   │   ├── home-list.ejs
│   │   ├── index.ejs
│   │   ├── favourite-list.ejs
│   │   ├── bookings.ejs
│   │   └── reserve.ejs
│   ├── partials/           # Reusable components
│   │   ├── head.ejs
│   │   ├── nav.ejs
│   │   └── favourite.ejs
│   ├── 404.ejs             # Error page
│   └── input.css           # Tailwind input
├── public/                 # Static assets
│   ├── images/            # Property images
│   ├── home.css           # Custom CSS
│   └── output.css         # Compiled Tailwind CSS
├── util/                   # Utility functions
│   ├── databaseUtil.js    # Database operations
│   └── pathUtil.js        # Path utilities
├── uploads/                # Uploaded property images
├── rules/                  # House rules templates
├── data/                   # JSON data files
│   ├── homes.json         # Property data
│   └── favourite.json     # Favorites data
├── app.js                 # Main application entry point
├── package.json           # Dependencies & scripts
├── .env                   # Environment variables
├── nodemon.json           # Nodemon configuration
├── tailwind.config.js     # Tailwind configuration
└── README.md              # This file
```

## 🚀 Getting Started

### Prerequisites

- **Node.js** v14 or higher
- **MongoDB** (local or cloud - Atlas)
- **npm** or yarn

### Installation

1. **Clone the repository**

```bash
git clone <repository-url>
cd airbnb
```

2. **Install all dependencies**

```bash
npm install
```

3. **Configure environment variables**
   Create a `.env` file in the root directory:

```env
PORT=5000
MONGODB_URI=***REMOVED***/airbnb?appName=CompleteCoding
NODE_ENV=development
```

4. **Run the application (starts both server and Tailwind CSS)**

```bash
npm run dev
```

Or run separately in two terminals:

```bash
# Terminal 1 - Start the server
npm start

# Terminal 2 - Build and watch Tailwind CSS
npm run tailwind
```

The application will be available at `http://localhost:5000`

## 📝 Available Scripts

```bash
# Start the server with Nodemon (auto-reload)
npm start

# Run both server and Tailwind CSS in parallel (recommended for development)
npm run dev

# Run Tailwind CSS build and watch for changes
npm run tailwind

# Run tests (not configured yet)
npm test
```

## 🎨 Design System

### Color Palette

- **Primary Gradient**: Red to Orange (`from-red-500 to-orange-500`)
- **Secondary Gradient**: Purple to Indigo (`from-purple-500 to-indigo-600`)
- **Background**: Dark Gradient (`from-slate-900 via-purple-900 to-slate-900`)
- **Accent Colors**: Blue, Yellow, Green
- **Neutral**: Gray scale for text and backgrounds

### UI Components

- **Navigation Bar** - Sticky gradient header with emoji icons
- **Property Cards** - Modern cards with hover effects and animations
- **Forms** - Clean, organized with section breaks and error handling
- **Buttons** - Gradient buttons with scale animations on hover
- **Alerts** - Error boxes with bullet points for validation messages

## 🔐 Security & Authentication

- **Password Encryption** - bcryptjs with salt rounds
- **Session Management** - Secure sessions stored in MongoDB
- **Route Protection** - Authentication middleware for protected routes
- **CSRF Prevention** - Session-based validation
- **File Upload Validation** - Image MIME type checking

## 📱 Responsive Breakpoints

The application is fully responsive using Tailwind CSS breakpoints:

- **Mobile**: Default (320px+)
- **Tablet**: `md:` (768px+)
- **Desktop**: `lg:` (1024px+)
- **Large Desktop**: `xl:` (1280px+)

## 🌐 API Routes

### Authentication Routes

```
POST /signup              - Register new user
POST /login               - User login
GET  /logout              - User logout
```

### Store Routes (Guest)

```
GET  /                    - Home page with featured properties
GET  /homes               - Browse all properties
GET  /homes/:id           - Property details
GET  /favourites          - View favorite properties
POST /favourites          - Add property to favorites
GET  /bookings            - View bookings
```

### Host Routes (Protected)

```
GET  /host/homes          - Host dashboard
GET  /host/edit-home/:id  - Edit property
POST /host/edit-home/:id  - Update property
GET  /host/add-home       - Add new property form
POST /host/add-home       - Create new property
DELETE /host/homes/:id    - Delete property
```

## 📊 Database Schema

### Home Model

```javascript
{
  _id: ObjectId,
  houseName: String,         // Property name
  address: String,           // Property address
  location: String,          // Location/city
  price: Number,             // Price per night
  rating: Number,            // Property rating (1-5)
  photo: String,             // Photo URL
  hostId: ObjectId,          // Host user ID
  createdAt: Date,           // Creation timestamp
  updatedAt: Date            // Last update timestamp
}
```

### Favourite Model

```javascript
{
  _id: ObjectId,
  userId: ObjectId,          // Guest user ID
  homeId: ObjectId,          // Property ID
  addedAt: Date              // When added to favorites
}
```

## 🎯 Key Features Explained

### Download House Rules

- Guests can download property house rules in multiple formats
- Supports PDF, Word, and Excel formats
- Accessible from property details page (`/homes/:id`)
- Professional UI with gradient button styling
- Files stored in `rules/` directory

### Favorites System (❤️)

- Save properties to favorites list for future reference
- Quick access from dedicated `/favourites` page
- Visual favorite button (➕) on property cards
- Add/remove from favorites with smooth animations
- Persistent storage in MongoDB with user association

### Property Management

- Hosts can list unlimited properties with complete details
- Edit property information through `/host/edit-home/:id`
- Delete properties from dashboard
- Upload property photos with validation (PNG, JPG, JPEG only)
- Store and manage house rules documents

### House Rules Management

- Store house rules templates in `rules/` directory
- Download rules in multiple formats (PDF, Word, Excel)
- Accessible from property details page
- Professional UI with gradient button styling

### User Roles

- **Guest Role**: Browse, book, and favorite properties
- **Host Role**: Manage property listings
- Role selected at signup
- Different UI/routes based on role

## 🐛 Error Handling

The application includes comprehensive error handling with bullet-point error messages:

```
* Please enter a valid email address.
* Password must be at least 8 characters.
* Please select an account type.
```

## 🔧 Configuration

### Environment Variables (.env)

```env
PORT=5000                    # Server port
MONGODB_URI=...              # MongoDB connection string
NODE_ENV=development         # Environment type
```

### Tailwind CSS

Edit `tailwind.config.js` to customize colors, spacing, and breakpoints.

## 📈 Future Enhancements

- [ ] Payment gateway integration (Stripe/PayPal)
- [ ] Real review and rating system
- [ ] Real-time notifications
- [ ] Email confirmations for bookings
- [ ] Advanced search with filters
- [ ] Map integration (Google Maps)
- [ ] Chat messaging system
- [ ] Admin panel with analytics
- [ ] Two-factor authentication

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 🆘 Troubleshooting

### Port already in use

Change the PORT in `.env` file to an available port (e.g., 5001, 8000)

### MongoDB connection failed

- Ensure MongoDB is running
- Verify connection string in `.env`
- Check credentials for MongoDB Atlas

### Images not loading

- Check `public/images/` directory exists
- Verify image paths in database
- Check file upload permissions

### Tailwind CSS not compiling

- Run `npm run tailwind` in a separate terminal
- Clear browser cache
- Rebuild CSS files

### Session not persisting

- Ensure MongoDB connection is working
- Clear browser cookies
- Restart the server

## 📞 Support

For issues or questions:

- Open an issue on GitHub
- Check existing documentation
- Contact the development team

---

**Built with ❤️ using Node.js, Express, MongoDB, and Tailwind CSS**

Last Updated: 2025
