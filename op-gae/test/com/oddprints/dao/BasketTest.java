package com.oddprints.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.oddprints.Environment;
import com.oddprints.PrintSize;

public class BasketTest {

    @Test
    public void rounds_percentage_discount_properly() {
        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._2x4, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        assertEquals(0, (int) b.getDiscountPercentage());
        assertEquals(0, (int) b.getDiscountAmount());
        b.setDiscountPercentage(10);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        assertEquals(45 + 299, b.getTotalPrice());
        assertEquals(10, (int) b.getDiscountPercentage());
        assertEquals(5, (int) b.getDiscountAmount());

        // Let's round up to nearest penny
        b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._2x4, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        b.setDiscountPercentage(1);
        assertEquals(1, (int) b.getDiscountAmount());

        b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._2x4, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        b.setDiscountPercentage(50);
        assertEquals(25, (int) b.getDiscountAmount());
    }

}
