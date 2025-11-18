-- =====================================================
-- Quorion B2B Comprehensive Test Seed Data
-- =====================================================
-- This script creates test data for all modules:
-- - Authentication & RBAC
-- - Tenants (Buyers, Sellers)
-- - Customers
-- - Products & SKUs
-- - Pricing (List Prices, Price Tiers)
-- - Carts & Cart Items
-- - Quote Requests
-- - Purchase Orders
-- - Payments
-- - Shipments
-- =====================================================

-- =====================================================
-- 1. TENANTS (Organizations)
-- =====================================================
INSERT INTO tenant (id, organization_name, tenant_type, email, phone, website, tax_id, registration_number, is_active, created_at, updated_at)
VALUES
-- Sellers
('11111111-1111-1111-1111-111111111111', 'Acme Supplies Inc', 'SELLER', 'sales@acme.com', '+1-555-0101', 'https://acme.com', 'TAX-ACME-001', 'REG-ACME-001', true, NOW(), NOW()),
('22222222-2222-2222-2222-222222222222', 'Global Electronics Ltd', 'SELLER', 'sales@globalelec.com', '+1-555-0102', 'https://globalelec.com', 'TAX-GE-001', 'REG-GE-001', true, NOW(), NOW()),
('33333333-3333-3333-3333-333333333333', 'Premium Parts Co', 'SELLER', 'info@premiumparts.com', '+1-555-0103', 'https://premiumparts.com', 'TAX-PP-001', 'REG-PP-001', true, NOW(), NOW()),
-- Buyers
('44444444-4444-4444-4444-444444444444', 'Tech Manufacturing Corp', 'BUYER', 'procurement@techmanuf.com', '+1-555-0201', 'https://techmanuf.com', 'TAX-TM-001', 'REG-TM-001', true, NOW(), NOW()),
('55555555-5555-5555-5555-555555555555', 'Industrial Solutions LLC', 'BUYER', 'buying@indsol.com', '+1-555-0202', 'https://indsol.com', 'TAX-IS-001', 'REG-IS-001', true, NOW(), NOW()),
('66666666-6666-6666-6666-666666666666', 'Smart Retail Group', 'BUYER', 'orders@smartretail.com', '+1-555-0203', 'https://smartretail.com', 'TAX-SR-001', 'REG-SR-001', true, NOW(), NOW()),
-- Marketplace operator
('77777777-7777-7777-7777-777777777777', 'Quorion Marketplace', 'MARKETPLACE', 'admin@quorion.com', '+1-555-0001', 'https://quorion.com', 'TAX-QM-001', 'REG-QM-001', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 2. TENANT ADDRESSES
-- =====================================================
INSERT INTO tenant_address (id, tenant_id, address_type, street_address, city, state, postal_code, country, is_default, created_at, updated_at)
VALUES
-- Seller addresses
('a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'HEADQUARTERS', '100 Acme Way', 'New York', 'NY', '10001', 'USA', true, NOW(), NOW()),
('a2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'HEADQUARTERS', '200 Tech Blvd', 'San Francisco', 'CA', '94105', 'USA', true, NOW(), NOW()),
('a3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'WAREHOUSE', '300 Parts Drive', 'Chicago', 'IL', '60601', 'USA', true, NOW(), NOW()),
-- Buyer addresses
('a4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'SHIPPING', '400 Manufacturing Rd', 'Detroit', 'MI', '48201', 'USA', true, NOW(), NOW()),
('a5555555-5555-5555-5555-555555555555', '55555555-5555-5555-5555-555555555555', 'SHIPPING', '500 Industrial Ave', 'Houston', 'TX', '77001', 'USA', true, NOW(), NOW()),
('a6666666-6666-6666-6666-666666666666', '66666666-6666-6666-6666-666666666666', 'BILLING', '600 Retail Plaza', 'Miami', 'FL', '33101', 'USA', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 3. TENANT ASSOCIATIONS (Buyer-Seller Relationships)
-- =====================================================
INSERT INTO tenant_association (id, seller_id, buyer_id, association_type, status, credit_limit, payment_terms, created_at, updated_at)
VALUES
('b1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', 'APPROVED', 'ACTIVE', 100000.00, 'NET30', NOW(), NOW()),
('b2222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555555', 'APPROVED', 'ACTIVE', 50000.00, 'NET15', NOW(), NOW()),
('b3333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', 'APPROVED', 'ACTIVE', 75000.00, 'NET30', NOW(), NOW()),
('b4444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', '66666666-6666-6666-6666-666666666666', 'PENDING', 'PENDING', 25000.00, 'COD', NOW(), NOW()),
('b5555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', 'APPROVED', 'ACTIVE', 150000.00, 'NET45', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 4. USERS (for Authentication)
-- Password: "password123" hashed with BCrypt
-- =====================================================
INSERT INTO app_user (id, email, password, first_name, last_name, phone, role, tenant_id, is_active, email_verified, created_at, updated_at)
VALUES
-- Admin users
('u0000000-0000-0000-0000-000000000001', 'admin@quorion.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'System', 'Admin', '+1-555-0001', 'ADMIN', '77777777-7777-7777-7777-777777777777', true, true, NOW(), NOW()),
-- Seller users
('u1111111-1111-1111-1111-111111111111', 'seller1@acme.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'John', 'Seller', '+1-555-0101', 'SELLER', '11111111-1111-1111-1111-111111111111', true, true, NOW(), NOW()),
('u2222222-2222-2222-2222-222222222222', 'seller2@globalelec.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Jane', 'Electronics', '+1-555-0102', 'SELLER', '22222222-2222-2222-2222-222222222222', true, true, NOW(), NOW()),
-- Buyer users
('u4444444-4444-4444-4444-444444444444', 'buyer1@techmanuf.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Bob', 'Buyer', '+1-555-0201', 'BUYER', '44444444-4444-4444-4444-444444444444', true, true, NOW(), NOW()),
('u5555555-5555-5555-5555-555555555555', 'buyer2@indsol.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Alice', 'Industrial', '+1-555-0202', 'BUYER', '55555555-5555-5555-5555-555555555555', true, true, NOW(), NOW()),
('u6666666-6666-6666-6666-666666666666', 'buyer3@smartretail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Charlie', 'Retail', '+1-555-0203', 'BUYER', '66666666-6666-6666-6666-666666666666', true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 5. CUSTOMERS (End customers in marketplace mode)
-- =====================================================
INSERT INTO customer (id, tenant_id, first_name, last_name, email, phone, company_name, is_active, created_at, updated_at)
VALUES
('c1111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', 'Mike', 'Customer', 'mike@customer.com', '+1-555-1001', 'Mikes Shop', true, NOW(), NOW()),
('c2222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', 'Sarah', 'EndUser', 'sarah@enduser.com', '+1-555-1002', NULL, true, NOW(), NOW()),
('c3333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', 'David', 'Business', 'david@business.com', '+1-555-1003', 'Davids Enterprise', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 6. PRODUCTS
-- =====================================================
INSERT INTO product (id, name, description, category, brand, seller_id, is_active, created_at, updated_at)
VALUES
-- Acme products
('p1111111-1111-1111-1111-111111111111', 'Industrial Widget A', 'High-quality industrial widget for manufacturing', 'INDUSTRIAL', 'Acme', '11111111-1111-1111-1111-111111111111', true, NOW(), NOW()),
('p1111111-1111-1111-1111-111111111112', 'Industrial Widget B', 'Premium grade widget with extended warranty', 'INDUSTRIAL', 'Acme', '11111111-1111-1111-1111-111111111111', true, NOW(), NOW()),
('p1111111-1111-1111-1111-111111111113', 'Safety Gear Set', 'Complete safety equipment set', 'SAFETY', 'Acme', '11111111-1111-1111-1111-111111111111', true, NOW(), NOW()),
-- Global Electronics products
('p2222222-2222-2222-2222-222222222221', 'Circuit Board Pro', 'Professional-grade circuit board', 'ELECTRONICS', 'GlobalElec', '22222222-2222-2222-2222-222222222222', true, NOW(), NOW()),
('p2222222-2222-2222-2222-222222222222', 'Power Supply Unit', 'High-efficiency power supply', 'ELECTRONICS', 'GlobalElec', '22222222-2222-2222-2222-222222222222', true, NOW(), NOW()),
('p2222222-2222-2222-2222-222222222223', 'LED Display Panel', '4K resolution LED panel', 'ELECTRONICS', 'GlobalElec', '22222222-2222-2222-2222-222222222222', true, NOW(), NOW()),
-- Premium Parts products
('p3333333-3333-3333-3333-333333333331', 'Precision Bearing', 'High-precision industrial bearing', 'MECHANICAL', 'Premium', '33333333-3333-3333-3333-333333333333', true, NOW(), NOW()),
('p3333333-3333-3333-3333-333333333332', 'Hydraulic Pump', 'Heavy-duty hydraulic pump', 'MECHANICAL', 'Premium', '33333333-3333-3333-3333-333333333333', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 7. PRODUCT SKUs
-- =====================================================
INSERT INTO product_sku (id, product_id, sku_code, name, description, unit_of_measure, weight, is_active, created_at, updated_at)
VALUES
-- Widget A SKUs
('s1111111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 'WIDGET-A-SM', 'Widget A Small', 'Small size widget', 'PIECE', 0.5, true, NOW(), NOW()),
('s1111111-1111-1111-1111-111111111112', 'p1111111-1111-1111-1111-111111111111', 'WIDGET-A-MD', 'Widget A Medium', 'Medium size widget', 'PIECE', 1.0, true, NOW(), NOW()),
('s1111111-1111-1111-1111-111111111113', 'p1111111-1111-1111-1111-111111111111', 'WIDGET-A-LG', 'Widget A Large', 'Large size widget', 'PIECE', 2.0, true, NOW(), NOW()),
-- Widget B SKUs
('s1111111-1111-1111-1111-111111111121', 'p1111111-1111-1111-1111-111111111112', 'WIDGET-B-STD', 'Widget B Standard', 'Standard widget B', 'PIECE', 1.5, true, NOW(), NOW()),
-- Safety Gear SKUs
('s1111111-1111-1111-1111-111111111131', 'p1111111-1111-1111-1111-111111111113', 'SAFETY-KIT-BASIC', 'Basic Safety Kit', 'Basic safety equipment', 'SET', 5.0, true, NOW(), NOW()),
('s1111111-1111-1111-1111-111111111132', 'p1111111-1111-1111-1111-111111111113', 'SAFETY-KIT-PRO', 'Pro Safety Kit', 'Professional safety equipment', 'SET', 8.0, true, NOW(), NOW()),
-- Electronics SKUs
('s2222222-2222-2222-2222-222222222211', 'p2222222-2222-2222-2222-222222222221', 'CB-PRO-V1', 'Circuit Board Pro V1', 'Version 1 circuit board', 'PIECE', 0.2, true, NOW(), NOW()),
('s2222222-2222-2222-2222-222222222221', 'p2222222-2222-2222-2222-222222222222', 'PSU-500W', 'PSU 500W', '500 Watt power supply', 'PIECE', 2.5, true, NOW(), NOW()),
('s2222222-2222-2222-2222-222222222231', 'p2222222-2222-2222-2222-222222222223', 'LED-24IN', 'LED 24 inch', '24 inch LED panel', 'PIECE', 4.0, true, NOW(), NOW()),
-- Mechanical SKUs
('s3333333-3333-3333-3333-333333333311', 'p3333333-3333-3333-3333-333333333331', 'BEARING-6205', 'Bearing 6205', 'Standard 6205 bearing', 'PIECE', 0.3, true, NOW(), NOW()),
('s3333333-3333-3333-3333-333333333321', 'p3333333-3333-3333-3333-333333333332', 'PUMP-HYD-100', 'Hydraulic Pump 100', '100 GPM hydraulic pump', 'PIECE', 25.0, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 8. LIST PRICES (Base prices)
-- =====================================================
INSERT INTO list_price (id, sku_id, price, currency, is_active, effective_from, created_at, updated_at)
VALUES
('lp111111-1111-1111-1111-111111111111', 's1111111-1111-1111-1111-111111111111', 25.00, 'USD', true, NOW(), NOW(), NOW()),
('lp111111-1111-1111-1111-111111111112', 's1111111-1111-1111-1111-111111111112', 45.00, 'USD', true, NOW(), NOW(), NOW()),
('lp111111-1111-1111-1111-111111111113', 's1111111-1111-1111-1111-111111111113', 75.00, 'USD', true, NOW(), NOW(), NOW()),
('lp111111-1111-1111-1111-111111111121', 's1111111-1111-1111-1111-111111111121', 55.00, 'USD', true, NOW(), NOW(), NOW()),
('lp111111-1111-1111-1111-111111111131', 's1111111-1111-1111-1111-111111111131', 150.00, 'USD', true, NOW(), NOW(), NOW()),
('lp111111-1111-1111-1111-111111111132', 's1111111-1111-1111-1111-111111111132', 350.00, 'USD', true, NOW(), NOW(), NOW()),
('lp222222-2222-2222-2222-222222222211', 's2222222-2222-2222-2222-222222222211', 120.00, 'USD', true, NOW(), NOW(), NOW()),
('lp222222-2222-2222-2222-222222222221', 's2222222-2222-2222-2222-222222222221', 89.00, 'USD', true, NOW(), NOW(), NOW()),
('lp222222-2222-2222-2222-222222222231', 's2222222-2222-2222-2222-222222222231', 450.00, 'USD', true, NOW(), NOW(), NOW()),
('lp333333-3333-3333-3333-333333333311', 's3333333-3333-3333-3333-333333333311', 35.00, 'USD', true, NOW(), NOW(), NOW()),
('lp333333-3333-3333-3333-333333333321', 's3333333-3333-3333-3333-333333333321', 2500.00, 'USD', true, NOW(), NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 9. PRICE TIERS (Volume discounts per buyer)
-- =====================================================
INSERT INTO price_tier (id, seller_id, buyer_id, destination_id, product_sku_id, minimum_uom_quantity, maximum_uom_quantity, price_per_uom, discount_percent, currency, is_active, created_at, updated_at)
VALUES
-- Tech Manufacturing gets volume discounts from Acme
('pt111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', 's1111111-1111-1111-1111-111111111111', 10, 49, 22.50, 10.00, 'USD', true, NOW(), NOW()),
('pt111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', 's1111111-1111-1111-1111-111111111111', 50, 99, 20.00, 20.00, 'USD', true, NOW(), NOW()),
('pt111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', 's1111111-1111-1111-1111-111111111111', 100, NULL, 17.50, 30.00, 'USD', true, NOW(), NOW()),
-- Industrial Solutions gets different pricing
('pt222222-2222-2222-2222-222222222221', '22222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', 'a5555555-5555-5555-5555-555555555555', 's2222222-2222-2222-2222-222222222211', 5, 24, 108.00, 10.00, 'USD', true, NOW(), NOW()),
('pt222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', 'a5555555-5555-5555-5555-555555555555', 's2222222-2222-2222-2222-222222222211', 25, NULL, 96.00, 20.00, 'USD', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 10. CARTS (Shopping carts for testing)
-- =====================================================
INSERT INTO cart (id, buyer_id, customer_id, session_key, is_active, name, created_at, updated_at)
VALUES
-- B2B cart (buyer with no customer)
('cart1111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', NULL, 'session-b2b-001', true, 'Tech Manufacturing Cart', NOW(), NOW()),
-- Marketplace cart (with customer)
('cart2222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', 'c1111111-1111-1111-1111-111111111111', 'session-mp-001', true, 'Marketplace Customer Cart', NOW(), NOW()),
-- Empty cart for testing
('cart3333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', NULL, 'session-b2b-002', true, 'Industrial Solutions Cart', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 11. CART ITEMS
-- =====================================================
INSERT INTO cart_item (id, cart_id, product_id, quantity, unit_price, notes, created_at, updated_at)
VALUES
-- Items in B2B cart
('ci111111-1111-1111-1111-111111111111', 'cart1111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 50, 20.00, 'Bulk order - Widget A', NOW(), NOW()),
('ci111111-1111-1111-1111-111111111112', 'cart1111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111113', 5, 150.00, 'Safety gear sets', NOW(), NOW()),
-- Items in Marketplace cart
('ci222222-2222-2222-2222-222222222221', 'cart2222-2222-2222-2222-222222222222', 'p2222222-2222-2222-2222-222222222221', 2, 120.00, NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 12. QUOTE REQUESTS (RFQs)
-- =====================================================
INSERT INTO quote_request (id, buyer_id, seller_id, status, notes, valid_until, created_at, updated_at)
VALUES
('qr111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'SUBMITTED', 'Request for bulk widget pricing', NOW() + INTERVAL '30 days', NOW(), NOW()),
('qr222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', 'QUOTED', 'Electronics order quote', NOW() + INTERVAL '15 days', NOW(), NOW()),
('qr333333-3333-3333-3333-333333333333', '44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', 'ACCEPTED', 'Mechanical parts quote - ready for PO', NOW() + INTERVAL '7 days', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 13. QUOTE REQUEST DETAILS
-- =====================================================
INSERT INTO quote_request_detail (id, quote_request_id, product_id, sku_id, requested_quantity, quoted_unit_price, notes, created_at, updated_at)
VALUES
('qrd11111-1111-1111-1111-111111111111', 'qr111111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 's1111111-1111-1111-1111-111111111111', 200, 17.50, 'Volume pricing requested', NOW(), NOW()),
('qrd11111-1111-1111-1111-111111111112', 'qr111111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111112', 's1111111-1111-1111-1111-111111111121', 100, 50.00, NULL, NOW(), NOW()),
('qrd22222-2222-2222-2222-222222222221', 'qr222222-2222-2222-2222-222222222222', 'p2222222-2222-2222-2222-222222222221', 's2222222-2222-2222-2222-222222222211', 50, 96.00, 'Quoted with 20% discount', NOW(), NOW()),
('qrd33333-3333-3333-3333-333333333331', 'qr333333-3333-3333-3333-333333333333', 'p3333333-3333-3333-3333-333333333331', 's3333333-3333-3333-3333-333333333311', 500, 28.00, 'Accepted - create PO', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 14. PURCHASE ORDERS
-- =====================================================
INSERT INTO purchase_order (id, buyer_id, seller_id, warehouse_id, order_number, status, total_amount, currency, payment_terms, delivery_terms, notes, created_at, updated_at)
VALUES
('po111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'a4444444-4444-4444-4444-444444444444', 'PO-2024-001', 'CONFIRMED', 8500.00, 'USD', 'NET30', 'FOB', 'First order from Tech Manufacturing', NOW(), NOW()),
('po222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', 'a5555555-5555-5555-5555-555555555555', 'PO-2024-002', 'SHIPPED', 4800.00, 'USD', 'NET15', 'CIF', 'Electronics order - in transit', NOW(), NOW()),
('po333333-3333-3333-3333-333333333333', '44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', 'a4444444-4444-4444-4444-444444444444', 'PO-2024-003', 'DELIVERED', 14000.00, 'USD', 'NET45', 'DAP', 'Mechanical parts - completed', NOW(), NOW()),
('po444444-4444-4444-4444-444444444444', '66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', 'a6666666-6666-6666-6666-666666666666', 'PO-2024-004', 'PENDING', 2500.00, 'USD', 'COD', 'EXW', 'New buyer - pending approval', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 15. PURCHASE ORDER DETAILS
-- =====================================================
INSERT INTO purchase_order_detail (id, order_id, product_id, sku_id, quantity, unit_price, total_price, notes, created_at, updated_at)
VALUES
-- PO-001 items
('pod11111-1111-1111-1111-111111111111', 'po111111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 's1111111-1111-1111-1111-111111111111', 200, 17.50, 3500.00, NULL, NOW(), NOW()),
('pod11111-1111-1111-1111-111111111112', 'po111111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111112', 's1111111-1111-1111-1111-111111111121', 100, 50.00, 5000.00, NULL, NOW(), NOW()),
-- PO-002 items
('pod22222-2222-2222-2222-222222222221', 'po222222-2222-2222-2222-222222222222', 'p2222222-2222-2222-2222-222222222221', 's2222222-2222-2222-2222-222222222211', 50, 96.00, 4800.00, 'Shipped via FedEx', NOW(), NOW()),
-- PO-003 items
('pod33333-3333-3333-3333-333333333331', 'po333333-3333-3333-3333-333333333333', 'p3333333-3333-3333-3333-333333333331', 's3333333-3333-3333-3333-333333333311', 500, 28.00, 14000.00, 'Delivered successfully', NOW(), NOW()),
-- PO-004 items
('pod44444-4444-4444-4444-444444444441', 'po444444-4444-4444-4444-444444444444', 'p1111111-1111-1111-1111-111111111111', 's1111111-1111-1111-1111-111111111111', 100, 25.00, 2500.00, 'Awaiting approval', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 16. PAYMENTS
-- =====================================================
INSERT INTO payment (id, order_id, payment_method, amount, currency, status, transaction_id, payment_gateway, notes, created_at, updated_at)
VALUES
('pay11111-1111-1111-1111-111111111111', 'po111111-1111-1111-1111-111111111111', 'BANK_TRANSFER', 8500.00, 'USD', 'COMPLETED', 'TXN-001-2024', 'STRIPE', 'Full payment received', NOW(), NOW()),
('pay22222-2222-2222-2222-222222222221', 'po222222-2222-2222-2222-222222222222', 'CREDIT_CARD', 2400.00, 'USD', 'COMPLETED', 'TXN-002-2024-A', 'STRIPE', 'Partial payment 1/2', NOW(), NOW()),
('pay22222-2222-2222-2222-222222222222', 'po222222-2222-2222-2222-222222222222', 'CREDIT_CARD', 2400.00, 'USD', 'PENDING', 'TXN-002-2024-B', 'STRIPE', 'Partial payment 2/2 pending', NOW(), NOW()),
('pay33333-3333-3333-3333-333333333331', 'po333333-3333-3333-3333-333333333333', 'WIRE_TRANSFER', 14000.00, 'USD', 'COMPLETED', 'TXN-003-2024', 'BANK', 'Full payment completed', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 17. SHIPMENT ADVICES
-- =====================================================
INSERT INTO shipment_advice (id, order_id, shipment_number, carrier, tracking_number, status, shipped_date, estimated_delivery, actual_delivery, notes, created_at, updated_at)
VALUES
('ship1111-1111-1111-1111-111111111111', 'po111111-1111-1111-1111-111111111111', 'SHIP-001-2024', 'FedEx', 'FX123456789', 'PREPARING', NULL, NOW() + INTERVAL '5 days', NULL, 'Preparing for shipment', NOW(), NOW()),
('ship2222-2222-2222-2222-222222222222', 'po222222-2222-2222-2222-222222222222', 'SHIP-002-2024', 'UPS', 'UPS987654321', 'IN_TRANSIT', NOW() - INTERVAL '2 days', NOW() + INTERVAL '3 days', NULL, 'Currently in transit', NOW(), NOW()),
('ship3333-3333-3333-3333-333333333333', 'po333333-3333-3333-3333-333333333333', 'SHIP-003-2024', 'DHL', 'DHL456789123', 'DELIVERED', NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days', 'Delivered successfully', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 18. LEADS (B2B Sales Leads)
-- =====================================================
INSERT INTO lead (id, buyer_id, seller_id, status, source, notes, created_at, updated_at)
VALUES
('lead1111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'QUALIFIED', 'WEBSITE', 'High-value lead from Tech Manufacturing', NOW(), NOW()),
('lead2222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', 'CONVERTED', 'REFERRAL', 'Converted to customer', NOW(), NOW()),
('lead3333-3333-3333-3333-333333333333', '66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', 'NEW', 'TRADE_SHOW', 'Met at industry expo', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
-- Run these to verify seed data:
-- SELECT 'tenants' as entity, COUNT(*) as count FROM tenant;
-- SELECT 'users' as entity, COUNT(*) as count FROM app_user;
-- SELECT 'products' as entity, COUNT(*) as count FROM product;
-- SELECT 'skus' as entity, COUNT(*) as count FROM product_sku;
-- SELECT 'orders' as entity, COUNT(*) as count FROM purchase_order;
-- SELECT 'payments' as entity, COUNT(*) as count FROM payment;
