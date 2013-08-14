package com.oddprints.checkout;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
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
    public void on_authorize_submits_to_pwinty_and_updates_email() {
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
        assertEquals("brian@asd.com", basket.getBuyerEmail());

    }

    @Test
    public void addresses_with_line_breaks_are_split() {
        PaypalCheckoutNotificationHandler p = new PaypalCheckoutNotificationHandler(
                null);

        Map<String, String> parameterMap = new HashMap<String, String>();

        parameterMap.put("address_street", "Road");
        Address address = p.extractAddress(parameterMap);
        assertEquals("Road", address.getAddress1());
        assertEquals(null, address.getAddress2());

        parameterMap.put("address_street", "Road\nTown");
        address = p.extractAddress(parameterMap);
        assertEquals("Road", address.getAddress1());
        assertEquals("Town", address.getAddress2());

        parameterMap.put("address_street", "Road\nTown\nCity");
        address = p.extractAddress(parameterMap);
        assertEquals("Road", address.getAddress1());
        assertEquals("Town, City", address.getAddress2());

        parameterMap.put("address_street", "Road\nTown\nCity\nState");
        address = p.extractAddress(parameterMap);
        assertEquals("Road", address.getAddress1());
        assertEquals("Town, City, State", address.getAddress2());
    }

}
