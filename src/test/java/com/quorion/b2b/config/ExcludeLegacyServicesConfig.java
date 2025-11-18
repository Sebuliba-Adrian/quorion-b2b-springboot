package com.quorion.b2b.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

/**
 * Configuration to exclude legacy services from component scanning in tests
 */
@TestConfiguration
@Profile("test")
@ComponentScan(
    basePackages = "com.quorion.b2b",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.quorion.b2b.service.CustomerService.class,
                com.quorion.b2b.service.TenantService.class,
                com.quorion.b2b.service.ProductService.class,
                com.quorion.b2b.service.AuthService.class,
                com.quorion.b2b.service.PaymentTermService.class,
                com.quorion.b2b.service.DeliveryTermService.class,
                com.quorion.b2b.service.ListPriceService.class,
                com.quorion.b2b.service.PurchaseOrderService.class,
                com.quorion.b2b.service.QuoteRequestService.class,
                com.quorion.b2b.service.LeadService.class,
                com.quorion.b2b.service.TenantAssociationService.class,
                com.quorion.b2b.service.TenantAddressService.class,
                com.quorion.b2b.service.ShipmentAdviceService.class,
                com.quorion.b2b.service.SellerMarketplaceService.class,
                com.quorion.b2b.service.ProductSKUService.class,
                com.quorion.b2b.service.PriceTierService.class,
                com.quorion.b2b.service.PaymentModeService.class,
                com.quorion.b2b.service.PackagingTypeService.class,
                com.quorion.b2b.service.PackagingUnitService.class,
                com.quorion.b2b.service.MarketplaceConfigService.class
            }
        )
    }
)
public class ExcludeLegacyServicesConfig {
    // This configuration excludes legacy services from component scanning
    // so our test stubs can be used instead
}


