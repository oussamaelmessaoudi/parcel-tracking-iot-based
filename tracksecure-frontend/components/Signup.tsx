import React from 'react';
import { useAuth } from '../context/AuthContext';
import { LogoIcon } from './Icons';

interface SignupProps {
    onNavigateToLogin: () => void;
}

const Signup: React.FC<SignupProps> = ({ onNavigateToLogin }) => {
    const { signup, isAuthenticating } = useAuth();

    const handleSignup = () => {
        // Appel sans arguments car la saisie des informations se fait sur Keycloak
        signup(); 
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8" style={{background: 'radial-gradient(circle at top left, #f0fdfa, white 60%)'}}>
            <button onClick={onNavigateToLogin} className="absolute top-4 left-4 text-gray-600 hover:text-emerald-500 transition-colors">
                &larr; Retour à la connexion
            </button>
            <div className="max-w-md w-full space-y-8 p-10 bg-white shadow-xl rounded-2xl">
                <div>
                    <div className="flex justify-center">
                        <LogoIcon className="h-12 w-auto text-emerald-500" />
                    </div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        Créer un nouveau compte
                    </h2>
                    <p className="mt-2 text-center text-sm text-gray-600">
                        Rejoignez TrackSecure pour suivre vos colis en temps réel.
                        L'inscription est sécurisée et gérée par notre système d'authentification centralisé.
                    </p>
                </div>

                <div className="mt-8 space-y-6">
                     <button
                        onClick={handleSignup}
                        disabled={isAuthenticating}
                        className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:bg-emerald-300Qq transition-all transform hover:scale-105 shadow-lg"
                    >
                        {isAuthenticating ? 'Redirection...' : "S'inscrire avec Keycloak"}
                    </button>
                </div>
                
                <div className="text-sm text-center mt-4">
                    <button
                        onClick={onNavigateToLogin}
                        className="font-medium text-emerald-600 hover:text-emerald-500"
                    >
                        Déjà un compte ? Se connecter
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Signup;