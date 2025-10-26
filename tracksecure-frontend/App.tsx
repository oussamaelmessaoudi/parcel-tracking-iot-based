import React, { useState } from 'react';
import { useAuth } from './context/AuthContext';
import Login from './components/Login';
import TrackingDashboard from './components/tracking/TrackingDashboard';
import AdminDashboard from './components/admin/AdminDashboard';
import Header from './components/Header';
import LandingPage from './components/LandingPage';

const App: React.FC = () => {
  const { user } = useAuth();
  const [showLoginPage, setShowLoginPage] = useState(false);

  const handleNavigateToLogin = () => {
    setShowLoginPage(true);
  };

  if (!user) {
    if (showLoginPage) {
      return <Login onBackToLanding={() => setShowLoginPage(false)} />;
    }
    return <LandingPage onNavigateToLogin={handleNavigateToLogin} />;
  }

  return (
    <div className="min-h-screen bg-slate-50 font-sans">
      <Header />
      <main className="container mx-auto p-4 md:p-6 lg:p-8">
        {user.role === 'administrateur' ? <AdminDashboard /> : <TrackingDashboard />}
      </main>
    </div>
  );
};

export default App;
