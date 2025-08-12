package com.quorion.b2b.service;

import com.quorion.b2b.exception.InvalidStateTransitionException;
import com.quorion.b2b.model.commerce.Lead;
import com.quorion.b2b.model.commerce.SalesLeadStatus;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.repository.LeadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for Lead management with state machine transitions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;

    public List<Lead> findAll() {
        return leadRepository.findAll();
    }

    public Lead findById(UUID id) {
        return leadRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Lead not found with id: " + id));
    }

    public List<Lead> findBySeller(Tenant seller) {
        return leadRepository.findBySeller(seller);
    }

    @Transactional
    public Lead create(Lead lead) {
        lead.setStatus(SalesLeadStatus.NO_LEAD);
        return leadRepository.save(lead);
    }

    /**
     * State Transition: NO_LEAD → NEW
     */
    @Transactional
    public Lead createLead(UUID leadId) {
        Lead lead = findById(leadId);
        validateTransition(lead.getStatus(), SalesLeadStatus.NEW);

        lead.setStatus(SalesLeadStatus.NEW);
        log.info("Lead {} transitioned to NEW", leadId);
        return leadRepository.save(lead);
    }

    /**
     * State Transition: NEW → CONVERTED
     */
    @Transactional
    public Lead convert(UUID leadId) {
        Lead lead = findById(leadId);
        validateTransition(lead.getStatus(), SalesLeadStatus.CONVERTED);

        lead.setStatus(SalesLeadStatus.CONVERTED);
        log.info("Lead {} converted", leadId);
        return leadRepository.save(lead);
    }

    /**
     * State Transition: NEW → FORWARDED
     */
    @Transactional
    public Lead forwardToDistributor(UUID leadId, Tenant distributor) {
        Lead lead = findById(leadId);
        validateTransition(lead.getStatus(), SalesLeadStatus.FORWARDED);

        // Create child lead for distributor
        Lead childLead = Lead.builder()
            .seller(distributor)
            .buyerFirstName(lead.getBuyerFirstName())
            .buyerLastName(lead.getBuyerLastName())
            .buyerEmail(lead.getBuyerEmail())
            .buyerPhone(lead.getBuyerPhone())
            .buyerCompanyName(lead.getBuyerCompanyName())
            .parentLead(lead)
            .source("forwarded")
            .status(SalesLeadStatus.SENT_TO_DISTRIBUTOR)
            .build();

        leadRepository.save(childLead);

        lead.setStatus(SalesLeadStatus.FORWARDED);
        log.info("Lead {} forwarded to distributor {}", leadId, distributor.getId());
        return leadRepository.save(lead);
    }

    /**
     * State Transition: SENT_TO_DISTRIBUTOR → ACCEPTED_BY_DISTRIBUTOR
     */
    @Transactional
    public Lead acceptByDistributor(UUID leadId) {
        Lead lead = findById(leadId);
        validateTransition(lead.getStatus(), SalesLeadStatus.ACCEPTED_BY_DISTRIBUTOR);

        lead.setStatus(SalesLeadStatus.ACCEPTED_BY_DISTRIBUTOR);
        log.info("Lead {} accepted by distributor", leadId);
        return leadRepository.save(lead);
    }

    /**
     * State Transition: SENT_TO_DISTRIBUTOR → REJECTED_BY_DISTRIBUTOR
     */
    @Transactional
    public Lead rejectByDistributor(UUID leadId) {
        Lead lead = findById(leadId);
        validateTransition(lead.getStatus(), SalesLeadStatus.REJECTED_BY_DISTRIBUTOR);

        lead.setStatus(SalesLeadStatus.REJECTED_BY_DISTRIBUTOR);
        log.info("Lead {} rejected by distributor", leadId);
        return leadRepository.save(lead);
    }

    private void validateTransition(SalesLeadStatus current, SalesLeadStatus target) {
        boolean valid = switch (current) {
            case NO_LEAD -> target == SalesLeadStatus.NEW;
            case NEW -> target == SalesLeadStatus.CONVERTED || target == SalesLeadStatus.FORWARDED;
            case SENT_TO_DISTRIBUTOR -> target == SalesLeadStatus.ACCEPTED_BY_DISTRIBUTOR ||
                                       target == SalesLeadStatus.REJECTED_BY_DISTRIBUTOR;
            default -> false;
        };

        if (!valid) {
            throw new InvalidStateTransitionException(current.name(), target.name());
        }
    }
}
