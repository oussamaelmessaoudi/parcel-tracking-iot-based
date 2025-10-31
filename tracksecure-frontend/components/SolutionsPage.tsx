import React from 'react';
import { MapPinIcon, ThermometerIcon, ExclamationTriangleIcon, PackageIcon } from './Icons';

interface SolutionsPageProps {
    onBackToLanding: () => void;
}

const FeatureCard: React.FC<{ icon: React.ReactNode; title: string; description: string }> = ({ icon, title, description }) => (
    <div className="bg-white p-6 rounded-2xl shadow-lg hover:shadow-xl transition-shadow duration-300 flex flex-col items-center text-center">
        <div className="flex items-center justify-center h-16 w-16 rounded-full bg-emerald-100 mb-4">
            {icon}
        </div>
        <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
        <p className="text-gray-600 text-sm leading-relaxed">{description}</p>
    </div>
);

const SolutionsPage: React.FC<SolutionsPageProps> = ({ onBackToLanding }) => {
    const features = [
        {
            icon: <MapPinIcon className="h-8 w-8 text-emerald-600" />,
            title: "Géolocalisation en Temps Réel",
            description: "Suivez la position exacte de vos colis à tout moment, n'importe où, avec une précision GPS de pointe."
        },
        {
            icon: <ThermometerIcon className="h-8 w-8 text-red-500" />,
            title: "Surveillance Environnementale",
            description: "Contrôlez la température et l'humidité pour garantir que vos marchandises sensibles sont transportées dans des conditions optimales."
        },
        {
            icon: <ExclamationTriangleIcon className="h-8 w-8 text-yellow-500" />,
            title: "Alertes de Sécurité",
            description: "Recevez des notifications instantanées en cas de chocs, d'ouvertures non autorisées ou d'écarts de température."
        },
        {
            icon: <PackageIcon className="h-8 w-8 text-blue-500" />,
            title: "Tableau de Bord Intuitif",
            description: "Visualisez toutes les données pertinentes sur une interface claire et facile à utiliser, accessible depuis n'importe quel appareil."
        },
        {
            icon: <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V7a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>,
            title: "Rapports Détaillés",
            description: "Générez des rapports complets sur l'historique des trajets, les conditions environnementales et les événements pour une traçabilité complète."
        }
    ];

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8" style={{ background: 'radial-gradient(circle at top left, #f0fdfa, white 60%)' }}>
            <button onClick={onBackToLanding} className="absolute top-4 left-4 text-gray-600 hover:text-emerald-500 transition-colors z-10">
                &larr; Retour à l'accueil
            </button>
            <div className="relative max-w-5xl mx-auto">
                <div className="text-center">
                    <h2 className="text-3xl font-extrabold tracking-tight text-gray-900 sm:text-4xl">Nos Solutions pour un Suivi Sécurisé</h2>
                    <p className="mt-4 text-lg leading-6 text-gray-500">
                        Découvrez comment TrackSecure protège et surveille vos actifs les plus précieux.
                    </p>
                </div>

                <div className="mt-16 grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
                    {features.map((feature, index) => (
                        <FeatureCard key={index} icon={feature.icon} title={feature.title} description={feature.description} />
                    ))}
                </div>
            </div>
        </div>
    );
};

export default SolutionsPage;