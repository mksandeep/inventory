import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Paper, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Grid, Card, CardContent, Tabs, Tab
} from '@mui/material';
import api from '../api';
import DateRangeFilter from '../components/DateRangeFilter';

interface PurchaseDetail {
  purchaseId: number;
  orderReferenceNo: string;
  productId: number;
  vendor: string;
  unitsOrdered: number;
  costPerUnit: number;
  totalPurchaseAmount: number;
  orderDate: string;
  expectedDeliveryDate: string;
}

interface QuarterlyPurchaseMetrics {
  quarter: string;
  totalSpent: number;
  totalStock: number;
  purchases: PurchaseDetail[];
}

const PurchaseDetailsSummary: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [quarterlyData, setQuarterlyData] = useState<QuarterlyPurchaseMetrics[]>([]);
  const [loading, setLoading] = useState(false);
  const [dateRangeStart, setDateRangeStart] = useState<string | null>(null);
  const [dateRangeEnd, setDateRangeEnd] = useState<string | null>(null);

  const handleDateRangeChange = async (startDate: string | null, endDate: string | null) => {
    setDateRangeStart(startDate);
    setDateRangeEnd(endDate);

    if (startDate && endDate) {
      setLoading(true);
      try {
        const res = await api.get(`/purchases/by-date-range?startDate=${startDate}&endDate=${endDate}`);
        const totalSpent = res.data.reduce((sum: number, p: any) => sum + (p.totalPurchaseAmount || 0), 0);
        const totalStock = res.data.reduce((sum: number, p: any) => sum + (p.unitsOrdered || 0), 0);
        setQuarterlyData([{
          quarter: 'Filtered Results',
          totalSpent: totalSpent,
          totalStock: totalStock,
          purchases: res.data
        }]);
      } catch (error) {
        console.error('Error fetching filtered data:', error);
      } finally {
        setLoading(false);
      }
    } else {
      fetchPurchaseData();
    }
  };

  useEffect(() => {
    fetchPurchaseData();
  }, []);

  const fetchPurchaseData = async () => {
    setLoading(true);
    try {
      const [purchasesRes, spentRes, stockRes] = await Promise.all([
        api.get('/purchases'),
        api.get('/purchases/total-spent-by-quarter'),
        api.get('/purchases/stock-received-by-quarter')
      ]);

      const quarters = ['Quarter 1', 'Quarter 2', 'Quarter 3', 'Quarter 4'];
      const data = quarters.map((q, idx) => ({
        quarter: q,
        totalSpent: spentRes.data[idx]?.spent || 0,
        totalStock: stockRes.data[idx]?.stock || 0,
        purchases: purchasesRes.data.slice(idx * 5, (idx + 1) * 5)
      }));

      setQuarterlyData(data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getTotalSpent = () => quarterlyData.reduce((sum, q) => sum + q.totalSpent, 0);
  const getTotalStock = () => quarterlyData.reduce((sum, q) => sum + q.totalStock, 0);

  return (
    <Box p={3}>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 3 }}>
        Purchase Details Summary
      </Typography>

      {/* Date Range Filter */}
      <DateRangeFilter onFilterChange={handleDateRangeChange} />

      {/* Summary Cards */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#b3e5fc' }}>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Inventory Spent
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                ${getTotalSpent().toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#81d4fa' }}>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Stock Received
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                {getTotalStock()} units
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        {quarterlyData.map((q, idx) => (
          <Grid item xs={12} sm={6} md={4} key={idx}>
            <Card sx={{ backgroundColor: '#b2dfdb' }}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  {q.quarter} Spend
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 700 }}>
                  ${q.totalSpent.toFixed(2)}
                </Typography>
                <Typography variant="caption" color="textSecondary">
                  Stock: {q.totalStock} units
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Tabs for Quarters */}
      <Paper sx={{ mb: 2 }}>
        <Tabs
          value={tabValue}
          onChange={(e, v) => setTabValue(v)}
          sx={{ backgroundColor: '#f5f5f5' }}
        >
          {quarterlyData.map((q, idx) => (
            <Tab key={idx} label={q.quarter} />
          ))}
        </Tabs>
      </Paper>

      {/* Purchases Table */}
      {quarterlyData[tabValue] && (
        <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
          <Table>
            <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 600 }}>Order Reference No.</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Item Description</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Order Date</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Expected Delivery Date</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Vendor</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Units Ordered</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Cost Per Unit</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Total Purchase</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {quarterlyData[tabValue].purchases.length > 0 ? (
                quarterlyData[tabValue].purchases.map((purchase) => (
                  <TableRow key={purchase.purchaseId} hover>
                    <TableCell sx={{ fontWeight: 600 }}>{purchase.orderReferenceNo}</TableCell>
                    <TableCell>Item {purchase.productId}</TableCell>
                    <TableCell>{purchase.orderDate}</TableCell>
                    <TableCell>{purchase.expectedDeliveryDate}</TableCell>
                    <TableCell>{purchase.vendor}</TableCell>
                    <TableCell align="right">{purchase.unitsOrdered}</TableCell>
                    <TableCell align="right">${purchase.costPerUnit.toFixed(2)}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 600, backgroundColor: '#c8e6c9' }}>
                      ${purchase.totalPurchaseAmount.toFixed(2)}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={8} align="center" sx={{ py: 3 }}>
                    No purchase data for this quarter
                  </TableCell>
                </TableRow>
              )}
              <TableRow sx={{ backgroundColor: '#c8e6c9', fontWeight: 600 }}>
                <TableCell colSpan={5} sx={{ fontWeight: 700 }}>
                  Total Purchase Amount:
                </TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>
                  {quarterlyData[tabValue].purchases.reduce((sum, p) => sum + p.unitsOrdered, 0)}
                </TableCell>
                <TableCell colSpan={1}></TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>
                  ${quarterlyData[tabValue].purchases.reduce((sum, p) => sum + p.totalPurchaseAmount, 0).toFixed(2)}
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
};

export default PurchaseDetailsSummary;

