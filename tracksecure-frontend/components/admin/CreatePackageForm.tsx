import React, { useState } from 'react';
import { createPackage } from '../../services/trackingService';
import { User } from '../../types';

interface CreatePackageFormProps {
    users: User[];
    onSuccess: () => void;
    onCancel: () => void;
}

const CreatePackageForm: React.FC<CreatePackageFormProps> = ({ users, onSuccess, onCancel }) => {
    const [packageId, setPackageId] = useState('');
    const [selectedUsername, setSelectedUsername] = useState<string>(users.find(u => u.role === 'utilisateur')?.username || '');
    const [departureLocation, setDepartureLocation] = useState('');
    const [pickupLocation, setPickupLocation] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    
    const userOptions = users.filter(u => u.role === 'utilisateur');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!packageId.trim() || !selectedUsername || !departureLocation.trim() || !pickupLocation.trim()) {
            setError('Tous les champs sont requis.');
            return;
        }

        setIsSubmitting(true);
        try {
            await createPackage(packageId, selectedUsername, departureLocation, pickupLocation);
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
            <h2 className="text-2xl font-bold text-slate-800 mb-6">Assigner un nouveau colis</h2>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label htmlFor="package-id" className="block text-sm font-medium text-gray-700">Numéro de colis</label>
                    <input
                        type="text"
                        id="package-id"
                        value={packageId}
                        onChange={(e) => setPackageId(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
                        placeholder="ex: PKG-XYZ789"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="departure-location" className="block text-sm font-medium text-gray-700">Lieu de départ</label>
                    <input
                        type="text"
                        id="departure-location"
                        value={departureLocation}
                        onChange={(e) => setDepartureLocation(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
                        placeholder="ex: Entrepôt A, Paris"
                        required
                    />
                </div>
                 <div>
                    <label htmlFor="pickup-location" className="block text-sm font-medium text-gray-700">Lieu de prise en charge</label>
                    <input
                        type="text"
                        id="pickup-location"
                        value={pickupLocation}
                        onChange={(e) => setPickupLocation(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
                        placeholder="ex: Client X, Lyon"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="user-select" className="block text-sm font-medium text-gray-700">Assigner à l'utilisateur</label>
                    <select
                        id="user-select"
                        value={selectedUsername}
                        onChange={(e) => setSelectedUsername(e.target.value)}
                        className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm rounded-md"
                        required
                    >
                        <option value="" disabled>Sélectionnez un utilisateur</option>
                        {userOptions.map(user => (
                            <option key={user.username} value={user.username}>{user.username}</option>
                        ))}
                    </select>
                     {userOptions.length === 0 && <p className="mt-2 text-sm text-gray-500">Aucun utilisateur disponible. Veuillez d'abord créer un utilisateur.</p>}
                </div>

                {error && <p className="text-sm text-red-500">{error}</p>}

                <div className="flex justify-end gap-4">
                    <button type="button" onClick={onCancel} className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none">
                        Annuler
                    </button>
                    <button type="submit" disabled={isSubmitting || userOptions.length === 0} className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none disabled:bg-emerald-300">
                        {isSubmitting ? 'Assignation...' : 'Assigner le colis'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreatePackageForm;