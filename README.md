# Quorion B2B API - Spring Boot Version

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

A comprehensive Spring Boot implementation of the B2B distributor buyer-seller negotiation system.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [State Machines](#state-machines)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Testing](#testing)
- [Documentation](#documentation)

## Features

### Complete Transaction Lifecycle

- **Shopping Cart**: Full-featured cart with add/remove/update items, soft delete, cart-to-lead conversion
- **Lead Management**: Create, forward to distributors, accept/reject with FSM state machine
- **Quote Negotiation**: Multi-step price negotiation with state machine
- **Order Processing**: Full order fulfillment workflow with FSM
- **Shipping**: Shipment advice and tracking
- **Price Tiers**: Volume-based pricing with buyer-specific rates

### State Machines

- **Lead State Machine**: `no_lead → new → converted/forwarded → accepted/rejected`
- **Quote State Machine**: `no_request → new → requested → responded → accepted`
- **Order State Machine**: `no_order → new → accepted → in_progress → invoiced → shipped → completed`

### Multi-Tenant Architecture

- **Tenant Types**: Seller, Buyer, Distributor
- **Tenant Associations**: Manage relationships between tenants
- **Distributor Integration**: Forward leads, create distributor SKUs

### Products & SKUs

- **Product Management**: Create products with multiple SKUs
- **SKU Types**: Product SKU, Distributor SKU, Buyer SKU
- **Packaging**: Packaging types and units
- **Price Tiers**: Volume-based pricing per buyer/destination

### Flexible Marketplace Configuration

- **Multiple Marketplace Modes**: B2B Negotiation, Direct Marketplace, Hybrid, Multi-Vendor
- **30+ Feature Flags**: Granular control over marketplace behavior
- **Seller Storefronts**: Individual seller marketplace configurations
- **Global & Per-Seller Settings**: Override global settings per seller
- **Cached Configuration**: High-performance configuration with Spring caching

## Technology Stack

### Backend Framework
- **Spring Boot 3.2.5** - Main application framework
- **Spring Data JPA** - Data persistence layer
- **Spring Web** - RESTful web services
- **Hibernate** - ORM implementation

### Database
- **PostgreSQL** - Production database
- **H2** - In-memory database for development/testing

### Development Tools
- **Lombok** - Reduce boilerplate code
- **MapStruct** - DTO mapping (ready for implementation)
- **SpringDoc OpenAPI** - API documentation (Swagger UI)

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing with Docker

### Build Tool
- **Maven** - Dependency management and build

## Project Structure

```
quorion-b2b-springboot/
├── src/
│   ├── main/
│   │   ├── java/com/quorion/b2b/
│   │   │   ├── QuorionB2bApplication.java
│   │   │   ├── config/               # Configuration classes
│   │   │   ├── controller/           # REST controllers
│   │   │   │   ├── LeadController.java
│   │   │   │   ├── QuoteRequestController.java
│   │   │   │   └── PurchaseOrderController.java
│   │   │   ├── model/                # Domain models
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── tenant/           # Tenant entities
│   │   │   │   │   ├── Tenant.java
│   │   │   │   │   ├── TenantAddress.java
│   │   │   │   │   ├── TenantAssociation.java
│   │   │   │   │   ├── MarketplaceConfig.java
│   │   │   │   │   └── SellerMarketplace.java
│   │   │   │   ├── product/          # Product entities
│   │   │   │   │   ├── Product.java
│   │   │   │   │   ├── ProductSKU.java
│   │   │   │   │   ├── PackagingType.java
│   │   │   │   │   ├── PackagingUnit.java
│   │   │   │   │   └── ListPrice.java
│   │   │   │   └── commerce/         # Commerce entities
│   │   │   │       ├── Customer.java
│   │   │   │       ├── Cart.java
│   │   │   │       ├── CartItem.java
│   │   │   │       ├── Lead.java
│   │   │   │       ├── QuoteRequest.java
│   │   │   │       ├── QuoteRequestDetail.java
│   │   │   │       ├── PurchaseOrder.java
│   │   │   │       ├── PurchaseOrderDetail.java
│   │   │   │       ├── PriceTier.java
│   │   │   │       ├── ShipmentAdvice.java
│   │   │   │       ├── DeliveryTerm.java
│   │   │   │       ├── PaymentTerm.java
│   │   │   │       └── PaymentMode.java
│   │   │   ├── repository/           # Spring Data repositories
│   │   │   │   ├── TenantRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── LeadRepository.java
│   │   │   │   ├── QuoteRequestRepository.java
│   │   │   │   ├── PurchaseOrderRepository.java
│   │   │   │   └── ...
│   │   │   ├── service/              # Business logic services
│   │   │   │   ├── LeadService.java
│   │   │   │   ├── QuoteRequestService.java
│   │   │   │   └── PurchaseOrderService.java
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── mapper/               # DTO Mappers (MapStruct)
│   │   │   ├── exception/            # Custom exceptions
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── InvalidStateTransitionException.java
│   │   │   └── statemachine/         # State machine configs
│   │   └── resources/
│   │       └── application.yml       # Application configuration
│   └── test/                         # Test classes
├── pom.xml                           # Maven dependencies
└── README.md
```

## Installation

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 12+ (for production)

### Setup

1. **Clone the repository**
```bash
cd quorion-b2b-springboot
```

2. **Configure database (Optional)**

For development, the application uses H2 in-memory database by default. For production, update `application.yml`:

```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/quorion_db
    username: your_username
    password: your_password
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Base URL
`http://localhost:8080/api`

### Leads API

- `GET /api/commerce/leads` - List all leads
- `POST /api/commerce/leads` - Create lead
- `GET /api/commerce/leads/{id}` - Get lead details
- `POST /api/commerce/leads/{id}/create_lead` - Initialize lead (state: new)
- `POST /api/commerce/leads/{id}/convert` - Convert to quote
- `POST /api/commerce/leads/{id}/accept_by_distributor` - Distributor accepts
- `POST /api/commerce/leads/{id}/reject_by_distributor` - Distributor rejects

### Quote Requests API

- `GET /api/commerce/quotes` - List quotes
- `POST /api/commerce/quotes` - Create quote
- `GET /api/commerce/quotes/{id}` - Get quote details
- `POST /api/commerce/quotes/{id}/buyer_requests` - Buyer submits quote
- `POST /api/commerce/quotes/{id}/seller_responds` - Seller responds with pricing
- `POST /api/commerce/quotes/{id}/buyer_accepts` - Buyer accepts (creates order)
- `POST /api/commerce/quotes/{id}/cancel` - Cancel quote

### Purchase Orders API

- `GET /api/commerce/orders` - List orders
- `GET /api/commerce/orders/{id}` - Get order details
- `POST /api/commerce/orders/{id}/accept` - Seller accepts order
- `POST /api/commerce/orders/{id}/make_in_progress` - Start processing
- `POST /api/commerce/orders/{id}/invoice` - Generate invoice
- `POST /api/commerce/orders/{id}/ship_order` - Ship order
- `POST /api/commerce/orders/{id}/receive_payment` - Receive payment
- `POST /api/commerce/orders/{id}/complete` - Complete order
- `POST /api/commerce/orders/{id}/cancel` - Cancel order

## State Machines

### Lead State Machine

```
no_lead → [create] → new → [convert] → converted
                              ↓
                         [forward_to_distributor]
                              ↓
                         forwarded → [accept_by_distributor] → accepted_by_distributor
                                  → [reject_by_distributor] → rejected_by_distributor
```

### Quote State Machine

```
no_request → [create_quote] → new → [buyer_requests] → requested
                                                    ↓
                                            [seller_responds]
                                                    ↓
                                            responded → [buyer_accepts] → accepted
                                                    ↓
                                            [buyer_responds] → requested (re-negotiation)
```

### Order State Machine

```
no_order → [create_order] → new → [accept] → accepted → [make_in_progress] → in_progress
                                                                                    ↓
                                                                            [invoice] → invoiced
                                                                                    ↓
                                                                            [ship_order] → shipped
                                                                                    ↓
                                                                        [receive_payment] → payment_received
                                                                                    ↓
                                                                            [complete] → completed
```

## Database Schema

### Core Entities

#### Tenant Module
- `Tenant` - Multi-tenant companies (Seller/Buyer/Distributor)
- `TenantAddress` - Addresses for tenants
- `TenantAssociation` - Relationships between tenants
- `MarketplaceConfig` - Global marketplace configuration
- `SellerMarketplace` - Seller-specific marketplace settings

#### Product Module
- `Product` - Base product model
- `ProductSKU` - SKU variants with packaging
- `PackagingType` - Packaging types (Drum, Bag, etc.)
- `PackagingUnit` - Units (kg, L, etc.)
- `ListPrice` - Base list prices

#### Commerce Module
- `Customer` - Customer created from converted lead
- `Cart` - Shopping cart
- `CartItem` - Cart line items
- `Lead` - Sales leads (with FSM)
- `QuoteRequest` - Price negotiation documents (with FSM)
- `QuoteRequestDetail` - Quote line items
- `PurchaseOrder` - Confirmed orders (with FSM)
- `PurchaseOrderDetail` - Order line items
- `PriceTier` - Volume-based pricing
- `ShipmentAdvice` - Shipping information
- `DeliveryTerm` - Delivery terms (FOB, CIF, etc.)
- `PaymentTerm` - Payment terms (Net 30, Net 60, etc.)
- `PaymentMode` - Payment modes

## Configuration

### Application Properties

Key configuration properties in `application.yml`:

```yaml
quorion:
  b2b:
    marketplace:
      default-currency: USD
      session-timeout-minutes: 30
      max-cart-items: 100
      min-order-value: 0.00
    cache:
      marketplace-config-ttl: 300
```

### Profiles

- **dev** - Development profile (H2 database, verbose logging)
- **prod** - Production profile (PostgreSQL, minimal logging)

## Testing

### Run all tests
```bash
mvn test
```

### Run with coverage
```bash
mvn test jacoco:report
```

Coverage report will be available in `target/site/jacoco/index.html`

## Documentation

### Swagger UI

Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification

View the OpenAPI JSON specification at:
```
http://localhost:8080/api-docs
```

## Development

### Build the project
```bash
mvn clean install
```

### Run in development mode
```bash
mvn spring-boot:run
```

### Package for deployment
```bash
mvn clean package
java -jar target/quorion-b2b-api-1.0.0-SNAPSHOT.jar
```

## Key Features Implemented

### State Machine Enforcement
- Invalid transitions are prevented
- State changes only through defined service methods
- Validation logic in service layer

### Price Negotiation
- Automatic price resolution from tiers
- Manual price setting
- Re-negotiation support
- Price calculation (subtotal + shipping)

### Order Creation
- Automatic order creation from accepted quote
- Items copied from quote to order
- Pricing preserved
- Shipping costs transferred

### Distributor Workflow
- Lead forwarding creates child leads
- Parent-child relationship tracking
- Distributor-specific actions

## Roadmap

Future enhancements:

- [ ] Add authentication/authorization (Spring Security + JWT)
- [ ] Implement comprehensive DTO layer with MapStruct
- [ ] Add email notifications
- [ ] Add PDF generation for quotes/orders
- [ ] Add inventory management
- [ ] Add payment gateway integration
- [ ] Add reporting/analytics
- [ ] Add webhooks for external integrations
- [ ] Implement full test coverage
- [ ] Add Docker support
- [ ] Add Kubernetes deployment configurations

## License

This is an MVP implementation for demonstration purposes.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact

For questions or support, please open an issue in the repository.

---

**Status: Ready for Development** 🚀

This Spring Boot version provides a solid foundation for building a production-ready B2B marketplace with full state machine support, multi-tenant architecture, and comprehensive business logic.
