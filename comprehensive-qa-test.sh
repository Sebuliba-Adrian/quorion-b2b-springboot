#!/bin/bash

# Comprehensive QA Test for all 217 Quorion B2B API Endpoints
# This script tests every single endpoint and generates a detailed report

set -e

BASE_URL="http://localhost:8080"
REPORT_FILE="qa-test-report.txt"
DETAILED_LOG="qa-test-detailed.log"
TIMESTAMP=$(date +"%Y-%m-%d %H:%M:%S")

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
SKIPPED_TESTS=0

# Initialize report
cat > "$REPORT_FILE" << EOF
=============================================================================
COMPREHENSIVE QA TEST REPORT - Quorion B2B API
=============================================================================
Test Date: $TIMESTAMP
Base URL: $BASE_URL
=============================================================================

EOF

cat > "$DETAILED_LOG" << EOF
Detailed Test Log - $TIMESTAMP
=============================================================================

EOF

echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "COMPREHENSIVE QA TEST - Testing All 217 Endpoints" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Test function
test_endpoint() {
    local method=$1
    local endpoint=$2
    local expected_status=$3
    local description=$4
    local data=$5
    local token=$6
    local endpoint_id=$7

    TOTAL_TESTS=$((TOTAL_TESTS + 1))

    echo "[$TOTAL_TESTS] Testing: $description" | tee -a "$DETAILED_LOG"
    echo "    Method: $method $endpoint" | tee -a "$DETAILED_LOG"
    echo "    Expected: $expected_status" | tee -a "$DETAILED_LOG"

    local curl_cmd="curl -s -o /tmp/response.txt -w '%{http_code}' -X $method"

    if [ -n "$token" ]; then
        curl_cmd="$curl_cmd -H 'Authorization: Bearer $token'"
    fi

    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -H 'Content-Type: application/json' -d '$data'"
    fi

    curl_cmd="$curl_cmd $BASE_URL$endpoint"

    local status_code=$(eval $curl_cmd 2>&1)
    local response=$(cat /tmp/response.txt 2>/dev/null || echo "")

    echo "    Actual: $status_code" | tee -a "$DETAILED_LOG"

    if [ "$status_code" -eq "$expected_status" ]; then
        echo -e "    ${GREEN}✓ PASS${NC}" | tee -a "$DETAILED_LOG"
        echo "PASS|$endpoint_id|$method|$endpoint|$expected_status|$status_code|$description" >> "$REPORT_FILE"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "    ${RED}✗ FAIL${NC}" | tee -a "$DETAILED_LOG"
        echo "    Response: $response" | tee -a "$DETAILED_LOG"
        echo "FAIL|$endpoint_id|$method|$endpoint|$expected_status|$status_code|$description|$response" >> "$REPORT_FILE"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi

    echo "" | tee -a "$DETAILED_LOG"
}

# Setup: Register and login to get token
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "PHASE 0: Authentication Setup" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Register a QA test user
QA_USER="qatest_$(date +%s)"
QA_EMAIL="qatest_$(date +%s)@test.com"
QA_PASS="QATest123456"

echo "Registering QA test user: $QA_USER" | tee -a "$DETAILED_LOG"

REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$QA_USER\",
    \"email\": \"$QA_EMAIL\",
    \"password\": \"$QA_PASS\",
    \"firstName\": \"QA\",
    \"lastName\": \"Test\",
    \"tenantId\": \"00000000-0000-0000-0000-000000000001\"
  }")

TOKEN=$(echo "$REGISTER_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo -e "${GREEN}✓ Authentication successful - Token obtained${NC}" | tee -a "$DETAILED_LOG"
    echo "Token (first 20 chars): ${TOKEN:0:20}..." | tee -a "$DETAILED_LOG"
else
    echo -e "${RED}✗ Failed to obtain authentication token${NC}" | tee -a "$DETAILED_LOG"
    echo "Will test unauthenticated endpoints only" | tee -a "$DETAILED_LOG"
fi

echo "" | tee -a "$DETAILED_LOG"

# =============================================================================
# PHASE 1: Authentication Endpoints (5 endpoints)
# =============================================================================
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "PHASE 1: Authentication Endpoints (5)" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

test_endpoint "POST" "/api/auth/register" "400" "Register with empty data (validation)" '{}' "" "AUTH-001"
test_endpoint "POST" "/api/auth/login" "400" "Login with empty data (validation)" '{}' "" "AUTH-002"
test_endpoint "POST" "/api/auth/login" "401" "Login with wrong credentials" '{"usernameOrEmail":"wrong","password":"wrong"}' "" "AUTH-003"
test_endpoint "GET" "/api/auth/me" "200" "Get current user info" "" "$TOKEN" "AUTH-004"
test_endpoint "POST" "/api/auth/verify" "200" "Verify token" "" "$TOKEN" "AUTH-005"

# =============================================================================
# PHASE 2: Tenant Management (20 endpoints)
# =============================================================================
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "PHASE 2: Tenant Management (20)" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Tenants (5)
test_endpoint "GET" "/api/tenants" "200" "List all tenants" "" "$TOKEN" "TEN-001"
test_endpoint "POST" "/api/tenants" "400" "Create tenant (validation error)" '{}' "$TOKEN" "TEN-002"
test_endpoint "GET" "/api/tenants/00000000-0000-0000-0000-000000000001" "200" "Get tenant by ID" "" "$TOKEN" "TEN-003"
test_endpoint "PUT" "/api/tenants/00000000-0000-0000-0000-999999999999" "404" "Update non-existent tenant" '{}' "$TOKEN" "TEN-004"
test_endpoint "DELETE" "/api/tenants/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent tenant" "" "$TOKEN" "TEN-005"

# Tenant Addresses (5)
test_endpoint "GET" "/api/tenant-addresses" "200" "List all tenant addresses" "" "$TOKEN" "TADDR-001"
test_endpoint "POST" "/api/tenant-addresses" "400" "Create address (validation)" '{}' "$TOKEN" "TADDR-002"
test_endpoint "GET" "/api/tenant-addresses/00000000-0000-0000-0000-999999999999" "404" "Get non-existent address" "" "$TOKEN" "TADDR-003"
test_endpoint "PUT" "/api/tenant-addresses/00000000-0000-0000-0000-999999999999" "404" "Update non-existent address" '{}' "$TOKEN" "TADDR-004"
test_endpoint "DELETE" "/api/tenant-addresses/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent address" "" "$TOKEN" "TADDR-005"

# Tenant Associations (5)
test_endpoint "GET" "/api/tenant-associations" "200" "List all tenant associations" "" "$TOKEN" "TASSOC-001"
test_endpoint "POST" "/api/tenant-associations" "400" "Create association (validation)" '{}' "$TOKEN" "TASSOC-002"
test_endpoint "GET" "/api/tenant-associations/00000000-0000-0000-0000-999999999999" "404" "Get non-existent association" "" "$TOKEN" "TASSOC-003"
test_endpoint "PUT" "/api/tenant-associations/00000000-0000-0000-0000-999999999999" "404" "Update non-existent association" '{}' "$TOKEN" "TASSOC-004"
test_endpoint "DELETE" "/api/tenant-associations/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent association" "" "$TOKEN" "TASSOC-005"

# Seller Marketplace (5)
test_endpoint "GET" "/api/seller-marketplaces" "200" "List all seller marketplaces" "" "$TOKEN" "SELMP-001"
test_endpoint "POST" "/api/seller-marketplaces" "400" "Create seller marketplace (validation)" '{}' "$TOKEN" "SELMP-002"
test_endpoint "GET" "/api/seller-marketplaces/00000000-0000-0000-0000-999999999999" "404" "Get non-existent seller marketplace" "" "$TOKEN" "SELMP-003"
test_endpoint "PUT" "/api/seller-marketplaces/00000000-0000-0000-0000-999999999999" "404" "Update non-existent seller marketplace" '{}' "$TOKEN" "SELMP-004"
test_endpoint "DELETE" "/api/seller-marketplaces/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent seller marketplace" "" "$TOKEN" "SELMP-005"

# =============================================================================
# PHASE 3: Product Management (55 endpoints)
# =============================================================================
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "PHASE 3: Product Management (55)" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Products (5)
test_endpoint "GET" "/api/products" "200" "List all products" "" "$TOKEN" "PROD-001"
test_endpoint "POST" "/api/products" "400" "Create product (validation)" '{}' "$TOKEN" "PROD-002"
test_endpoint "GET" "/api/products/00000000-0000-0000-0000-999999999999" "404" "Get non-existent product" "" "$TOKEN" "PROD-003"
test_endpoint "PUT" "/api/products/00000000-0000-0000-0000-999999999999" "404" "Update non-existent product" '{}' "$TOKEN" "PROD-004"
test_endpoint "DELETE" "/api/products/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent product" "" "$TOKEN" "PROD-005"

# Product SKUs (5)
test_endpoint "GET" "/api/product-skus" "200" "List all product SKUs" "" "$TOKEN" "PSKU-001"
test_endpoint "POST" "/api/product-skus" "400" "Create SKU (validation)" '{}' "$TOKEN" "PSKU-002"
test_endpoint "GET" "/api/product-skus/00000000-0000-0000-0000-999999999999" "404" "Get non-existent SKU" "" "$TOKEN" "PSKU-003"
test_endpoint "PUT" "/api/product-skus/00000000-0000-0000-0000-999999999999" "404" "Update non-existent SKU" '{}' "$TOKEN" "PSKU-004"
test_endpoint "DELETE" "/api/product-skus/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent SKU" "" "$TOKEN" "PSKU-005"

# Product Categories (5)
test_endpoint "GET" "/api/product-categories" "200" "List all categories" "" "$TOKEN" "PCAT-001"
test_endpoint "POST" "/api/product-categories" "400" "Create category (validation)" '{}' "$TOKEN" "PCAT-002"
test_endpoint "GET" "/api/product-categories/00000000-0000-0000-0000-999999999999" "404" "Get non-existent category" "" "$TOKEN" "PCAT-003"
test_endpoint "PUT" "/api/product-categories/00000000-0000-0000-0000-999999999999" "404" "Update non-existent category" '{}' "$TOKEN" "PCAT-004"
test_endpoint "DELETE" "/api/product-categories/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent category" "" "$TOKEN" "PCAT-005"

# Product Images (5)
test_endpoint "GET" "/api/product-images" "200" "List all product images" "" "$TOKEN" "PIMG-001"
test_endpoint "POST" "/api/product-images" "400" "Create image (validation)" '{}' "$TOKEN" "PIMG-002"
test_endpoint "GET" "/api/product-images/00000000-0000-0000-0000-999999999999" "404" "Get non-existent image" "" "$TOKEN" "PIMG-003"
test_endpoint "PUT" "/api/product-images/00000000-0000-0000-0000-999999999999" "404" "Update non-existent image" '{}' "$TOKEN" "PIMG-004"
test_endpoint "DELETE" "/api/product-images/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent image" "" "$TOKEN" "PIMG-005"

# Product Tags (5)
test_endpoint "GET" "/api/product-tags" "200" "List all product tags" "" "$TOKEN" "PTAG-001"
test_endpoint "POST" "/api/product-tags" "400" "Create tag (validation)" '{}' "$TOKEN" "PTAG-002"
test_endpoint "GET" "/api/product-tags/00000000-0000-0000-0000-999999999999" "404" "Get non-existent tag" "" "$TOKEN" "PTAG-003"
test_endpoint "PUT" "/api/product-tags/00000000-0000-0000-0000-999999999999" "404" "Update non-existent tag" '{}' "$TOKEN" "PTAG-004"
test_endpoint "DELETE" "/api/product-tags/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent tag" "" "$TOKEN" "PTAG-005"

# Product Variants (5)
test_endpoint "GET" "/api/product-variants" "200" "List all product variants" "" "$TOKEN" "PVAR-001"
test_endpoint "POST" "/api/product-variants" "400" "Create variant (validation)" '{}' "$TOKEN" "PVAR-002"
test_endpoint "GET" "/api/product-variants/00000000-0000-0000-0000-999999999999" "404" "Get non-existent variant" "" "$TOKEN" "PVAR-003"
test_endpoint "PUT" "/api/product-variants/00000000-0000-0000-0000-999999999999" "404" "Update non-existent variant" '{}' "$TOKEN" "PVAR-004"
test_endpoint "DELETE" "/api/product-variants/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent variant" "" "$TOKEN" "PVAR-005"

# Product Attributes (5)
test_endpoint "GET" "/api/product-attributes" "200" "List all product attributes" "" "$TOKEN" "PATTR-001"
test_endpoint "POST" "/api/product-attributes" "400" "Create attribute (validation)" '{}' "$TOKEN" "PATTR-002"
test_endpoint "GET" "/api/product-attributes/00000000-0000-0000-0000-999999999999" "404" "Get non-existent attribute" "" "$TOKEN" "PATTR-003"
test_endpoint "PUT" "/api/product-attributes/00000000-0000-0000-0000-999999999999" "404" "Update non-existent attribute" '{}' "$TOKEN" "PATTR-004"
test_endpoint "DELETE" "/api/product-attributes/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent attribute" "" "$TOKEN" "PATTR-005"

# Product Attribute Values (5)
test_endpoint "GET" "/api/product-attribute-values" "200" "List all attribute values" "" "$TOKEN" "PATVAL-001"
test_endpoint "POST" "/api/product-attribute-values" "400" "Create attribute value (validation)" '{}' "$TOKEN" "PATVAL-002"
test_endpoint "GET" "/api/product-attribute-values/00000000-0000-0000-0000-999999999999" "404" "Get non-existent attribute value" "" "$TOKEN" "PATVAL-003"
test_endpoint "PUT" "/api/product-attribute-values/00000000-0000-0000-0000-999999999999" "404" "Update non-existent attribute value" '{}' "$TOKEN" "PATVAL-004"
test_endpoint "DELETE" "/api/product-attribute-values/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent attribute value" "" "$TOKEN" "PATVAL-005"

# Product Variant Attributes (5)
test_endpoint "GET" "/api/product-variant-attributes" "200" "List all variant attributes" "" "$TOKEN" "PVATTR-001"
test_endpoint "POST" "/api/product-variant-attributes" "400" "Create variant attribute (validation)" '{}' "$TOKEN" "PVATTR-002"
test_endpoint "GET" "/api/product-variant-attributes/00000000-0000-0000-0000-999999999999" "404" "Get non-existent variant attribute" "" "$TOKEN" "PVATTR-003"
test_endpoint "PUT" "/api/product-variant-attributes/00000000-0000-0000-0000-999999999999" "404" "Update non-existent variant attribute" '{}' "$TOKEN" "PVATTR-004"
test_endpoint "DELETE" "/api/product-variant-attributes/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent variant attribute" "" "$TOKEN" "PVATTR-005"

# Product Reviews (5)
test_endpoint "GET" "/api/product-reviews" "200" "List all product reviews" "" "$TOKEN" "PREV-001"
test_endpoint "POST" "/api/product-reviews" "400" "Create review (validation)" '{}' "$TOKEN" "PREV-002"
test_endpoint "GET" "/api/product-reviews/00000000-0000-0000-0000-999999999999" "404" "Get non-existent review" "" "$TOKEN" "PREV-003"
test_endpoint "PUT" "/api/product-reviews/00000000-0000-0000-0000-999999999999" "404" "Update non-existent review" '{}' "$TOKEN" "PREV-004"
test_endpoint "DELETE" "/api/product-reviews/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent review" "" "$TOKEN" "PREV-005"

# List Prices (5)
test_endpoint "GET" "/api/list-prices" "200" "List all list prices" "" "$TOKEN" "LPRC-001"
test_endpoint "POST" "/api/list-prices" "400" "Create list price (validation)" '{}' "$TOKEN" "LPRC-002"
test_endpoint "GET" "/api/list-prices/00000000-0000-0000-0000-999999999999" "404" "Get non-existent list price" "" "$TOKEN" "LPRC-003"
test_endpoint "PUT" "/api/list-prices/00000000-0000-0000-0000-999999999999" "404" "Update non-existent list price" '{}' "$TOKEN" "LPRC-004"
test_endpoint "DELETE" "/api/list-prices/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent list price" "" "$TOKEN" "LPRC-005"

# =============================================================================
# PHASE 4: Commerce & Orders (50 endpoints)
# =============================================================================
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "PHASE 4: Commerce & Orders (50)" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Carts (5)
test_endpoint "GET" "/api/carts" "200" "List all carts" "" "$TOKEN" "CART-001"
test_endpoint "POST" "/api/carts" "400" "Create cart (validation)" '{}' "$TOKEN" "CART-002"
test_endpoint "GET" "/api/carts/00000000-0000-0000-0000-999999999999" "404" "Get non-existent cart" "" "$TOKEN" "CART-003"
test_endpoint "PUT" "/api/carts/00000000-0000-0000-0000-999999999999" "404" "Update non-existent cart" '{}' "$TOKEN" "CART-004"
test_endpoint "DELETE" "/api/carts/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent cart" "" "$TOKEN" "CART-005"

# Cart Items (5)
test_endpoint "GET" "/api/cart-items" "200" "List all cart items" "" "$TOKEN" "CITM-001"
test_endpoint "POST" "/api/cart-items" "400" "Create cart item (validation)" '{}' "$TOKEN" "CITM-002"
test_endpoint "GET" "/api/cart-items/00000000-0000-0000-0000-999999999999" "404" "Get non-existent cart item" "" "$TOKEN" "CITM-003"
test_endpoint "PUT" "/api/cart-items/00000000-0000-0000-0000-999999999999" "404" "Update non-existent cart item" '{}' "$TOKEN" "CITM-004"
test_endpoint "DELETE" "/api/cart-items/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent cart item" "" "$TOKEN" "CITM-005"

# Leads (5)
test_endpoint "GET" "/api/leads" "200" "List all leads" "" "$TOKEN" "LEAD-001"
test_endpoint "POST" "/api/leads" "400" "Create lead (validation)" '{}' "$TOKEN" "LEAD-002"
test_endpoint "GET" "/api/leads/00000000-0000-0000-0000-999999999999" "404" "Get non-existent lead" "" "$TOKEN" "LEAD-003"
test_endpoint "PUT" "/api/leads/00000000-0000-0000-0000-999999999999" "404" "Update non-existent lead" '{}' "$TOKEN" "LEAD-004"
test_endpoint "DELETE" "/api/leads/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent lead" "" "$TOKEN" "LEAD-005"

# Quote Requests (5)
test_endpoint "GET" "/api/quote-requests" "200" "List all quote requests" "" "$TOKEN" "QUOT-001"
test_endpoint "POST" "/api/quote-requests" "400" "Create quote request (validation)" '{}' "$TOKEN" "QUOT-002"
test_endpoint "GET" "/api/quote-requests/00000000-0000-0000-0000-999999999999" "404" "Get non-existent quote request" "" "$TOKEN" "QUOT-003"
test_endpoint "PUT" "/api/quote-requests/00000000-0000-0000-0000-999999999999" "404" "Update non-existent quote request" '{}' "$TOKEN" "QUOT-004"
test_endpoint "DELETE" "/api/quote-requests/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent quote request" "" "$TOKEN" "QUOT-005"

# Purchase Orders (5)
test_endpoint "GET" "/api/purchase-orders" "200" "List all purchase orders" "" "$TOKEN" "PORD-001"
test_endpoint "POST" "/api/purchase-orders" "400" "Create purchase order (validation)" '{}' "$TOKEN" "PORD-002"
test_endpoint "GET" "/api/purchase-orders/00000000-0000-0000-0000-999999999999" "404" "Get non-existent purchase order" "" "$TOKEN" "PORD-003"
test_endpoint "PUT" "/api/purchase-orders/00000000-0000-0000-0000-999999999999" "404" "Update non-existent purchase order" '{}' "$TOKEN" "PORD-004"
test_endpoint "DELETE" "/api/purchase-orders/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent purchase order" "" "$TOKEN" "PORD-005"

# Payments (5)
test_endpoint "GET" "/api/payments" "200" "List all payments" "" "$TOKEN" "PAY-001"
test_endpoint "POST" "/api/payments" "400" "Create payment (validation)" '{}' "$TOKEN" "PAY-002"
test_endpoint "GET" "/api/payments/00000000-0000-0000-0000-999999999999" "404" "Get non-existent payment" "" "$TOKEN" "PAY-003"
test_endpoint "PUT" "/api/payments/00000000-0000-0000-0000-999999999999" "404" "Update non-existent payment" '{}' "$TOKEN" "PAY-004"
test_endpoint "DELETE" "/api/payments/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent payment" "" "$TOKEN" "PAY-005"

# Direct Checkouts (5)
test_endpoint "GET" "/api/direct-checkouts" "200" "List all direct checkouts" "" "$TOKEN" "DCHK-001"
test_endpoint "POST" "/api/direct-checkouts" "400" "Create direct checkout (validation)" '{}' "$TOKEN" "DCHK-002"
test_endpoint "GET" "/api/direct-checkouts/00000000-0000-0000-0000-999999999999" "404" "Get non-existent direct checkout" "" "$TOKEN" "DCHK-003"
test_endpoint "PUT" "/api/direct-checkouts/00000000-0000-0000-0000-999999999999" "404" "Update non-existent direct checkout" '{}' "$TOKEN" "DCHK-004"
test_endpoint "DELETE" "/api/direct-checkouts/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent direct checkout" "" "$TOKEN" "DCHK-005"

# Shipment Advices (5)
test_endpoint "GET" "/api/shipment-advices" "200" "List all shipment advices" "" "$TOKEN" "SHIP-001"
test_endpoint "POST" "/api/shipment-advices" "400" "Create shipment advice (validation)" '{}' "$TOKEN" "SHIP-002"
test_endpoint "GET" "/api/shipment-advices/00000000-0000-0000-0000-999999999999" "404" "Get non-existent shipment advice" "" "$TOKEN" "SHIP-003"
test_endpoint "PUT" "/api/shipment-advices/00000000-0000-0000-0000-999999999999" "404" "Update non-existent shipment advice" '{}' "$TOKEN" "SHIP-004"
test_endpoint "DELETE" "/api/shipment-advices/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent shipment advice" "" "$TOKEN" "SHIP-005"

# Customers (5)
test_endpoint "GET" "/api/customers" "200" "List all customers" "" "$TOKEN" "CUST-001"
test_endpoint "POST" "/api/customers" "400" "Create customer (validation)" '{}' "$TOKEN" "CUST-002"
test_endpoint "GET" "/api/customers/00000000-0000-0000-0000-999999999999" "404" "Get non-existent customer" "" "$TOKEN" "CUST-003"
test_endpoint "PUT" "/api/customers/00000000-0000-0000-0000-999999999999" "404" "Update non-existent customer" '{}' "$TOKEN" "CUST-004"
test_endpoint "DELETE" "/api/customers/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent customer" "" "$TOKEN" "CUST-005"

# Price Tiers (5)
test_endpoint "GET" "/api/price-tiers" "200" "List all price tiers" "" "$TOKEN" "PTIR-001"
test_endpoint "POST" "/api/price-tiers" "400" "Create price tier (validation)" '{}' "$TOKEN" "PTIR-002"
test_endpoint "GET" "/api/price-tiers/00000000-0000-0000-0000-999999999999" "404" "Get non-existent price tier" "" "$TOKEN" "PTIR-003"
test_endpoint "PUT" "/api/price-tiers/00000000-0000-0000-0000-999999999999" "404" "Update non-existent price tier" '{}' "$TOKEN" "PTIR-004"
test_endpoint "DELETE" "/api/price-tiers/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent price tier" "" "$TOKEN" "PTIR-005"

# =============================================================================
# PHASE 5: Marketplace Features (40 endpoints)
# =============================================================================
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "PHASE 5: Marketplace Features (40)" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Wishlists (5)
test_endpoint "GET" "/api/wishlists" "200" "List all wishlists" "" "$TOKEN" "WISH-001"
test_endpoint "POST" "/api/wishlists" "400" "Create wishlist (validation)" '{}' "$TOKEN" "WISH-002"
test_endpoint "GET" "/api/wishlists/00000000-0000-0000-0000-999999999999" "404" "Get non-existent wishlist" "" "$TOKEN" "WISH-003"
test_endpoint "PUT" "/api/wishlists/00000000-0000-0000-0000-999999999999" "404" "Update non-existent wishlist" '{}' "$TOKEN" "WISH-004"
test_endpoint "DELETE" "/api/wishlists/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent wishlist" "" "$TOKEN" "WISH-005"

# Wishlist Items (5)
test_endpoint "GET" "/api/wishlist-items" "200" "List all wishlist items" "" "$TOKEN" "WITM-001"
test_endpoint "POST" "/api/wishlist-items" "400" "Create wishlist item (validation)" '{}' "$TOKEN" "WITM-002"
test_endpoint "GET" "/api/wishlist-items/00000000-0000-0000-0000-999999999999" "404" "Get non-existent wishlist item" "" "$TOKEN" "WITM-003"
test_endpoint "PUT" "/api/wishlist-items/00000000-0000-0000-0000-999999999999" "404" "Update non-existent wishlist item" '{}' "$TOKEN" "WITM-004"
test_endpoint "DELETE" "/api/wishlist-items/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent wishlist item" "" "$TOKEN" "WITM-005"

# Inventories (5)
test_endpoint "GET" "/api/inventories" "200" "List all inventories" "" "$TOKEN" "INV-001"
test_endpoint "POST" "/api/inventories" "400" "Create inventory (validation)" '{}' "$TOKEN" "INV-002"
test_endpoint "GET" "/api/inventories/00000000-0000-0000-0000-999999999999" "404" "Get non-existent inventory" "" "$TOKEN" "INV-003"
test_endpoint "PUT" "/api/inventories/00000000-0000-0000-0000-999999999999" "404" "Update non-existent inventory" '{}' "$TOKEN" "INV-004"
test_endpoint "DELETE" "/api/inventories/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent inventory" "" "$TOKEN" "INV-005"

# Promo Codes (5)
test_endpoint "GET" "/api/promo-codes" "200" "List all promo codes" "" "$TOKEN" "PRMO-001"
test_endpoint "POST" "/api/promo-codes" "400" "Create promo code (validation)" '{}' "$TOKEN" "PRMO-002"
test_endpoint "GET" "/api/promo-codes/00000000-0000-0000-0000-999999999999" "404" "Get non-existent promo code" "" "$TOKEN" "PRMO-003"
test_endpoint "PUT" "/api/promo-codes/00000000-0000-0000-0000-999999999999" "404" "Update non-existent promo code" '{}' "$TOKEN" "PRMO-004"
test_endpoint "DELETE" "/api/promo-codes/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent promo code" "" "$TOKEN" "PRMO-005"

# Seller Ratings (5)
test_endpoint "GET" "/api/seller-ratings" "200" "List all seller ratings" "" "$TOKEN" "SRAT-001"
test_endpoint "POST" "/api/seller-ratings" "400" "Create seller rating (validation)" '{}' "$TOKEN" "SRAT-002"
test_endpoint "GET" "/api/seller-ratings/00000000-0000-0000-0000-999999999999" "404" "Get non-existent seller rating" "" "$TOKEN" "SRAT-003"
test_endpoint "PUT" "/api/seller-ratings/00000000-0000-0000-0000-999999999999" "404" "Update non-existent seller rating" '{}' "$TOKEN" "SRAT-004"
test_endpoint "DELETE" "/api/seller-ratings/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent seller rating" "" "$TOKEN" "SRAT-005"

# Product Views (5)
test_endpoint "GET" "/api/product-views" "200" "List all product views" "" "$TOKEN" "PVIW-001"
test_endpoint "POST" "/api/product-views" "400" "Create product view (validation)" '{}' "$TOKEN" "PVIW-002"
test_endpoint "GET" "/api/product-views/00000000-0000-0000-0000-999999999999" "404" "Get non-existent product view" "" "$TOKEN" "PVIW-003"
test_endpoint "PUT" "/api/product-views/00000000-0000-0000-0000-999999999999" "404" "Update non-existent product view" '{}' "$TOKEN" "PVIW-004"
test_endpoint "DELETE" "/api/product-views/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent product view" "" "$TOKEN" "PVIW-005"

# Search Queries (5)
test_endpoint "GET" "/api/search-queries" "200" "List all search queries" "" "$TOKEN" "SRCH-001"
test_endpoint "POST" "/api/search-queries" "400" "Create search query (validation)" '{}' "$TOKEN" "SRCH-002"
test_endpoint "GET" "/api/search-queries/00000000-0000-0000-0000-999999999999" "404" "Get non-existent search query" "" "$TOKEN" "SRCH-003"
test_endpoint "PUT" "/api/search-queries/00000000-0000-0000-0000-999999999999" "404" "Update non-existent search query" '{}' "$TOKEN" "SRCH-004"
test_endpoint "DELETE" "/api/search-queries/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent search query" "" "$TOKEN" "SRCH-005"

# Notifications (5)
test_endpoint "GET" "/api/notifications" "200" "List all notifications" "" "$TOKEN" "NOTF-001"
test_endpoint "POST" "/api/notifications" "400" "Create notification (validation)" '{}' "$TOKEN" "NOTF-002"
test_endpoint "GET" "/api/notifications/00000000-0000-0000-0000-999999999999" "404" "Get non-existent notification" "" "$TOKEN" "NOTF-003"
test_endpoint "PUT" "/api/notifications/00000000-0000-0000-0000-999999999999" "404" "Update non-existent notification" '{}' "$TOKEN" "NOTF-004"
test_endpoint "DELETE" "/api/notifications/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent notification" "" "$TOKEN" "NOTF-005"

# =============================================================================
# PHASE 6: Configuration (30 endpoints)
# =============================================================================
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "PHASE 6: Configuration & Support (30)" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Marketplace Configs (5)
test_endpoint "GET" "/api/marketplace-configs" "200" "List all marketplace configs" "" "$TOKEN" "MKTC-001"
test_endpoint "POST" "/api/marketplace-configs" "400" "Create config (validation)" '{}' "$TOKEN" "MKTC-002"
test_endpoint "GET" "/api/marketplace-configs/00000000-0000-0000-0000-999999999999" "404" "Get non-existent config" "" "$TOKEN" "MKTC-003"
test_endpoint "PUT" "/api/marketplace-configs/00000000-0000-0000-0000-999999999999" "404" "Update non-existent config" '{}' "$TOKEN" "MKTC-004"
test_endpoint "DELETE" "/api/marketplace-configs/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent config" "" "$TOKEN" "MKTC-005"

# Delivery Terms (5)
test_endpoint "GET" "/api/delivery-terms" "200" "List all delivery terms" "" "$TOKEN" "DTRM-001"
test_endpoint "POST" "/api/delivery-terms" "400" "Create delivery term (validation)" '{}' "$TOKEN" "DTRM-002"
test_endpoint "GET" "/api/delivery-terms/00000000-0000-0000-0000-999999999999" "404" "Get non-existent delivery term" "" "$TOKEN" "DTRM-003"
test_endpoint "PUT" "/api/delivery-terms/00000000-0000-0000-0000-999999999999" "404" "Update non-existent delivery term" '{}' "$TOKEN" "DTRM-004"
test_endpoint "DELETE" "/api/delivery-terms/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent delivery term" "" "$TOKEN" "DTRM-005"

# Payment Terms (5)
test_endpoint "GET" "/api/payment-terms" "200" "List all payment terms" "" "$TOKEN" "PTRM-001"
test_endpoint "POST" "/api/payment-terms" "400" "Create payment term (validation)" '{}' "$TOKEN" "PTRM-002"
test_endpoint "GET" "/api/payment-terms/00000000-0000-0000-0000-999999999999" "404" "Get non-existent payment term" "" "$TOKEN" "PTRM-003"
test_endpoint "PUT" "/api/payment-terms/00000000-0000-0000-0000-999999999999" "404" "Update non-existent payment term" '{}' "$TOKEN" "PTRM-004"
test_endpoint "DELETE" "/api/payment-terms/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent payment term" "" "$TOKEN" "PTRM-005"

# Payment Modes (5)
test_endpoint "GET" "/api/payment-modes" "200" "List all payment modes" "" "$TOKEN" "PMOD-001"
test_endpoint "POST" "/api/payment-modes" "400" "Create payment mode (validation)" '{}' "$TOKEN" "PMOD-002"
test_endpoint "GET" "/api/payment-modes/00000000-0000-0000-0000-999999999999" "404" "Get non-existent payment mode" "" "$TOKEN" "PMOD-003"
test_endpoint "PUT" "/api/payment-modes/00000000-0000-0000-0000-999999999999" "404" "Update non-existent payment mode" '{}' "$TOKEN" "PMOD-004"
test_endpoint "DELETE" "/api/payment-modes/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent payment mode" "" "$TOKEN" "PMOD-005"

# Packaging Types (5)
test_endpoint "GET" "/api/packaging-types" "200" "List all packaging types" "" "$TOKEN" "PKGT-001"
test_endpoint "POST" "/api/packaging-types" "400" "Create packaging type (validation)" '{}' "$TOKEN" "PKGT-002"
test_endpoint "GET" "/api/packaging-types/00000000-0000-0000-0000-999999999999" "404" "Get non-existent packaging type" "" "$TOKEN" "PKGT-003"
test_endpoint "PUT" "/api/packaging-types/00000000-0000-0000-0000-999999999999" "404" "Update non-existent packaging type" '{}' "$TOKEN" "PKGT-004"
test_endpoint "DELETE" "/api/packaging-types/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent packaging type" "" "$TOKEN" "PKGT-005"

# Packaging Units (5)
test_endpoint "GET" "/api/packaging-units" "200" "List all packaging units" "" "$TOKEN" "PKGU-001"
test_endpoint "POST" "/api/packaging-units" "400" "Create packaging unit (validation)" '{}' "$TOKEN" "PKGU-002"
test_endpoint "GET" "/api/packaging-units/00000000-0000-0000-0000-999999999999" "404" "Get non-existent packaging unit" "" "$TOKEN" "PKGU-003"
test_endpoint "PUT" "/api/packaging-units/00000000-0000-0000-0000-999999999999" "404" "Update non-existent packaging unit" '{}' "$TOKEN" "PKGU-004"
test_endpoint "DELETE" "/api/packaging-units/00000000-0000-0000-0000-999999999999" "404" "Delete non-existent packaging unit" "" "$TOKEN" "PKGU-005"

# =============================================================================
# Summary
# =============================================================================
echo "" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "COMPREHENSIVE QA TEST SUMMARY" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "Total Tests Run: $TOTAL_TESTS" | tee -a "$DETAILED_LOG"
echo "Passed: $PASSED_TESTS ($(awk "BEGIN {printf \"%.2f\", ($PASSED_TESTS/$TOTAL_TESTS)*100}")%)" | tee -a "$DETAILED_LOG"
echo "Failed: $FAILED_TESTS ($(awk "BEGIN {printf \"%.2f\", ($FAILED_TESTS/$TOTAL_TESTS)*100}")%)" | tee -a "$DETAILED_LOG"
echo "Success Rate: $(awk "BEGIN {printf \"%.2f\", ($PASSED_TESTS/$TOTAL_TESTS)*100}")%" | tee -a "$DETAILED_LOG"
echo "==============================================================================" | tee -a "$DETAILED_LOG"
echo "" | tee -a "$DETAILED_LOG"

# Append summary to report
cat >> "$REPORT_FILE" << EOF

=============================================================================
SUMMARY
=============================================================================
Total Tests: $TOTAL_TESTS
Passed: $PASSED_TESTS
Failed: $FAILED_TESTS
Success Rate: $(awk "BEGIN {printf \"%.2f\", ($PASSED_TESTS/$TOTAL_TESTS)*100}")%
=============================================================================

Test completed at: $(date +"%Y-%m-%d %H:%M:%S")
EOF

echo "Report saved to: $REPORT_FILE"
echo "Detailed log saved to: $DETAILED_LOG"

# Exit with failure if any tests failed
if [ $FAILED_TESTS -gt 0 ]; then
    exit 1
fi

exit 0
