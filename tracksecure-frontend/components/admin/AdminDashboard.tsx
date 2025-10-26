import React, { useState, useEffect } from 'react';
import CreateUserForm from './CreateUserForm';
import CreatePackageForm from './CreatePackageForm';
import { getPackages } from '../../services/trackingService';
import { PackageData, User } from '../../types';
import { getUsers } from '../../services/authService';
import { UserPlusIcon, PackageIcon } from '../Icons';

interface AdminDashboardProps {
  onTrackPackage: (packageId: string) => void;
}

const AdminDashboard: React.FC<AdminDashboardProps> = ({ onTrackPackage }) => {
  const [view, setView] = useState<'packages' | 'createUser' | 'createPackage'>('packages');
  const [packages, setPackages] = useState<PackageData[]>([]);
  const [users, setUsers] = useState<User[]>([]);

  const fetchData = async () => {
    const packagesData = await getPackages();
    const usersData = await getUsers();
    setPackages(packagesData);
    setUsers(usersData);
  };

  useEffect(() => {
    fetchData();
  }, []);
  
  const handleSuccess = () => {
    fetchData(); // Refresh data
    setView('packages'); // Go back to the list view
  };

  const renderView = () => {
    switch (view) {
      case 'createUser':
        return <CreateUserForm onSuccess={handleSuccess} onCancel={() => setView('packages')} />;
      case 'createPackage':
        return <CreatePackageForm users={users} onSuccess={handleSuccess} onCancel={() => setView('packages')} />;
      case 'packages':
      default:
        return (
          <div>
            <div className="flex flex-col sm:flex-row sm:justify-end gap-4 mb-4">
               <button onClick={() => setView('createUser')} className="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                <UserPlusIcon className="h-5 w-5 mr-2" />
                Créer un utilisateur
              </button>
              <button onClick={() => setView('createPackage')} className="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500">
                <PackageIcon className="h-5 w-5 mr-2" />
                Assigner un colis
              </button>
            </div>
            
            <h2 className="text-2xl font-semibold text-slate-700 mb-4">Liste de tous les colis</h2>
            <div className="bg-white shadow overflow-hidden sm:rounded-md">
              <ul role="list" className="divide-y divide-gray-200">
                {packages.map((pkg) => (
                  <li key={pkg.id} onClick={() => onTrackPackage(pkg.id)} className="cursor-pointer hover:bg-gray-50 transition-colors duration-200">
                    <div className="px-4 py-4 sm:px-6">
                      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
                        <p className="text-sm font-medium text-emerald-600 truncate">{pkg.id}</p>
                        <div className="sm:ml-2 flex-shrink-0 flex">
                          <p className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                            Assigné à: {pkg.username}
                          </p>
                        </div>
                      </div>
                      <div className="mt-2 text-sm text-gray-600">
                        <span>Départ: <strong>{pkg.departureLocation}</strong></span>
                        <span className="mx-2 text-gray-400">→</span>
                        <span>Prise en charge: <strong>{pkg.pickupLocation}</strong></span>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
             {packages.length === 0 && <p className="text-center text-gray-500 mt-4">Aucun colis n'a été créé.</p>}
          </div>
        );
    }
  };

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-6">Panneau d'administration</h1>
      {renderView()}
    </div>
  );
};

export default AdminDashboard;