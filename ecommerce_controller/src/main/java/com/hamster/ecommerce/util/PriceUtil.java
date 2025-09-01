package com.hamster.ecommerce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class PriceUtil
{
    public PriceUtil() {}

    public static BigDecimal scale(BigDecimal amount, String currencyCode) {
        if (amount == null) return null;
        int digits = Currency.getInstance(currencyCode).getDefaultFractionDigits();
        return amount.setScale(digits, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiply(BigDecimal unitPrice, int qty, String currency) {
        return scale(unitPrice.multiply(BigDecimal.valueOf(qty)), currency);
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b, String currency) {
        return scale(a.add(b), currency);
    }
}
