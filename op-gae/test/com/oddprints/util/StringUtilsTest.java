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
        assertEquals(
                "2014/02/12-2014/02/17",
                StringUtils.estimatedDeliveryDate(mon.getTime(), CountryCode.GB));
        assertEquals(
                "2014/02/18-2014/02/21",
                StringUtils.estimatedDeliveryDate(fri.getTime(), CountryCode.GB));
        assertEquals(
                "2014/02/12-2014/02/17",
                StringUtils.estimatedDeliveryDate(sat.getTime(), CountryCode.GB));
        assertEquals(
                "2014/03/03-2014/03/06",
                StringUtils.estimatedDeliveryDate(eom.getTime(), CountryCode.GB));

        // Other - min 3, max 9
        assertEquals(
                "2014/02/13-2014/02/21",
                StringUtils.estimatedDeliveryDate(mon.getTime(), CountryCode.AD));
        assertEquals(
                "2014/02/19-2014/02/27",
                StringUtils.estimatedDeliveryDate(fri.getTime(), CountryCode.AD));
        assertEquals(
                "2014/02/13-2014/02/21",
                StringUtils.estimatedDeliveryDate(sat.getTime(), CountryCode.AD));
        assertEquals(
                "2014/03/04-2014/03/12",
                StringUtils.estimatedDeliveryDate(eom.getTime(), CountryCode.AD));
    }
}
