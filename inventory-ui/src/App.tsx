import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Box, Container, ThemeProvider, CssBaseline } from '@mui/material';
import { theme } from './theme/theme';
import Header from './components/Header';
import Login from './pages/Login';
import ProtectedRoute from './components/ProtectedRoute';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import Stock from './pages/Stock';
import Purchases from './pages/Purchases';
import Sales from './pages/Sales';
import ProductDetailsSummary from './pages/ProductDetailsSummary';
import SalesDetailsSummary from './pages/SalesDetailsSummary';
import PurchaseDetailsSummary from './pages/PurchaseDetailsSummary';
import { AuthProvider } from './context/AuthContext';

const App: React.FC = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    setIsLoggedIn(!!token);
    setLoading(false);
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <AuthProvider>
          {isLoggedIn ? (
            <>
              <Header />
              <Box
                sx={{
                  minHeight: '100vh',
                  background: 'linear-gradient(180deg, #f8fafc 0%, #ffffff 45%)',
                  py: 4
                }}
              >
                <Container maxWidth="xl">
                   <Routes>
                    <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
                    <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
                    <Route path="/stocks" element={<ProtectedRoute><Stock /></ProtectedRoute>} />
                    <Route path="/stock-summary" element={<ProtectedRoute><ProductDetailsSummary /></ProtectedRoute>} />
                    <Route path="/purchases" element={<ProtectedRoute><Purchases /></ProtectedRoute>} />
                    <Route path="/purchases/summary" element={<ProtectedRoute><PurchaseDetailsSummary /></ProtectedRoute>} />
                    <Route path="/sales" element={<ProtectedRoute><Sales /></ProtectedRoute>} />
                    <Route path="/sales-summary" element={<ProtectedRoute><SalesDetailsSummary /></ProtectedRoute>} />
                    <Route path="/login" element={<Navigate to="/" replace />} />
                  </Routes>
                </Container>
              </Box>
            </>
          ) : (
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
          )}
        </AuthProvider>
      </Router>
    </ThemeProvider>
  );
};

export default App;
