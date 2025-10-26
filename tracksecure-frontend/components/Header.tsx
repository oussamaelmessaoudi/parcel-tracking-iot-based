import React from 'react';
import { useAuth } from '../context/AuthContext';
import { LogoIcon, LogoutIcon, UserIcon } from './Icons';

interface HeaderProps {
    /** Fonction à appeler lorsque l'utilisateur clique sur le bouton de connexion. */
    onLoginClick?: () => void;
}

/**
 * Un composant d'en-tête adaptatif qui change en fonction de l'état d'authentification de l'utilisateur.
 * - En mode non authentifié (page d'accueil), il affiche les liens de navigation et un bouton de connexion.
 * - En mode authentifié (tableaux de bord), il affiche le nom de l'utilisateur et un bouton de déconnexion.
 */
const Header: React.FC<HeaderProps> = ({ onLoginClick }) => {
    const { user, logout } = useAuth();

    // Rendu pour l'utilisateur authentifié (dans les tableaux de bord)
    if (user) {
        return (
            <header className="bg-white shadow-sm sticky top-0 z-50">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center py-3">
                        <div className="flex items-center">
                            <LogoIcon className="h-8 w-8 text-emerald-500 mr-2" />
                            <span className="text-xl font-bold text-slate-800">TrackSecure</span>
                        </div>
                        <div className="flex items-center space-x-2 sm:space-x-4">
                            <div className="flex items-center space-x-2 bg-gray-100 py-1.5 px-3 rounded-full">
                                <UserIcon className="h-5 w-5 text-gray-500" />
                                <span className="text-gray-700 font-medium text-sm hidden sm:inline">{user.username}</span>
                            </div>
                            <button
                                onClick={logout}
                                className="p-2 rounded-full text-gray-600 hover:bg-gray-100 hover:text-emerald-600 transition-colors"
                                title="Se déconnecter"
                            >
                                <LogoutIcon className="h-6 w-6" />
                            </button>
                        </div>
                    </div>
                </div>
            </header>
        );
    }

    // Rendu pour l'utilisateur non authentifié (sur la page d'accueil)
    return (
        <header className="flex justify-between items-center py-4">
            <div className="flex items-center">
                <LogoIcon className="h-8 w-8 text-emerald-500 mr-2" />
                <span className="text-2xl font-bold text-slate-800">TrackSecure</span>
            </div>
            <nav className="hidden md:flex items-center space-x-8">
                <a href="#" className="text-gray-600 hover:text-emerald-500 transition-colors">Solutions</a>
                <a href="#" className="text-gray-600 hover:text-emerald-500 transition-colors">Contact</a>
                <button onClick={onLoginClick} className="bg-emerald-400 hover:bg-emerald-500 text-white font-bold py-2 px-6 rounded-full transition duration-300 transform hover:scale-105">
                    Se Connecter
                </button>
            </nav>
            <div className="md:hidden">
                <button onClick={onLoginClick} className="bg-emerald-400 text-white font-bold py-2 px-4 rounded-full text-sm">
                    Connexion
                </button>
            </div>
        </header>
    );
};

export default Header;