package com.quorion.b2b.service;

import com.quorion.b2b.model.product.Product;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.repository.ProductRepository;
import com.quorion.b2b.repository.ProductSKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Product Service
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSKURepository productSKURepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found"));
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, Product details) {
        Product product = getProductById(id);
        if (details.getName() != null) product.setName(details.getName());
        if (details.getDescription() != null) product.setDescription(details.getDescription());
        if (details.getShortDescription() != null) product.setShortDescription(details.getShortDescription());
        if (details.getSlug() != null) product.setSlug(details.getSlug());
        if (details.getBrandProductName() != null) product.setBrandProductName(details.getBrandProductName());
        if (details.getViewCount() != null) product.setViewCount(details.getViewCount());
        if (details.getIsActive() != null) product.setIsActive(details.getIsActive());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductSKU createSKU(UUID productId, ProductSKU sku) {
        Product product = getProductById(productId);
        sku.setProduct(product);
        return productSKURepository.save(sku);
    }
}
