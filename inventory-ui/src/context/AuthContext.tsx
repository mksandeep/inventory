import React, { createContext, useContext, ReactNode } from 'react';

interface User {
  email: string;
  fullName: string;
  roles: string[];
}

interface AuthContextType {
  user: User | null;
  isAdmin: boolean;
  isManager: boolean;
  canEdit: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const email = localStorage.getItem('email') || '';
  const fullName = localStorage.getItem('fullName') || 'User';
  const rolesStr = localStorage.getItem('roles') || '[]';
  const roles = JSON.parse(rolesStr);

  const user: User | null = email ? { email, fullName, roles } : null;
  const isAdmin = roles.includes('ADMIN');
  const isManager = roles.includes('MANAGER');
  const canEdit = isAdmin || isManager;

  const value: AuthContextType = {
    user,
    isAdmin,
    isManager,
    canEdit,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

