import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { LogoIcon } from './Icons';

interface LoginProps {
    onBackToLanding: () => void;
}

const Login: React.FC<LoginProps> = ({ onBackToLanding }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { login, authError, isAuthenticating } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (username && password) {
      await login(username, password);
    }
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
            Connectez-vous à votre compte
          </h2>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4">
            <div>
              <label htmlFor="username" className="sr-only">Nom d'utilisateur</label>
              <input
                id="username"
                name="username"
                type="text"
                autoComplete="username"
                required
                className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 focus:z-10 sm:text-sm"
                placeholder="Nom d'utilisateur (ex: admin ou user)"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
            <div>
              <label htmlFor="password-input" className="sr-only">Mot de passe</label>
              <input
                id="password-input"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 focus:z-10 sm:text-sm"
                placeholder="Mot de passe (ex: admin123 ou user123)"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          {authError && (
            <p className="text-sm text-red-600 text-center">{authError}</p>
          )}

          <div>
            <button
              type="submit"
              disabled={isAuthenticating}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:bg-emerald-300"
            >
              {isAuthenticating ? 'Connexion...' : 'Se connecter'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;