package com.layer.quick_start_android;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    public static String LayerAppIDString = "LAYER_APP_ID";
    public static String GoogleCloudMessagingID = "GCM ID";


    private LayerClient layerClient;
    private ConversationViewController conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(layerClient == null)
            setContentView(R.layout.activity_loading);

        System.out.println("onCreate");
    }

    protected void onResume(){
        super.onResume();

        System.out.println("onResume");

        loadLayerClient();
    }

    private void loadLayerClient(){
        System.out.println("Starting loading");

        // Check if Sample App is using a valid app ID.
        if (isValidAppID()) {

            if(layerClient == null){
                // Initializes a LYRClient object
                UUID appID = UUID.fromString(LayerAppIDString);
                layerClient = LayerClient.newInstance(this, appID, GoogleCloudMessagingID);

                //Register the connection and authentication listeners
                layerClient.registerConnectionListener(new MyConnectionListener(this));
                layerClient.registerAuthenticationListener(new MyAuthenticationListener(this));

                //Log.v("TAG", "Registered listeners");
                System.out.println("Registered Listeners");

                //Log.v("TAG", "Created Layer client");
                System.out.println("Created layer client");
            }

            if(!layerClient.isConnected()) {
                // Asks the LayerSDK to establish a network connection with the Layer service
                layerClient.connect();

                System.out.println("Connecting...");
                //Log.v("TAG", "Connecting...");
            } else if (!layerClient.isAuthenticated()) {
                System.out.println("Authenticating...");
                layerClient.authenticate();
            } else {
                System.out.println("Launching...");
                onUserAuthenticated();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isValidAppID() {
        if(LayerAppIDString.equalsIgnoreCase("LAYER_APP_ID")) {

            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("To correctly use this project you need to replace LAYER_APP_ID in MainActivity.java (line 11) with your App ID from developer.layer.com.")
                    .setTitle(":-(");

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        }

        return true;
    }

    public static String getUserID(){
        if(Build.FINGERPRINT.startsWith("generic"))
            return "Emulator";

        return "Device";
    }

    public static String getParticipantUserID(){
        if(Build.FINGERPRINT.startsWith("generic"))
            return "Device";

        return "Emulator";
    }

    public static String getInitialMessage(){
        return "Hey " + getParticipantUserID() + "! This is your friend, " + getUserID();
    }

    //Once the user has successfully authenticated, begin the conversation
    public void onUserAuthenticated(){
        setContentView(R.layout.activity_main);

        Log.v("TAG", "Creating new conversation");
        conversation = new ConversationViewController(this, layerClient, getConversation());
    }

    //Determines if there is an existing conversation between the emulator and device. If not, creates a new conversation
    private Conversation getConversation(){

        System.out.println("Getting conversation");

        List<Conversation> allConversations = layerClient.getConversationsWithParticipants(Arrays.asList(getParticipantUserID()));

        //Grabs the last conversation if one exists
        if(allConversations != null && allConversations.size() > 0)
            return allConversations.get(allConversations.size() - 1);

        return null;
    }
}
