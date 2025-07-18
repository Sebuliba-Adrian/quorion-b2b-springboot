package com.quorion.b2b.service;

import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantType;
import com.quorion.b2b.repository.ProductSKURepository;
import com.quorion.b2b.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * ProductSKU Service
 */
@Service
@RequiredArgsConstructor
public class ProductSKUService {

    private final ProductSKURepository productSKURepository;
    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public List<ProductSKU> getAllSKUs() {
        return productSKURepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductSKU getSKUById(UUID id) {
        return productSKURepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("SKU not found"));
    }

    @Transactional
    public ProductSKU createSKU(ProductSKU sku) {
        return productSKURepository.save(sku);
    }

    @Transactional
    public ProductSKU updateSKU(UUID id, ProductSKU details) {
        ProductSKU sku = getSKUById(id);
        if (details.getNumber() != null) sku.setNumber(details.getNumber());
        if (details.getName() != null) sku.setName(details.getName());
        if (details.getDescription() != null) sku.setDescription(details.getDescription());
        if (details.getIsActive() != null) sku.setIsActive(details.getIsActive());
        return productSKURepository.save(sku);
    }

    @Transactional
    public void deleteSKU(UUID id) {
        if (!productSKURepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("ProductSKU not found with id: " + id);
        }
        productSKURepository.deleteById(id);
    }

    @Transactional
    public ProductSKU createDistributorCopy(UUID skuId, UUID distributorId) {
        ProductSKU originalSku = getSKUById(skuId);

        Tenant distributor = tenantRepository.findById(distributorId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Distributor not found"));

        if (distributor.getType() != TenantType.DISTRIBUTOR) {
            throw new jakarta.persistence.EntityNotFoundException("Tenant must be a distributor");
        }

        // Create a copy
        ProductSKU distributorSku = new ProductSKU();
        distributorSku.setProduct(originalSku.getProduct());
        distributorSku.setNumber(originalSku.getNumber() + "-" + distributor.getCode());
        distributorSku.setName(originalSku.getName() + " (Distributor: " + distributor.getName() + ")");
        distributorSku.setDescription(originalSku.getDescription());
        distributorSku.setIsActive(originalSku.getIsActive());
        distributorSku.setDistributor(distributor);
        distributorSku.setOriginalSku(originalSku);

        return productSKURepository.save(distributorSku);
    }
}
