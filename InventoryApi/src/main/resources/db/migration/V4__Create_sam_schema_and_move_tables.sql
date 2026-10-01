-- Create schema 'sam' if it doesn't exist
CREATE SCHEMA IF NOT EXISTS sam;

-- Move tables only if they don't already exist in sam schema
-- This migration is idempotent - it won't fail if tables already exist in sam schema

-- Move users table if it exists in public but not in sam
DO $$
BEGIN
  -- Only move if exists in public AND not in sam
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users')
     AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'sam' AND table_name = 'users') THEN
    ALTER TABLE public.users SET SCHEMA sam;
  END IF;
END $$;

-- Move roles table if it exists in public but not in sam
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'roles')
     AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'sam' AND table_name = 'roles') THEN
    ALTER TABLE public.roles SET SCHEMA sam;
  END IF;
END $$;

-- Move user_roles table if it exists in public but not in sam
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'user_roles')
     AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'sam' AND table_name = 'user_roles') THEN
    ALTER TABLE public.user_roles SET SCHEMA sam;
  END IF;
END $$;

-- Move products table if it exists in public but not in sam
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'products')
     AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'sam' AND table_name = 'products') THEN
    ALTER TABLE public.products SET SCHEMA sam;
  END IF;
END $$;

-- Move sales table if it exists in public but not in sam
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'sales')
     AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'sam' AND table_name = 'sales') THEN
    ALTER TABLE public.sales SET SCHEMA sam;
  END IF;
END $$;

-- Move purchases table if it exists in public but not in sam
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'purchases')
     AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'sam' AND table_name = 'purchases') THEN
    ALTER TABLE public.purchases SET SCHEMA sam;
  END IF;
END $$;

-- Grant privileges on schema to user
GRANT ALL PRIVILEGES ON SCHEMA sam TO sandeep;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA sam TO sandeep;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA sam TO sandeep;


