import React, { useState } from 'react';
import { EnvelopeIcon, PhoneIcon, BuildingOfficeIcon } from './Icons';

interface ContactPageProps {
    onBackToLanding: () => void;
}

const ContactPage: React.FC<ContactPageProps> = ({ onBackToLanding }) => {
    const [formData, setFormData] = useState({ name: '', email: '', subject: '', message: '' });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitStatus, setSubmitStatus] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsSubmitting(true);
        setSubmitStatus(null);
        console.log('Form Data Submitted:', formData);

        // Simulate API call
        setTimeout(() => {
            setIsSubmitting(false);
            setSubmitStatus({ type: 'success', message: 'Votre message a bien été envoyé ! Nous vous répondrons bientôt.' });
            setFormData({ name: '', email: '', subject: '', message: '' });
        }, 1500);
    };

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8" style={{ background: 'radial-gradient(circle at top left, #f0fdfa, white 60%)' }}>
             <button onClick={onBackToLanding} className="absolute top-4 left-4 text-gray-600 hover:text-emerald-500 transition-colors z-10">
                &larr; Retour à l'accueil
            </button>
            <div className="relative max-w-5xl mx-auto">
                <div className="text-center">
                    <h2 className="text-3xl font-extrabold tracking-tight text-gray-900 sm:text-4xl">Contactez-nous</h2>
                    <p className="mt-4 text-lg leading-6 text-gray-500">
                        Une question ou une remarque ? Remplissez simplement le formulaire pour nous contacter.
                    </p>
                </div>

                <div className="mt-12 bg-white shadow-xl rounded-2xl">
                    <div className="grid grid-cols-1 lg:grid-cols-2">
                        {/* Contact Information */}
                        <div className="relative px-6 py-10 sm:px-10 lg:p-12 bg-emerald-500 rounded-l-2xl text-white flex flex-col justify-center">
                            <h3 className="text-2xl font-semibold">Nos coordonnées</h3>
                            <p className="mt-2 text-emerald-100">
                                Nous sommes là pour vous aider et répondre à toutes vos questions.
                            </p>
                            <div className="mt-8 space-y-6">
                                <div className="flex items-start">
                                    <div className="flex-shrink-0">
                                        <BuildingOfficeIcon className="h-6 w-6 text-emerald-200" />
                                    </div>
                                    <div className="ml-4">
                                        <p className="font-medium">Adresse</p>
                                        <p className="text-emerald-100">123 Rue Fictive, 75001 Paris, France</p>
                                    </div>
                                </div>
                                <div className="flex items-start">
                                    <div className="flex-shrink-0">
                                        <PhoneIcon className="h-6 w-6 text-emerald-200" />
                                    </div>
                                    <div className="ml-4">
                                        <p className="font-medium">Téléphone</p>
                                        <p className="text-emerald-100">+33 1 23 45 67 89</p>
                                    </div>
                                </div>
                                <div className="flex items-start">
                                    <div className="flex-shrink-0">
                                        <EnvelopeIcon className="h-6 w-6 text-emerald-200" />
                                    </div>
                                    <div className="ml-4">
                                        <p className="font-medium">Email</p>
                                        <p className="text-emerald-100">contact@tracksecure.com</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Contact Form */}
                        <div className="px-6 py-10 sm:px-10 lg:p-12">
                            <h3 className="text-2xl font-semibold text-gray-900">Envoyer un message</h3>
                            <form onSubmit={handleSubmit} className="mt-8 grid grid-cols-1 gap-y-6">
                                <div>
                                    <label htmlFor="name" className="sr-only">Nom complet</label>
                                    <input type="text" name="name" id="name" required value={formData.name} onChange={handleChange} className="block w-full shadow-sm py-3 px-4 placeholder-gray-500 focus:ring-emerald-500 focus:border-emerald-500 border-gray-300 rounded-md" placeholder="Nom complet" />
                                </div>
                                <div>
                                    <label htmlFor="email" className="sr-only">Email</label>
                                    <input id="email" name="email" type="email" required value={formData.email} onChange={handleChange} autoComplete="email" className="block w-full shadow-sm py-3 px-4 placeholder-gray-500 focus:ring-emerald-500 focus:border-emerald-500 border-gray-300 rounded-md" placeholder="Adresse e-mail" />
                                </div>
                                <div>
                                    <label htmlFor="subject" className="sr-only">Sujet</label>
                                    <input type="text" name="subject" id="subject" required value={formData.subject} onChange={handleChange} className="block w-full shadow-sm py-3 px-4 placeholder-gray-500 focus:ring-emerald-500 focus:border-emerald-500 border-gray-300 rounded-md" placeholder="Sujet" />
                                </div>
                                <div>
                                    <label htmlFor="message" className="sr-only">Message</label>
                                    <textarea id="message" name="message" rows={4} required value={formData.message} onChange={handleChange} className="block w-full shadow-sm py-3 px-4 placeholder-gray-500 focus:ring-emerald-500 focus:border-emerald-500 border-gray-300 rounded-md" placeholder="Votre message"></textarea>
                                </div>
                                 {submitStatus && (
                                    <div className={`p-3 rounded-md text-sm ${submitStatus.type === 'success' ? 'bg-green-50 text-green-800' : 'bg-red-50 text-red-800'}`}>
                                        {submitStatus.message}
                                    </div>
                                )}
                                <div>
                                    <button type="submit" disabled={isSubmitting} className="w-full inline-flex items-center justify-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:bg-emerald-300">
                                        {isSubmitting ? 'Envoi en cours...' : 'Envoyer le message'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ContactPage;