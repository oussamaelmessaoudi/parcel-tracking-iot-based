
import React, { createContext, useState, useContext, ReactNode } from 'react';
import * as authService from '../services/authService';
import { User } from '../types';

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  signup: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  authError: string | null;
  isAuthenticating: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [authError, setAuthError] = useState<string | null>(null);
  const [isAuthenticating, setIsAuthenticating] = useState<boolean>(false);

  const login = async (username: string, password: string) => {
    setIsAuthenticating(true);
    setAuthError(null);
    try {
      const userData = await authService.login(username, password);
      setUser(userData);
    } catch (error) {
      if (error instanceof Error) {
        setAuthError(error.message);
      } else {
        setAuthError('Une erreur inconnue est survenue.');
      }
    } finally {
      setIsAuthenticating(false);
    }
  };

  const signup = async (username: string, email: string, password: string) => {
    setIsAuthenticating(true);
    setAuthError(null);
    try {
      await authService.createUser(username, password, email);
      const userData = await authService.login(username, password);
      setUser(userData);
    } catch (error) {
      if (error instanceof Error) {
        setAuthError(error.message);
      } else {
        setAuthError('Une erreur inconnue est survenue.');
      }
      throw error;
    } finally {
      setIsAuthenticating(false);
    }
  };

  const logout = () => {
    setUser(null);
  };

  const value = { user, login, signup, logout, authError, isAuthenticating };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth doit être utilisé à l\'intérieur d\'un AuthProvider');
  }
  return context;
};