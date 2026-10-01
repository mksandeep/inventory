-- Database Reset Script for Inventory Management System
-- This script will drop all existing tables and schemas to allow fresh migration
-- WARNING: This will delete all data! Use only for development/testing

-- 1. Drop all constraints and tables in sam schema
DROP TABLE IF EXISTS sam.user_roles CASCADE;
DROP TABLE IF EXISTS sam.roles CASCADE;
DROP TABLE IF EXISTS sam.users CASCADE;
DROP TABLE IF EXISTS sam.sales CASCADE;
DROP TABLE IF EXISTS sam.purchases CASCADE;
DROP TABLE IF EXISTS sam.products CASCADE;
DROP SCHEMA IF EXISTS sam CASCADE;

-- 2. Drop all tables in public schema (in case they exist there)
DROP TABLE IF EXISTS public.user_roles CASCADE;
DROP TABLE IF EXISTS public.roles CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.sales CASCADE;
DROP TABLE IF EXISTS public.purchases CASCADE;
DROP TABLE IF EXISTS public.products CASCADE;

-- 3. Drop Flyway history table (to restart migrations from beginning)
DROP TABLE IF EXISTS public.flyway_schema_history CASCADE;

-- 4. Create fresh schema
CREATE SCHEMA IF NOT EXISTS sam;

-- 5. Grant privileges
GRANT ALL PRIVILEGES ON SCHEMA sam TO sandeep;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA sam TO sandeep;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA sam TO sandeep;

-- 6. Set default search path
ALTER ROLE sandeep SET search_path = 'sam,public';

COMMIT;

-- The migrations will now run from V1 when the application starts

