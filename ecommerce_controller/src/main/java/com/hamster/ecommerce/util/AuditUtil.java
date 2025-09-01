package com.hamster.ecommerce.util;

import com.hamster.ecommerce.model.entity.Auditable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditUtil
{
    private final ContextUtil contextUtil;

    public AuditUtil(ContextUtil contextUtil)
    {
        this.contextUtil = contextUtil;
    }

    public void setAuditColumns(Auditable auditable)
    {
        auditable.setUpdateLoginId(contextUtil.getCurrentUserId());
        auditable.setUpdateTimestamp(LocalDateTime.now());
    }
}
