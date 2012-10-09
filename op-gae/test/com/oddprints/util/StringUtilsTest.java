package com.oddprints.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void test_money_has_no_spaces() {
        assertEquals("&pound;0.00", StringUtils.formatMoney(0));
        assertEquals("&pound;123.45", StringUtils.formatMoney(12345));
    }
}
