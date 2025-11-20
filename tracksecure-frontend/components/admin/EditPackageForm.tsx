import React, { useState, useEffect } from 'react';
import { updatePackage } from '../../services/trackingService';
import { User, PackageData } from '../../types';

interface EditPackageFormProps {
    pkg: PackageData;
    users: User[];
    onSuccess: () => void;
    onCancel: () => void;
}

const EditPackageForm: React.FC<EditPackageFormProps> = ({ pkg, users, onSuccess, onCancel }) => {
    const [selectedUsername, setSelectedUsername] = useState('');
    const [departureLocation, setDepartureLocation] = useState('');
    const [pickupLocation, setPickupLocation] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (pkg) {
            setSelectedUsername(pkg.username);
            setDepartureLocation(pkg.departureLocation);
            setPickupLocation(pkg.pickupLocation);
        }
    }, [pkg]);

    const userOptions = users.filter(u => u.role === 'utilisateur');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!selectedUsername || !departureLocation.trim() || !pickupLocation.trim()) {
            setError('Tous les champs sont requis.');
            return;
        }

        setIsSubmitting(true);
        try {
            await updatePackage(pkg.id, { username: selectedUsername, departureLocation, pickupLocation });
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
            <h2 className="text-2xl font-bold text-slate-800 mb-6">Modifier le colis <span className="text-emerald-600 font-mono">{pkg.id}</span></h2>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label htmlFor="package-id" className="block text-sm font-medium text-gray-700">Numéro de colis</label>
                    <input
                        type="text"
                        id="package-id"
                        value={pkg.id}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-gray-100 sm:text-sm"
                        disabled
                    />
                </div>
                <div>
                    <label htmlFor="departure-location-edit" className="block text-sm font-medium text-gray-700">Lieu de départ</label>
                    <input
                        type="text"
                        id="departure-location-edit"
                        value={departureLocation}
                        onChange={(e) => setDepartureLocation(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
                        required
                    />
                </div>
                 <div>
                    <label htmlFor="pickup-location-edit" className="block text-sm font-medium text-gray-700">Lieu de prise en charge</label>
                    <input
                        type="text"
                        id="pickup-location-edit"
                        value={pickupLocation}
                        onChange={(e) => setPickupLocation(e.target.value)}
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="user-select-edit" className="block text-sm font-medium text-gray-700">Assigner à l'utilisateur</label>
                    <select
                        id="user-select-edit"
                        value={selectedUsername}
                        onChange={(e) => setSelectedUsername(e.target.value)}
                        className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm rounded-md"
                        required
                    >
                        {userOptions.map(user => (
                            <option key={user.username} value={user.username}>{user.username}</option>
                        ))}
                    </select>
                </div>

                {error && <p className="text-sm text-red-500">{error}</p>}

                <div className="flex justify-end gap-4">
                    <button type="button" onClick={onCancel} className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none">
                        Annuler
                    </button>
                    <button type="submit" disabled={isSubmitting} className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none disabled:bg-emerald-300">
                        {isSubmitting ? 'Mise à jour...' : 'Mettre à jour le colis'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditPackageForm;
