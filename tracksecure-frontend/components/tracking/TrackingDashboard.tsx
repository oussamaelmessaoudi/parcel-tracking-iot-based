import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { fetchTrackingData, getPackages, getPackagesForUser } from '../../services/trackingService';
import { TrackingData, PackageData } from '../../types';
import DashboardCard from '../DashboardCard';
import MapCard from '../MapCard';
import { ThermometerIcon, DropletIcon, MapPinIcon, RefreshIcon, PackageIcon, ExclamationTriangleIcon } from '../Icons';

interface TrackingDashboardProps {
  selectedPackageIdFromAdmin?: string;
  onBackToAdmin?: () => void;
}

const TrackingDashboard: React.FC<TrackingDashboardProps> = ({ selectedPackageIdFromAdmin, onBackToAdmin }) => {
  const { user } = useAuth();
  const [packages, setPackages] = useState<PackageData[]>([]);
  const [selectedPackageId, setSelectedPackageId] = useState<string | null>(null);
  const [trackingData, setTrackingData] = useState<TrackingData | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      const fetcher = onBackToAdmin ? getPackages : () => getPackagesForUser(user.username);
      fetcher()
        .then(pkgs => {
          setPackages(pkgs);
          if (selectedPackageIdFromAdmin) {
            setSelectedPackageId(selectedPackageIdFromAdmin);
          } else if (pkgs.length > 0 && !selectedPackageId) {
            setSelectedPackageId(pkgs[0].id);
          }
        });
    }
  }, [user, onBackToAdmin, selectedPackageIdFromAdmin]);


  const loadTrackingData = async () => {
    if (!selectedPackageId) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await fetchTrackingData(selectedPackageId);
      setTrackingData(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Une erreur inconnue est survenue.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (selectedPackageId) {
      loadTrackingData();
      const interval = setInterval(loadTrackingData, 10000); // Refresh every 10 seconds
      return () => clearInterval(interval);
    } else {
        setTrackingData(null);
    }
  }, [selectedPackageId]);

  const handleRefresh = () => {
    if (selectedPackageId) {
      loadTrackingData();
    }
  };
  
  return (
    <div>
      <div className="flex items-start mb-6">
        {onBackToAdmin && (
            <button onClick={onBackToAdmin} className="mr-2 mt-1 flex-shrink-0 p-2 rounded-full hover:bg-gray-200 transition-colors" title="Retour à la liste">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
            </button>
        )}
        <h1 className="text-3xl font-bold text-slate-800">Tableau de bord de suivi</h1>
      </div>

      <div className="mb-6">
        <label htmlFor="package-select" className="block text-sm font-medium text-gray-700 mb-2">Sélectionnez un colis :</label>
        <select
          id="package-select"
          value={selectedPackageId || ''}
          onChange={(e) => setSelectedPackageId(e.target.value)}
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm rounded-md"
          disabled={packages.length === 0}
        >
          {packages.length > 0 ? (
            packages.map(p => <option key={p.id} value={p.id}>{p.id}</option>)
          ) : (
            <option>Aucun colis trouvé</option>
          )}
        </select>
      </div>

      {isLoading && !trackingData && <div className="text-center text-gray-600 py-4">Chargement des données...</div>}
      
      {error && (
          <div className="bg-red-50 border-l-4 border-red-400 p-4 mb-6 rounded-r-lg" role="alert">
              <div className="flex">
                  <div className="py-1">
                      <ExclamationTriangleIcon className="h-6 w-6 text-red-400 mr-4" />
                  </div>
                  <div>
                      <p className="font-bold text-red-800">Erreur de connexion</p>
                      <p className="text-sm text-red-700">Les données n'ont pas pu être reçues.</p>
                  </div>
              </div>
          </div>
      )}


      {trackingData ? (
        <>
            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4 mb-4">
                <h2 className="text-xl font-semibold text-slate-700 text-center sm:text-left">Données en temps réel pour <span className="text-emerald-600">{trackingData.packageId}</span></h2>
                <button onClick={handleRefresh} disabled={isLoading} className="flex items-center justify-center w-full sm:w-auto px-3 py-1.5 border border-transparent text-xs font-medium rounded-md shadow-sm text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:bg-emerald-300">
                    <RefreshIcon className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                    Rafraîchir
                </button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <DashboardCard
                    icon={<ThermometerIcon className="h-8 w-8 text-red-500" />}
                    title="Température"
                    value={`${trackingData.temperature.toFixed(1)}°C`}
                    footerText={`Dernière mise à jour : ${new Date(trackingData.timestamp).toLocaleTimeString()}`}
                />
                <DashboardCard
                    icon={<DropletIcon className="h-8 w-8 text-blue-500" />}
                    title="Humidité"
                    value={`${trackingData.humidity.toFixed(1)}%`}
                    footerText="Niveau optimal"
                />
                <DashboardCard
                    icon={<MapPinIcon className="h-8 w-8 text-green-500" />}
                    title="Localisation"
                    value={`${trackingData.coordinates.lat.toFixed(4)}, ${trackingData.coordinates.lon.toFixed(4)}`}
                    footerText="Coordonnées GPS"
                />
            </div>
            <div className="mt-6">
                <MapCard coordinates={trackingData.coordinates} />
            </div>
        </>
      ) : !isLoading && !error && (
        <div className="text-center py-10 px-6 bg-white rounded-lg shadow">
            <PackageIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">Aucun colis sélectionné</h3>
            <p className="mt-1 text-sm text-gray-500">Veuillez sélectionner un colis pour afficher ses données de suivi.</p>
        </div>
      )}
    </div>
  );
};

export default TrackingDashboard;
