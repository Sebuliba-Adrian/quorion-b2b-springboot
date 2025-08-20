package com.quorion.b2b.dto;

import com.quorion.b2b.model.commerce.QuoteRequestDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for seller quote responses and modifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteResponseDTO {
    private List<QuoteRequestDetail> itemUpdates;
    private BigDecimal shippingCost;
}
