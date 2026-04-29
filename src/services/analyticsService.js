const Home = require('../models/home');
const User = require('../models/user');
const BookingService = require('./bookingService');

class AnalyticsService {
  static async getDashboardStats() {
    try {
      const totalProperties = await Home.countDocuments();
      const totalUsers = await User.countDocuments();
      const totalGuests = await User.countDocuments({ userType: 'guest' });
      const totalHosts = await User.countDocuments({ userType: 'host' });
      
      const bookingsResult = await BookingService.getAllBookings();
      const bookings = bookingsResult.success ? bookingsResult.data : [];
      
      const totalRevenue = bookings
        .filter(b => b.status === 'CONFIRMED' || b.status === 'COMPLETED')
        .reduce((sum, b) => sum + (b.totalPrice || 0), 0);
      
      return {
        properties: { total: totalProperties },
        users: { total: totalUsers, guests: totalGuests, hosts: totalHosts },
        bookings: {
          total: bookings.length,
          confirmed: bookings.filter(b => b.status === 'CONFIRMED').length,
          pending: bookings.filter(b => b.status === 'PENDING').length,
          cancelled: bookings.filter(b => b.status === 'CANCELLED').length,
          completed: bookings.filter(b => b.status === 'COMPLETED').length
        },
        revenue: { total: totalRevenue, average: bookings.length > 0 ? totalRevenue / bookings.length : 0 }
      };
    } catch (error) {
      console.error('Analytics error:', error);
      throw error;
    }
  }
  
  static async getPropertyPerformance() {
    try {
      const bookingsResult = await BookingService.getAllBookings();
      const bookings = bookingsResult.success ? bookingsResult.data : [];
      const properties = await Home.find();
      
      return properties.map(property => {
        const propertyBookings = bookings.filter(b => b.propertyId === property._id.toString());
        const revenue = propertyBookings
          .filter(b => b.status === 'CONFIRMED' || b.status === 'COMPLETED')
          .reduce((sum, b) => sum + (b.totalPrice || 0), 0);
        
        return {
          id: property._id,
          name: property.houseName,
          location: property.location,
          totalBookings: propertyBookings.length,
          revenue: revenue,
          rating: property.rating
        };
      }).sort((a, b) => b.revenue - a.revenue);
    } catch (error) {
      console.error('Property performance error:', error);
      throw error;
    }
  }
}

module.exports = AnalyticsService;
