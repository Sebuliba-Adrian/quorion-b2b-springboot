package com.quorion.b2b.service;

import com.quorion.b2b.exception.InvalidStateTransitionException;
import com.quorion.b2b.model.commerce.*;
import com.quorion.b2b.model.product.Product;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import com.quorion.b2b.repository.PurchaseOrderRepository;
import com.quorion.b2b.repository.QuoteRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QuoteRequestService
 * Tests all state machine transitions and business logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QuoteRequestService Tests")
class QuoteRequestServiceTest {

    @Mock
    private QuoteRequestRepository quoteRequestRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private QuoteRequestService quoteRequestService;

    private UUID quoteId;
    private QuoteRequest testQuote;
    private Tenant buyer;
    private Tenant seller;
    private TenantAddress warehouse;
    private DeliveryTerm deliveryTerm;
    private PaymentTerm paymentTerm;
    private PaymentMode paymentMode;

    @BeforeEach
    void setUp() {
        quoteId = UUID.randomUUID();
        buyer = createTenant("Buyer Corp");
        seller = createTenant("Seller Inc");
        warehouse = createWarehouse();
        deliveryTerm = createDeliveryTerm();
        paymentTerm = createPaymentTerm();
        paymentMode = createPaymentMode();

        testQuote = QuoteRequest.builder()
                .buyer(buyer)
                .seller(seller)
                .warehouse(warehouse)
                .deliveryTerm(deliveryTerm)
                .paymentTerm(paymentTerm)
                .paymentMode(paymentMode)
                .number("QT-12345")
                .status(QuoteStatus.NO_REQUEST)
                .shippingCost(BigDecimal.TEN)
                .currency("USD")
                .isActive(true)
                .items(new ArrayList<>())
                .build();
        testQuote.setId(quoteId);
    }

    // ==================== Basic CRUD Tests ====================

    @Test
    @DisplayName("Should find all quote requests")
    void testFindAll() {
        // Arrange
        List<QuoteRequest> quotes = List.of(testQuote);
        when(quoteRequestRepository.findAll()).thenReturn(quotes);

        // Act
        List<QuoteRequest> result = quoteRequestService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(quoteRequestRepository).findAll();
    }

    @Test
    @DisplayName("Should find quote by ID successfully")
    void testFindById_Success() {
        // Arrange
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act
        QuoteRequest result = quoteRequestService.findById(quoteId);

        // Assert
        assertNotNull(result);
        assertEquals(quoteId, result.getId());
        verify(quoteRequestRepository).findById(quoteId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when quote not found")
    void testFindById_NotFound() {
        // Arrange
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            quoteRequestService.findById(quoteId);
        });
        verify(quoteRequestRepository).findById(quoteId);
    }

    @Test
    @DisplayName("Should create new quote with NO_REQUEST status")
    void testCreate() {
        // Arrange
        QuoteRequest newQuote = QuoteRequest.builder()
                .buyer(buyer)
                .seller(seller)
                .build();

        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(newQuote);

        // Act
        QuoteRequest result = quoteRequestService.create(newQuote);

        // Assert
        assertNotNull(result);
        assertEquals(QuoteStatus.NO_REQUEST, result.getStatus());
        assertNotNull(result.getNumber());
        assertTrue(result.getNumber().startsWith("QT-"));
        verify(quoteRequestRepository).save(newQuote);
    }

    // ==================== State Machine Transition Tests ====================

    @Test
    @DisplayName("Should transition from NO_REQUEST to NEW")
    void testCreateQuote() {
        // Arrange
        testQuote.setStatus(QuoteStatus.NO_REQUEST);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);

        // Act
        QuoteRequest result = quoteRequestService.createQuote(quoteId);

        // Assert
        assertEquals(QuoteStatus.NEW, result.getStatus());
        assertTrue(result.getIsActive());
        verify(quoteRequestRepository).findById(quoteId);
        verify(quoteRequestRepository).save(testQuote);
    }

    @Test
    @DisplayName("Should transition from NEW to REQUESTED (buyer requests quote)")
    void testBuyerRequests() {
        // Arrange
        testQuote.setStatus(QuoteStatus.NEW);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);

        // Act
        QuoteRequest result = quoteRequestService.buyerRequests(quoteId);

        // Assert
        assertEquals(QuoteStatus.REQUESTED, result.getStatus());
        verify(quoteRequestRepository).findById(quoteId);
        verify(quoteRequestRepository).save(testQuote);
    }

    @Test
    @DisplayName("Should transition from REQUESTED to RESPONDED (seller responds with pricing)")
    void testSellerResponds() {
        // Arrange
        testQuote.setStatus(QuoteStatus.REQUESTED);

        QuoteRequestDetail item1 = createQuoteItem(testQuote, new BigDecimal("10"));
        QuoteRequestDetail item2 = createQuoteItem(testQuote, new BigDecimal("20"));
        testQuote.getItems().add(item1);
        testQuote.getItems().add(item2);

        List<QuoteRequestDetail> itemUpdates = new ArrayList<>();
        QuoteRequestDetail update1 = new QuoteRequestDetail();
        update1.setId(item1.getId());
        update1.setPricePerUnit(new BigDecimal("15.00"));
        itemUpdates.add(update1);

        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);

        // Act
        QuoteRequest result = quoteRequestService.sellerResponds(quoteId, itemUpdates, new BigDecimal("12.00"));

        // Assert
        assertEquals(QuoteStatus.RESPONDED, result.getStatus());
        assertEquals(new BigDecimal("12.00"), result.getShippingCost());
        assertEquals(new BigDecimal("15.00"), item1.getPricePerUnit());
        verify(quoteRequestRepository).save(testQuote);
    }

    @Test
    @DisplayName("Should transition from RESPONDED to REQUESTED (buyer counter-offers for re-negotiation)")
    void testBuyerResponds_Renegotiation() {
        // Arrange
        testQuote.setStatus(QuoteStatus.RESPONDED);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);

        // Act
        QuoteRequest result = quoteRequestService.buyerResponds(quoteId);

        // Assert
        assertEquals(QuoteStatus.REQUESTED, result.getStatus());
        verify(quoteRequestRepository).save(testQuote);
    }

    @Test
    @DisplayName("Should transition from RESPONDED to ACCEPTED and create PurchaseOrder")
    void testBuyerAccepts() {
        // Arrange
        testQuote.setStatus(QuoteStatus.RESPONDED);

        QuoteRequestDetail quoteItem = createQuoteItem(testQuote, new BigDecimal("25.00"));
        quoteItem.setNoOfUnits(new BigDecimal("10"));
        quoteItem.setTotalQuantity(new BigDecimal("100"));
        testQuote.getItems().add(quoteItem);

        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        QuoteRequest result = quoteRequestService.buyerAccepts(quoteId);

        // Assert
        assertEquals(QuoteStatus.ACCEPTED, result.getStatus());
        assertFalse(result.getIsActive());

        // Verify purchase order was created
        ArgumentCaptor<PurchaseOrder> orderCaptor = ArgumentCaptor.forClass(PurchaseOrder.class);
        verify(purchaseOrderRepository).save(orderCaptor.capture());

        PurchaseOrder createdOrder = orderCaptor.getValue();
        assertNotNull(createdOrder);
        assertEquals(buyer, createdOrder.getBuyer());
        assertEquals(seller, createdOrder.getSeller());
        assertEquals(testQuote, createdOrder.getQuoteRequest());
        assertEquals(OrderStatus.NEW, createdOrder.getStatus());
        assertTrue(createdOrder.getNumber().startsWith("PO-"));
        assertEquals(1, createdOrder.getItems().size());
    }

    @Test
    @DisplayName("Should allow seller to modify pricing while RESPONDED")
    void testSellerModifies() {
        // Arrange
        testQuote.setStatus(QuoteStatus.RESPONDED);

        QuoteRequestDetail item = createQuoteItem(testQuote, new BigDecimal("20.00"));
        testQuote.getItems().add(item);

        List<QuoteRequestDetail> itemUpdates = new ArrayList<>();
        QuoteRequestDetail update = new QuoteRequestDetail();
        update.setId(item.getId());
        update.setPricePerUnit(new BigDecimal("18.00"));
        itemUpdates.add(update);

        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);

        // Act
        QuoteRequest result = quoteRequestService.sellerModifies(quoteId, itemUpdates, new BigDecimal("8.00"));

        // Assert
        assertEquals(QuoteStatus.RESPONDED, result.getStatus());
        assertEquals(new BigDecimal("18.00"), item.getPricePerUnit());
        assertEquals(new BigDecimal("8.00"), result.getShippingCost());
        verify(quoteRequestRepository).save(testQuote);
    }

    @Test
    @DisplayName("Should allow seller to decline quote from any state")
    void testSellerDeclines() {
        // Arrange
        testQuote.setStatus(QuoteStatus.REQUESTED);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);

        // Act
        QuoteRequest result = quoteRequestService.sellerDeclines(quoteId);

        // Assert
        assertEquals(QuoteStatus.DECLINED, result.getStatus());
        assertFalse(result.getIsActive());
        verify(quoteRequestRepository).save(testQuote);
    }

    @Test
    @DisplayName("Should allow cancellation from any state")
    void testCancel() {
        // Arrange
        testQuote.setStatus(QuoteStatus.REQUESTED);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(testQuote);

        // Act
        QuoteRequest result = quoteRequestService.cancel(quoteId);

        // Assert
        assertEquals(QuoteStatus.CANCELLED, result.getStatus());
        assertFalse(result.getIsActive());
        verify(quoteRequestRepository).save(testQuote);
    }

    // ==================== Invalid State Transition Tests ====================

    @Test
    @DisplayName("Should reject invalid transition from NEW to RESPONDED")
    void testInvalidTransition_NewToResponded() {
        // Arrange
        testQuote.setStatus(QuoteStatus.NEW);

        List<QuoteRequestDetail> itemUpdates = new ArrayList<>();
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.sellerResponds(quoteId, itemUpdates, BigDecimal.TEN);
        });
        verify(quoteRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid transition from NO_REQUEST to REQUESTED")
    void testInvalidTransition_NoRequestToRequested() {
        // Arrange
        testQuote.setStatus(QuoteStatus.NO_REQUEST);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.buyerRequests(quoteId);
        });
        verify(quoteRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid transition from NO_REQUEST to RESPONDED")
    void testInvalidTransition_NoRequestToResponded() {
        // Arrange
        testQuote.setStatus(QuoteStatus.NO_REQUEST);

        List<QuoteRequestDetail> itemUpdates = new ArrayList<>();
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.sellerResponds(quoteId, itemUpdates, BigDecimal.TEN);
        });
        verify(quoteRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid transition from NEW to ACCEPTED")
    void testInvalidTransition_NewToAccepted() {
        // Arrange
        testQuote.setStatus(QuoteStatus.NEW);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.buyerAccepts(quoteId);
        });
        verify(quoteRequestRepository, never()).save(any());
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid transition from REQUESTED to ACCEPTED")
    void testInvalidTransition_RequestedToAccepted() {
        // Arrange
        testQuote.setStatus(QuoteStatus.REQUESTED);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.buyerAccepts(quoteId);
        });
        verify(quoteRequestRepository, never()).save(any());
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid transition from REQUESTED to NEW")
    void testInvalidTransition_RequestedToNew() {
        // Arrange
        testQuote.setStatus(QuoteStatus.REQUESTED);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.createQuote(quoteId);
        });
        verify(quoteRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid transition from NO_REQUEST to ACCEPTED")
    void testInvalidTransition_NoRequestToAccepted() {
        // Arrange
        testQuote.setStatus(QuoteStatus.NO_REQUEST);
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.buyerAccepts(quoteId);
        });
        verify(quoteRequestRepository, never()).save(any());
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject seller modify when not in RESPONDED state")
    void testSellerModifies_InvalidState() {
        // Arrange
        testQuote.setStatus(QuoteStatus.REQUESTED);  // Wrong state - must be RESPONDED

        List<QuoteRequestDetail> itemUpdates = new ArrayList<>();
        when(quoteRequestRepository.findById(quoteId)).thenReturn(Optional.of(testQuote));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            quoteRequestService.sellerModifies(quoteId, itemUpdates, BigDecimal.TEN);
        });
        verify(quoteRequestRepository, never()).save(any());
    }

    // ========== Helper Methods for Test Data ==========

    private Tenant createTenant(String name) {
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setName(name);
        return tenant;
    }

    private TenantAddress createWarehouse() {
        TenantAddress address = new TenantAddress();
        address.setId(UUID.randomUUID());
        address.setAddressType(com.quorion.b2b.model.tenant.AddressType.WAREHOUSE);
        return address;
    }

    private DeliveryTerm createDeliveryTerm() {
        DeliveryTerm term = new DeliveryTerm();
        term.setId(UUID.randomUUID());
        term.setName("FOB");
        return term;
    }

    private PaymentTerm createPaymentTerm() {
        PaymentTerm term = new PaymentTerm();
        term.setId(UUID.randomUUID());
        term.setName("Net 30");
        return term;
    }

    private PaymentMode createPaymentMode() {
        PaymentMode mode = new PaymentMode();
        mode.setId(UUID.randomUUID());
        mode.setName("Bank Transfer");
        return mode;
    }

    private QuoteRequestDetail createQuoteItem(QuoteRequest quote, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");

        ProductSKU sku = new ProductSKU();
        sku.setId(UUID.randomUUID());
        sku.setNumber("SKU-001");

        QuoteRequestDetail item = QuoteRequestDetail.builder()
                .quoteRequest(quote)
                .product(product)
                .sku(sku)
                .noOfUnits(new BigDecimal("5"))
                .totalQuantity(new BigDecimal("50"))
                .pricePerUnit(price)
                .currency("USD")
                .build();
        item.setId(UUID.randomUUID());

        return item;
    }
}
