import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Box, Button, Container, Menu, MenuItem, Dialog, DialogTitle, DialogContent, DialogActions, TextField, Alert } from '@mui/material';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import SREEMALLLogo from '../assets/SREEMALLLogo.svg';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LogoutIcon from '@mui/icons-material/Logout';
import LockResetIcon from '@mui/icons-material/LockReset';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import api from '../api';
import { encryptPasswordSimple } from '../utils/encryption';

const navLinks = [
  { label: 'Home', path: '/' },
  { label: 'Dashboard', path: '/dashboard' },
  { label: 'Stock', path: '/stocks' },
  { label: 'Purchases', path: '/purchases' },
  { label: 'Sales', path: '/sales' }
];

const Header: React.FC = () => {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [openPasswordDialog, setOpenPasswordDialog] = useState(false);
  const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [passwordError, setPasswordError] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState('');
  const [openUserDialog, setOpenUserDialog] = useState(false);
  const [userForm, setUserForm] = useState({ email: '', fullName: '', password: '', role: 'USER' });
  const [userError, setUserError] = useState('');
  const [userSuccess, setUserSuccess] = useState('');

  const fullName = localStorage.getItem('fullName') || 'User';
  const email = localStorage.getItem('email') || '';
  const roles = JSON.parse(localStorage.getItem('roles') || '[]');
  const isAdmin = roles.includes('ADMIN');

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    localStorage.clear();
    handleMenuClose();
    window.location.href = '/login';
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError('');
    setPasswordSuccess('');

    if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
      setPasswordError('Please fill in all fields');
      return;
    }

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError('New passwords do not match');
      return;
    }

    try {
      const encryptedCurrent = encryptPasswordSimple(passwordForm.currentPassword);
      const encryptedNew = encryptPasswordSimple(passwordForm.newPassword);

      await api.post('/auth/change-password', {
        email,
        currentPassword: encryptedCurrent,
        newPassword: encryptedNew,
        encrypted: true
      });

      setPasswordSuccess('Password changed successfully!');
      setTimeout(() => {
        setOpenPasswordDialog(false);
        setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
      }, 2000);
    } catch (err: any) {
      setPasswordError(err.response?.data?.message || 'Failed to change password');
    }
  };

  const handleAddUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setUserError('');
    setUserSuccess('');

    if (!userForm.email || !userForm.fullName || !userForm.password) {
      setUserError('Please fill in all fields');
      return;
    }

    try {
      await api.post('/auth/admin/add-user', {
        email: userForm.email,
        fullName: userForm.fullName,
        password: userForm.password,
        role: userForm.role
      });

      setUserSuccess('User added successfully!');
      setTimeout(() => {
        setOpenUserDialog(false);
        setUserForm({ email: '', fullName: '', password: '', role: 'USER' });
      }, 2000);
    } catch (err: any) {
      setUserError(err.response?.data?.message || 'Failed to add user');
    }
  };

  return (
    <>
      <AppBar
        position="sticky"
        elevation={0}
        color="transparent"
        sx={{
          backdropFilter: 'blur(12px)',
          backgroundColor: 'rgba(255, 255, 255, 0.86)',
          borderBottom: '1px solid #e4ebf7'
        }}
      >
        <Container maxWidth="xl">
          <Toolbar disableGutters sx={{ py: 1, gap: 3 }}>
            <Box display="flex" alignItems="center" sx={{ mr: 'auto' }}>
              <img src={SREEMALLLogo} alt="SREEMALL Logo" style={{ height: 40, marginRight: 12 }} />
              <Typography variant="h5" color="primary" sx={{ fontWeight: 800 }}>
                SREEMALL
              </Typography>
            </Box>

            <Box display="flex" alignItems="center" gap={1}>
              {navLinks.map((link) => {
                const isActive = pathname === link.path;
                return (
                  <Button
                    key={link.path}
                    component={RouterLink}
                    to={link.path}
                    variant={isActive ? 'contained' : 'text'}
                    color={isActive ? 'primary' : 'inherit'}
                    sx={{
                      borderRadius: 999,
                      px: 2,
                      textTransform: 'none',
                      fontWeight: 600
                    }}
                  >
                    {link.label}
                  </Button>
                );
              })}
            </Box>

            {/* User Menu */}
            <Box display="flex" alignItems="center">
              <Button
                startIcon={<AccountCircleIcon />}
                onClick={handleMenuOpen}
                sx={{
                  textTransform: 'none',
                  color: 'inherit',
                  fontWeight: 500
                }}
              >
                {fullName}
              </Button>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                transformOrigin={{ vertical: 'top', horizontal: 'right' }}
              >
                <MenuItem disabled>
                  <Typography variant="body2">{email}</Typography>
                </MenuItem>
                <MenuItem onClick={() => {
                  setOpenPasswordDialog(true);
                  handleMenuClose();
                }}>
                  <LockResetIcon sx={{ mr: 1 }} />
                  Change Password
                </MenuItem>
                {isAdmin && (
                  <MenuItem onClick={() => {
                    setOpenUserDialog(true);
                    handleMenuClose();
                  }}>
                    <PersonAddIcon sx={{ mr: 1 }} />
                    Add New User
                  </MenuItem>
                )}
                <MenuItem onClick={handleLogout}>
                  <LogoutIcon sx={{ mr: 1 }} />
                  Logout
                </MenuItem>
              </Menu>
            </Box>
          </Toolbar>
        </Container>
      </AppBar>

      {/* Change Password Dialog */}
      <Dialog open={openPasswordDialog} onClose={() => {
        setOpenPasswordDialog(false);
        setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
        setPasswordError('');
        setPasswordSuccess('');
      }} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Change Password</DialogTitle>
        <DialogContent dividers>
          {passwordSuccess && <Alert severity="success" sx={{ mb: 2 }}>{passwordSuccess}</Alert>}
          {passwordError && <Alert severity="error" sx={{ mb: 2 }}>{passwordError}</Alert>}
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
            <TextField
              fullWidth
              label="Current Password"
              type="password"
              value={passwordForm.currentPassword}
              onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
            />
            <TextField
              fullWidth
              label="New Password"
              type="password"
              value={passwordForm.newPassword}
              onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
            />
            <TextField
              fullWidth
              label="Confirm Password"
              type="password"
              value={passwordForm.confirmPassword}
              onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
            />
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => {
            setOpenPasswordDialog(false);
            setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
            setPasswordError('');
            setPasswordSuccess('');
          }} color="inherit">Cancel</Button>
          <Button onClick={handleChangePassword} variant="contained" startIcon={<LockResetIcon />}>
            Change Password
          </Button>
        </DialogActions>
      </Dialog>

      {/* Add User Dialog (Admin Only) */}
      {isAdmin && (
        <Dialog open={openUserDialog} onClose={() => {
          setOpenUserDialog(false);
          setUserForm({ email: '', fullName: '', password: '', role: 'USER' });
          setUserError('');
          setUserSuccess('');
        }} maxWidth="sm" fullWidth>
          <DialogTitle sx={{ fontWeight: 700 }}>Add New User</DialogTitle>
          <DialogContent dividers>
            {userSuccess && <Alert severity="success" sx={{ mb: 2 }}>{userSuccess}</Alert>}
            {userError && <Alert severity="error" sx={{ mb: 2 }}>{userError}</Alert>}
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={userForm.email}
                onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
              />
              <TextField
                fullWidth
                label="Full Name"
                type="text"
                value={userForm.fullName}
                onChange={(e) => setUserForm({ ...userForm, fullName: e.target.value })}
              />
              <TextField
                fullWidth
                label="Password"
                type="password"
                value={userForm.password}
                onChange={(e) => setUserForm({ ...userForm, password: e.target.value })}
              />
              <TextField
                select
                fullWidth
                label="Role"
                value={userForm.role}
                onChange={(e) => setUserForm({ ...userForm, role: e.target.value })}
              >
                <MenuItem value="USER">User</MenuItem>
                <MenuItem value="ADMIN">Admin</MenuItem>
              </TextField>
            </Box>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => {
              setOpenUserDialog(false);
              setUserForm({ email: '', fullName: '', password: '', role: 'USER' });
              setUserError('');
              setUserSuccess('');
            }} color="inherit">Cancel</Button>
            <Button onClick={handleAddUser} variant="contained" startIcon={<PersonAddIcon />}>
              Add User
            </Button>
          </DialogActions>
        </Dialog>
      )}
    </>
  );
};

export default Header;
