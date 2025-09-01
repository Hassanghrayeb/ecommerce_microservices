package com.hamster.ecommerce.repository.impl;

import com.hamster.ecommerce.model.entity.AuditHistory;
import com.hamster.ecommerce.repository.AuditHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class AuditHistoryRepositoryImpl implements AuditHistoryRepository
{
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void save(AuditHistory auditHistory)
    {
        namedParameterJdbcTemplate.update("insert into audit_history " +
                        "(action, table_name, table_pk, row_data, update_login_id, update_timestamp)" +
                        " values (:action, :tableName, :tablePk, :rowData::jsonb, :updateLoginId, :updateTimestamp)",
                Map.ofEntries(
                        Map.entry("action", auditHistory.getAction()),
                        Map.entry("tableName", auditHistory.getTableName()),
                        Map.entry("tablePk", auditHistory.getTablePk()),
                        Map.entry("rowData", auditHistory.getRowData().toString()),
                        Map.entry("updateLoginId", auditHistory.getUpdateLoginId()),
                        Map.entry("updateTimestamp", auditHistory.getUpdateTimestamp())
                             )
                                         );
    }
}
