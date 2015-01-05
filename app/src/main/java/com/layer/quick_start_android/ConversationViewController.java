package com.layer.quick_start_android;

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
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.LayerObject;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by neilmehta on 1/2/15.
 */
public class ConversationViewController implements View.OnClickListener, LayerChangeEventListener {

    private MainActivity mainActivity;
    private LayerClient layerClient;

    private Button sendButton;
    private EditText userInput;
    private ScrollView conversationScroll;
    private LinearLayout conversationView;
    private TextView typingIndicator;

    private Conversation activeConversation;

    private Hashtable<String, MessageView> allMessages;

    public ConversationViewController(MainActivity ma, LayerClient client, Conversation conversation) {

        //Cache off controller objects
        mainActivity = ma;
        layerClient = client;
        activeConversation = getConversation();

        //Cache off view objects
        sendButton = (Button) mainActivity.findViewById(R.id.send);
        userInput = (EditText) mainActivity.findViewById(R.id.input);
        conversationScroll = (ScrollView) mainActivity.findViewById(R.id.scrollView);
        conversationView = (LinearLayout) mainActivity.findViewById(R.id.conversation);
        typingIndicator = (TextView) mainActivity.findViewById(R.id.typingIndicator);

        sendButton.setOnClickListener(this);
        userInput.setText(mainActivity.getInitialMessage());

        layerClient.registerEventListener(this);

        populateConversation();
    }

    private Conversation getConversation(){
        if(activeConversation == null){

            String userID = mainActivity.getUserID();
            String otherID = mainActivity.getParticipantUserID();
            List<Conversation> allConversations = layerClient.getConversationsWithParticipants(userID, otherID);

            if(allConversations != null && allConversations.size() > 0)
                return allConversations.get(allConversations.size() - 1);
        }

        return null;
    }


    @Override
    public void onClick(View v) {
        if(v == sendButton){

            if(activeConversation == null){
                activeConversation = getConversation();
                if(activeConversation == null){
                    activeConversation = Conversation.newInstance(Arrays.asList(mainActivity.getParticipantUserID()));
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

    private void populateConversation(){

        if(activeConversation != null) {
            List<Message> allMsgs = layerClient.getMessages(activeConversation);

            conversationView.removeAllViews();

            allMessages = new Hashtable<String, MessageView>();
            for (int i = 0; i < allMsgs.size(); i++) {
                addMessageToView(allMsgs.get(i));
            }

            conversationScroll.post(new Runnable() {
                @Override
                public void run() {
                    conversationScroll.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private void addMessageToView(Message msg){

        if(msg == null)
            return;

        String msgId = msg.getId().toString();

        if(!allMessages.contains(msgId)) {
            MessageView msgView = new MessageView(conversationView, msg);
            allMessages.put(msgId, msgView);
        }
    }

    public void onEventMainThread(LayerChangeEvent event) {

        List<LayerChange> changes = event.getChanges();
        for (int i = 0; i < changes.size(); i++) {
            LayerChange currentChange = changes.get(i);
            if (currentChange.getObjectType() == LayerObject.Type.CONVERSATION && activeConversation == null){
                activeConversation = (Conversation)currentChange.getObject();
            } else if (currentChange.getObjectType() == LayerObject.Type.MESSAGE){
                layerClient.markMessageAsRead((Message)currentChange.getObject());
            }
        }

        populateConversation();
    }
}
