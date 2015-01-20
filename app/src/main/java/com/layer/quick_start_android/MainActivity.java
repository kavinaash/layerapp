package com.layer.quick_start_android;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.layer.sdk.LayerClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    //Replace this with your App ID from the Layer Dashboard: http://developer.layer.com
    public static String LayerAppIDString = "LAYER_APP_ID";

    private LayerClient layerClient;
    private ConversationViewController conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(layerClient == null)
            setContentView(R.layout.activity_loading);
    }

    protected void onResume(){
        super.onResume();

        loadLayerClient();

        if(layerClient != null && conversation != null)
            layerClient.registerTypingIndicator(conversation);
    }

    protected void onPause(){
        super.onPause();

        if(layerClient != null && conversation != null)
            layerClient.unregisterTypingIndicator(conversation);
    }

    private void loadLayerClient(){

        // Check if Sample App is using a valid app ID.
        if (isValidAppID()) {

            if(layerClient == null){
                // Initializes a LYRClient object
                UUID appID = UUID.fromString(LayerAppIDString);
                layerClient = LayerClient.newInstance(this, appID, "");

                //Register the connection and authentication listeners
                layerClient.registerConnectionListener(new MyConnectionListener(this));
                layerClient.registerAuthenticationListener(new MyAuthenticationListener(this));
            }

            if(!layerClient.isConnected()) {
                // Asks the LayerSDK to establish a network connection with the Layer service
                layerClient.connect();

            } else if (!layerClient.isAuthenticated()) {
                layerClient.authenticate();
            } else {
                onUserAuthenticated();
            }
        }
    }

    private boolean isValidAppID() {
        if(LayerAppIDString.equalsIgnoreCase("LAYER_APP_ID")) {

            // Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage("To correctly use this project you need to replace LAYER_APP_ID in MainActivity.java (line 11) with your App ID from developer.layer.com.")
                    .setTitle(":-(");

            // Get the AlertDialog from create() and then show() it
            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        }

        return true;
    }

    public static String getUserID(){
        if(Build.FINGERPRINT.startsWith("generic"))
            return "Simulator";

        return "Device";
    }

    public static List<String> getAllParticipants(){
        return Arrays.asList("Device", "Simulator", "Dashboard");
    }

    public static String getInitialMessage(){
        return "Hey, everyone! This is your friend, " + getUserID();
    }

    //Once the user has successfully authenticated, begin the conversation
    public void onUserAuthenticated(){

        if(conversation == null) {

            conversation = new ConversationViewController(this, layerClient);

            if (layerClient != null) {
                layerClient.registerTypingIndicator(conversation);
            }
        }
    }
}
