package com.layer.quickstart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.layer.quickstart.layer.LayerAuthenticationListenerImpl;
import com.layer.quickstart.layer.LayerConnectionListenerImpl;
import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MyActivity extends Activity implements LayerChangeEventListener {
    public final static String EXTRA_MESSAGE = "com.layer.quickstart.MESSAGE";

    private final String LAYER_APP_ID = "ENTER LAYER APP ID HERE";
    private final String GCM_ID = "ENTER_GCM_ID_HERE";
    private LayerClient layerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        setupLayerclient();
    }

    private void setupLayerclient () {
        System.out.println("Setting up Layer Client");
        layerClient = LayerClient.newInstance(this, UUID.fromString(LAYER_APP_ID), GCM_ID);

        layerClient.registerConnectionListener(new LayerConnectionListenerImpl());
        layerClient.registerAuthenticationListener(new LayerAuthenticationListenerImpl());
        layerClient.registerEventListener(this);

        layerClient.connect();
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        sendLayerMessage(message);

        startActivity(intent);
    }

    private void sendLayerMessage (String txtMsg) {
        // Creates and returns a new conversation object with sample participant identifiers
        Conversation conversation = Conversation.newInstance(Arrays.asList("000000000"));       //Change this to a valid USER ID on your side

        // Create a message part with a string of text
        MessagePart messagePart = MessagePart.newInstance("text/plain", txtMsg.getBytes());

        // Creates and returns a new message object with the given conversation and array of message parts
        Message message = Message.newInstance(conversation, Arrays.asList(messagePart));

        //Sends the specified message
        layerClient.sendMessage(message);
    }

    public void onEventMainThread(LayerChangeEvent event) {
        List<LayerChange> changes = event.getChanges();
        for (LayerChange change: changes) {
            switch (change.getChangeType()) {
                case INSERT:
                    // Object was created
                    System.out.println("Object was created.");
                    break;

                case UPDATE:
                    // Object was update
                    System.out.println("Object was updated.");
                    break;

                case DELETE:
                    // Object was deleted
                    System.out.println("Object was deleted.");
                    break;
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
