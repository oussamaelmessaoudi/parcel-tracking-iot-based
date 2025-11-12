import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { LogoIcon } from './Icons';

interface SignupProps {
    onNavigateToLogin: () => void;
}

const Signup: React.FC<SignupProps> = ({ onNavigateToLogin }) => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [formError, setFormError] = useState<string | null>(null);
    const { signup, authError, isAuthenticating } = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setFormError(null);

        if (password !== confirmPassword) {
            setFormError('Les mots de passe ne correspondent pas.');
            return;
        }

        if (!/\S+@\S+\.\S+/.test(email)) {
            setFormError('Veuillez entrer une adresse e-mail valide.');
            return;
        }

        try {
            await signup(username, email, password);
        } catch (error) {
            console.error("L'inscription a échoué:", error);
        }
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
                </div>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="space-y-4">
                        <div>
                            <label htmlFor="username-signup" className="sr-only">Nom d'utilisateur</label>
                            <input
                                id="username-signup"
                                name="username"
                                type="text"
                                autoComplete="username"
                                required
                                className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 focus:z-10 sm:text-sm"
                                placeholder="Nom d'utilisateur"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                        </div>
                        <div>
                            <label htmlFor="email-signup" className="sr-only">Adresse e-mail</label>
                            <input
                                id="email-signup"
                                name="email"
                                type="email"
                                autoComplete="email"
                                required
                                className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 focus:z-10 sm:text-sm"
                                placeholder="Adresse e-mail"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                        <div>
                            <label htmlFor="password-signup" className="sr-only">Mot de passe</label>
                            <input
                                id="password-signup"
                                name="password"
                                type="password"
                                autoComplete="new-password"
                                required
                                className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 focus:z-10 sm:text-sm"
                                placeholder="Mot de passe"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                        <div>
                            <label htmlFor="confirm-password-signup" className="sr-only">Confirmer le mot de passe</label>
                            <input
                                id="confirm-password-signup"
                                name="confirmPassword"
                                type="password"
                                autoComplete="new-password"
                                required
                                className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 focus:z-10 sm:text-sm"
                                placeholder="Confirmer le mot de passe"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                            />
                        </div>
                    </div>

                    {(formError || authError) && (
                        <p className="text-sm text-red-600 text-center">{formError || authError}</p>
                    )}

                    <div>
                        <button
                            type="submit"
                            disabled={isAuthenticating}
                            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:bg-emerald-300"
                        >
                            {isAuthenticating ? 'Création...' : "S'inscrire"}
                        </button>
                    </div>
                </form>
                <div className="text-sm text-center">
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