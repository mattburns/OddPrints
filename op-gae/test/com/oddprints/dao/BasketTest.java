package com.oddprints.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.mattburns.pwinty.v2.CountryCode;
import uk.co.mattburns.pwinty.v2.Order;
import uk.co.mattburns.pwinty.v2.Order.QualityLevel;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.TestUtils;
import com.oddprints.checkout.Address;
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

    @Test
    public void address_country_code_is_mandatory() {
        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "4x18", PrintSize._4x18, 10);

        try {
            b.createOrderOnPwinty(new Address());
            fail();
        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Name is null", e.getMessage());
        }
    }

    @Test
    public void cant_create_order_before_basket_persisted() {
        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "4x18", PrintSize._4x18, 10);

        Address address = new Address();
        address.setCountryCode("GB");
        try {
            b.createOrderOnPwinty(address);
            fail();
        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("cant get url before BasketItem persisted",
                    e.getMessage());
        }
    }

    @Test
    public void order_will_be_pro_if_it_cant_be_done_with_standard() {
        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "4x18", PrintSize._4x18, 10);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        pm.makePersistentAll(b);

        Address address = new Address();
        address.setCountryCode("GB");
        Order order = b.createOrderOnPwinty(address);
        assertEquals(QualityLevel.Pro, order.getQualityLevel());
    }

    @Test
    public void order_will_be_printed_pro_and_in_gb_if_it_cant_be_done_in_destination_country() {
        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "4x6", PrintSize._4x6, 10);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        pm.makePersistentAll(b);

        Address address = new Address();
        address.setCountryCode("AD");

        Order order = b.createOrderOnPwinty(address);
        assertEquals(QualityLevel.Pro, order.getQualityLevel());
        assertEquals(CountryCode.GB, order.getCountryCode());
        assertEquals(CountryCode.AD, order.getDestinationCountryCode());
    }

    @Test
    public void can_create_order_from_basket() {
        Basket b = new Basket(Environment.SANDBOX);
        b.addItem(null, 1, "4x6", PrintSize._4x6, 10);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        pm.makePersistentAll(b);

        Address address = new Address();
        address.setCountryCode("GB");
        Order order = b.createOrderOnPwinty(address);

        assertEquals(QualityLevel.Standard, order.getQualityLevel());
        assertEquals(CountryCode.GB, order.getCountryCode());
        assertEquals(CountryCode.GB, order.getDestinationCountryCode());
    }
}
