package com.oddprints.checkout;

import static org.junit.Assert.assertEquals;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.checkout.sdk.domain.Address;
import com.google.checkout.sdk.domain.AnyMultiple;
import com.google.checkout.sdk.domain.AuthorizationAmountNotification;
import com.google.checkout.sdk.domain.OrderSummary;
import com.google.checkout.sdk.domain.ShoppingCart;
import com.google.common.collect.Lists;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.TestUtils;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.State;

public class GoogleCheckoutNotificationHandlerTest {

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
    public void on_authorize_submits_to_pwinty() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        // create basket
        Basket basket = new Basket(Environment.SANDBOX);
        basket.addItem(null, 1, "2x2", PrintSize._4x6);
        basket.setState(State.awaiting_payment);
        pm.makePersistent(basket);

        String idString = basket.getIdString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        OrderSummary orderSummary = Mockito.mock(OrderSummary.class);
        ShoppingCart cart = Mockito.mock(ShoppingCart.class);
        Mockito.when(orderSummary.getShoppingCart()).thenReturn(cart);

        AnyMultiple am = Mockito.mock(AnyMultiple.class);
        Mockito.when(am.getContent()).thenReturn(
                Lists.newArrayList((Object) idString));
        Mockito.when(cart.getMerchantPrivateData()).thenReturn(am);

        Address address = Mockito.mock(Address.class);
        Mockito.when(address.getEmail()).thenReturn("fred@test.com");
        Mockito.when(address.getContactName()).thenReturn("junit google");
        Mockito.when(orderSummary.getBuyerShippingAddress())
                .thenReturn(address);

        AuthorizationAmountNotification notification = Mockito
                .mock(AuthorizationAmountNotification.class);

        CheckoutNotificationHandler checkoutNotificationHandler = new CheckoutNotificationHandler();
        GoogleCheckoutNotificationHandler g = new GoogleCheckoutNotificationHandler(
                request, response, checkoutNotificationHandler);

        g.onAuthorizationAmountNotification(orderSummary, notification);

        pm = PMF.get().getPersistenceManager();
        basket = Basket.getBasketByKeyString(idString, pm);

        assertEquals(State.payment_received, basket.getState());

    }

}
