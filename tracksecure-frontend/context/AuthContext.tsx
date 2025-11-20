import React, { createContext, useState, useContext, ReactNode, useEffect } from 'react';
import keycloak from '../keycloak';
import { User } from '../types';

interface AuthContextType {
  user: User | null;
  token: string | undefined;
  login: () => void;
  signup: (username?: string, email?: string, password?: string) => Promise<void>; // Arguments optionnels pour compatibilité
  logout: () => void;
  authError: string | null;
  isAuthenticating: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | undefined>(undefined);
  const [authError, setAuthError] = useState<string | null>(null);
  const [isAuthenticating, setIsAuthenticating] = useState<boolean>(true);

  useEffect(() => {
    // Initialisation de Keycloak au chargement de l'application
    keycloak.init({
      onLoad: 'check-sso', // Vérifie si l'utilisateur est déjà connecté (via cookie) sans forcer le login
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      pkceMethod: 'S256', // Recommandé pour la sécurité
    }).then((authenticated) => {
      if (authenticated) {
        setToken(keycloak.token);
        
        // Récupération du profil utilisateur depuis Keycloak
        keycloak.loadUserProfile().then((profile) => {
          // Mapping des rôles Keycloak vers notre type User
          // On suppose que les rôles sont dans realm_access ou resource_access
          const roles = keycloak.realmAccess?.roles || [];
          const isAdmin = roles.includes('admin') || roles.includes('administrateur');
          
          setUser({
            username: profile.username || 'Utilisateur',
            email: profile.email || '',
            role: isAdmin ? 'administrateur' : 'utilisateur',
          });
        }).catch(() => {
          setAuthError("Impossible de charger le profil utilisateur");
        });

      } else {
        setUser(null);
        setToken(undefined);
      }
      setIsAuthenticating(false);
    }).catch((err) => {
      console.error("Échec de l'initialisation Keycloak", err);
      setAuthError("Erreur de connexion au service d'authentification");
      setIsAuthenticating(false);
    });
  }, []);

  const login = () => {
    keycloak.login();
  };

  // Pour Keycloak, l'inscription se fait via la page de login/register de Keycloak
  // Les arguments sont ignorés ici car c'est Keycloak qui gère le formulaire
  const signup = async (username?: string, email?: string, password?: string) => {
    // On redirige vers la page d'inscription de Keycloak
    await keycloak.register({
        loginHint: email // Pré-remplit l'email si fourni
    });
  };

  const logout = () => {
    keycloak.logout({ redirectUri: window.location.origin });
    setUser(null);
    setToken(undefined);
  };

  const value = { user, token, login, signup, logout, authError, isAuthenticating };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth doit être utilisé à l\'intérieur d\'un AuthProvider');
  }
  return context;
};