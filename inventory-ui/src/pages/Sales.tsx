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

interface Sale {
  saleId: number;
  invoiceNo: string;
  stockId: number;
  customerName: string;
  unitsSold: number;
  purchasedPrice: number;
  salePrice: number;
  totalSalesAmount: number;
  salesCommission: number;
  shippingCost: number;
  handlingCost: number;
  profit: number;
  transactionDate: string;
}

type SortField = 'saleId' | 'invoiceNo' | 'customerName' | 'transactionDate' | 'unitsSold' | 'totalSalesAmount' | 'profit';
type SortOrder = 'asc' | 'desc';

const emptySale: Omit<Sale, 'saleId' | 'totalSalesAmount' | 'profit'> = {
  invoiceNo: '',
  stockId: 0,
  customerName: '',
  unitsSold: 0,
  purchasedPrice: 0,
  salePrice: 0,
  salesCommission: 0,
  shippingCost: 0,
  handlingCost: 0,
  transactionDate: '',
};

const Sales: React.FC = () => {
  const navigate = useNavigate();
  const [sales, setSales] = useState<Sale[]>([]);
  const [products, setProducts] = useState<{productId: number, productDescription: string}[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [editSale, setEditSale] = useState<Sale | null>(null);
  const [form, setForm] = useState<Omit<Sale, 'saleId' | 'totalSalesAmount' | 'profit'>>(emptySale);
  const [snackbar, setSnackbar] = useState<{open: boolean, message: string, severity: 'success'|'error'}>({open: false, message: '', severity: 'success'});
  const [searchQuery, setSearchQuery] = useState('');
  const [filterCustomer, setFilterCustomer] = useState('');
  const [sortField, setSortField] = useState<SortField>('saleId');
  const [sortOrder, setSortOrder] = useState<SortOrder>('desc');
  const [customers, setCustomers] = useState<string[]>([]);
  const [openUploadDialog, setOpenUploadDialog] = useState(false);
  const [dateRangeStart, setDateRangeStart] = useState<string | null>(null);
  const [dateRangeEnd, setDateRangeEnd] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const pageSize = 20;

  const fetchSales = async (pageNum: number = 1, append: boolean = false) => {
    if (pageNum === 1) {
      setLoading(true);
    } else {
      setLoadingMore(true);
    }
    try {
      const res = await api.get('/sales');
      const allSales = res.data;
      const startIdx = (pageNum - 1) * pageSize;
      const endIdx = startIdx + pageSize;
      const pageSales = allSales.slice(startIdx, endIdx);

      if (append) {
        setSales(prev => [...prev, ...pageSales]);
      } else {
        setSales(pageSales);
      }

      const uniqueCustomers = Array.from(new Set(allSales.map((s: Sale) => s.customerName).filter(Boolean)));
      setCustomers(uniqueCustomers as string[]);

      setHasMore(endIdx < allSales.length);
      setPage(pageNum);
    } catch (e) {
      setError('Failed to fetch sales');
    } finally {
      if (pageNum === 1) {
        setLoading(false);
      } else {
        setLoadingMore(false);
      }
    }
  };

  const handleLoadMore = () => {
    fetchSales(page + 1, true);
  };

  const handleDateRangeChange = async (startDate: string | null, endDate: string | null) => {
    setDateRangeStart(startDate);
    setDateRangeEnd(endDate);

    if (startDate && endDate) {
      setLoading(true);
      try {
        const res = await api.get(`/sales/by-date-range?startDate=${startDate}&endDate=${endDate}`);
        setSales(res.data);
      } catch (e) {
        setSnackbar({open: true, message: 'Failed to fetch sales by date range', severity: 'error'});
      } finally {
        setLoading(false);
      }
    } else {
      fetchSales();
    }
  };

  const fetchProducts = async () => {
    try {
      const res = await api.get('/stocks');
      setProducts(res.data.map((i: any) => ({ productId: i.productId, productDescription: i.productDescription })));
    } catch {}
  };

  const filteredAndSortedSales = useMemo(() => {
    let result = [...sales];

    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(sale =>
        sale.invoiceNo.toLowerCase().includes(query) ||
        sale.customerName.toLowerCase().includes(query) ||
        products.find(i => i.productId === sale.stockId)?.productDescription.toLowerCase().includes(query)
      );
    }

    if (filterCustomer) {
      result = result.filter(sale => sale.customerName === filterCustomer);
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
  }, [sales, searchQuery, filterCustomer, sortField, sortOrder, products]);

  useEffect(() => {
    fetchSales();
    fetchProducts();
  }, []);

  const handleOpenDialog = (sale?: Sale) => {
    if (sale) {
      setEditSale(sale);
      const { saleId, totalSalesAmount, profit, ...rest } = sale;
      setForm(rest);
    } else {
      setEditSale(null);
      setForm(emptySale);
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditSale(null);
    setForm(emptySale);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSave = async () => {
    try {
      if (editSale) {
        await api.put(`/sales/${editSale.saleId}`, form);
        setSnackbar({open: true, message: 'Sale updated', severity: 'success'});
      } else {
        await api.post('/sales', form);
        setSnackbar({open: true, message: 'Sale created', severity: 'success'});
      }
      fetchSales();
      handleCloseDialog();
    } catch (e) {
      setSnackbar({open: true, message: 'Failed to save sale', severity: 'error'});
    }
  };

  const handleDelete = async (saleId: number) => {
    if (!window.confirm('Delete this sale?')) return;
    try {
      await api.delete(`/sales/${saleId}`);
      setSnackbar({open: true, message: 'Sale deleted', severity: 'success'});
      fetchSales();
    } catch (e) {
      setSnackbar({open: true, message: 'Failed to delete sale', severity: 'error'});
    }
  };

  return (
    <Box p={2}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" sx={{ fontWeight: 700 }}>Sales</Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="contained"
            color="secondary"
            startIcon={<BarChartIcon />}
            onClick={() => navigate('/sales/summary')}
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
            Add Sale
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <>
        {/* Date Range Filter */}
        <DateRangeFilter onFilterChange={handleDateRangeChange} />

        {/* Sales View */}

        <Paper sx={{ p: 2, mb: 3, backgroundColor: '#f8f9fa', borderRadius: 2 }}>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} sm={6} md={4}>
              <TextField
                fullWidth
                placeholder="Search by invoice, customer or product..."
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
                <Select value={filterCustomer} onChange={(e) => setFilterCustomer(e.target.value)} displayEmpty>
                  <MenuItem value="">All Customers</MenuItem>
                  {customers.map(cust => <MenuItem key={cust} value={cust}>{cust}</MenuItem>)}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <FormControl fullWidth size="small">
                <Select value={sortField} onChange={(e) => setSortField(e.target.value as SortField)}>
                  <MenuItem value="saleId">Sort by ID</MenuItem>
                  <MenuItem value="invoiceNo">Sort by Invoice</MenuItem>
                  <MenuItem value="customerName">Sort by Customer</MenuItem>
                  <MenuItem value="transactionDate">Sort by Date</MenuItem>
                  <MenuItem value="unitsSold">Sort by Units</MenuItem>
                  <MenuItem value="totalSalesAmount">Sort by Amount</MenuItem>
                  <MenuItem value="profit">Sort by Profit</MenuItem>
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
                  setSearchQuery(''); setFilterCustomer(''); setSortField('saleId'); setSortOrder('desc');
                }}>Reset</Button>
            </Grid>
          </Grid>
        </Paper>

        <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
          <Table sx={{ minWidth: 1200 }}>
            <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 600 }}>ID</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Invoice</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Product</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Date</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Customer</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Units</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Purchased Price</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Sale Price</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Total Sales</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Commission</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Shipping</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Handling</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Profit</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow><TableCell colSpan={14} align="center" sx={{ py: 3 }}>Loading...</TableCell></TableRow>
              ) : filteredAndSortedSales.length === 0 ? (
                <TableRow><TableCell colSpan={14} align="center" sx={{ py: 3, color: 'text.secondary' }}>No sales found</TableCell></TableRow>
              ) : (
                filteredAndSortedSales.map(s => (
                  <TableRow key={s.saleId} hover>
                    <TableCell>{s.saleId}</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>{s.invoiceNo}</TableCell>
                    <TableCell>{products.find(i => i.productId === s.stockId)?.productDescription || s.stockId}</TableCell>
                    <TableCell>{s.transactionDate}</TableCell>
                    <TableCell><Chip label={s.customerName} size="small" sx={{ backgroundColor: '#e3f2fd', color: '#1565c0' }} /></TableCell>
                    <TableCell align="right">{s.unitsSold}</TableCell>
                    <TableCell align="right">${s.purchasedPrice.toFixed(2)}</TableCell>
                    <TableCell align="right">${s.salePrice.toFixed(2)}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 600 }}>${s.totalSalesAmount.toFixed(2)}</TableCell>
                    <TableCell align="right">${s.salesCommission.toFixed(2)}</TableCell>
                    <TableCell align="right">${s.shippingCost.toFixed(2)}</TableCell>
                    <TableCell align="right">${s.handlingCost.toFixed(2)}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 600, color: s.profit >= 0 ? '#2e7d32' : '#c62828' }}>
                      ${s.profit.toFixed(2)}
                    </TableCell>
                    <TableCell align="center">
                      <Tooltip title="Edit">
                        <IconButton size="small" onClick={() => handleOpenDialog(s)} color="primary"><EditIcon fontSize="small" /></IconButton>
                      </Tooltip>
                      <Tooltip title="Delete">
                        <IconButton size="small" onClick={() => handleDelete(s.saleId)} color="error"><DeleteIcon fontSize="small" /></IconButton>
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
            Showing {sales.length} sales
          </Typography>
        </Box>

        <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
          <DialogTitle sx={{ fontWeight: 700 }}>{editSale ? 'Edit Sale' : 'Add Sale'}</DialogTitle>
          <DialogContent dividers>
            <Grid container spacing={2} sx={{ mt: 0.5 }}>
              <Grid item xs={12} md={6}>
                <TextField label="Invoice No" name="invoiceNo" value={form.invoiceNo} onChange={handleChange} fullWidth required />
              </Grid>
               <Grid item xs={12} md={6}>
                 <TextField select label="Product" name="stockId" value={form.stockId} onChange={handleChange} fullWidth required>
                   {products.map(i => <MenuItem key={i.productId} value={i.productId}>{i.productDescription}</MenuItem>)}
                 </TextField>
               </Grid>
              <Grid item xs={12} md={6}>
                <TextField label="Customer Name" name="customerName" value={form.customerName} onChange={handleChange} fullWidth required />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField label="Transaction Date" name="transactionDate" type="date" value={form.transactionDate} onChange={handleChange} fullWidth InputLabelProps={{ shrink: true }} />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="Units Sold" name="unitsSold" type="number" value={form.unitsSold} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="Purchased Price ($)" name="purchasedPrice" type="number" inputProps={{ step: "0.01" }} value={form.purchasedPrice} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="Sale Price ($)" name="salePrice" type="number" inputProps={{ step: "0.01" }} value={form.salePrice} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="Sales Commission ($)" name="salesCommission" type="number" inputProps={{ step: "0.01" }} value={form.salesCommission} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="Shipping Cost ($)" name="shippingCost" type="number" inputProps={{ step: "0.01" }} value={form.shippingCost} onChange={handleChange} fullWidth />
              </Grid>
              <Grid item xs={12} md={4}>
                <TextField label="Handling Cost ($)" name="handlingCost" type="number" inputProps={{ step: "0.01" }} value={form.handlingCost} onChange={handleChange} fullWidth />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={handleCloseDialog} color="inherit">Cancel</Button>
            <Button onClick={handleSave} variant="contained" disabled={!form.invoiceNo || !form.stockId || !form.customerName}>Save</Button>
          </DialogActions>
        </Dialog>

        <Snackbar open={snackbar.open} autoHideDuration={3000} onClose={() => setSnackbar(s => ({...s, open: false}))}>
          <Alert severity={snackbar.severity} variant="filled">{snackbar.message}</Alert>
        </Snackbar>

        <FileUploadDialog
          open={openUploadDialog}
          onClose={() => setOpenUploadDialog(false)}
          title="Bulk Upload Sales"
          uploadEndpoint="/sales/upload"
          templateEndpoint="/sales/template"
          onSuccess={() => {
            fetchSales();
            setSnackbar({ open: true, message: 'Sales uploaded successfully!', severity: 'success' });
          }}
        />
      </>
    </Box>
  );
};

export default Sales;
