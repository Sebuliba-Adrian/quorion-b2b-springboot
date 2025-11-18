package com.quorion.b2b.config;

import com.quorion.b2b.repository.*;
import com.quorion.b2b.service.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Comprehensive Legacy Service Stubs
 * 
 * Provides mock implementations of ALL legacy services required by legacy controllers
 * This allows the application to start during tests while legacy controllers are being migrated
 * 
 * These beans override the @Service annotated legacy services using @Primary
 * Using @Configuration instead of @TestConfiguration ensures beans are loaded early
 */
@Configuration
@Profile("test")
public class ComprehensiveLegacyServiceStubs {

    // Core Services
    // Note: These services will use the mocked repositories from LegacyRepositoryMocks
    // The @Primary annotation ensures these override any @Service beans
    @Bean
    @Primary
    public CustomerService customerService(CustomerRepository customerRepository) {
        return new CustomerService(customerRepository);
    }

    @Bean
    @Primary
    public TenantService tenantService(TenantRepository tenantRepository, TenantAssociationRepository tenantAssociationRepository) {
        return new TenantService(tenantRepository, tenantAssociationRepository);
    }

    @Bean
    @Primary
    public ProductService productService(ProductRepository productRepository, ProductSKURepository productSKURepository) {
        return new ProductService(productRepository, productSKURepository);
    }

    @Bean
    @Primary
    public AuthService authService(
            UserRepository userRepository,
            TenantRepository tenantRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            org.springframework.security.authentication.AuthenticationManager authenticationManager,
            com.quorion.b2b.security.JwtTokenProvider jwtTokenProvider) {
        return new AuthService(userRepository, tenantRepository, passwordEncoder, authenticationManager, jwtTokenProvider);
    }
    
    @Bean
    @Primary
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return Mockito.mock(org.springframework.security.crypto.password.PasswordEncoder.class);
    }
    
    @Bean
    @Primary
    public org.springframework.security.authentication.AuthenticationManager authenticationManager() {
        return Mockito.mock(org.springframework.security.authentication.AuthenticationManager.class);
    }
    
    @Bean
    @Primary
    public com.quorion.b2b.security.JwtTokenProvider jwtTokenProvider() {
        return Mockito.mock(com.quorion.b2b.security.JwtTokenProvider.class);
    }

    // Additional Legacy Services
    @Bean
    @Primary
    public PaymentTermService paymentTermService(PaymentTermRepository paymentTermRepository) {
        return new PaymentTermService(paymentTermRepository);
    }

    @Bean
    @Primary
    public DeliveryTermService deliveryTermService(DeliveryTermRepository deliveryTermRepository) {
        return new DeliveryTermService(deliveryTermRepository);
    }

    @Bean
    @Primary
    public ListPriceService listPriceService(ListPriceRepository listPriceRepository) {
        return new ListPriceService(listPriceRepository);
    }

    @Bean
    @Primary
    public PurchaseOrderService purchaseOrderService(PurchaseOrderRepository purchaseOrderRepository) {
        return new PurchaseOrderService(purchaseOrderRepository);
    }

    @Bean
    @Primary
    public QuoteRequestService quoteRequestService(QuoteRequestRepository quoteRequestRepository, PurchaseOrderRepository purchaseOrderRepository) {
        return new QuoteRequestService(quoteRequestRepository, purchaseOrderRepository);
    }

    @Bean
    @Primary
    public LeadService leadService(LeadRepository leadRepository) {
        return new LeadService(leadRepository);
    }

    @Bean
    @Primary
    public TenantAssociationService tenantAssociationService(TenantAssociationRepository tenantAssociationRepository) {
        return new TenantAssociationService(tenantAssociationRepository);
    }

    @Bean
    @Primary
    public TenantAddressService tenantAddressService(TenantAddressRepository tenantAddressRepository) {
        return new TenantAddressService(tenantAddressRepository);
    }

    @Bean
    @Primary
    public ShipmentAdviceService shipmentAdviceService(ShipmentAdviceRepository shipmentAdviceRepository) {
        return new ShipmentAdviceService(shipmentAdviceRepository);
    }

    @Bean
    @Primary
    public MarketplaceConfigService marketplaceConfigService(MarketplaceConfigRepository marketplaceConfigRepository) {
        return new MarketplaceConfigService(marketplaceConfigRepository);
    }

    @Bean
    @Primary
    public SellerMarketplaceService sellerMarketplaceService(SellerMarketplaceRepository sellerMarketplaceRepository, MarketplaceConfigService marketplaceConfigService) {
        return new SellerMarketplaceService(sellerMarketplaceRepository, marketplaceConfigService);
    }

    @Bean
    @Primary
    public ProductSKUService productSKUService(ProductSKURepository productSKURepository, TenantRepository tenantRepository) {
        return new ProductSKUService(productSKURepository, tenantRepository);
    }

    @Bean
    @Primary
    public PriceTierService priceTierService(PriceTierRepository priceTierRepository) {
        return new PriceTierService(priceTierRepository);
    }

    @Bean
    @Primary
    public PaymentModeService paymentModeService(PaymentModeRepository paymentModeRepository) {
        return new PaymentModeService(paymentModeRepository);
    }

    @Bean
    @Primary
    public PackagingTypeService packagingTypeService(PackagingTypeRepository packagingTypeRepository) {
        return new PackagingTypeService(packagingTypeRepository);
    }

    @Bean
    @Primary
    public PackagingUnitService packagingUnitService(PackagingUnitRepository packagingUnitRepository) {
        return new PackagingUnitService(packagingUnitRepository);
    }
}

