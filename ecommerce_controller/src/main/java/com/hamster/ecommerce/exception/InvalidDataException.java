package com.hamster.ecommerce.exception;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class InvalidDataException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<InvalidData> invalidDataList = new ArrayList<>();

    public InvalidDataException(String message)
    {
        super(message);
    }

    public void addError(String object, String field, String errorMessage)
    {
        invalidDataList.add(new InvalidData()
                .setObject(object)
                .setField(field)
                .setErrorMessage(errorMessage));
    }

    public boolean hasErrors()
    {
        return !invalidDataList.isEmpty();
    }

    public List<InvalidData> getInvalidDataExceptionList()
    {
        return invalidDataList;
    }

    public static class InvalidData
    {
        private String object;
        private String field;
        private String errorMessage;

        public InvalidData setObject(String object)
        {
            this.object = object;
            return this;
        }

        public String getObject()
        {
            return object;
        }

        public InvalidData setField(String field)
        {
            this.field = field;
            return this;
        }

        public String getField()
        {
            return field;
        }

        public InvalidData setErrorMessage(String errorMessage)
        {
            this.errorMessage = errorMessage;
            return this;
        }

        public String getErrorMessage()
        {
            return errorMessage;
        }

        public InvalidDataException build()
        {
            return new InvalidDataException("Error in " + object + "." + field + ": " + errorMessage);
        }
    }
}
