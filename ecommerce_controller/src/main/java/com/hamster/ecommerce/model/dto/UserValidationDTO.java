package com.hamster.ecommerce.model.dto;

import java.util.ArrayList;
import java.util.List;

public class UserValidationDTO
{
    private List<String> missingRequiredFieldList = new ArrayList<>();
    private String message;

    public List<String> getMissingRequiredFieldList()
    {
        return missingRequiredFieldList;
    }

    public void setMissingRequiredFieldList(List<String> missingRequiredFieldList)
    {
        this.missingRequiredFieldList = missingRequiredFieldList;
    }

    public void addRequiredField(String requiredField)
    {
        missingRequiredFieldList.add(requiredField);
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
