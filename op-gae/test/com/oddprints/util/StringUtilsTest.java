package com.oddprints.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import uk.co.mattburns.pwinty.v2.CountryCode;

public class StringUtilsTest {

    @Test
    public void test_money_has_no_spaces() {
        assertEquals("&pound;0.00", StringUtils.formatMoney(0));
        assertEquals("&pound;123.45", StringUtils.formatMoney(12345));
    }

    @Test
    public void can_generate_delivery_estimate() {

        Calendar mon = Calendar.getInstance();
        mon.set(2014, Calendar.FEBRUARY, 10);
        Calendar fri = Calendar.getInstance();
        fri.set(2014, Calendar.FEBRUARY, 14);
        Calendar sat = Calendar.getInstance(); // Same as Monday
        sat.set(2014, Calendar.FEBRUARY, 8);

        Calendar eom = Calendar.getInstance(); // Same as Monday
        eom.set(2014, Calendar.FEBRUARY, 27);

        // GB, US - min 2, max 5
        assertEquals("Feb 12th - Feb 17th", StringUtils.estimatedDeliveryDate(
                mon.getTime(), CountryCode.GB));
        assertEquals("Feb 18th - Feb 21st", StringUtils.estimatedDeliveryDate(
                fri.getTime(), CountryCode.GB));
        assertEquals("Feb 12th - Feb 17th", StringUtils.estimatedDeliveryDate(
                sat.getTime(), CountryCode.GB));
        assertEquals("Mar 3rd - Mar 6th", StringUtils.estimatedDeliveryDate(
                eom.getTime(), CountryCode.GB));

        // Other - min 3, max 9
        assertEquals("Feb 13th - Feb 21st", StringUtils.estimatedDeliveryDate(
                mon.getTime(), CountryCode.AD));
        assertEquals("Feb 19th - Feb 27th", StringUtils.estimatedDeliveryDate(
                fri.getTime(), CountryCode.AD));
        assertEquals("Feb 13th - Feb 21st", StringUtils.estimatedDeliveryDate(
                sat.getTime(), CountryCode.AD));
        assertEquals("Mar 4th - Mar 12th", StringUtils.estimatedDeliveryDate(
                eom.getTime(), CountryCode.AD));
    }
}
