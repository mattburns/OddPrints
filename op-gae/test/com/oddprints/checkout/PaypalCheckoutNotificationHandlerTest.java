package com.oddprints.checkout;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Maps;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.TestUtils;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.State;

public class PaypalCheckoutNotificationHandlerTest {

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
    public void on_new_order_updates_email() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        // create basket
        Basket basket = new Basket(Environment.SANDBOX);
        pm.makePersistent(basket);

        String idString = basket.getIdString();

        CheckoutNotificationHandler checkoutNotificationHandler = new CheckoutNotificationHandler();
        PaypalCheckoutNotificationHandler pp = new PaypalCheckoutNotificationHandler(
                checkoutNotificationHandler);

        Map<String, String> params = Maps.newHashMap();
        params.put("txn_id", "123");
        params.put("custom", idString);
        params.put("payer_email", "brian@asd.com");

        pp.onNewOrderNotification(params);
        pm = PMF.get().getPersistenceManager();
        basket = Basket.getBasketByKeyString(idString, pm);

        assertEquals("brian@asd.com", basket.getBuyerEmail());
    }

    @Test
    public void on_authorize_submits_to_pwinty() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        // create basket
        Basket basket = new Basket(Environment.SANDBOX);
        basket.addItem(null, 0, "2x2", PrintSize._4x6);
        basket.setState(State.awaiting_payment);
        pm.makePersistent(basket);

        String idString = basket.getIdString();

        CheckoutNotificationHandler checkoutNotificationHandler = new CheckoutNotificationHandler();
        PaypalCheckoutNotificationHandler pp = new PaypalCheckoutNotificationHandler(
                checkoutNotificationHandler);

        Map<String, String> params = Maps.newHashMap();
        params.put("txn_id", "123");
        params.put("custom", idString);
        params.put("payer_email", "brian@asd.com");
        params.put("address_name", "junit paypal");

        pp.onAuthorizationAmountNotification(params);

        pm = PMF.get().getPersistenceManager();
        basket = Basket.getBasketByKeyString(idString, pm);

        assertEquals(State.payment_received, basket.getState());

    }

}
