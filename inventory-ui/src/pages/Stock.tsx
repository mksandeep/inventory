import React, { useEffect, useState, useMemo } from 'react';
import {
  Box, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Snackbar, Alert, FormControl, Select, MenuItem, Paper, Grid,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  IconButton, Tooltip, Chip, CircularProgress
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import BarChartIcon from '@mui/icons-material/BarChart';
import WarningIcon from '@mui/icons-material/Warning';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import FileUploadDialog from '../components/FileUploadDialog';
import DateRangeFilter from '../components/DateRangeFilter';
import InfiniteScroll from '../components/InfiniteScroll';
import { useAuth } from '../context/AuthContext';

interface Stock {
  stockId: number;
  productDescription: string;
  upc: string;
  sku: string;
  dimensions: string;
  category: string;
  aisleNumber: string;
  bayNumber: string;
  startingInventory: number;
  lowStockThreshold: number;
  unitCost: number;
  markup: number;
  retailPrice: number;
  currentStockLevel: number;
  inventoryValue: number;
  expiryDate: string;
  unitsSold?: number;
  createdBy: string;
  createdTimestamp: string;
  updatedBy: string;
  updatedTimestamp: string;
}

type SortField = 'stockId' | 'productDescription' | 'upc' | 'sku' | 'category' | 'unitCost' | 'currentStockLevel' | 'inventoryValue';
type SortOrder = 'asc' | 'desc';

const emptyStock: Omit<Stock, 'stockId'> = {
  productDescription: '',
  upc: '',
  sku: '',
  dimensions: '',
  category: '',
  aisleNumber: '',
  bayNumber: '',
  startingInventory: 0,
  lowStockThreshold: 0,
  unitCost: 0,
  markup: 0,
  retailPrice: 0,
  currentStockLevel: 0,
  inventoryValue: 0,
  expiryDate: '',
  unitsSold: 0,
  createdBy: '',
  createdTimestamp: '',
  updatedBy: '',
  updatedTimestamp: '',
};

const Stock: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes('ADMIN');
  const isManager = user?.roles?.includes('MANAGER');
  const canEdit = isAdmin || isManager;

  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [stockToDelete, setStockToDelete] = useState<number | null>(null);
  const [editStock, setEditStock] = useState<Stock | null>(null);
  const [form, setForm] = useState<Omit<Stock, 'stockId'>>(emptyStock);
  const [snackbar, setSnackbar] = useState<{open: boolean, message: string, severity: 'success'|'error'}>({open: false, message: '', severity: 'success'});
  const [searchQuery, setSearchQuery] = useState('');
  const [filterCategory, setFilterCategory] = useState('');
  const [sortField, setSortField] = useState<SortField>('stockId');
  const [sortOrder, setSortOrder] = useState<SortOrder>('desc');
  const [categories, setCategories] = useState<string[]>([]);
  const [openUploadDialog, setOpenUploadDialog] = useState(false);
  const [dateRangeStart, setDateRangeStart] = useState<string | null>(null);
  const [dateRangeEnd, setDateRangeEnd] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const pageSize = 20;

  const fetchStocks = async (pageNum: number = 1, append: boolean = false) => {
    if (pageNum === 1) {
      setLoading(true);
    } else {
      setLoadingMore(true);
    }
    try {
      const res = await api.get('/stocks');
      const allStocks = res.data;
      const startIdx = (pageNum - 1) * pageSize;
      const endIdx = startIdx + pageSize;
      const pageStocks = allStocks.slice(startIdx, endIdx);

      if (append) {
        setStocks(prev => [...prev, ...pageStocks]);
      } else {
        setStocks(pageStocks);
      }

      const uniqueCategories = Array.from(new Set(allStocks.map((s: Stock) => s.category).filter(Boolean)));
      setCategories(uniqueCategories as string[]);

      setHasMore(endIdx < allStocks.length);
      setPage(pageNum);
    } catch (e) {
      setError('Failed to fetch stocks');
    } finally {
      if (pageNum === 1) {
        setLoading(false);
      } else {
        setLoadingMore(false);
      }
    }
  };

  const handleLoadMore = () => {
    fetchStocks(page + 1, true);
  };

  const filteredAndSortedStocks = useMemo(() => {
    let result = [...stocks];

    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(stock =>
        stock.productDescription.toLowerCase().includes(query) ||
        stock.upc.toLowerCase().includes(query) ||
        stock.sku.toLowerCase().includes(query)
      );
    }

    if (filterCategory) {
      result = result.filter(stock => stock.category === filterCategory);
    }

    result.sort((a, b) => {
      let aVal: any = a[sortField];
      let bVal: any = b[sortField];

      if (typeof aVal === 'string') {
        aVal = (aVal || '').toLowerCase();
        bVal = (bVal as string || '').toLowerCase();
      }

      const comparison = aVal < bVal ? -1 : aVal > bVal ? 1 : 0;
      return sortOrder === 'asc' ? comparison : -comparison;
    });

    return result;
  }, [stocks, searchQuery, filterCategory, sortField, sortOrder]);

  useEffect(() => { fetchStocks(); }, []);

  const handleOpenDialog = (stock?: Stock) => {
    if (stock) {
      setEditStock(stock);
      setForm({ ...stock });
    } else {
      setEditStock(null);
      setForm(emptyStock);
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditStock(null);
    setForm(emptyStock);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | { name?: string; value: unknown }>) => {
    const { name, value } = e.target;
    const numericFields = ['startingInventory', 'lowStockThreshold', 'unitCost', 'markup'];

    setForm((prev) => ({
      ...prev,
      [name!]: numericFields.includes(name!) ? (value === '' ? 0 : Number(value)) : value
    }));
  };

  const handleSave = async () => {
    if (!form.productDescription || !form.upc || !form.sku) {
      setSnackbar({open: true, message: 'Description, UPC, and SKU are required', severity: 'error'});
      return;
    }

    try {
      if (editStock) {
        await api.put(`/stocks/${editStock.stockId}`, form);
        setSnackbar({open: true, message: 'Stock updated', severity: 'success'});
      } else {
        await api.post('/stocks', form);
        setSnackbar({open: true, message: 'Stock created', severity: 'success'});
      }
      fetchStocks();
      handleCloseDialog();
    } catch (e: any) {
      setSnackbar({open: true, message: e.response?.data?.message || 'Failed to save stock', severity: 'error'});
    }
  };

  const handleDelete = async (stockId: number) => {
    setStockToDelete(stockId);
    setDeleteConfirmOpen(true);
  };

  const confirmDelete = async () => {
    if (stockToDelete === null) return;
    try {
      await api.delete(`/stocks/${stockToDelete}`);
      setSnackbar({open: true, message: 'Stock deleted successfully', severity: 'success'});
      fetchStocks();
    } catch (e) {
      setSnackbar({open: true, message: 'Failed to delete stock', severity: 'error'});
    } finally {
      setDeleteConfirmOpen(false);
      setStockToDelete(null);
    }
  };

  const handleDateRangeChange = async (startDate: string | null, endDate: string | null) => {
    setDateRangeStart(startDate);
    setDateRangeEnd(endDate);

    if (startDate && endDate) {
      setLoading(true);
      try {
        const res = await api.get(`/stocks/by-date-range?startDate=${startDate}&endDate=${endDate}`);
        setStocks(res.data);
      } catch (e) {
        setSnackbar({open: true, message: 'Failed to fetch stocks by date range', severity: 'error'});
      } finally {
        setLoading(false);
      }
    } else {
      fetchStocks();
    }
  };

  return (
    <Box>
      {/* Page Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" sx={{ fontWeight: 800, color: '#0d47a1', mb: 1 }}>
          Stock Management
        </Typography>
      </Box>

      {/* Top Action Bar - Moved buttons */}
      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <Button
            variant="outlined"
            startIcon={<BarChartIcon />}
            onClick={() => navigate('/stock-summary')}
            sx={{ fontWeight: 700 }}
          >
            View Summary
          </Button>
          {canEdit && (
            <>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => handleOpenDialog()}
                sx={{ fontWeight: 700 }}
              >
                Add Stock
              </Button>
              <Button
                variant="outlined"
                startIcon={<UploadFileIcon />}
                onClick={() => setOpenUploadDialog(true)}
                sx={{ fontWeight: 700 }}
              >
                Bulk Upload
              </Button>
            </>
          )}
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>{error}</Alert>}

      <>
        {/* Date Range Filter */}
        <DateRangeFilter onFilterChange={handleDateRangeChange} />

        {/* Search and Filter */}
        <Paper sx={{ p: 3, mb: 3, backgroundColor: '#f8fafc', borderRadius: 2, border: '1px solid #e2e8f0' }}>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} sm={6} md={4}>
              <TextField
                fullWidth
                placeholder="Search by description, UPC, or SKU..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                InputProps={{
                  startAdornment: <SearchIcon sx={{ mr: 1, color: '#666' }} />,
                  endAdornment: searchQuery && (
                    <ClearIcon
                      sx={{ cursor: 'pointer', color: '#666' }}
                      onClick={() => setSearchQuery('')}
                    />
                  ),
                }}
                size="small"
              />
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <FormControl fullWidth size="small">
                <Select
                  value={filterCategory}
                  onChange={(e) => setFilterCategory(e.target.value)}
                  displayEmpty
                >
                  <MenuItem value="">All Categories</MenuItem>
                  {categories.map(cat => (
                    <MenuItem key={cat} value={cat}>{cat}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <FormControl fullWidth size="small">
                <Select
                  value={sortField}
                  onChange={(e) => setSortField(e.target.value as SortField)}
                >
                  <MenuItem value="stockId">Sort by ID</MenuItem>
                  <MenuItem value="productDescription">Sort by Name</MenuItem>
                  <MenuItem value="upc">Sort by UPC</MenuItem>
                  <MenuItem value="sku">Sort by SKU</MenuItem>
                  <MenuItem value="category">Sort by Category</MenuItem>
                  <MenuItem value="unitCost">Sort by Cost</MenuItem>
                  <MenuItem value="currentStockLevel">Sort by Stock</MenuItem>
                  <MenuItem value="inventoryValue">Sort by Value</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <FormControl fullWidth size="small">
                <Select
                  value={sortOrder}
                  onChange={(e) => setSortOrder(e.target.value as SortOrder)}
                >
                  <MenuItem value="asc">Ascending</MenuItem>
                  <MenuItem value="desc">Descending</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <Button
                variant="outlined"
                fullWidth
                onClick={() => {
                  setSearchQuery('');
                  setFilterCategory('');
                  setSortField('stockId');
                  setSortOrder('asc');
                }}
              >
                Reset
              </Button>
            </Grid>
          </Grid>
        </Paper>

        {/* Table */}
        <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: '0 2px 8px rgba(0,0,0,0.08)', border: '1px solid #e2e8f0', overflow: 'auto' }}>
          <Table sx={{ minWidth: 1400 }}>
            <TableHead sx={{ backgroundColor: '#f8fafc', borderBottom: '2px solid #e2e8f0' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>ID</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>Description</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>UPC</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>SKU</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>Category</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>Location</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }} align="right">Stock</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }} align="right">Unit Cost</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }} align="right">Retail Price</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }} align="right">Inv Value</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>Expiry</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }}>Created By</TableCell>
                <TableCell sx={{ fontWeight: 700, color: '#1e293b', fontSize: '0.875rem' }} align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow><TableCell colSpan={13} align="center" sx={{ py: 3 }}>Loading stocks...</TableCell></TableRow>
              ) : filteredAndSortedStocks.length === 0 ? (
                <TableRow><TableCell colSpan={13} align="center" sx={{ py: 3, color: 'text.secondary' }}>No stocks found</TableCell></TableRow>
              ) : (
                filteredAndSortedStocks.map(stock => (
                  <TableRow key={stock.stockId} hover>
                    <TableCell>{stock.stockId}</TableCell>
                    <TableCell sx={{ fontWeight: 500 }}>{stock.productDescription}</TableCell>
                    <TableCell><Chip label={stock.upc} size="small" sx={{ fontFamily: 'monospace' }} /></TableCell>
                    <TableCell><Typography variant="body2" sx={{ fontFamily: 'monospace' }}>{stock.sku}</Typography></TableCell>
                    <TableCell>
                      {stock.category && <Chip label={stock.category} size="small" sx={{ backgroundColor: '#e3f2fd', color: '#1976d2' }} />}
                    </TableCell>
                    <TableCell>
                      <Typography variant="caption" display="block">Aisle: {stock.aisleNumber || '-'}</Typography>
                      <Typography variant="caption" display="block">Bay: {stock.bayNumber || '-'}</Typography>
                    </TableCell>
                    <TableCell align="right">
                      <Typography sx={{ fontWeight: 700, color: stock.currentStockLevel <= stock.lowStockThreshold ? 'error.main' : 'inherit' }}>
                        {stock.currentStockLevel}
                      </Typography>
                      {stock.currentStockLevel <= stock.lowStockThreshold && (
                        <Typography variant="caption" color="error">Low Stock</Typography>
                      )}
                    </TableCell>
                    <TableCell align="right">${stock.unitCost?.toFixed(2)}</TableCell>
                    <TableCell align="right">${stock.retailPrice?.toFixed(2)}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 600 }}>${stock.inventoryValue?.toFixed(2)}</TableCell>
                    <TableCell>{stock.expiryDate || '-'}</TableCell>
                    <TableCell><Typography variant="caption">{stock.createdBy}</Typography></TableCell>
                    <TableCell align="center">
                      {canEdit && (
                        <>
                          <Tooltip title="Edit">
                            <IconButton size="small" onClick={() => handleOpenDialog(stock)} color="primary">
                              <EditIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          {isAdmin && (
                            <Tooltip title="Delete">
                              <IconButton size="small" onClick={() => handleDelete(stock.stockId)} color="error">
                                <DeleteIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>
                          )}
                        </>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        <InfiniteScroll
          onLoadMore={handleLoadMore}
          isLoading={loadingMore}
          hasMore={hasMore}
        />

        <Box sx={{ mt: 2, p: 1, textAlign: 'center' }}>
          <Typography variant="caption" color="textSecondary">
            Showing {stocks.length} stocks
          </Typography>
        </Box>

        {/* Add/Edit Dialog */}
        <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
          <DialogTitle sx={{ fontWeight: 700 }}>{editStock ? 'Edit Stock' : 'Add New Stock'}</DialogTitle>
          <DialogContent dividers>
            <Grid container spacing={2} sx={{ mt: 0.5 }}>
              <Grid item xs={12} md={8}>
                <TextField label="Description" name="productDescription" value={form.productDescription} onChange={handleChange} fullWidth required />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="UPC *" name="upc" value={form.upc} onChange={handleChange} fullWidth required />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField label="SKU" name="sku" value={form.sku} onChange={handleChange} fullWidth required />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField label="Category" name="category" value={form.category} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="Dimensions" name="dimensions" value={form.dimensions} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={2}>
                <TextField label="Aisle" name="aisleNumber" value={form.aisleNumber} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={2}>
                <TextField label="Bay" name="bayNumber" value={form.bayNumber} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={3}>
                <TextField label="Starting Inv" name="startingInventory" type="number" value={form.startingInventory} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={3}>
                <TextField label="Low Stock Alert" name="lowStockThreshold" type="number" value={form.lowStockThreshold} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={3}>
                <TextField label="Unit Cost ($)" name="unitCost" type="number" value={form.unitCost} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={3}>
                <TextField label="Markup (%)" name="markup" type="number" value={form.markup} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12}>
                <TextField label="Expiry Date" name="expiryDate" type="date" value={form.expiryDate} onChange={handleChange} fullWidth InputLabelProps={{ shrink: true }} />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={handleCloseDialog} color="inherit">Cancel</Button>
            <Button onClick={handleSave} variant="contained" disabled={!form.productDescription || !form.upc || !form.sku}>
              {editStock ? 'Update Stock' : 'Create Stock'}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Delete Confirmation Dialog */}
        <Dialog
          open={deleteConfirmOpen}
          onClose={() => setDeleteConfirmOpen(false)}
          maxWidth="sm"
          fullWidth
          PaperProps={{
            sx: {
              borderRadius: 2,
              boxShadow: '0 20px 60px rgba(0,0,0,0.3)'
            }
          }}
        >
          <DialogTitle sx={{ textAlign: 'center', fontWeight: 700, pb: 1 }}>
            Confirm Delete
          </DialogTitle>
          <DialogContent sx={{ textAlign: 'center', py: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
              <WarningIcon sx={{ fontSize: 48, color: '#ff9800' }} />
            </Box>
            <Typography variant="body1" sx={{ mb: 1, fontWeight: 500 }}>
              Are you sure you want to delete this stock?
            </Typography>
            <Typography variant="caption" color="textSecondary">
              This action cannot be undone. All related data will be removed.
            </Typography>
          </DialogContent>
          <DialogActions sx={{ p: 2, justifyContent: 'center', gap: 1 }}>
            <Button
              onClick={() => setDeleteConfirmOpen(false)}
              variant="outlined"
              sx={{ px: 4 }}
            >
              Cancel
            </Button>
            <Button
              onClick={confirmDelete}
              variant="contained"
              color="error"
              sx={{ px: 4 }}
            >
              Delete
            </Button>
          </DialogActions>
        </Dialog>

        <Snackbar open={snackbar.open} autoHideDuration={3000} onClose={() => setSnackbar(s => ({...s, open: false}))}>
          <Alert severity={snackbar.severity} variant="filled" sx={{ width: '100%' }}>{snackbar.message}</Alert>
        </Snackbar>

        <FileUploadDialog
          open={openUploadDialog}
          onClose={() => setOpenUploadDialog(false)}
          title="Bulk Upload Stocks"
          uploadEndpoint="/stocks/upload"
          templateEndpoint="/stocks/template"
          onSuccess={() => {
            fetchStocks();
            setSnackbar({ open: true, message: 'Stocks uploaded successfully!', severity: 'success' });
          }}
        />
      </>
    </Box>
  );
};

export default Stock;

