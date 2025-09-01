package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "audit_history")
public class AuditHistory extends Auditable
{
    @Id
    private Long id;
    private char action;
    private String tableName;
    private Long tablePk;
    private String rowData;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public char getAction()
    {
        return action;
    }

    public void setAction(char action)
    {
        this.action = action;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public Long getTablePk()
    {
        return tablePk;
    }

    public void setTablePk(Long tablePk)
    {
        this.tablePk = tablePk;
    }

    public String getRowData()
    {
        return rowData;
    }

    public void setRowData(String rowData)
    {
        this.rowData = rowData;
    }
}
