import React, { useEffect, useState, useMemo } from 'react';
import {
  Box, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Snackbar, Alert, MenuItem, FormControl, Select, Paper, Grid,
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
import { useNavigate } from 'react-router-dom';
import api from '../api';
import FileUploadDialog from '../components/FileUploadDialog';
import DateRangeFilter from '../components/DateRangeFilter';
import InfiniteScroll from '../components/InfiniteScroll';

interface Purchase {
  purchaseId: number;
  orderReferenceNo: string;
  productId: number;
  orderDate: string;
  expectedDeliveryDate: string;
  vendor: string;
  unitsOrdered: number;
  costPerUnit: number;
  totalPurchaseAmount: number;
  aisleNumber: string;
  bayNumber: string;
}

type SortField = 'purchaseId' | 'orderReferenceNo' | 'vendor' | 'orderDate' | 'unitsOrdered' | 'totalPurchaseAmount';
type SortOrder = 'asc' | 'desc';

const emptyPurchase: Omit<Purchase, 'purchaseId' | 'totalPurchaseAmount'> = {
  orderReferenceNo: '',
  productId: 0,
  orderDate: '',
  expectedDeliveryDate: '',
  vendor: '',
  unitsOrdered: 0,
  costPerUnit: 0,
  aisleNumber: '',
  bayNumber: '',
};

const Purchases: React.FC = () => {
  const navigate = useNavigate();
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [products, setProducts] = useState<{productId: number, productDescription: string}[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [editPurchase, setEditPurchase] = useState<Purchase | null>(null);
  const [form, setForm] = useState<Omit<Purchase, 'purchaseId' | 'totalPurchaseAmount'>>(emptyPurchase);
  const [snackbar, setSnackbar] = useState<{open: boolean, message: string, severity: 'success'|'error'}>({open: false, message: '', severity: 'success'});
  const [searchQuery, setSearchQuery] = useState('');
  const [filterVendor, setFilterVendor] = useState('');
  const [sortField, setSortField] = useState<SortField>('purchaseId');
  const [sortOrder, setSortOrder] = useState<SortOrder>('desc');
   const [vendors, setVendors] = useState<string[]>([]);
   const [openUploadDialog, setOpenUploadDialog] = useState(false);
   const [dateRangeStart, setDateRangeStart] = useState<string | null>(null);
   const [dateRangeEnd, setDateRangeEnd] = useState<string | null>(null);
   const [page, setPage] = useState(1);
   const [hasMore, setHasMore] = useState(true);
   const [loadingMore, setLoadingMore] = useState(false);
   const pageSize = 20;

   const fetchPurchases = async (pageNum: number = 1, append: boolean = false) => {
     if (pageNum === 1) {
       setLoading(true);
     } else {
       setLoadingMore(true);
     }
     try {
       const res = await api.get('/purchases');
       const allPurchases = res.data;
       const startIdx = (pageNum - 1) * pageSize;
       const endIdx = startIdx + pageSize;
       const pagePurchases = allPurchases.slice(startIdx, endIdx);

       if (append) {
         setPurchases(prev => [...prev, ...pagePurchases]);
       } else {
         setPurchases(pagePurchases);
       }

       const uniqueVendors = Array.from(new Set(allPurchases.map((p: Purchase) => p.vendor).filter(Boolean)));
       setVendors(uniqueVendors as string[]);

       setHasMore(endIdx < allPurchases.length);
       setPage(pageNum);
     } catch (e) {
       setError('Failed to fetch purchases');
     } finally {
       if (pageNum === 1) {
         setLoading(false);
       } else {
         setLoadingMore(false);
       }
     }
   };

   const handleLoadMore = () => {
     fetchPurchases(page + 1, true);
   };

  const handleDateRangeChange = async (startDate: string | null, endDate: string | null) => {
    setDateRangeStart(startDate);
    setDateRangeEnd(endDate);

    if (startDate && endDate) {
      setLoading(true);
      try {
        const res = await api.get(`/purchases/by-date-range?startDate=${startDate}&endDate=${endDate}`);
        setPurchases(res.data);
      } catch (e) {
        setSnackbar({open: true, message: 'Failed to fetch purchases by date range', severity: 'error'});
      } finally {
        setLoading(false);
      }
    } else {
      fetchPurchases();
    }
  };

  const fetchProducts = async () => {
    try {
      const res = await api.get('/products');
      setProducts(res.data.map((i: any) => ({ productId: i.productId, productDescription: i.productDescription })));
    } catch {}
  };

  const filteredAndSortedPurchases = useMemo(() => {
    let result = [...purchases];

    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(purchase =>
        purchase.orderReferenceNo.toLowerCase().includes(query) ||
        purchase.vendor.toLowerCase().includes(query) ||
        products.find(i => i.productId === purchase.productId)?.productDescription.toLowerCase().includes(query)
      );
    }

    if (filterVendor) {
      result = result.filter(purchase => purchase.vendor === filterVendor);
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
  }, [purchases, searchQuery, filterVendor, sortField, sortOrder, products]);

  useEffect(() => { fetchPurchases(); fetchProducts(); }, []);

  const handleOpenDialog = (purchase?: Purchase) => {
    if (purchase) {
      setEditPurchase(purchase);
      setForm({ ...purchase });
    } else {
      setEditPurchase(null);
      setForm(emptyPurchase);
    }
    setOpenDialog(true);
  };
  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditPurchase(null);
    setForm(emptyPurchase);
  };
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };
  const handleSave = async () => {
    try {
      if (editPurchase) {
        await api.put(`/purchases/${editPurchase.purchaseId}`, form);
        setSnackbar({open: true, message: 'Purchase updated', severity: 'success'});
      } else {
        await api.post('/purchases', form);
        setSnackbar({open: true, message: 'Purchase created', severity: 'success'});
      }
      fetchPurchases();
      handleCloseDialog();
    } catch (e) {
      setSnackbar({open: true, message: 'Failed to save purchase', severity: 'error'});
    }
  };
  const handleDelete = async (purchaseId: number) => {
    if (!window.confirm('Delete this purchase?')) return;
    try {
      await api.delete(`/purchases/${purchaseId}`);
      setSnackbar({open: true, message: 'Purchase deleted', severity: 'success'});
      fetchPurchases();
    } catch (e) {
      setSnackbar({open: true, message: 'Failed to delete purchase', severity: 'error'});
    }
  };

  return (
    <Box p={2}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" sx={{ fontWeight: 700 }}>Purchases</Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="contained"
            color="secondary"
            startIcon={<BarChartIcon />}
            onClick={() => navigate('/purchases/summary')}
          >
            View Summary
          </Button>
          <Button
            variant="outlined"
            color="primary"
            startIcon={<UploadFileIcon />}
            onClick={() => setOpenUploadDialog(true)}
          >
            Bulk Upload
          </Button>
          <Button
            variant="contained"
            color="primary"
            startIcon={<AddIcon />}
            onClick={() => handleOpenDialog()}
          >
            Add Purchase
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <>
        {/* Date Range Filter */}
        <DateRangeFilter onFilterChange={handleDateRangeChange} />

        {/* Purchases View */}

      <Paper sx={{ p: 2, mb: 3, backgroundColor: '#f8f9fa', borderRadius: 2 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={4}>
            <TextField
              fullWidth
              placeholder="Search by order ref, vendor or product..."
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
              <Select value={filterVendor} onChange={(e) => setFilterVendor(e.target.value)} displayEmpty>
                <MenuItem value="">All Vendors</MenuItem>
                {vendors.map(vend => <MenuItem key={vend} value={vend}>{vend}</MenuItem>)}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <FormControl fullWidth size="small">
              <Select value={sortField} onChange={(e) => setSortField(e.target.value as SortField)}>
                <MenuItem value="purchaseId">Sort by ID</MenuItem>
                <MenuItem value="orderReferenceNo">Sort by Order Ref</MenuItem>
                <MenuItem value="vendor">Sort by Vendor</MenuItem>
                <MenuItem value="orderDate">Sort by Date</MenuItem>
                <MenuItem value="unitsOrdered">Sort by Units</MenuItem>
                <MenuItem value="totalPurchaseAmount">Sort by Amount</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <FormControl fullWidth size="small">
              <Select value={sortOrder} onChange={(e) => setSortOrder(e.target.value as SortOrder)}>
                <MenuItem value="asc">Ascending</MenuItem>
                <MenuItem value="desc">Descending</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <Button variant="outlined" fullWidth onClick={() => {
                setSearchQuery(''); setFilterVendor(''); setSortField('purchaseId'); setSortOrder('desc');
              }}>Reset</Button>
          </Grid>
        </Grid>
      </Paper>

      <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
        <Table sx={{ minWidth: 1000 }}>
          <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 600 }}>ID</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Order Ref</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Product</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Date</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Vendor</TableCell>
              <TableCell sx={{ fontWeight: 600 }} align="right">Units</TableCell>
              <TableCell sx={{ fontWeight: 600 }} align="right">Cost/Unit</TableCell>
              <TableCell sx={{ fontWeight: 600 }} align="right">Total</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Location</TableCell>
              <TableCell sx={{ fontWeight: 600 }} align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={10} align="center" sx={{ py: 3 }}>Loading...</TableCell></TableRow>
            ) : filteredAndSortedPurchases.length === 0 ? (
              <TableRow><TableCell colSpan={10} align="center" sx={{ py: 3, color: 'text.secondary' }}>No purchases found</TableCell></TableRow>
            ) : (
              filteredAndSortedPurchases.map(p => (
                <TableRow key={p.purchaseId} hover>
                  <TableCell>{p.purchaseId}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{p.orderReferenceNo}</TableCell>
                  <TableCell>{products.find(i => i.productId === p.productId)?.productDescription || p.productId}</TableCell>
                  <TableCell>
                    <Typography variant="body2">{p.orderDate}</Typography>
                    <Typography variant="caption" color="textSecondary">Exp: {p.expectedDeliveryDate}</Typography>
                  </TableCell>
                  <TableCell><Chip label={p.vendor} size="small" sx={{ backgroundColor: '#e8f5e9', color: '#2e7d32' }} /></TableCell>
                  <TableCell align="right">{p.unitsOrdered}</TableCell>
                  <TableCell align="right">${p.costPerUnit.toFixed(2)}</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 600 }}>${p.totalPurchaseAmount.toFixed(2)}</TableCell>
                  <TableCell>
                    <Typography variant="caption" display="block">Aisle: {p.aisleNumber}</Typography>
                    <Typography variant="caption" display="block">Bay: {p.bayNumber}</Typography>
                  </TableCell>
                  <TableCell align="center">
                    <Tooltip title="Edit">
                      <IconButton size="small" onClick={() => handleOpenDialog(p)} color="primary"><EditIcon fontSize="small" /></IconButton>
                    </Tooltip>
                    <Tooltip title="Delete">
                      <IconButton size="small" onClick={() => handleDelete(p.purchaseId)} color="error"><DeleteIcon fontSize="small" /></IconButton>
                    </Tooltip>
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
          Showing {purchases.length} purchases
        </Typography>
      </Box>

      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{editPurchase ? 'Edit Purchase' : 'Add Purchase'}</DialogTitle>
        <DialogContent dividers>
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid item xs={12} md={6}>
              <TextField label="Order Ref No" name="orderReferenceNo" value={form.orderReferenceNo} onChange={handleChange} fullWidth required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField select label="Product" name="productId" value={form.productId} onChange={handleChange} fullWidth required>
                {products.map(i => <MenuItem key={i.productId} value={i.productId}>{i.productDescription}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField label="Order Date" name="orderDate" type="date" value={form.orderDate} onChange={handleChange} fullWidth InputLabelProps={{ shrink: true }} />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField label="Expected Delivery" name="expectedDeliveryDate" type="date" value={form.expectedDeliveryDate} onChange={handleChange} fullWidth InputLabelProps={{ shrink: true }} />
            </Grid>
            <Grid item xs={12} md={12}>
              <TextField label="Vendor" name="vendor" value={form.vendor} onChange={handleChange} fullWidth />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField label="Units Ordered" name="unitsOrdered" type="number" value={form.unitsOrdered} onChange={handleChange} fullWidth />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField label="Cost Per Unit ($)" name="costPerUnit" type="number" value={form.costPerUnit} onChange={handleChange} fullWidth />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField label="Aisle Number" name="aisleNumber" value={form.aisleNumber} onChange={handleChange} fullWidth />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField label="Bay Number" name="bayNumber" value={form.bayNumber} onChange={handleChange} fullWidth />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={handleCloseDialog} color="inherit">Cancel</Button>
          <Button onClick={handleSave} variant="contained" disabled={!form.orderReferenceNo || !form.productId}>Save</Button>
        </DialogActions>
      </Dialog>
      <Snackbar open={snackbar.open} autoHideDuration={3000} onClose={() => setSnackbar(s => ({...s, open: false}))}>
        <Alert severity={snackbar.severity} variant="filled">{snackbar.message}</Alert>
      </Snackbar>

      <FileUploadDialog
        open={openUploadDialog}
        onClose={() => setOpenUploadDialog(false)}
        title="Bulk Upload Purchases"
        uploadEndpoint="/purchases/upload"
        templateEndpoint="/purchases/template"
        onSuccess={() => {
          fetchPurchases();
          setSnackbar({ open: true, message: 'Purchases uploaded successfully!', severity: 'success' });
        }}
      />
      </>
    </Box>
  );
};

export default Purchases;
