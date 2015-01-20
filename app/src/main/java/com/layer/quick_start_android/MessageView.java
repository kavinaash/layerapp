package com.layer.quick_start_android;

import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by neilmehta on 1/5/15.
 * Takes a Layer Message object, formats the text and attaches it to a LinearLayout
 */
public class MessageView {

    //The parent object (in this case, a LinearLayout object with a ScrollView parent)
    private LinearLayout myParent;

    //The sender and message views
    private TextView senderTV;
    private TextView messageTV;

    //Takes the Layout parent object and message
    public MessageView(LinearLayout parent, Message msg){
        myParent = parent;

        //Creates the sender text view, sets the text to be italic, and attaches it to the parent
        senderTV = new TextView(parent.getContext());
        senderTV.setTypeface(null, Typeface.ITALIC);
        myParent.addView(senderTV);

        //Creates the message text view and attaches it to the parent
        messageTV = new TextView(parent.getContext());
        myParent.addView(messageTV);

        //Populates the text views
        UpdateMessage(msg);
    }

    //Takes a message and sets the text in the two text views
    public void UpdateMessage(Message msg){
        String senderTxt = craftSenderText(msg);
        String msgTxt = craftMsgText(msg);

        senderTV.setText(senderTxt);
        messageTV.setText(msgTxt);
    }

    //The sender text is formatted like so:
    //  User @ Timestamp - Status
    private String craftSenderText(Message msg){

        //The User ID
        String senderTxt = msg.getSentByUserId();

        //Add the timestamp
        if(msg.getReceivedAt() != null) {
            senderTxt += " @ " + new SimpleDateFormat("HH:mm:ss").format(msg.getReceivedAt());
        }

        //Set the status
        if(msg.getSentByUserId() != MainActivity.getUserID()){
            senderTxt += " - Read";
        } else {
           switch(getMessageStatus(msg)){
               case PENDING:
                   senderTxt += " - Pending";
                   break;

               case SENT:
                   senderTxt += " - Sent";
                   break;

               case DELIVERED:
                   senderTxt += " - Delivered";
                   break;

               case READ:
                   senderTxt += " - Read";
                   break;
           }
        }

        //Return the formatted text
        return senderTxt;
    }

    //Checks the recipient status of the message (based on all participants)
    private Message.RecipientStatus getMessageStatus(Message msg){

        //Assume the message has been read
        Message.RecipientStatus msgStatus = Message.RecipientStatus.READ;

        //Check the status for all users
        for(int i = 1; i < MainActivity.getAllParticipants().size(); i++){

            //Grab the status for each participant
            String firstParticipant = MainActivity.getAllParticipants().get(i-1);
            String secondParticipant = MainActivity.getAllParticipants().get(i);

            //If they have different statuses, check to see if the message has been sent or not
            if(msg.getRecipientStatus(firstParticipant) != msg.getRecipientStatus(secondParticipant)) {
                if(msg.isSent())
                    return Message.RecipientStatus.SENT;
                return Message.RecipientStatus.PENDING;
            }

            msgStatus = msg.getRecipientStatus(firstParticipant);
        }

        //If all users have the same status, return that status
        return msgStatus;
    }

    //Checks the message parts and parses the message contents
    private String craftMsgText(Message msg){

        //The message text
        String msgText = "";

        //Go through each part, and if it is text (which it should be by default), append it to the
        // message text
        List<MessagePart> parts = msg.getMessageParts();
        for(int i = 0; i < msg.getMessageParts().size(); i++){

            //You can always set the mime type when creating a message part, by default it is
            // set to plain test
            if(parts.get(i).getMimeType().equalsIgnoreCase("text/plain")) {
                try {
                    msgText += new String(parts.get(i).getData(), "UTF-8") + "\n";
                } catch (UnsupportedEncodingException e) {

                }
            }
        }

        //Return the assembled text
        return msgText;
    }
}
