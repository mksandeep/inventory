import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  LinearProgress,
  Alert,
  List,
  ListItem,
  ListItemText,
  Divider,
  Paper
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import GetAppIcon from '@mui/icons-material/GetApp';
import api from '../api';

interface FileUploadDialogProps {
  open: boolean;
  onClose: () => void;
  title: string;
  uploadEndpoint: string;
  templateEndpoint: string;
  onSuccess: () => void;
}

const FileUploadDialog: React.FC<FileUploadDialogProps> = ({
  open,
  onClose,
  title,
  uploadEndpoint,
  templateEndpoint,
  onSuccess
}) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [uploadResult, setUploadResult] = useState<any>(null);
  const [error, setError] = useState('');

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (files && files[0]) {
      const file = files[0];
      if (file.name.endsWith('.xlsx') || file.name.endsWith('.xls')) {
        setSelectedFile(file);
        setError('');
      } else {
        setError('Please select a valid Excel file (.xlsx or .xls)');
      }
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setError('Please select a file');
      return;
    }

    setLoading(true);
    const formData = new FormData();
    formData.append('file', selectedFile);

    try {
      const response = await api.post(uploadEndpoint, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      setUploadResult(response.data);
      if (response.data.failureCount === 0) {
        setTimeout(() => {
          handleClose();
          onSuccess();
        }, 1500);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Upload failed');
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadTemplate = async () => {
    try {
      const response = await api.get(templateEndpoint, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `template.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
    } catch (err) {
      setError('Failed to download template');
    }
  };

  const handleClose = () => {
    setSelectedFile(null);
    setUploadResult(null);
    setError('');
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ fontWeight: 700 }}>{title}</DialogTitle>
      <DialogContent dividers>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>

          {/* Download Template Section */}
          <Paper sx={{ p: 2, backgroundColor: '#e3f2fd' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <Typography variant="body2" sx={{ fontWeight: 500 }}>
                Need a template? Download the Excel template first.
              </Typography>
              <Button
                size="small"
                startIcon={<GetAppIcon />}
                onClick={handleDownloadTemplate}
                color="primary"
              >
                Download Template
              </Button>
            </Box>
          </Paper>

          <Divider sx={{ my: 1 }} />

          {/* File Upload Section */}
          {!uploadResult ? (
            <>
              <Box
                sx={{
                  border: '2px dashed #1976d2',
                  borderRadius: 2,
                  p: 3,
                  textAlign: 'center',
                  backgroundColor: '#f5f5f5',
                  cursor: 'pointer',
                  transition: 'all 0.3s',
                  '&:hover': {
                    backgroundColor: '#eeeeee',
                    borderColor: '#1565c0'
                  }
                }}
              >
                <input
                  type="file"
                  accept=".xlsx,.xls"
                  onChange={handleFileSelect}
                  style={{ display: 'none' }}
                  id="file-input"
                />
                <label htmlFor="file-input" style={{ cursor: 'pointer', display: 'block' }}>
                  <CloudUploadIcon sx={{ fontSize: 48, color: '#1976d2', mb: 1 }} />
                  <Typography variant="body1" sx={{ fontWeight: 600, mb: 0.5 }}>
                    Click to browse or drag and drop
                  </Typography>
                  <Typography variant="caption" color="textSecondary">
                    Select an Excel file (.xlsx or .xls)
                  </Typography>
                </label>
              </Box>

              {selectedFile && (
                <Box sx={{ p: 2, backgroundColor: '#f0f4c3', borderRadius: 1 }}>
                  <Typography variant="body2" sx={{ fontWeight: 600 }}>
                    Selected file: {selectedFile.name}
                  </Typography>
                  <Typography variant="caption" color="textSecondary">
                    Size: {(selectedFile.size / 1024).toFixed(2)} KB
                  </Typography>
                </Box>
              )}

              {error && (
                <Alert severity="error" onClose={() => setError('')}>
                  {error}
                </Alert>
              )}

              {loading && <LinearProgress />}
            </>
          ) : (
            /* Upload Result Section */
            <Box>
              {uploadResult.failureCount === 0 ? (
                <Box sx={{ textAlign: 'center' }}>
                  <CheckCircleIcon sx={{ fontSize: 60, color: 'success.main', mb: 1 }} />
                  <Typography variant="h6" sx={{ fontWeight: 700, color: 'success.main', mb: 1 }}>
                    Upload Successful!
                  </Typography>
                  <Typography variant="body2" sx={{ mb: 2 }}>
                    {uploadResult.message}
                  </Typography>
                </Box>
              ) : (
                <>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" sx={{ fontWeight: 600, color: 'text.primary', mb: 1 }}>
                      {uploadResult.message}
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 2 }}>
                      <Box>
                        <CheckCircleIcon sx={{ color: 'success.main', mr: 0.5, verticalAlign: 'middle' }} />
                        <Typography variant="caption" sx={{ fontWeight: 600 }}>
                          Success: {uploadResult.successCount}
                        </Typography>
                      </Box>
                      <Box>
                        <ErrorIcon sx={{ color: 'error.main', mr: 0.5, verticalAlign: 'middle' }} />
                        <Typography variant="caption" sx={{ fontWeight: 600, color: 'error.main' }}>
                          Failed: {uploadResult.failureCount}
                        </Typography>
                      </Box>
                    </Box>
                  </Box>

                  {uploadResult.errors && uploadResult.errors.length > 0 && (
                    <Paper sx={{ maxHeight: 250, overflow: 'auto', backgroundColor: '#ffebee' }}>
                      <List dense>
                        {uploadResult.errors.slice(0, 10).map((err: string, idx: number) => (
                          <ListItem key={idx} sx={{ py: 0.5 }}>
                            <ListItemText
                              primary={
                                <Typography variant="caption" sx={{ color: 'error.main', fontFamily: 'monospace' }}>
                                  {err}
                                </Typography>
                              }
                            />
                          </ListItem>
                        ))}
                        {uploadResult.errors.length > 10 && (
                          <ListItem>
                            <Typography variant="caption" color="textSecondary">
                              ... and {uploadResult.errors.length - 10} more errors
                            </Typography>
                          </ListItem>
                        )}
                      </List>
                    </Paper>
                  )}
                </>
              )}
            </Box>
          )}
        </Box>
      </DialogContent>

      <DialogActions sx={{ p: 2 }}>
        <Button onClick={handleClose} color="inherit">
          {uploadResult ? 'Close' : 'Cancel'}
        </Button>
        {!uploadResult && (
          <Button
            onClick={handleUpload}
            variant="contained"
            disabled={!selectedFile || loading}
            sx={{ minWidth: 100 }}
          >
            {loading ? 'Uploading...' : 'Upload'}
          </Button>
        )}
        {uploadResult && uploadResult.failureCount > 0 && (
          <Button
            onClick={() => {
              setUploadResult(null);
              setSelectedFile(null);
            }}
            variant="contained"
          >
            Upload More
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default FileUploadDialog;

