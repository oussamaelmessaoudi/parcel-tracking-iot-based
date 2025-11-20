import React, { useState, useEffect } from 'react';
import CreateUserForm from './CreateUserForm';
import CreatePackageForm from './CreatePackageForm';
import EditUserForm from './EditUserForm';
import EditPackageForm from './EditPackageForm';
import { getPackages, deletePackage } from '../../services/trackingService';
import { PackageData, User } from '../../types';
import { getUsers, deleteUser } from '../../services/authService';
import { UserPlusIcon, PackageIcon, UserIcon as UsersIcon } from '../Icons';

interface AdminDashboardProps {
  onTrackPackage: (packageId: string) => void;
}

const AdminDashboard: React.FC<AdminDashboardProps> = ({ onTrackPackage }) => {
  const [activeTab, setActiveTab] = useState<'packages' | 'users'>('packages');
  const [view, setView] = useState<'list' | 'create' | 'edit'>('list');

  const [packages, setPackages] = useState<PackageData[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [editingPackage, setEditingPackage] = useState<PackageData | null>(null);

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
    setView('list'); // Go back to the list view
    setEditingUser(null);
    setEditingPackage(null);
  };

  const handleCancel = () => {
    setView('list');
    setEditingUser(null);
    setEditingPackage(null);
  };

  const handleEditUserClick = (user: User) => {
    setEditingUser(user);
    setView('edit');
  };

  const handleDeleteUser = async (username: string) => {
    if (window.confirm(`Êtes-vous sûr de vouloir supprimer l'utilisateur "${username}" ? Cette action est irréversible.`)) {
        try {
            await deleteUser(username);
            await fetchData();
        } catch (err) {
            alert(err instanceof Error ? err.message : 'Une erreur est survenue lors de la suppression.');
        }
    }
  };
  
  const handleEditPackageClick = (pkg: PackageData) => {
    setEditingPackage(pkg);
    setView('edit');
  };

  const handleDeletePackage = async (packageId: string) => {
    if (window.confirm(`Êtes-vous sûr de vouloir supprimer le colis "${packageId}" ? Cette action est irréversible.`)) {
        try {
            await deletePackage(packageId);
            await fetchData();
        } catch (err) {
            alert(err instanceof Error ? err.message : 'Une erreur est survenue lors de la suppression.');
        }
    }
  };

  const renderPackagesView = () => {
    if (view === 'create') {
        return <CreatePackageForm users={users} onSuccess={handleSuccess} onCancel={handleCancel} />;
    }
    if (view === 'edit' && editingPackage) {
        return <EditPackageForm pkg={editingPackage} users={users} onSuccess={handleSuccess} onCancel={handleCancel} />;
    }
    // List view
    return (
        <div>
            <div className="flex flex-col sm:flex-row sm:justify-end gap-4 mb-4">
                <button onClick={() => setView('create')} className="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500">
                    <PackageIcon className="h-5 w-5 mr-2" />
                    Assigner un colis
                </button>
            </div>
            
            <h2 className="text-2xl font-semibold text-slate-700 mb-4">Liste de tous les colis</h2>
            <div className="bg-white shadow overflow-hidden sm:rounded-md">
                <ul role="list" className="divide-y divide-gray-200">
                    {packages.map((pkg) => (
                        <li key={pkg.id} onClick={() => onTrackPackage(pkg.id)} className="cursor-pointer hover:bg-gray-50 transition-colors duration-200">
                           <div className="px-4 py-4 sm:px-6 flex items-center justify-between flex-wrap gap-4">
                                <div className="flex-grow">
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
                                <div className="flex items-center gap-x-4 ml-auto flex-shrink-0">
                                    <button onClick={(e) => { e.stopPropagation(); handleEditPackageClick(pkg); }} className="text-sm font-medium text-indigo-600 hover:text-indigo-900">Modifier</button>
                                    <button onClick={(e) => { e.stopPropagation(); handleDeletePackage(pkg.id); }} className="text-sm font-medium text-red-600 hover:text-red-900">Supprimer</button>
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
            {packages.length === 0 && <p className="text-center text-gray-500 mt-4">Aucun colis n'a été créé.</p>}
        </div>
    );
  };

  const renderUsersView = () => {
    if (view === 'create') {
        return <CreateUserForm onSuccess={handleSuccess} onCancel={handleCancel} />;
    }
    if (view === 'edit' && editingUser) {
        return <EditUserForm user={editingUser} onSuccess={handleSuccess} onCancel={handleCancel} />;
    }
    // List view
    return (
        <div>
            <div className="flex flex-col sm:flex-row sm:justify-end gap-4 mb-4">
                <button onClick={() => setView('create')} className="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                    <UserPlusIcon className="h-5 w-5 mr-2" />
                    Créer un utilisateur
                </button>
            </div>
            
            <h2 className="text-2xl font-semibold text-slate-700 mb-4">Liste des utilisateurs</h2>
            <div className="bg-white shadow overflow-hidden sm:rounded-md">
                <ul role="list" className="divide-y divide-gray-200">
                    {users.map((user) => (
                        <li key={user.username}>
                            <div className="px-4 py-4 sm:px-6 flex items-center justify-between gap-4 flex-wrap">
                                <div className="flex items-center min-w-0">
                                    <div className="flex-shrink-0">
                                        <div className="flex items-center justify-center h-10 w-10 rounded-full bg-gray-100">
                                            <UsersIcon className="h-6 w-6 text-gray-500" />
                                        </div>
                                    </div>
                                    <div className="ml-4 min-w-0">
                                        <p className="text-sm font-medium text-gray-900 truncate">{user.username}</p>
                                        <p className="text-sm text-gray-500 truncate">{user.email}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-x-4 ml-auto">
                                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${user.role === 'administrateur' ? 'bg-indigo-100 text-indigo-800' : 'bg-green-100 text-green-800'}`}>
                                        {user.role}
                                    </span>
                                    <button onClick={() => handleEditUserClick(user)} className="text-sm font-medium text-indigo-600 hover:text-indigo-900">Modifier</button>
                                    <button onClick={() => handleDeleteUser(user.username)} disabled={user.username === 'admin'} className="text-sm font-medium text-red-600 hover:text-red-900 disabled:text-gray-400 disabled:cursor-not-allowed">Supprimer</button>
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
            {users.length === 0 && <p className="text-center text-gray-500 mt-4">Aucun utilisateur trouvé.</p>}
        </div>
    );
  };


  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-6">Panneau d'administration</h1>
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8" aria-label="Tabs">
            <button
                onClick={() => { setActiveTab('packages'); setView('list'); }}
                className={`whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'packages' ? 'border-emerald-500 text-emerald-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
            >
                Gestion des Colis
            </button>
            <button
                onClick={() => { setActiveTab('users'); setView('list'); }}
                className={`whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm ${activeTab === 'users' ? 'border-emerald-500 text-emerald-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'}`}
            >
                Gestion des Utilisateurs
            </button>
        </nav>
      </div>

      {activeTab === 'packages' ? renderPackagesView() : renderUsersView()}
    </div>
  );
};

export default AdminDashboard;