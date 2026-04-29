const AnalyticsService = require('../services/analyticsService');

exports.getDashboard = async (req, res, next) => {
  try {
    if (!req.session.user || req.session.user.userType !== 'host') {
      return res.status(403).render('error', {
        error: 'Access denied. Host privileges required.',
        pageTitle: 'Access Denied',
        isLoggedIn: req.isLoggedIn,
        user: req.session.user
      });
    }

    const stats = await AnalyticsService.getDashboardStats();
    const propertyPerformance = await AnalyticsService.getPropertyPerformance();

    res.render('admin/analytics-dashboard', {
      pageTitle: 'Analytics Dashboard',
      currentPage: 'analytics',
      isLoggedIn: req.isLoggedIn,
      user: req.session.user,
      stats: stats,
      propertyPerformance: propertyPerformance.slice(0, 5)
    });
  } catch (error) {
    console.error('Dashboard error:', error);
    next(error);
  }
};

exports.getStats = async (req, res, next) => {
  try {
    if (!req.session.user || req.session.user.userType !== 'host') {
      return res.status(403).json({ error: 'Access denied' });
    }

    const stats = await AnalyticsService.getDashboardStats();
    res.json(stats);
  } catch (error) {
    console.error('Stats API error:', error);
    res.status(500).json({ error: 'Failed to fetch statistics' });
  }
};

exports.getPropertyPerformance = async (req, res, next) => {
  try {
    if (!req.session.user || req.session.user.userType !== 'host') {
      return res.status(403).json({ error: 'Access denied' });
    }

    const performance = await AnalyticsService.getPropertyPerformance();
    res.json(performance);
  } catch (error) {
    console.error('Property performance API error:', error);
    res.status(500).json({ error: 'Failed to fetch property performance' });
  }
};
