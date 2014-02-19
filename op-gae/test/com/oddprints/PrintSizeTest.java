package com.oddprints;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrintSizeTest {

    @Test
    public void to_string_is_unicode() {
        assertEquals("5\"×7\"", PrintSize._5x7.getDisplayString());
        assertEquals(5, PrintSize._5x7.getDisplayString().length());
    }

}
