package com.oddprints.dao;

import static org.junit.Assert.assertEquals;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.TestUtils;
import com.oddprints.dao.Coupon.DiscountType;

public class BasketTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        TestUtils.INSTANCE.addSettingsFromTestFile();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void rounds_percentage_discount_properly() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Coupon c10 = new Coupon(DiscountType.percentage, 10);
        Coupon c1 = new Coupon(DiscountType.percentage, 1);
        Coupon c50 = new Coupon(DiscountType.percentage, 50);
        pm.makePersistentAll(c10, c1, c50);

        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._4x6, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        assertEquals(0, (int) b.getDiscountAmount());
        b.setCoupon(c10);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        assertEquals(45 + 299, b.getTotalPrice());
        assertEquals(5, (int) b.getDiscountAmount());

        // Let's round up to nearest penny
        b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._4x6, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        b.setCoupon(c1);
        assertEquals(1, (int) b.getDiscountAmount());

        b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._4x6, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        b.setCoupon(c50);
        assertEquals(25, (int) b.getDiscountAmount());
    }

    @Test
    public void discount_cannot_be_greater_than_products_in_cart() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Coupon c1000 = new Coupon(DiscountType.percentage, 1000);
        Coupon c999 = new Coupon(DiscountType.pence, 999);
        pm.makePersistentAll(c1000, c999);

        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._4x6, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        assertEquals(0, (int) b.getDiscountAmount());

        b.setCoupon(c1000);
        assertEquals(50, b.getPriceOfPrintsInPennies());
        assertEquals(299 + 1, b.getTotalPrice());
        assertEquals("Pay 1p for print", 50 - 1, (int) b.getDiscountAmount());
    }

    @Test
    public void postage_can_be_free_for_pence_coupons() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Coupon c1000 = new Coupon(DiscountType.percentage, 1000);
        Coupon c999 = new Coupon(DiscountType.pence, 999);
        pm.makePersistentAll(c1000, c999);

        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "6x3", PrintSize._4x6, 1);
        assertEquals(50, b.getPriceOfPrintsInPennies());

        b.setCoupon(c999);
        assertEquals(1, b.getTotalPrice());
        assertEquals(299 + 50 - 1, (int) b.getDiscountAmount());
    }

    @Test
    public void discount_does_not_exceed_coupon() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Coupon c2000 = new Coupon(DiscountType.pence, 2000);
        pm.makePersistentAll(c2000);

        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "4x18", PrintSize._4x18, 10);
        assertEquals(499 * 10, b.getPriceOfPrintsInPennies());

        b.setCoupon(c2000);
        assertEquals(2000, (int) b.getDiscountAmount());
        assertEquals(299 + (499 * 10) - 2000, b.getTotalPrice());
    }

}
