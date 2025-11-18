package com.quorion.b2b.config;

import com.quorion.b2b.repository.*;
import com.quorion.b2b.service.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Legacy Service Stubs using @MockBean
 * 
 * This approach uses Spring Boot's @MockBean which properly integrates with Spring's DI
 */
@Configuration
@Profile("test")
public class LegacyServiceStubsWithMockBean {

    // Mock all repositories
    @MockBean
    private CustomerRepository customerRepository;
    
    @MockBean
    private TenantRepository tenantRepository;
    
    @MockBean
    private TenantAssociationRepository tenantAssociationRepository;
    
    @MockBean
    private ProductRepository productRepository;
    
    @MockBean
    private ProductSKURepository productSKURepository;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private PaymentTermRepository paymentTermRepository;
    
    @MockBean
    private DeliveryTermRepository deliveryTermRepository;
    
    @MockBean
    private ListPriceRepository listPriceRepository;
    
    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @MockBean
    private QuoteRequestRepository quoteRequestRepository;
    
    @MockBean
    private LeadRepository leadRepository;
    
    @MockBean
    private TenantAddressRepository tenantAddressRepository;
    
    @MockBean
    private ShipmentAdviceRepository shipmentAdviceRepository;
    
    @MockBean
    private SellerMarketplaceRepository sellerMarketplaceRepository;
    
    @MockBean
    private PriceTierRepository priceTierRepository;
    
    @MockBean
    private PaymentModeRepository paymentModeRepository;
    
    @MockBean
    private PackagingTypeRepository packagingTypeRepository;
    
    @MockBean
    private PackagingUnitRepository packagingUnitRepository;
    
    @MockBean
    private MarketplaceConfigRepository marketplaceConfigRepository;

    // The real @Service beans will be created and will use the mocked repositories above
    // No need to create stub services - the real ones will work with mocked repos
}


