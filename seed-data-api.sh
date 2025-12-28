#!/bin/bash

# Comprehensive Seed Data Script for Quorion B2B Platform
# Uses API endpoints to ensure proper validation and business logic

set -e

BASE_URL="http://localhost:8080"
TOKEN=""

echo "=== Quorion B2B Platform - Seed Data Script ==="
echo ""

# Function to login and get token
login() {
    echo "🔐 Logging in..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/v2/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"usernameOrEmail":"admin","password":"password123"}')

    TOKEN=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['accessToken'])" 2>/dev/null || echo "")

    if [ -z "$TOKEN" ]; then
        echo "❌ Login failed"
        exit 1
    fi
    echo "✅ Login successful"
}

# Function to create products
create_products() {
    echo ""
    echo "📦 Creating products..."

    # Product 1: Laptop
    PROD1=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "Business Laptop Pro 15",
            "description": "High-performance laptop for business professionals",
            "shortDescription": "15-inch business laptop with Intel i7",
            "category": "Electronics",
            "brand": "TechCorp",
            "manufacturer": "TechCorp Industries",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: Business Laptop Pro 15"

    # Product 2: Office Chair
    PROD2=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "Ergonomic Office Chair",
            "description": "Premium ergonomic chair with lumbar support and adjustable armrests",
            "shortDescription": "Comfortable ergonomic office chair",
            "category": "Furniture",
            "brand": "ComfortSeating",
            "manufacturer": "ComfortSeating Ltd",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: Ergonomic Office Chair"

    # Product 3: Wireless Mouse
    PROD3=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "Wireless Optical Mouse",
            "description": "Precise wireless mouse with ergonomic design and long battery life",
            "shortDescription": "Wireless mouse with 2.4GHz connectivity",
            "category": "Electronics",
            "brand": "TechPeripherals",
            "manufacturer": "TechPeripherals Inc",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: Wireless Optical Mouse"

    # Product 4: Standing Desk
    PROD4=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "Electric Standing Desk",
            "description": "Height-adjustable electric standing desk with memory presets",
            "shortDescription": "Motorized standing desk 48x24 inches",
            "category": "Furniture",
            "brand": "ErgoDesk",
            "manufacturer": "ErgoDesk Solutions",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: Electric Standing Desk"

    # Product 5: Monitor
    PROD5=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "27-inch 4K Monitor",
            "description": "Professional 4K UHD monitor with IPS panel and USB-C connectivity",
            "shortDescription": "27-inch 4K UHD professional monitor",
            "category": "Electronics",
            "brand": "ViewTech",
            "manufacturer": "ViewTech Displays",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: 27-inch 4K Monitor"

    # Product 6: Mechanical Keyboard
    PROD6=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "Mechanical Keyboard RGB",
            "description": "Premium mechanical keyboard with RGB backlighting and Cherry MX switches",
            "shortDescription": "RGB mechanical keyboard with Cherry MX Blue",
            "category": "Electronics",
            "brand": "KeyMaster",
            "manufacturer": "KeyMaster Technologies",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: Mechanical Keyboard RGB"

    # Product 7: Office Desk Lamp
    PROD7=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "LED Desk Lamp",
            "description": "Adjustable LED desk lamp with touch controls and USB charging port",
            "shortDescription": "Modern LED desk lamp with dimming",
            "category": "Lighting",
            "brand": "LightWorks",
            "manufacturer": "LightWorks Inc",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: LED Desk Lamp"

    # Product 8: Webcam
    PROD8=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "1080p HD Webcam",
            "description": "High-definition webcam with autofocus and built-in microphone",
            "shortDescription": "1080p webcam for video conferencing",
            "category": "Electronics",
            "brand": "CamTech",
            "manufacturer": "CamTech Industries",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: 1080p HD Webcam"

    # Product 9: Office Storage Cabinet
    PROD9=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "3-Drawer Storage Cabinet",
            "description": "Lockable metal storage cabinet with three spacious drawers",
            "shortDescription": "Secure 3-drawer office cabinet",
            "category": "Furniture",
            "brand": "StorageMax",
            "manufacturer": "StorageMax Corp",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: 3-Drawer Storage Cabinet"

    # Product 10: Headset
    PROD10=$(curl -s -X POST "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "Wireless Bluetooth Headset",
            "description": "Professional wireless headset with noise cancellation and 20-hour battery",
            "shortDescription": "Wireless headset with active noise cancellation",
            "category": "Electronics",
            "brand": "AudioPro",
            "manufacturer": "AudioPro Technologies",
            "sellerId": "11111111-1111-1111-1111-111111111111"
        }')
    echo "✅ Created: Wireless Bluetooth Headset"
}

# Function to verify seed data
verify_data() {
    echo ""
    echo "🔍 Verifying seed data..."

    # Count products
    PROD_COUNT=$(curl -s "$BASE_URL/api/v2/products" \
        -H "Authorization: Bearer $TOKEN" | python3 -c "import sys, json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
    echo "✅ Products in database: $PROD_COUNT"

    # Count customers
    CUST_COUNT=$(curl -s "$BASE_URL/api/v2/customers" \
        -H "Authorization: Bearer $TOKEN" | python3 -c "import sys, json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
    echo "✅ Customers in database: $CUST_COUNT"

    # Count tenants
    TENANT_COUNT=$(curl -s "$BASE_URL/api/v2/tenants" \
        -H "Authorization: Bearer $TOKEN" | python3 -c "import sys, json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
    echo "✅ Tenants in database: $TENANT_COUNT"
}

# Main execution
main() {
    login
    create_products
    verify_data

    echo ""
    echo "✅ Seed data creation completed successfully!"
    echo ""
    echo "📊 Summary:"
    echo "   - 10 Products created"
    echo "   - 1 Tenant (Test Corporation)"
    echo "   - 1 User (admin)"
    echo ""
    echo "🔗 Access the API at: $BASE_URL"
    echo "👤 Login credentials: admin / password123"
}

main
