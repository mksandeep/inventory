import React from 'react';
import { Box, Grid, Paper, Typography, Button, Stack } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';
import ShoppingCartRoundedIcon from '@mui/icons-material/ShoppingCartRounded';
import PointOfSaleRoundedIcon from '@mui/icons-material/PointOfSaleRounded';

const quickLinks = [
  {
    title: 'Dashboard',
    description: 'Get a complete snapshot of performance, stock value, and key trends.',
    path: '/dashboard',
    icon: <DashboardRoundedIcon fontSize="large" />
  },
  {
    title: 'Stock',
    description: 'Manage stock details, UPC codes, pricing, stock levels, and reorder thresholds.',
    path: '/stocks',
    icon: <Inventory2RoundedIcon fontSize="large" />
  },
  {
    title: 'Purchases',
    description: 'Track purchase orders, expected deliveries, and supplier activity.',
    path: '/purchases',
    icon: <ShoppingCartRoundedIcon fontSize="large" />
  },
  {
    title: 'Sales',
    description: 'Monitor outgoing sales, customer invoices, and revenue movement.',
    path: '/sales',
    icon: <PointOfSaleRoundedIcon fontSize="large" />
  }
];

const Home: React.FC = () => {
  return (
    <Stack spacing={4}>
      <Paper
        elevation={0}
        sx={{
          borderRadius: 4,
          p: { xs: 3, md: 5 },
          background: 'linear-gradient(135deg, #0d47a1 0%, #1976d2 55%, #42a5f5 100%)',
          color: 'white'
        }}
      >
        <Typography variant="h3" sx={{ fontWeight: 800, mb: 1 }}>
          Welcome to SREEMALL
        </Typography>
        <Typography sx={{ maxWidth: 760, opacity: 0.95, mb: 3 }}>
          Keep your warehouse operation organized with one clean workspace for tracking products,
          purchases, sales, and high-level business metrics.
        </Typography>
        <Button
          component={RouterLink}
          to="/dashboard"
          variant="contained"
          size="large"
          sx={{
            bgcolor: '#fff',
            color: '#0d47a1',
            fontWeight: 700,
            textTransform: 'none',
            '&:hover': { bgcolor: '#eaf3ff' }
          }}
        >
          Open Dashboard
        </Button>
      </Paper>

      <Box>
        <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
          Quick Navigation
        </Typography>
        <Grid container spacing={2}>
          {quickLinks.map((link) => (
            <Grid item xs={12} md={6} key={link.path}>
              <Paper
                elevation={0}
                sx={{
                  p: 3,
                  height: '100%',
                  borderRadius: 3,
                  border: '1px solid #e3eaf5',
                  transition: 'all 180ms ease',
                  '&:hover': {
                    transform: 'translateY(-3px)',
                    borderColor: '#90caf9',
                    boxShadow: '0 10px 20px rgba(13, 71, 161, 0.12)'
                  }
                }}
              >
                <Stack direction="row" spacing={2} alignItems="flex-start">
                  <Box sx={{ color: 'primary.main', mt: 0.5 }}>{link.icon}</Box>
                  <Box>
                    <Typography variant="h6" sx={{ fontWeight: 700 }}>
                      {link.title}
                    </Typography>
                    <Typography color="text.secondary" sx={{ mb: 2 }}>
                      {link.description}
                    </Typography>
                    <Button
                      component={RouterLink}
                      to={link.path}
                      variant="outlined"
                      sx={{ textTransform: 'none', fontWeight: 600 }}
                    >
                      Go to {link.title}
                    </Button>
                  </Box>
                </Stack>
              </Paper>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Stack>
  );
};

export default Home;
