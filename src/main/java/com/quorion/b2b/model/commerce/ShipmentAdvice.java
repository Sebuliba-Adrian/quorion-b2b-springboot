package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Shipping information and tracking
 */
@Entity
@Table(name = "shipment_advice")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "order")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentAdvice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    @JsonIgnore
    private PurchaseOrder order;

    @Column(name = "number", unique = true, length = 50)
    private String number;

    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "estimated_time_of_dispatch")
    private LocalDateTime estimatedTimeOfDispatch;

    @Column(name = "estimated_time_of_arrival")
    private LocalDateTime estimatedTimeOfArrival;

    @Column(name = "carrier", length = 100)
    private String carrier;

    @Column(name = "carrier_number", length = 100)
    private String carrierNumber;

    @Column(name = "vessel_name", length = 100)
    private String vesselName;

    @Column(name = "port_of_loading", length = 100)
    private String portOfLoading;

    @Column(name = "port_of_discharge", length = 100)
    private String portOfDischarge;

    @Column(name = "additional_comments", columnDefinition = "TEXT")
    private String additionalComments;
}
