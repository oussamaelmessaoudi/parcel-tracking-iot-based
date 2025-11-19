import React, { useState } from 'react';
import { createUser } from '../../services/authService';

interface CreateUserFormProps {
    onSuccess: () => void;
    onCancel: () => void;
}

const CreateUserForm: React.FC<CreateUserFormProps> = ({ onSuccess, onCancel }) => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!username.trim() || !password.trim() || !email.trim() || !confirmPassword.trim()) {
            setError('Tous les champs sont requis.');
            return;
        }

        if (password !== confirmPassword) {
            setError('Les mots de passe ne correspondent pas.');
            return;
        }
        
        if (!/\S+@\S+\.\S+/.test(email)) {
            setError('Veuillez entrer une adresse e-mail valide.');
            return;
        }

        setIsSubmitting(true);
        try {
            await createUser(username, password, email);
            onSuccess();
        } catch (err) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError('Une erreur est survenue.');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="max-w-lg mx-auto bg-white p-8 rounded-lg shadow">
            <h2 className="text-2xl font-bold text-slate-800 mb-6">Créer un nouvel utilisateur</h2>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label htmlFor="new-username" className="block text-sm font-medium text-gray-700">Nom d'utilisateur</label>
                    <input
                        type="text"
                        id="new-username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="new-email" className="block text-sm font-medium text-gray-700">Adresse e-mail</label>
                    <input
                        type="email"
                        id="new-email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="new-password" className="block text-sm font-medium text-gray-700">Mot de passe</label>
                    <input
                        type="password"
                        id="new-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        required
                    />
                </div>
                 <div>
                    <label htmlFor="confirm-password" className="block text-sm font-medium text-gray-700">Confirmer le mot de passe</label>
                    <input
                        type="password"
                        id="confirm-password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        required
                    />
                </div>
                
                {error && <p className="text-sm text-red-500">{error}</p>}

                <div className="flex justify-end gap-4">
                    <button type="button" onClick={onCancel} className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none">
                        Annuler
                    </button>
                    <button type="submit" disabled={isSubmitting} className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none disabled:bg-indigo-300">
                        {isSubmitting ? 'Création...' : 'Créer l\'utilisateur'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateUserForm;