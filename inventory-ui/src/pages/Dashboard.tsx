import React, { useEffect, useState } from 'react';
import { Box, Grid, Paper, Typography } from '@mui/material';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LabelList
} from 'recharts';
import api from '../api';

// Types for dashboard data
interface QuarterData {
  quarter: string;
  value: number;
}
interface TopProduct {
  quarter: string;
  items: string[];
}
interface CustomerRevenue {
  customer: string;
  value: number;
}

const Dashboard: React.FC = () => {
  // State for all dashboard metrics
  const [inventoryValue, setInventoryValue] = useState<number>(0);
  const [topProducts, setTopProducts] = useState<TopProduct[]>([]);
  const [salesRevenue, setSalesRevenue] = useState<QuarterData[]>([]);
  const [unitsSold, setUnitsSold] = useState<QuarterData[]>([]);
  const [spentInventory, setSpentInventory] = useState<QuarterData[]>([]);
  const [stockReceived, setStockReceived] = useState<QuarterData[]>([]);
  const [customerRevenue, setCustomerRevenue] = useState<Record<string, CustomerRevenue[]>>({});

  useEffect(() => {
    // Fetch all dashboard data from API with error handling
    api.get('/stocks/total-value').then(r => setInventoryValue(r.data || 0)).catch(() => setInventoryValue(0));
    api.get('/sales/top5-products-per-quarter').then(r => setTopProducts(r.data || [])).catch(() => setTopProducts([]));
    api.get('/sales/revenue-by-quarter').then(r => setSalesRevenue(r.data || [])).catch(() => setSalesRevenue([]));
    api.get('/sales/units-sold-by-quarter').then(r => setUnitsSold(r.data || [])).catch(() => setUnitsSold([]));
    api.get('/purchases/total-spent-by-quarter').then(r => setSpentInventory(r.data || [])).catch(() => setSpentInventory([]));
    api.get('/purchases/stock-received-by-quarter').then(r => setStockReceived(r.data || [])).catch(() => setStockReceived([]));
    api.get('/sales/revenue-by-customer-per-quarter').then(r => setCustomerRevenue(r.data || {})).catch(() => setCustomerRevenue({}));
  }, []);

  return (
    <Box sx={{ py: 3 }}>
      {/* Page Title */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" sx={{ fontWeight: 700, color: '#1a237e', mb: 1 }}>
          Dashboard
        </Typography>
        <Typography variant="subtitle1" sx={{ color: '#546e7a' }}>
          Welcome back! Here's your inventory overview.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Total Inventory Value - Hero Card */}
        <Grid item xs={12} md={4}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              borderRadius: 2,
              boxShadow: '0 8px 24px rgba(102, 126, 234, 0.3)',
              transition: 'transform 0.3s ease, box-shadow 0.3s ease',
              '&:hover': {
                transform: 'translateY(-4px)',
                boxShadow: '0 12px 32px rgba(102, 126, 234, 0.4)'
              }
            }}
          >
            <Typography variant="subtitle1" sx={{ fontWeight: 600, opacity: 0.9, mb: 1 }}>
              Total Inventory Value
            </Typography>
            <Typography variant="h3" sx={{ fontWeight: 700, mb: 2 }}>
              ${inventoryValue.toLocaleString(undefined, { minimumFractionDigits: 2 })}
            </Typography>
            <Typography variant="caption" sx={{ opacity: 0.8 }}>
              Current valuation across all products
            </Typography>
          </Paper>
        </Grid>

        {/* Top 5 Products Sold Per Quarter */}
        <Grid item xs={12} md={4}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
              color: 'white',
              borderRadius: 2,
              boxShadow: '0 8px 24px rgba(245, 87, 108, 0.3)',
              height: '100%',
              transition: 'transform 0.3s ease, box-shadow 0.3s ease',
              '&:hover': {
                transform: 'translateY(-4px)',
                boxShadow: '0 12px 32px rgba(245, 87, 108, 0.4)'
              }
            }}
          >
            <Typography variant="subtitle1" sx={{ fontWeight: 600, opacity: 0.9, mb: 2 }}>
              Top 5 Products Sold Per Quarter
            </Typography>
            <Box sx={{ maxHeight: 200, overflowY: 'auto' }}>
              {topProducts.map((q) => (
                <Box key={q.quarter} mb={1.5} pb={1.5} sx={{ borderBottom: '1px solid rgba(255,255,255,0.2)', '&:last-child': { borderBottom: 'none' } }}>
                  <Typography variant="caption" sx={{ fontWeight: 700, opacity: 0.8, display: 'block', mb: 0.5 }}>
                    {q.quarter}
                  </Typography>
                  <ol style={{ margin: 0, paddingLeft: 16, fontSize: '12px' }}>
                    {q.items.slice(0, 3).map((item, i) => (
                      <li key={i} style={{ marginBottom: '4px', opacity: 0.95 }}>
                        {item}
                      </li>
                    ))}
                  </ol>
                </Box>
              ))}
            </Box>
          </Paper>
        </Grid>

        {/* Key Metrics Summary */}
        <Grid item xs={12} md={4}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
              color: 'white',
              borderRadius: 2,
              boxShadow: '0 8px 24px rgba(79, 172, 254, 0.3)',
              height: '100%',
              transition: 'transform 0.3s ease, box-shadow 0.3s ease',
              '&:hover': {
                transform: 'translateY(-4px)',
                boxShadow: '0 12px 32px rgba(79, 172, 254, 0.4)'
              }
            }}
          >
            <Typography variant="subtitle1" sx={{ fontWeight: 600, opacity: 0.9, mb: 2 }}>
              Key Metrics Summary
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Box>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>Total Units Sold</Typography>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  {unitsSold.reduce((acc, q) => acc + q.value, 0).toLocaleString()}
                </Typography>
              </Box>
              <Box>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>Total Revenue</Typography>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  ${salesRevenue.reduce((acc, q) => acc + q.value, 0).toLocaleString()}
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Grid>

        {/* Total Sales Revenue by Quarter */}
        <Grid item xs={12} md={8}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              borderRadius: 2,
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
              backgroundColor: '#ffffff',
              border: '1px solid #e8eef7'
            }}
          >
            <Typography variant="h6" sx={{ fontWeight: 700, color: '#1a237e', mb: 2 }}>
              Sales Revenue by Quarter
            </Typography>
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={salesRevenue} layout="vertical" margin={{ top: 10, right: 30, left: 80, bottom: 10 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                <XAxis type="number" tickFormatter={v => `$${v}`} stroke="#666" />
                <YAxis type="category" dataKey="quarter" stroke="#666" />
                <Tooltip 
                  formatter={(v: number) => `$${v}`}
                  contentStyle={{ backgroundColor: '#f5f5f5', border: '1px solid #ddd', borderRadius: '6px' }}
                />
                <Bar dataKey="value" fill="#667eea" radius={[0, 8, 8, 0]}>
                  <LabelList dataKey="value" position="right" formatter={(v: number) => `$${v}`} fill="#667eea" fontWeight={600} />
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>

        {/* Total Units Sold by Quarter */}
        <Grid item xs={12} md={4}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              borderRadius: 2,
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
              backgroundColor: '#ffffff',
              border: '1px solid #e8eef7'
            }}
          >
            <Typography variant="h6" sx={{ fontWeight: 700, color: '#1a237e', mb: 2 }}>
              Units Sold by Quarter
            </Typography>
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={unitsSold} layout="vertical" margin={{ top: 10, right: 30, left: 50, bottom: 10 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                <XAxis type="number" stroke="#666" />
                <YAxis type="category" dataKey="quarter" stroke="#666" />
                <Tooltip contentStyle={{ backgroundColor: '#f5f5f5', border: '1px solid #ddd', borderRadius: '6px' }} />
                <Bar dataKey="value" fill="#f5576c" radius={[0, 8, 8, 0]}>
                  <LabelList dataKey="value" position="right" fontWeight={600} fill="#f5576c" />
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>

        {/* Total Spent on Inventory */}
        <Grid item xs={12} md={6}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              borderRadius: 2,
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
              backgroundColor: '#ffffff',
              border: '1px solid #e8eef7'
            }}
          >
            <Typography variant="h6" sx={{ fontWeight: 700, color: '#1a237e', mb: 2 }}>
              Total Spent on Inventory by Quarter
            </Typography>
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={spentInventory} layout="vertical" margin={{ top: 10, right: 30, left: 50, bottom: 10 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                <XAxis type="number" tickFormatter={v => `$${v}`} stroke="#666" />
                <YAxis type="category" dataKey="quarter" stroke="#666" />
                <Tooltip 
                  formatter={(v: number) => `$${v}`}
                  contentStyle={{ backgroundColor: '#f5f5f5', border: '1px solid #ddd', borderRadius: '6px' }}
                />
                <Bar dataKey="value" fill="#00f2fe" radius={[0, 8, 8, 0]}>
                  <LabelList dataKey="value" position="right" formatter={(v: number) => `$${v}`} fill="#00f2fe" fontWeight={600} />
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>

        {/* Total Stock Received by Quarter */}
        <Grid item xs={12} md={6}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              borderRadius: 2,
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
              backgroundColor: '#ffffff',
              border: '1px solid #e8eef7'
            }}
          >
            <Typography variant="h6" sx={{ fontWeight: 700, color: '#1a237e', mb: 2 }}>
              Stock Received by Quarter
            </Typography>
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={stockReceived} layout="vertical" margin={{ top: 10, right: 30, left: 50, bottom: 10 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                <XAxis type="number" stroke="#666" />
                <YAxis type="category" dataKey="quarter" stroke="#666" />
                <Tooltip contentStyle={{ backgroundColor: '#f5f5f5', border: '1px solid #ddd', borderRadius: '6px' }} />
                <Bar dataKey="value" fill="#4caf50" radius={[0, 8, 8, 0]}>
                  <LabelList dataKey="value" position="right" fontWeight={600} fill="#4caf50" />
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>

        {/* Revenue by Customer per Quarter */}
        {['Q1', 'Q2', 'Q3', 'Q4'].map((q, idx) => (
          <Grid item xs={12} md={6} key={q}>
            <Paper
              elevation={0}
              sx={{
                p: 3,
                borderRadius: 2,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                backgroundColor: '#ffffff',
                border: '1px solid #e8eef7'
              }}
            >
              <Typography variant="h6" sx={{ fontWeight: 700, color: '#1a237e', mb: 2 }}>
                {q} Revenue by Customer
              </Typography>
              <ResponsiveContainer width="100%" height={240}>
                <BarChart data={customerRevenue[q] || []} margin={{ top: 10, right: 30, left: 30, bottom: 50 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                  <XAxis dataKey="customer" angle={-45} textAnchor="end" height={80} stroke="#666" />
                  <YAxis stroke="#666" />
                  <Tooltip 
                    formatter={(v: number) => `$${v}`}
                    contentStyle={{ backgroundColor: '#f5f5f5', border: '1px solid #ddd', borderRadius: '6px' }}
                  />
                  <Bar dataKey="value" fill={['#667eea', '#f5576c', '#00f2fe', '#4caf50'][idx]} radius={[8, 8, 0, 0]}>
                    <LabelList dataKey="value" position="top" formatter={(v: number) => `$${v}`} fill="#333" fontWeight={600} />
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default Dashboard;
