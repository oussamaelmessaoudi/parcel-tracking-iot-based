
import React, { useState, useEffect, useCallback } from 'react';
import { TrackingData } from './types';
import { fetchTrackingData } from './services/trackingService';
import DashboardCard from './components/DashboardCard';
import MapCard from './components/MapCard';
import { ThermometerIcon, DropletIcon, RefreshIcon } from './components/Icons';

const App: React.FC = () => {
  const [data, setData] = useState<TrackingData | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const loadData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const trackingData = await fetchTrackingData();
      setData(trackingData);
    } catch (err) {
      setError("Impossible de charger les données de suivi. Veuillez réessayer.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const SkeletonCard: React.FC = () => (
    <div className="bg-slate-800 p-6 rounded-2xl shadow-lg animate-pulse flex items-center space-x-6">
      <div className="p-4 rounded-full bg-slate-700 h-16 w-16"></div>
      <div>
        <div className="h-4 bg-slate-700 rounded w-24 mb-2"></div>
        <div className="h-8 bg-slate-700 rounded w-32"></div>
      </div>
    </div>
  );
  
  const SkeletonMapCard: React.FC = () => (
    <div className="bg-slate-800 p-6 rounded-2xl shadow-lg animate-pulse md:col-span-2">
        <div className="h-6 bg-slate-700 rounded w-48 mb-4"></div>
        <div className="aspect-video bg-slate-700 rounded-lg mb-4"></div>
        <div className="grid grid-cols-2 gap-4">
            <div className="h-10 bg-slate-700 rounded"></div>
            <div className="h-10 bg-slate-700 rounded"></div>
        </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 font-sans p-4 sm:p-6 lg:p-8">
      <div className="max-w-7xl mx-auto">
        <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8">
            <div>
                <h1 className="text-3xl font-bold text-white">Dashboard de Suivi de Colis</h1>
                <p className="text-slate-400 mt-1">
                    {data ? `Données pour le colis #${data.packageId}` : 'En attente de données...'}
                </p>
            </div>
          <button
            onClick={loadData}
            disabled={isLoading}
            className="mt-4 sm:mt-0 flex items-center px-4 py-2 bg-cyan-600 rounded-lg shadow-md hover:bg-cyan-500 transition-colors duration-300 disabled:bg-slate-600 disabled:cursor-not-allowed"
          >
            <RefreshIcon className={`h-5 w-5 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
            {isLoading ? 'Chargement...' : 'Actualiser'}
          </button>
        </header>

        {error && (
          <div className="bg-red-900/50 border border-red-700 text-red-200 px-4 py-3 rounded-lg text-center">
            <p>{error}</p>
          </div>
        )}

        <main className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {isLoading ? (
            <>
              <SkeletonCard />
              <SkeletonCard />
              <SkeletonMapCard />
            </>
          ) : data ? (
            <>
              <DashboardCard
                title="Température"
                value={data.temperature}
                unit="°C"
                icon={<ThermometerIcon className="h-8 w-8 text-white" />}
                colorClass="bg-amber-500"
              />
              <DashboardCard
                title="Humidité"
                value={data.humidity}
                unit="%"
                icon={<DropletIcon className="h-8 w-8 text-white" />}
                colorClass="bg-sky-500"
              />
              <MapCard coordinates={data.coordinates} />
            </>
          ) : null}
        </main>
        
        <footer className="text-center mt-12 text-slate-500 text-sm">
            <p>Dernière mise à jour : {data ? new Date(data.lastUpdated).toLocaleString('fr-FR') : 'N/A'}</p>
        </footer>
      </div>
    </div>
  );
};

export default App;
