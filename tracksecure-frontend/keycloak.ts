import Keycloak from 'keycloak-js';

// Configuration de Keycloak basée sur votre docker-compose
// Assurez-vous d'avoir créé un client 'tracksecure-frontend' dans votre realm 'parcel-tracking-auth'
// avec 'Access Type: public' et 'Valid Redirect URIs: http://localhost:3000/*'
const keycloakConfig = {
  url: 'http://localhost:8080',
  realm: 'parcel-tracking-auth',
  clientId: 'tracksecure-frontend', // Nom du client à créer dans Keycloak
};

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;