package com.quorion.b2b.config;

import com.quorion.b2b.repository.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Legacy Repository Mocks
 * 
 * Provides mock implementations of ALL legacy repositories
 * This allows legacy services to be created successfully during tests
 * Using @Configuration ensures beans are loaded early
 */
@Configuration
@Profile("test")
public class LegacyRepositoryMocks {

    @Bean
    @Primary
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    @Primary
    public TenantRepository tenantRepository() {
        return Mockito.mock(TenantRepository.class);
    }

    @Bean
    @Primary
    public TenantAssociationRepository tenantAssociationRepository() {
        return Mockito.mock(TenantAssociationRepository.class);
    }

    @Bean
    @Primary
    public ProductRepository productRepository() {
        return Mockito.mock(ProductRepository.class);
    }

    @Bean
    @Primary
    public ProductSKURepository productSKURepository() {
        return Mockito.mock(ProductSKURepository.class);
    }

    @Bean
    @Primary
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    @Primary
    public PaymentTermRepository paymentTermRepository() {
        return Mockito.mock(PaymentTermRepository.class);
    }

    @Bean
    @Primary
    public DeliveryTermRepository deliveryTermRepository() {
        return Mockito.mock(DeliveryTermRepository.class);
    }

    @Bean
    @Primary
    public ListPriceRepository listPriceRepository() {
        return Mockito.mock(ListPriceRepository.class);
    }

    @Bean
    @Primary
    public PurchaseOrderRepository purchaseOrderRepository() {
        return Mockito.mock(PurchaseOrderRepository.class);
    }

    @Bean
    @Primary
    public QuoteRequestRepository quoteRequestRepository() {
        return Mockito.mock(QuoteRequestRepository.class);
    }

    @Bean
    @Primary
    public LeadRepository leadRepository() {
        return Mockito.mock(LeadRepository.class);
    }

    @Bean
    @Primary
    public TenantAddressRepository tenantAddressRepository() {
        return Mockito.mock(TenantAddressRepository.class);
    }

    @Bean
    @Primary
    public ShipmentAdviceRepository shipmentAdviceRepository() {
        return Mockito.mock(ShipmentAdviceRepository.class);
    }

    @Bean
    @Primary
    public SellerMarketplaceRepository sellerMarketplaceRepository() {
        return Mockito.mock(SellerMarketplaceRepository.class);
    }

    @Bean
    @Primary
    public PriceTierRepository priceTierRepository() {
        return Mockito.mock(PriceTierRepository.class);
    }

    @Bean
    @Primary
    public PaymentModeRepository paymentModeRepository() {
        return Mockito.mock(PaymentModeRepository.class);
    }

    @Bean
    @Primary
    public PackagingTypeRepository packagingTypeRepository() {
        return Mockito.mock(PackagingTypeRepository.class);
    }

    @Bean
    @Primary
    public PackagingUnitRepository packagingUnitRepository() {
        return Mockito.mock(PackagingUnitRepository.class);
    }

    @Bean
    @Primary
    public MarketplaceConfigRepository marketplaceConfigRepository() {
        return Mockito.mock(MarketplaceConfigRepository.class);
    }
}

