import React, { useState } from 'react';
import {
  Box,
  TextField,
  Button,
  Grid,
  Paper
} from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import ClearIcon from '@mui/icons-material/Clear';

interface DateRangeFilterProps {
  onFilterChange: (startDate: string | null, endDate: string | null) => void;
  label?: string;
}

const DateRangeFilter: React.FC<DateRangeFilterProps> = ({ onFilterChange, label = 'Date Range Filter' }) => {
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');

  const handleApplyFilter = () => {
    if (startDate && endDate) {
      onFilterChange(startDate, endDate);
    }
  };

  const handleClearFilter = () => {
    setStartDate('');
    setEndDate('');
    onFilterChange(null, null);
  };

  return (
    <Paper sx={{ p: 2, mb: 2, backgroundColor: '#f5f5f5', borderRadius: 2 }}>
      <Grid container spacing={2} alignItems="center">
        <Grid item xs={12} sm={4}>
          <TextField
            fullWidth
            label="Start Date"
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            size="small"
          />
        </Grid>
        <Grid item xs={12} sm={4}>
          <TextField
            fullWidth
            label="End Date"
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            size="small"
          />
        </Grid>
        <Grid item xs={12} sm={4}>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button
              variant="contained"
              color="primary"
              startIcon={<FilterListIcon />}
              onClick={handleApplyFilter}
              fullWidth
              disabled={!startDate || !endDate}
            >
              Filter
            </Button>
            <Button
              variant="outlined"
              color="secondary"
              startIcon={<ClearIcon />}
              onClick={handleClearFilter}
            >
              Clear
            </Button>
          </Box>
        </Grid>
      </Grid>
    </Paper>
  );
};

export default DateRangeFilter;

