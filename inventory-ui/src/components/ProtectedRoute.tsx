import React from 'react';
import { Navigate } from 'react-router-dom';
import { CircularProgress, Box } from '@mui/material';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, requiredRoles }) => {
  const token = localStorage.getItem('token');
  const rolesStr = localStorage.getItem('roles');
  const userRoles = rolesStr ? JSON.parse(rolesStr) : [];

  // Check if token exists
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // Check if user has required roles
  if (requiredRoles && requiredRoles.length > 0) {
    const hasRequiredRole = requiredRoles.some(role => userRoles.includes(role));
    if (!hasRequiredRole) {
      return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
          <div>Access Denied: You don't have permission to access this page</div>
        </Box>
      );
    }
  }

  return <>{children}</>;
};

export default ProtectedRoute;

