
import React from 'react';

interface DashboardCardProps {
  title: string;
  value: string | number;
  unit: string;
  icon: React.ReactNode;
  colorClass: string;
}

const DashboardCard: React.FC<DashboardCardProps> = ({ title, value, unit, icon, colorClass }) => {
  return (
    <div className="bg-slate-800 p-6 rounded-2xl shadow-lg flex items-center space-x-6 transition-transform transform hover:scale-105 duration-300">
      <div className={`p-4 rounded-full ${colorClass}`}>
        {icon}
      </div>
      <div>
        <p className="text-slate-400 text-sm font-medium uppercase tracking-wider">{title}</p>
        <p className="text-3xl font-bold text-white">
          {value} <span className="text-xl font-light text-slate-300">{unit}</span>
        </p>
      </div>
    </div>
  );
};

export default DashboardCard;
