-- Seed Data for Integration Tests
-- This data is loaded before tests run
-- Password for all test users: Test123456!
-- BCrypt hash generated with: bcrypt.hashpw(b'Test123456!', bcrypt.gensalt(rounds=10, prefix=b'2a'))

-- Test Tenants
INSERT INTO tenant (id, organization_name, tenant_type, is_active, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000001', 'Test Seller Company', 'SELLER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000002', 'Test Buyer Company', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000003', 'Test Distributor', 'DISTRIBUTOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Test Users (table: app_user)
-- Password: Test123456! (BCrypt hash generated with Java BCryptPasswordEncoder)
-- Hash: $2a$10$JBFRMiZDWINfFWm0v3n/XebSpG9Belyd9u3BgvNrmMxEZLXrHzpcS
INSERT INTO app_user (id, username, email, password, first_name, last_name, tenant_id, is_active, is_staff, is_superuser, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000010', 'testuser', 'testuser@example.com', '$2a$10$JBFRMiZDWINfFWm0v3n/XebSpG9Belyd9u3BgvNrmMxEZLXrHzpcS', 'Test', 'User', '00000000-0000-0000-0000-000000000001', true, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000011', 'buyeruser', 'buyer@example.com', '$2a$10$JBFRMiZDWINfFWm0v3n/XebSpG9Belyd9u3BgvNrmMxEZLXrHzpcS', 'Buyer', 'User', '00000000-0000-0000-0000-000000000002', true, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000012', 'selleruser', 'seller@example.com', '$2a$10$JBFRMiZDWINfFWm0v3n/XebSpG9Belyd9u3BgvNrmMxEZLXrHzpcS', 'Seller', 'User', '00000000-0000-0000-0000-000000000001', true, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Test Customers
INSERT INTO customer (id, tenant_id, first_name, last_name, email, phone, company_name, is_active, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000020', '00000000-0000-0000-0000-000000000001', 'John', 'Doe', 'john.doe@example.com', '+1234567890', 'Doe Industries', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000021', '00000000-0000-0000-0000-000000000002', 'Jane', 'Smith', 'jane.smith@example.com', '+1234567891', 'Smith Corp', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Test Products (if product table exists)
-- Note: Adjust table/column names based on actual schema
-- INSERT INTO product (id, seller_id, name, description, category, is_active, created_at, updated_at) VALUES
-- ('00000000-0000-0000-0000-000000000030', '00000000-0000-0000-0000-000000000001', 'Test Product 1', 'Description for product 1', 'Electronics', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
-- ON CONFLICT (id) DO NOTHING;

-- Test Price Tiers
INSERT INTO price_tier (id, seller_id, buyer_id, destination_id, product_sku_id, minimum_uom_quantity, price_per_uom, currency, is_active, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000050', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000040', 1, 100.00, 'USD', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000051', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000040', 11, 90.00, 'USD', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Test List Prices
INSERT INTO list_price (id, sku_id, price, currency, is_active, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000060', '00000000-0000-0000-0000-000000000040', 100.00, 'USD', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000061', '00000000-0000-0000-0000-000000000041', 150.00, 'USD', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Test Tenant Associations (if table exists)
-- Note: tenant_association table may not exist in hexagonal architecture
-- INSERT INTO tenant_association (id, seller_id, buyer_id, association_type, is_active, created_at, updated_at) VALUES
-- ('00000000-0000-0000-0000-000000000070', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'DIRECT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
-- ON CONFLICT (id) DO NOTHING;
