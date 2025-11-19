import React, { ReactNode } from 'react';

interface DashboardCardProps {
  icon: ReactNode;
  title: string;
  value: string;
  footerText: string;
}

const DashboardCard: React.FC<DashboardCardProps> = ({ icon, title, value, footerText }) => {
  return (
    <div className="bg-white rounded-xl shadow-lg p-6 flex flex-col justify-between hover:shadow-xl transition-shadow duration-300">
      <div className="flex items-start justify-between">
        <div className="space-y-1">
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <p className="text-3xl font-bold text-gray-800">{value}</p>
        </div>
        <div className="bg-gray-100 rounded-full p-3">
            {icon}
        </div>
      </div>
      <div className="mt-4">
        <p className="text-xs text-gray-400">{footerText}</p>
      </div>
    </div>
  );
};

export default DashboardCard;
