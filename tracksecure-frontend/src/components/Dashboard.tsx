import React from 'react';
import { 
  Box, 
  Container, 
  Grid, 
  Paper, 
  Typography, 
  Card, 
  CardContent,
  CircularProgress,
  Alert,
  Button
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { SensorService, SensorPayload } from '../services/api';

// Styled components
const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: '1rem',
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  backgroundColor: '#f5f5f5'
}));

const DataCard = styled(Card)(({ theme }) => ({
  height: '100%',
  backgroundColor: '#ffffff',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  transition: 'transform 0.3s ease-in-out',
  '&:hover': {
    transform: 'translateY(-5px)',
    boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
  }
}));

const Dashboard: React.FC = () => {
  const [sensorData, setSensorData] = React.useState<SensorPayload | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<string>('');
  const [lastUpdated, setLastUpdated] = React.useState<string>('');

  const fetchData = async () => {
    try {
      setLoading(true);
      const data = await SensorService.getLatestData();
      setSensorData(data);
      setLastUpdated(new Date().toLocaleTimeString());
      setError('');
    } catch (err) {
      setError('Impossible de récupérer les données du capteur');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 5000);
    return () => clearInterval(interval);
  }, []);

  const renderDataCard = (title: string, value: string | number | undefined, unit: string = '') => (
    <Grid item xs={12} sm={6} md={3}>
      <DataCard>
        <CardContent>
          <Typography color="textSecondary" gutterBottom>
            {title}
          </Typography>
          <Typography variant="h4" component="div">
            {value !== undefined ? `${value}${unit}` : 'N/A'}
          </Typography>
        </CardContent>
      </DataCard>
    </Grid>
  );

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Tableau de Bord IoT
        </Typography>
        <Typography color="textSecondary">
          Surveillance en temps réel des colis
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <StyledPaper>
        <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">
            Données des Capteurs
            {lastUpdated && ` (Dernière mise à jour: ${lastUpdated})`}
          </Typography>
          <Button 
            variant="contained" 
            onClick={fetchData} 
            disabled={loading}
          >
            Actualiser
          </Button>
        </Box>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
            <CircularProgress />
          </Box>
        ) : (
          <Grid container spacing={3}>
            {renderDataCard('ID Appareil', sensorData?.deviceId)}
            {renderDataCard('Température', sensorData?.temperature, '°C')}
            {renderDataCard('Humidité', sensorData?.humidity, '%')}
            {renderDataCard('État', sensorData ? 'En ligne' : 'Hors ligne')}
            <Grid item xs={12}>
              <DataCard>
                <CardContent>
                  <Typography color="textSecondary" gutterBottom>
                    Localisation
                  </Typography>
                  <Typography variant="body1">
                    {sensorData?.location ? (
                      `Latitude: ${sensorData.location.latitude}, Longitude: ${sensorData.location.longitude}`
                    ) : (
                      'Localisation non disponible'
                    )}
                  </Typography>
                </CardContent>
              </DataCard>
            </Grid>
          </Grid>
        )}
      </StyledPaper>
    </Container>
  );
};

export default Dashboard;