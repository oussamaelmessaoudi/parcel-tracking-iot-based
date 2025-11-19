import React from 'react';
import { TrackingIllustration } from './Icons';
import Header from './Header';

interface LandingPageProps {
    onNavigateToLogin: () => void;
    onNavigateToContact: () => void;
    onNavigateToSolutions: () => void;
}

const LandingPage: React.FC<LandingPageProps> = ({ onNavigateToLogin, onNavigateToContact, onNavigateToSolutions }) => {
    return (
        <div className="min-h-screen font-sans bg-white text-gray-800 overflow-hidden" style={{background: 'radial-gradient(circle at top left, #f0fdfa, white 60%)'}}>
            <div className="container mx-auto px-6 py-4">
                {/* En-tête unifié */}
                <Header 
                    onLoginClick={onNavigateToLogin} 
                    onContactClick={onNavigateToContact}
                    onSolutionsClick={onNavigateToSolutions} 
                />

                {/* Section Hero */}
                <main className="mt-16 md:mt-24">
                    <div className="grid md:grid-cols-2 gap-12 items-center">
                        {/* Contenu texte */}
                        <div className="space-y-6 text-center md:text-left">
                            <h1 className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-extrabold leading-tight text-slate-900">
                                <span className="text-emerald-500">Suivi de colis.</span>
                                <br />
                                Simple. Instantané.
                            </h1>
                            <p className="text-lg text-gray-600 max-w-md mx-auto md:mx-0">
                                Gardez un œil sur vos biens précieux à chaque étape du voyage, de l'entrepôt à votre porte.
                            </p>
                            <button onClick={onNavigateToLogin} className="bg-emerald-500 hover:bg-emerald-600 text-white font-bold py-4 px-8 rounded-full text-lg transition duration-300 shadow-lg hover:shadow-xl transform hover:scale-105">
                                Commencer le suivi
                            </button>
                        </div>

                        {/* Illustration */}
                        <div className="relative">
                           <TrackingIllustration className="w-full h-auto max-w-lg mx-auto" />
                        </div>
                    </div>
                </main>
            </div>
        </div>
    );
};

export default LandingPage;