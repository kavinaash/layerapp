package com.layer.quick_start_android;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerConnectionListener;

public class MyConnectionListener implements LayerConnectionListener {

    private MainActivity main_activity;

    public MyConnectionListener(MainActivity ma){
        main_activity = ma;
    }

    @Override
    public void onConnectionConnected(LayerClient client) {
        System.out.println("Connected to Layer");

        if(client.isAuthenticated())
            main_activity.onUserAuthenticated();
        else
            client.authenticate();

    }

    @Override
    public void onConnectionDisconnected(LayerClient arg0) {
        System.out.println("Connection to Layer closed");
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnectionError(LayerClient arg0, LayerException e) {
        // TODO Auto-generated method stub
        System.out.println("Error connecting to layer: " + e.toString());
    }
}