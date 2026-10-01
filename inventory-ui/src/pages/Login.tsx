import React, { useState } from 'react';
import {
  Box, Container, Paper, TextField, Button, Alert, Typography,
  CircularProgress, Link, Card, CardContent, Dialog, DialogTitle, DialogContent, DialogActions
} from '@mui/material';
import LoginIcon from '@mui/icons-material/Login';
import LockResetIcon from '@mui/icons-material/LockReset';
import api from '../api';
import { encryptPasswordSimple } from '../utils/encryption';

interface LoginFormData {
  email: string;
  password: string;
}

const Login: React.FC = () => {
  const [formData, setFormData] = useState<LoginFormData>({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [openResetDialog, setOpenResetDialog] = useState(false);
  const [resetEmail, setResetEmail] = useState('');
  const [resetSuccess, setResetSuccess] = useState('');

  const handleLoginChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError('');
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.email || !formData.password) {
      setError('Please fill in all fields');
      return;
    }

    setLoading(true);
    try {
      // Encrypt password before sending
      const encryptedPassword = encryptPasswordSimple(formData.password);

      const response = await api.post('/auth/login', {
        email: formData.email,
        password: encryptedPassword,
        encrypted: true  // Flag to indicate encrypted password
      });
      const { token, refreshToken, email, fullName, roles } = response.data;

      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('email', email);
      localStorage.setItem('fullName', fullName);
      localStorage.setItem('roles', JSON.stringify(roles));

      window.location.href = '/';
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleRequestPasswordReset = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!resetEmail) {
      setError('Please enter your email');
      return;
    }

    try {
      await api.post('/auth/request-password-reset', { email: resetEmail });
      setResetSuccess('Password reset link has been sent to your email');
      setTimeout(() => {
        setOpenResetDialog(false);
        setResetEmail('');
        setResetSuccess('');
      }, 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to request password reset');
    }
  };

  return (
    <Box sx={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #0d47a1 0%, #1976d2 50%, #42a5f5 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: 2,
      position: 'relative',
      overflow: 'hidden',
      '&::before': {
        content: '""',
        position: 'absolute',
        top: '-50%',
        right: '-10%',
        width: '500px',
        height: '500px',
        backgroundColor: 'rgba(255, 255, 255, 0.05)',
        borderRadius: '50%',
      },
      '&::after': {
        content: '""',
        position: 'absolute',
        bottom: '-50%',
        left: '-10%',
        width: '500px',
        height: '500px',
        backgroundColor: 'rgba(255, 255, 255, 0.05)',
        borderRadius: '50%',
      }
    }}>
      <Container maxWidth="sm" sx={{ position: 'relative', zIndex: 1 }}>
        <Box
          sx={{
            backdropFilter: 'blur(8px)',
            background: 'rgba(255, 255, 255, 0.92)',
            borderRadius: 3,
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.6)',
            overflow: 'hidden',
            border: '1px solid rgba(255, 255, 255, 0.3)'
          }}
        >
          <Box sx={{ p: 4 }}>
            {/* Logo/Title */}
            <Box sx={{ textAlign: 'center', mb: 4 }}>
              <Box sx={{
                width: 80,
                height: 80,
                margin: '0 auto 20px',
                background: 'linear-gradient(135deg, #0d47a1 0%, #1976d2 100%)',
                borderRadius: '20px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 8px 16px rgba(13, 71, 161, 0.3)'
              }}>
                <LoginIcon sx={{ fontSize: 48, color: 'white' }} />
              </Box>
              <Typography variant="h3" sx={{ fontWeight: 800, mb: 1, color: '#0d47a1' }}>
                SREEMALL
              </Typography>
              <Typography variant="body1" sx={{ color: '#64748b', fontWeight: 500 }}>
                Manage your inventory with confidence
              </Typography>
            </Box>

            {/* Error Alert */}
            {error && (
              <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>
                {error}
              </Alert>
            )}

            {/* Login Form */}
            <Box component="form" onSubmit={handleLogin} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
              <TextField
                fullWidth
                label="Email Address"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleLoginChange}
                placeholder="your@email.com"
                disabled={loading}
                variant="outlined"
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    transition: 'all 0.3s ease',
                  }
                }}
              />
              <TextField
                fullWidth
                label="Password"
                type="password"
                name="password"
                value={formData.password}
                onChange={handleLoginChange}
                placeholder="••••••••"
                disabled={loading}
                variant="outlined"
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                    transition: 'all 0.3s ease',
                  }
                }}
              />
              <Button
                fullWidth
                variant="contained"
                size="large"
                type="submit"
                disabled={loading}
                sx={{
                  background: 'linear-gradient(135deg, #0d47a1 0%, #1976d2 100%)',
                  py: 1.75,
                  fontWeight: 700,
                  fontSize: '1rem',
                  textTransform: 'none',
                  borderRadius: 2,
                  boxShadow: '0 4px 12px rgba(13, 71, 161, 0.3)',
                  transition: 'all 0.3s ease',
                  '&:hover:not(:disabled)': {
                    boxShadow: '0 8px 24px rgba(13, 71, 161, 0.4)',
                    transform: 'translateY(-2px)'
                  }
                }}
              >
                {loading ? <CircularProgress size={24} sx={{ color: 'white' }} /> : 'Sign In'}
              </Button>
            </Box>

            {/* Forgot Password Link */}
            <Box sx={{ textAlign: 'center', mt: 3, pt: 3, borderTop: '1px solid #e2e8f0' }}>
              <Typography variant="body2" sx={{ color: '#64748b' }}>
                Forgot your password?{' '}
                <Link
                  component="button"
                  variant="body2"
                  onClick={(e) => {
                    e.preventDefault();
                    setOpenResetDialog(true);
                  }}
                  sx={{
                    color: '#0d47a1',
                    fontWeight: 700,
                    cursor: 'pointer',
                    textDecoration: 'none',
                    transition: 'all 0.2s ease',
                    '&:hover': {
                      textDecoration: 'underline',
                      color: '#1976d2'
                    }
                  }}
                >
                  Reset it here
                </Link>
              </Typography>
            </Box>
          </Box>
        </Box>
      </Container>

      {/* Password Reset Dialog */}
      <Dialog open={openResetDialog} onClose={() => {
        setOpenResetDialog(false);
        setResetEmail('');
        setResetSuccess('');
        setError('');
      }} maxWidth="sm" fullWidth
      PaperProps={{
        sx: {
          borderRadius: 3,
          backdropFilter: 'blur(8px)',
        }
      }}>
        <DialogTitle sx={{ fontWeight: 700, fontSize: '1.25rem' }}>Reset Password</DialogTitle>
        <DialogContent dividers>
          {resetSuccess ? (
            <Alert severity="success" sx={{ mb: 2, borderRadius: 2 }}>
              {resetSuccess}
            </Alert>
          ) : null}
          {error && (
            <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>
              {error}
            </Alert>
          )}
          <Typography variant="body2" sx={{ mb: 2.5, color: '#64748b' }}>
            Enter your email address and we'll send you a link to reset your password.
          </Typography>
          <TextField
            fullWidth
            label="Email Address"
            type="email"
            value={resetEmail}
            onChange={(e) => setResetEmail(e.target.value)}
            placeholder="your@email.com"
            variant="outlined"
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: 2,
              }
            }}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2, gap: 1 }}>
          <Button onClick={() => {
            setOpenResetDialog(false);
            setResetEmail('');
            setResetSuccess('');
            setError('');
          }} color="inherit" sx={{ fontWeight: 600 }}>
            Cancel
          </Button>
          <Button onClick={handleRequestPasswordReset} variant="contained" startIcon={<LockResetIcon />} sx={{ fontWeight: 600 }}>
            Send Reset Link
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Login;

