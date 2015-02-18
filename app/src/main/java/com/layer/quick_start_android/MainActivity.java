package com.layer.quick_start_android;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.layer.sdk.LayerClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
 * Handles the main activity and conversationView view
 */

public class MainActivity extends ActionBarActivity {

    //Replace this with your App ID from the Layer Developer page.
    //Go http://developer.layer.com, click on "Dashboard" and select "Info"
    public static String Layer_App_ID = "LAYER_APP_ID";

    //Replace this with your Project Number from http://console.developers.google.com
    public static String GCM_Project_Number = "00000";


    //Global variables used to manage the Layer Client and the conversations in this app
    private LayerClient layerClient;
    private ConversationViewController conversationView;

    //Layer connection and authentication callback listeners
    private MyConnectionListener connectionListener;
    private MyAuthenticationListener authenticationListener;

    //onCreate is called on App Start
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If we haven't created a LayerClient, show the loading splash screen
        if(layerClient == null)
            setContentView(R.layout.activity_loading);


        //Create the callback listeners

        if(connectionListener == null)
            connectionListener = new MyConnectionListener(this);

        if(authenticationListener == null)
            authenticationListener = new MyAuthenticationListener(this);
    }

    //onResume is called on App Start and when the app is brought to the foreground
    protected void onResume(){
        super.onResume();

        //Connect to Layer and Authenticate a user
        loadLayerClient();

        //Every time the app is brought to the foreground, register the typing indicator
        if(layerClient != null && conversationView != null)
            layerClient.registerTypingIndicator(conversationView);
    }

    //onPause is called when the app is sent to the background
    protected void onPause(){
        super.onPause();

        //When the app is moved to the background, unregister the typing indicator
        if(layerClient != null && conversationView != null)
            layerClient.unregisterTypingIndicator(conversationView);
    }

    //Checks to see if the SDK is connected to Layer and whether a user is authenticated
    //The respective callbacks are executed in MyConnectionListener and MyAuthenticationListener
    private void loadLayerClient(){

        // Check if Sample App is using a valid app ID.
        if (isValidAppID()) {

            if(layerClient == null){

                LayerClient.setLogLevel(LayerClient.LogLevel.DETAILED);

                // Initializes a LYRClient object
                UUID appID = UUID.fromString(Layer_App_ID);
                layerClient = LayerClient.newInstance(this, appID, GCM_Project_Number);

                //Register the connection and authentication listeners
                layerClient.registerConnectionListener(connectionListener);
                layerClient.registerAuthenticationListener(authenticationListener);
            }


            if (!layerClient.isAuthenticated()) {

                //First we try to authenticate the user. if the LayerClient is not connected, "connect()"
                //will be called automatically by the Layer SDK.
                layerClient.authenticate();

            } else if (!layerClient.isConnected()) {

                //If the user is authenticated, but Layer is not connected, make sure we connect in
                //order to send/receive messages
                layerClient.connect();

            } else {

                // If connected to Layer and the user is authenticated, start the conversationView view
                onUserAuthenticated();

            }
        }
    }

    //If you haven't replaced "LAYER_APP_ID" with your App ID, send a message
    private boolean isValidAppID() {
        if(Layer_App_ID.equalsIgnoreCase("LAYER_APP_ID")) {

            // Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage("To correctly use this project you need to replace LAYER_APP_ID in MainActivity.java (line 21) with your App ID from developer.layer.com.")
                    .setTitle(":-(");

            // Get the AlertDialog from create() and then show() it
            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        }

        return true;
    }

    //Return "Simulator" if this is an emulator, or "Device" if running on hardware
    public static String getUserID(){
        if(Build.FINGERPRINT.startsWith("generic"))
            return "Simulator";

        return "Device";
    }

    //By default, create a conversationView between these 3 participants
    public static List<String> getAllParticipants(){
        return Arrays.asList("Device", "Simulator", "Dashboard");
    }

    //Once the user has successfully authenticated, begin the conversationView
    public void onUserAuthenticated(){

        if(conversationView == null) {

            conversationView = new ConversationViewController(this, layerClient);

            if (layerClient != null) {
                layerClient.registerTypingIndicator(conversationView);
            }
        }
    }
}
