import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Paper, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Grid, Card, CardContent, Tabs, Tab
} from '@mui/material';
import api from '../api';
import DateRangeFilter from '../components/DateRangeFilter';

interface ProductDetail {
  productId: number;
  productDescription: string;
  sku: string;
  category: string;
  currentStockLevel: number;
  unitCost: number;
  retailPrice: number;
  inventoryValue: number;
}

interface QuarterlyData {
  quarter: string;
  totalValue: number;
  products: ProductDetail[];
}

const ProductDetailsSummary: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [quarterlyData, setQuarterlyData] = useState<QuarterlyData[]>([]);
  const [loading, setLoading] = useState(false);
  const [totalValue, setTotalValue] = useState(0);
  const [dateRangeStart, setDateRangeStart] = useState<string | null>(null);
  const [dateRangeEnd, setDateRangeEnd] = useState<string | null>(null);

  const handleDateRangeChange = async (startDate: string | null, endDate: string | null) => {
    setDateRangeStart(startDate);
    setDateRangeEnd(endDate);

    if (startDate && endDate) {
      setLoading(true);
      try {
        const res = await api.get(`/stocks/by-date-range?startDate=${startDate}&endDate=${endDate}`);
        setQuarterlyData([{
          quarter: 'Filtered Results',
          totalValue: res.data.reduce((sum: number, p: any) => sum + (p.inventoryValue || 0), 0),
          products: res.data
        }]);
      } catch (error) {
        console.error('Error fetching filtered data:', error);
      } finally {
        setLoading(false);
      }
    } else {
      fetchProductData();
    }
  };

  useEffect(() => {
    fetchProductData();
  }, []);

  const fetchProductData = async () => {
    setLoading(true);
    try {
       const [productsRes, totalRes] = await Promise.all([
         api.get('/stocks'),
         api.get('/stocks/total-value')
       ]);

      setTotalValue(totalRes.data);

      // Group products by quarter (simulated)
      const quarters = ['Quarter 1', 'Quarter 2', 'Quarter 3', 'Quarter 4'];
      const data = quarters.map((q, idx) => ({
        quarter: q,
        totalValue: totalRes.data / 4,
        products: productsRes.data
      }));

      setQuarterlyData(data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box p={3}>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 3 }}>
        Product Details Summary
      </Typography>

      {/* Date Range Filter */}
      <DateRangeFilter onFilterChange={handleDateRangeChange} />

      {/* Summary Cards */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#f0f4c3' }}>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Inventory Value
              </Typography>
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                ${totalValue.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        {quarterlyData.map((q, idx) => (
          <Grid item xs={12} sm={6} md={3} key={idx}>
            <Card sx={{ backgroundColor: '#c8e6c9' }}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  {q.quarter} Value
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 700 }}>
                  ${q.totalValue.toFixed(2)}
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

      {/* Products Table */}
      {quarterlyData[tabValue] && (
        <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
          <Table>
            <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 600 }}>Item Description</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>SKU</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Category</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Stock Level</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Unit Cost</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Retail Price</TableCell>
                <TableCell sx={{ fontWeight: 600 }} align="right">Inventory Value</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {quarterlyData[tabValue].products.length > 0 ? (
                quarterlyData[tabValue].products.map((product) => (
                  <TableRow key={product.productId} hover>
                    <TableCell>{product.productDescription}</TableCell>
                    <TableCell>{product.sku}</TableCell>
                    <TableCell>{product.category}</TableCell>
                    <TableCell align="right">{product.currentStockLevel}</TableCell>
                    <TableCell align="right">${product.unitCost.toFixed(2)}</TableCell>
                    <TableCell align="right">${product.retailPrice.toFixed(2)}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 600, backgroundColor: '#fff9c4' }}>
                      ${product.inventoryValue.toFixed(2)}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} align="center" sx={{ py: 3 }}>
                    No products found
                  </TableCell>
                </TableRow>
              )}
              <TableRow sx={{ backgroundColor: '#e8f5e9', fontWeight: 600 }}>
                <TableCell colSpan={6} sx={{ fontWeight: 700 }}>
                  Total Inventory:
                </TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>
                  ${quarterlyData[tabValue].products.reduce((sum, p) => sum + p.inventoryValue, 0).toFixed(2)}
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
};

export default ProductDetailsSummary;

