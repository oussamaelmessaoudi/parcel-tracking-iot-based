import React from 'react';
import { useAuth } from '../context/AuthContext';
import { LogoIcon } from './Icons';

interface LoginProps {
    onBackToLanding: () => void;
    onNavigateToSignup: () => void;
}

const Login: React.FC<LoginProps> = ({ onBackToLanding, onNavigateToSignup }) => {
  const { login, isAuthenticating } = useAuth();

  const handleLogin = () => {
    login(); // Redirige vers Keycloak
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8" style={{background: 'radial-gradient(circle at top left, #f0fdfa, white 60%)'}}>
        <button onClick={onBackToLanding} className="absolute top-4 left-4 text-gray-600 hover:text-emerald-500 transition-colors">
            &larr; Retour à l'accueil
        </button>
      <div className="max-w-md w-full space-y-8 p-10 bg-white shadow-xl rounded-2xl">
        <div>
            <div className="flex justify-center">
                <LogoIcon className="h-12 w-auto text-emerald-500" />
            </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Connexion sécurisée
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
              Accédez à votre tableau de bord via notre service d'authentification centralisé.
          </p>
        </div>

        <div className="mt-8 space-y-6">
            <button
              onClick={handleLogin}
              disabled={isAuthenticating}
              className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:bg-emerald-300 transition-all transform hover:scale-105 shadow-lg"
            >
              {isAuthenticating ? 'Initialisation...' : 'Se connecter avec Keycloak SSO'}
            </button>
            
             <div className="relative">
                <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-gray-300"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                    <span className="px-2 bg-white text-gray-500">Nouveau sur TrackSecure ?</span>
                </div>
            </div>

             <button
                onClick={onNavigateToSignup}
                className="w-full flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500"
            >
                Créer un compte
            </button>
        </div>
      </div>
    </div>
  );
};

export default Login;