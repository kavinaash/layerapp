package com.layer.quick_start_android;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.listeners.LayerTypingIndicatorListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.LayerObject;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by neilmehta on 1/2/15.
 * Handles the conversation between the pre-defined participants (Device, Emulator) and displays
 * messages in the GUI.
 */
public class ConversationViewController implements View.OnClickListener, LayerChangeEventListener, TextWatcher, LayerTypingIndicatorListener {

    private MainActivity mainActivity;
    private LayerClient layerClient;

    //GUI elements
    private Button sendButton;
    private EditText userInput;
    private ScrollView conversationScroll;
    private LinearLayout conversationView;
    private TextView typingIndicator;

    //List of all users currently typing
    private ArrayList<String> typingUsers;

    //Current conversation
    private Conversation activeConversation;

    //All messages
    private Hashtable<String, MessageView> allMessages;

    public ConversationViewController(MainActivity ma, LayerClient client) {

        //Cache off controller objects
        mainActivity = ma;
        layerClient = client;
        activeConversation = getConversation();

        //Cache off gui objects
        sendButton = (Button) mainActivity.findViewById(R.id.send);
        userInput = (EditText) mainActivity.findViewById(R.id.input);
        conversationScroll = (ScrollView) mainActivity.findViewById(R.id.scrollView);
        conversationView = (LinearLayout) mainActivity.findViewById(R.id.conversation);
        typingIndicator = (TextView) mainActivity.findViewById(R.id.typingIndicator);

        //Capture user input
        sendButton.setOnClickListener(this);
        userInput.setText(mainActivity.getInitialMessage());
        userInput.addTextChangedListener(this);

        //When conversations/messages change, capture them
        layerClient.registerEventListener(this);

        //List of users that are typing (used with LayerTypingIndicatorListener)
        typingUsers = new ArrayList<String>();

        //If there is an active conversation, draw it
        drawConversation();
    }

    //Checks to see if there is already a conversation between the device and emulator
    private Conversation getConversation(){
        if(activeConversation == null){

            //Grab the participants and check to see if there are any conversations
            List<Conversation> allConversations = layerClient.getConversationsWithParticipants(mainActivity.getAllParticipants());

            //Return the earliest conversation
            Conversation oldest = null;
            if(allConversations != null && allConversations.size() > 0) {
                for(int i = 0; i < allConversations.size(); i++){
                    if(oldest == null || allConversations.get(i).getLastMessage().getSentAt().before(oldest.getLastMessage().getSentAt()))
                        oldest = allConversations.get(i);
                }

                return oldest;
            }
        }

        //null by default
        return null;
    }

    //Redraws the conversation window in the GUI
    private void drawConversation(){

        //Only proceed if there is a valid conversation
        if(activeConversation != null) {

            //Clear the GUI first and empty the list of stored messages
            conversationView.removeAllViews();
            allMessages = new Hashtable<String, MessageView>();

            //Grab all the messages from the conversation and add them to the GUI
            List<Message> allMsgs = layerClient.getMessages(activeConversation);
            for (int i = 0; i < allMsgs.size(); i++) {
                addMessageToView(allMsgs.get(i));
            }

            //After redrawing, force the scroll view to the bottom (most recent message)
            conversationScroll.post(new Runnable() {
                @Override
                public void run() {
                    conversationScroll.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    //Creates a GUI element (header and body) for each Message
    private void addMessageToView(Message msg){

        //Make sure the message is valid
        if(msg == null)
            return;

        //Grab the message id
        String msgId = msg.getId().toString();

        //If we have already added this message to the GUI, skip it
        if(!allMessages.contains(msgId)) {
            //Build the GUI element and save it
            MessageView msgView = new MessageView(conversationView, msg);
            allMessages.put(msgId, msgView);
        }
    }

    //================================================================================
    // View.OnClickListener methods
    //================================================================================

    public void onClick(View v) {
        //When the "send" button is clicked,
        if(v == sendButton){

            if(activeConversation == null){
                activeConversation = getConversation();
                if(activeConversation == null){
                    activeConversation = Conversation.newInstance(mainActivity.getAllParticipants());
                }
            }

            MessagePart messagePart = MessagePart.newInstance(userInput.getText().toString());

            // Creates and returns a new message object with the given conversation and array of message parts
            Message message = Message.newInstance(activeConversation, Arrays.asList(messagePart));

            //Sends the specified message
            layerClient.sendMessage(message);

            //Clears the text input field
            userInput.setText("");
        }
    }

    //================================================================================
    // LayerChangeEventListener methods
    //================================================================================

    public void onEventMainThread(LayerChangeEvent event) {

        List<LayerChange> changes = event.getChanges();
        for(int i = 0; i < changes.size(); i++){
            if(changes.get(i).getObjectType() == LayerObject.Type.CONVERSATION && activeConversation == null){
                activeConversation = (Conversation)changes.get(i).getObject();
            }
        }

        //If anything in the conversation changes, re-draw it in the GUI
        drawConversation();
    }

    //================================================================================
    // TextWatcher methods
    //================================================================================

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public void afterTextChanged(Editable s) {
        //After the user has changed some text, we notify other participants that they are typing
        layerClient.sendTypingIndicator(activeConversation, LayerTypingIndicatorListener.TypingIndicator.STARTED);
    }

    //================================================================================
    // LayerTypingIndicatorListener methods
    //================================================================================

    @Override
    public void onTypingIndicator(LayerClient layerClient, Conversation conversation, String userID, TypingIndicator indicator) {
        switch (indicator) {
            case STARTED:
                // This user started typing, so add them to the typing list if they are not already on it.
                if(!typingUsers.contains(userID))
                    typingUsers.add(userID);
                break;

            case FINISHED:
                // This user isn't typing anymore, so remove them from the list.
                typingUsers.remove(userID);
                break;
        }


        if(typingUsers.size() == 0){

            //No one is typing
            typingIndicator.setText("");
        } else if (typingUsers.size() == 1) {

            //Name the one user that is typing
            typingIndicator.setText(typingUsers.get(0) + " is typing");
        } else if(typingUsers.size() > 1) {

            //Name all the users that are typing
            String users = "";
            for(int i = 0; i < typingUsers.size(); i++){
                users += typingUsers.get(i);
                if(i < typingUsers.size() - 1)
                    users += ", ";
            }

            typingIndicator.setText(users + " are typing");
        }

    }
}
