import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Paper, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Grid, Card, CardContent, Tabs, Tab
} from '@mui/material';
import api from '../api';
import DateRangeFilter from '../components/DateRangeFilter';

interface SaleDetail {
  saleId: number;
  invoiceNo: string;
  customerName: string;
  unitsSold: number;
  purchasedPrice: number;
  salePrice: number;
  totalSalesAmount: number;
  profit: number;
  transactionDate: string;
}

interface QuarterlyMetrics {
  quarter: string;
  totalRevenue: number;
  totalUnits: number;
  topProducts: string[];
  sales: SaleDetail[];
}

const SalesDetailsSummary: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [quarterlyData, setQuarterlyData] = useState<QuarterlyMetrics[]>([]);
  const [loading, setLoading] = useState(false);
  const [dateRangeStart, setDateRangeStart] = useState<string | null>(null);
  const [dateRangeEnd, setDateRangeEnd] = useState<string | null>(null);

  const handleDateRangeChange = async (startDate: string | null, endDate: string | null) => {
    setDateRangeStart(startDate);
    setDateRangeEnd(endDate);

    if (startDate && endDate) {
      setLoading(true);
      try {
        const res = await api.get(`/sales/by-date-range?startDate=${startDate}&endDate=${endDate}`);
        const totalRevenue = res.data.reduce((sum: number, s: any) => sum + (s.totalSalesAmount || 0), 0);
        const totalUnits = res.data.reduce((sum: number, s: any) => sum + (s.unitsSold || 0), 0);
        setQuarterlyData([{
          quarter: 'Filtered Results',
          totalRevenue: totalRevenue,
          totalUnits: totalUnits,
          topProducts: [],
          sales: res.data
        }]);
      } catch (error) {
        console.error('Error fetching filtered data:', error);
      } finally {
        setLoading(false);
      }
    } else {
      fetchSalesData();
    }
  };

  useEffect(() => {
    fetchSalesData();
  }, []);

  const fetchSalesData = async () => {
    setLoading(true);
    try {
      const [salesRes, revenueRes, unitsRes, topRes] = await Promise.all([
        api.get('/sales'),
        api.get('/sales/revenue-by-quarter'),
        api.get('/sales/units-sold-by-quarter'),
        api.get('/sales/top5-products-per-quarter')
      ]);

      const quarters = ['Quarter 1', 'Quarter 2', 'Quarter 3', 'Quarter 4'];
      const data = quarters.map((q, idx) => ({
        quarter: q,
        totalRevenue: revenueRes.data[idx]?.revenue || 0,
        totalUnits: unitsRes.data[idx]?.units || 0,
        topProducts: topRes.data[idx]?.products || [],
        sales: salesRes.data.slice(idx * 5, (idx + 1) * 5)
      }));

      setQuarterlyData(data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getTotalRevenue = () => quarterlyData.reduce((sum, q) => sum + q.totalRevenue, 0);

  return (
    <Box p={3}>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 3 }}>
        Sales Details Summary
      </Typography>

      {/* Date Range Filter */}
      <DateRangeFilter onFilterChange={handleDateRangeChange} />

      {/* Summary Cards */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#ffe0b2' }}>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Sales Revenue
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                ${getTotalRevenue().toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        {quarterlyData.map((q, idx) => (
          <Grid item xs={12} sm={6} md={3} key={idx}>
            <Card sx={{ backgroundColor: '#ffccbc' }}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  {q.quarter} Revenue
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 700 }}>
                  ${q.totalRevenue.toFixed(2)}
                </Typography>
                <Typography variant="caption" color="textSecondary">
                  Units: {q.totalUnits}
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

      {/* Top Products */}
      {quarterlyData[tabValue] && quarterlyData[tabValue].topProducts.length > 0 && (
        <Box sx={{ mb: 3, p: 2, backgroundColor: '#fff3e0', borderRadius: 2 }}>
          <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>
            Top 5 Items Sold
          </Typography>
          <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
            {quarterlyData[tabValue].topProducts.map((item, idx) => (
              <Typography
                key={idx}
                sx={{
                  backgroundColor: '#fff59d',
                  px: 2,
                  py: 1,
                  borderRadius: 1,
                  fontWeight: 500
                }}
              >
                Q{tabValue + 1}: {item}
              </Typography>
            ))}
          </Box>
        </Box>
      )}

       {/* Sales Table */}
       {quarterlyData[tabValue] && (
         <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
           <Table>
             <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
               <TableRow>
                 <TableCell sx={{ fontWeight: 600 }}>Invoice No.</TableCell>
                 <TableCell sx={{ fontWeight: 600 }}>Transaction Date</TableCell>
                 <TableCell sx={{ fontWeight: 600 }}>Customer Name</TableCell>
                 <TableCell sx={{ fontWeight: 600 }} align="right">Units Sold</TableCell>
                 <TableCell sx={{ fontWeight: 600 }} align="right">Purchase Price</TableCell>
                 <TableCell sx={{ fontWeight: 600 }} align="right">Sale Price</TableCell>
                 <TableCell sx={{ fontWeight: 600 }} align="right">Total Sales</TableCell>
                 <TableCell sx={{ fontWeight: 600 }} align="right">Profit</TableCell>
               </TableRow>
             </TableHead>
             <TableBody>
               {quarterlyData[tabValue].sales.length > 0 ? (
                 quarterlyData[tabValue].sales.map((sale) => (
                   <TableRow key={sale.saleId} hover>
                     <TableCell sx={{ fontWeight: 600 }}>{sale.invoiceNo}</TableCell>
                     <TableCell>{sale.transactionDate}</TableCell>
                     <TableCell>{sale.customerName}</TableCell>
                     <TableCell align="right">{sale.unitsSold}</TableCell>
                     <TableCell align="right">${sale.purchasedPrice.toFixed(2)}</TableCell>
                     <TableCell align="right">${sale.salePrice.toFixed(2)}</TableCell>
                     <TableCell align="right" sx={{ fontWeight: 600 }}>${sale.totalSalesAmount.toFixed(2)}</TableCell>
                     <TableCell align="right" sx={{ fontWeight: 600, color: sale.profit >= 0 ? '#2e7d32' : '#c62828' }}>
                       ${sale.profit.toFixed(2)}
                     </TableCell>
                   </TableRow>
                 ))
               ) : (
                 <TableRow>
                   <TableCell colSpan={8} align="center" sx={{ py: 3 }}>
                     No sales data for this quarter
                   </TableCell>
                 </TableRow>
               )}
               <TableRow sx={{ backgroundColor: '#ffebee', fontWeight: 600 }}>
                 <TableCell colSpan={3} sx={{ fontWeight: 700 }}>
                   Total for Quarter:
                 </TableCell>
                 <TableCell align="right" sx={{ fontWeight: 700 }}>
                   {quarterlyData[tabValue].sales.reduce((sum, s) => sum + s.unitsSold, 0)}
                 </TableCell>
                 <TableCell colSpan={1}></TableCell>
                 <TableCell colSpan={1}></TableCell>
                 <TableCell align="right" sx={{ fontWeight: 700 }}>
                   ${quarterlyData[tabValue].sales.reduce((sum, s) => sum + s.totalSalesAmount, 0).toFixed(2)}
                 </TableCell>
                 <TableCell align="right" sx={{ fontWeight: 700, color: quarterlyData[tabValue].sales.reduce((sum, s) => sum + s.profit, 0) >= 0 ? '#2e7d32' : '#c62828' }}>
                   ${quarterlyData[tabValue].sales.reduce((sum, s) => sum + s.profit, 0).toFixed(2)}
                 </TableCell>
               </TableRow>
             </TableBody>
           </Table>
         </TableContainer>
       )}
    </Box>
  );
};

export default SalesDetailsSummary;

