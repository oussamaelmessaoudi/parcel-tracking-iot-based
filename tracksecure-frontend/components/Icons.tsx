import React from 'react';

export const LogoIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} viewBox="0 0 24 24" fill="currentColor">
        <path d="M21.43,8.34,13.2,4.21a2,2,0,0,0-2.37,0L2.57,8.34a2,2,0,0,0-1,1.75v5.82a2,2,0,0,0,1,1.75l8.23,4.13a2,2,0,0,0,2.37,0l8.23-4.13a2,2,0,0,0,1-1.75V10.09A2,2,0,0,0,21.43,8.34ZM12,6.1,18.12,9.18,14.4,11.1,8.31,8ZM4.14,9.91l6.73,3.38v6.7L4.14,16.61ZM13.13,19.95V13.28l6.73-3.37v6.7Z"/>
    </svg>
);

export const TrackingIllustration: React.FC<{ className?: string }> = ({ className }) => (
    <svg className={className} viewBox="0 0 200 150" xmlns="http://www.w3.org/2000/svg">
        <defs>
            <linearGradient id="truckGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style={{stopColor: '#4ade80', stopOpacity: 1}} />
                <stop offset="100%" style={{stopColor: '#2dd4bf', stopOpacity: 1}} />
            </linearGradient>
            <style>
                {`.road-line { animation: dash 2s linear infinite; } @keyframes dash { to { stroke-dashoffset: -40; } }`}
            </style>
        </defs>
        
        <g transform="translate(10, 10)">
            {/* World Map Background */}
            <path d="M10,50 Q40,20 70,50 T130,50 Q160,20 190,50" fill="none" stroke="#e5e7eb" strokeWidth="2" strokeLinecap="round"/>
            <path d="M20,70 Q50,90 80,70 T140,70 Q170,90 200,70" fill="none" stroke="#e5e7eb" strokeWidth="2" strokeLinecap="round"/>

            {/* Road */}
            <path d="M0 130 H 200" stroke="#d1d5db" strokeWidth="2" />
            <path d="M0 130 H 200" stroke="white" strokeWidth="3" strokeDasharray="15 15" className="road-line" />

            {/* Truck */}
            <g transform="translate(50, 70)">
                <rect x="20" y="10" width="60" height="40" rx="5" fill="url(#truckGrad)" />
                <path d="M 25 10 H 5 V 35 Q 5 50 20 50 H 25 Z" fill="#60a5fa" />
                <rect x="8" y="15" width="12" height="15" rx="2" fill="#e0f2fe" />
                <circle cx="25" cy="55" r="8" fill="#4b5563" />
                <circle cx="70" cy="55" r="8" fill="#4b5563" />
                <circle cx="25" cy="55" r="3" fill="#d1d5db" />
                <circle cx="70" cy="55" r="3" fill="#d1d5db" />
            </g>
            
            {/* Location Pin */}
            <g transform="translate(150, 20)">
                <path d="M 15 0 C 6.71 0 0 6.71 0 15 C 0 26.25 15 40 15 40 S 30 26.25 30 15 C 30 6.71 23.29 0 15 0 Z" fill="#f87171"/>
                <circle cx="15" cy="15" r="6" fill="white" />
            </g>

             {/* Speed lines */}
            <path d="M 20 90 L 40 90" stroke="#9ca3af" strokeWidth="2" strokeLinecap="round" />
            <path d="M 15 105 L 35 105" stroke="#9ca3af" strokeWidth="2" strokeLinecap="round" />
        </g>
    </svg>
);


export const ThermometerIcon: React.FC<{ className?: string }> = ({ className }) => (
  <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M13 16V6a1 1 0 00-1-1H9a1 1 0 00-1 1v10a4 4 0 108 0v-2a2 2 0 00-2-2h-3z" />
    <path strokeLinecap="round" strokeLinejoin="round" d="M9 16a4 4 0 014 4m0 0a4 4 0 014-4m-4 4v-4m0 4h.01" />
  </svg>
);

export const DropletIcon: React.FC<{ className?: string }> = ({ className }) => (
  <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M7 16a7.94 7.94 0 01-1.5-1.5A8 8 0 1117 11a8 8 0 01-5 7.586V19a1 1 0 01-1 1h-2a1 1 0 01-1-1v-2.414A7.95 7.95 0 017 16z" />
  </svg>
);

export const MapPinIcon: React.FC<{ className?: string }> = ({ className }) => (
  <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
    <path strokeLinecap="round" strokeLinejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
  </svg>
);

export const RefreshIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M4 4v5h5M20 20v-5h-5M4 4l1.5 1.5A9 9 0 0120.5 15M20 20l-1.5-1.5A9 9 0 013.5 9" />
    </svg>
);

export const LogoutIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
    </svg>
);

export const UserIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
    </svg>
);

export const UserPlusIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
    </svg>
);

export const PackageIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-14L4 7m0 10l8 4m0 0l8-4m-8 4v-4m-8 4h16" />
    </svg>
);

export const ExclamationTriangleIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
    </svg>
);

export const EnvelopeIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
    </svg>
);

export const PhoneIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 6.75c0 8.284 6.716 15 15 15h2.25a2.25 2.25 0 002.25-2.25v-1.372c0-.516-.211-.99-.554-1.33l-2.162-2.162a1.5 1.5 0 00-1.06-.44H16.5a1.5 1.5 0 00-1.5 1.5v.75a.75.75 0 01-.75.75H13.5a3 3 0 01-3-3V10.5a.75.75 0 01.75-.75h.75a1.5 1.5 0 001.5-1.5V8.25a1.5 1.5 0 00-1.5-1.5H9a1.5 1.5 0 00-1.06.44L5.886 9.114A2.25 2.25 0 005.25 10.5v1.372c0 .516.211.99.554 1.33l.002.002z" />
    </svg>
);

export const BuildingOfficeIcon: React.FC<{ className?: string }> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 21h16.5M4.5 3h15M5.25 3v18m13.5-18v18M9 6.75h6.375M9 12.75h6.375m-6.375 6h6.375M9.75 6.75h4.5v10.5h-4.5V6.75z" />
    </svg>
);