package com.layer.quickstart.layer;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerConnectionListener;

/**
 * Sample implementation of LayerConnectionListener
 */
public class LayerConnectionListenerImpl implements LayerConnectionListener {

    @Override
    public void onConnectionConnected(LayerClient client) {
        System.out.println("Connected to Layer... now trying to Authenticate");
//        client.deauthenticate();
//        if (client.isAuthenticated())
//            System.out.println("We are authenticated already");
//        else
//            System.out.println("We are NOT authenticated");
        client.authenticate();
    }

    @Override
    public void onConnectionDisconnected(LayerClient arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionError(LayerClient arg0, LayerException ex) {
        // TODO Auto-generated method stub

    }
}
