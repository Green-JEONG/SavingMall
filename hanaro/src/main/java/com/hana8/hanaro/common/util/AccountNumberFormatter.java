package com.hana8.hanaro.common.util;

public final class AccountNumberFormatter {

    private AccountNumberFormatter() {
    }

    public static String format(String accountNumber) {
        if (accountNumber == null || accountNumber.length() != 11) {
            return accountNumber;
        }
        return accountNumber.substring(0, 3) + "-" + accountNumber.substring(3, 7) + "-" + accountNumber.substring(7);
    }
}
