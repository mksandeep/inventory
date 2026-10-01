import React, { useEffect, useRef } from 'react';
import { CircularProgress, Box } from '@mui/material';

interface InfiniteScrollProps {
  onLoadMore: () => void;
  isLoading: boolean;
  hasMore: boolean;
}

const InfiniteScroll: React.FC<InfiniteScrollProps> = ({ onLoadMore, isLoading, hasMore }) => {
  const observerTarget = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!hasMore || isLoading) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          onLoadMore();
        }
      },
      { threshold: 0.1 }
    );

    if (observerTarget.current) {
      observer.observe(observerTarget.current);
    }

    return () => {
      if (observerTarget.current) {
        observer.unobserve(observerTarget.current);
      }
    };
  }, [onLoadMore, isLoading, hasMore]);

  if (!hasMore && !isLoading) return null;

  return (
    <Box
      ref={observerTarget}
      sx={{
        display: 'flex',
        justifyContent: 'center',
        py: 3,
        width: '100%',
      }}
    >
      {isLoading && <CircularProgress size={40} />}
    </Box>
  );
};

export default InfiniteScroll;

