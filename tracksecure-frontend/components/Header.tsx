import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { LogoIcon, LogoutIcon, UserIcon } from './Icons';

interface HeaderProps {
    /** Fonction à appeler lorsque l'utilisateur clique sur le bouton de connexion. */
    onLoginClick?: () => void;
    /** Fonction à appeler lorsque l'utilisateur clique sur le lien de contact. */
    onContactClick?: () => void;
    /** Fonction à appeler lorsque l'utilisateur clique sur le lien des solutions. */
    onSolutionsClick?: () => void;
}

/**
 * Un composant d'en-tête adaptatif qui change en fonction de l'état d'authentification de l'utilisateur.
 * - En mode non authentifié (page d'accueil), il affiche les liens de navigation et un bouton de connexion.
 * - En mode authentifié (tableaux de bord), il affiche le nom de l'utilisateur et un bouton de déconnexion.
 */
const Header: React.FC<HeaderProps> = ({ onLoginClick, onContactClick, onSolutionsClick }) => {
    const { user, logout } = useAuth();
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    useEffect(() => {
        if (isMobileMenuOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'unset';
        }
        return () => {
            document.body.style.overflow = 'unset';
        };
    }, [isMobileMenuOpen]);

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
        <>
            <header className="flex justify-between items-center py-4">
                <div className="flex items-center">
                    <LogoIcon className="h-8 w-8 text-emerald-500 mr-2" />
                    <span className="text-2xl font-bold text-slate-800">TrackSecure</span>
                </div>
                {/* Desktop Nav */}
                <nav className="hidden md:flex items-center space-x-8">
                    <button onClick={onSolutionsClick} className="text-gray-600 hover:text-emerald-500 transition-colors">Solutions</button>
                    <button onClick={onContactClick} className="text-gray-600 hover:text-emerald-500 transition-colors">Contact</button>
                    <button onClick={onLoginClick} className="bg-emerald-400 hover:bg-emerald-500 text-white font-bold py-2 px-6 rounded-full transition duration-300 transform hover:scale-105">
                        Se Connecter
                    </button>
                </nav>
                {/* Mobile Nav Button */}
                <div className="md:hidden">
                    <button
                        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                        className="p-2 rounded-md text-gray-600 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-emerald-500 z-50 relative"
                        aria-label="Ouvrir le menu principal"
                        aria-expanded={isMobileMenuOpen}
                    >
                        <svg className="h-6 w-6" stroke="currentColor" fill="none" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d={!isMobileMenuOpen ? "M4 6h16M4 12h16M4 18h16" : "M6 18L18 6M6 6l12 12"} />
                        </svg>
                    </button>
                </div>
            </header>
            
            {/* Mobile Menu Overlay */}
            <div
                className={`fixed inset-0 z-30 transition-opacity duration-300 ease-in-out md:hidden ${
                isMobileMenuOpen ? 'bg-black bg-opacity-50' : 'bg-opacity-0 pointer-events-none'
                }`}
                onClick={() => setIsMobileMenuOpen(false)}
                aria-hidden="true"
            ></div>

            {/* Mobile Menu Panel */}
            <nav
                className={`fixed top-0 right-0 h-full w-3/4 max-w-xs bg-white shadow-2xl z-40 transform transition-transform duration-300 ease-in-out md:hidden flex flex-col p-6 pt-20 ${
                isMobileMenuOpen ? 'translate-x-0' : 'translate-x-full'
                }`}
                role="dialog"
                aria-modal="true"
            >
                <div className="flex-grow space-y-4">
                    <button 
                        onClick={() => { onSolutionsClick?.(); setIsMobileMenuOpen(false); }} 
                        className="w-full text-left block px-4 py-3 text-xl font-semibold text-gray-800 rounded-lg hover:bg-emerald-50 hover:text-emerald-600 transition-colors"
                    >
                        Solutions
                    </button>
                    <button 
                        onClick={() => { onContactClick?.(); setIsMobileMenuOpen(false); }} 
                        className="w-full text-left block px-4 py-3 text-xl font-semibold text-gray-800 rounded-lg hover:bg-emerald-50 hover:text-emerald-600 transition-colors"
                    >
                        Contact
                    </button>
                </div>
                <div className="pb-4">
                    <button 
                        onClick={() => { onLoginClick?.(); setIsMobileMenuOpen(false); }} 
                        className="w-full text-center block px-4 py-3 text-base text-white font-bold bg-emerald-500 hover:bg-emerald-600 rounded-full transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-xl"
                    >
                        Se connecter
                    </button>
                </div>
            </nav>
        </>
    );
};

export default Header;