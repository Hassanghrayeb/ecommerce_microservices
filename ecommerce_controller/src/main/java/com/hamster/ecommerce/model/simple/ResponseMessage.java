package com.hamster.ecommerce.model.simple;


import com.hamster.ecommerce.exception.ErrorCode;

import java.time.LocalDateTime;


public class ResponseMessage
{
    private String code;
    private String message;
    private LocalDateTime timestamp;

    public ResponseMessage()
    {
    }

    public ResponseMessage(String message, LocalDateTime timestamp)
    {
        super();
        this.message = message;
        this.timestamp = timestamp;
    }

    public ResponseMessage(String errorCode, String message, LocalDateTime timestamp)
    {
        super();
        this.code = errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ResponseMessage(ErrorCode errorCode)
    {
        super();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public static final class ResponseMessageBuilder
    {
        private String errorCode;
        private String message;
        private LocalDateTime timeStamp;

        public ResponseMessageBuilder()
        {
        }

        public static ResponseMessageBuilder anApiErrorResponse()
        {
            return new ResponseMessageBuilder();
        }


        public ResponseMessageBuilder withErrorCode(String errorCode)
        {
            this.errorCode = errorCode;
            return this;
        }

        public ResponseMessageBuilder withMessage(String message)
        {
            this.message = message;
            return this;
        }


        public ResponseMessageBuilder atDate(LocalDateTime timeStamp)
        {
            this.timeStamp = timeStamp;
            return this;
        }

        public ResponseMessage build()
        {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.code = this.errorCode;
            responseMessage.message = this.message;
            responseMessage.timestamp = this.timeStamp;
            return responseMessage;
        }
    }
}
