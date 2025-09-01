package com.hamster.ecommerce.repository;

import com.hamster.ecommerce.model.entity.AuditHistory;

public interface AuditHistoryRepository
{
    void save(AuditHistory auditHistory);
}
