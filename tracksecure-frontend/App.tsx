import React, { useState } from 'react';
import { useAuth } from './context/AuthContext';
import Login from './components/Login';
import TrackingDashboard from './components/tracking/TrackingDashboard';
import AdminDashboard from './components/admin/AdminDashboard';
import Header from './components/Header';
import LandingPage from './components/LandingPage';
import ContactPage from './components/ContactPage';
import SolutionsPage from './components/SolutionsPage';

type UnauthenticatedPage = 'landing' | 'login' | 'contact' | 'solutions';

const App: React.FC = () => {
  const { user } = useAuth();
  const [currentPage, setCurrentPage] = useState<UnauthenticatedPage>('landing');
  const [trackedPackageIdByAdmin, setTrackedPackageIdByAdmin] = useState<string | null>(null);


  const handleTrackPackage = (packageId: string) => {
    setTrackedPackageIdByAdmin(packageId);
  };

  const handleBackToAdminDashboard = () => {
    setTrackedPackageIdByAdmin(null);
  };

  if (!user) {
    switch (currentPage) {
        case 'login':
            return <Login onBackToLanding={() => setCurrentPage('landing')} />;
        case 'contact':
            return <ContactPage onBackToLanding={() => setCurrentPage('landing')} />;
        case 'solutions':
            return <SolutionsPage onBackToLanding={() => setCurrentPage('landing')} />;
        case 'landing':
        default:
            return (
                <LandingPage 
                    onNavigateToLogin={() => setCurrentPage('login')}
                    onNavigateToContact={() => setCurrentPage('contact')}
                    onNavigateToSolutions={() => setCurrentPage('solutions')}
                />
            );
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 font-sans">
      <Header />
      <main className="container mx-auto p-4 md:p-6 lg:p-8">
        {user.role === 'administrateur' ? (
            trackedPackageIdByAdmin ? (
                <TrackingDashboard 
                    selectedPackageIdFromAdmin={trackedPackageIdByAdmin} 
                    onBackToAdmin={handleBackToAdminDashboard} 
                />
            ) : (
                <AdminDashboard onTrackPackage={handleTrackPackage} />
            )
        ) : (
            <TrackingDashboard />
        )}
      </main>
    </div>
  );
};

export default App;
