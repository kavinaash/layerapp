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
 */
public class MessageView {

    private LinearLayout myParent;

    private TextView senderTV;
    private TextView messageTV;

    public MessageView(LinearLayout parent, Message msg){
        myParent = parent;

        senderTV = new TextView(parent.getContext());
        senderTV.setTypeface(null, Typeface.ITALIC);
        myParent.addView(senderTV);

        messageTV = new TextView(parent.getContext());
        myParent.addView(messageTV);

        UpdateMessage(msg);
    }

    public void UpdateMessage(Message msg){
        String senderTxt = craftSenderText(msg);
        String msgTxt = craftMsgText(msg);

        senderTV.setText(senderTxt);
        messageTV.setText(msgTxt);
    }

    private String craftSenderText(Message msg){
        String senderTxt = msg.getSentByUserId();
        if(msg.getReceivedAt() != null) {
            senderTxt += " @ " + new SimpleDateFormat("HH:mm:ss").format(msg.getReceivedAt());
        }

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

        return senderTxt;
    }

    private Message.RecipientStatus getMessageStatus(Message msg){

        Message.RecipientStatus sameStatus = Message.RecipientStatus.READ;
        for(int i = 1; i < MainActivity.getAllParticipants().size(); i++){
            String firstParticipant = MainActivity.getAllParticipants().get(i-1);
            String secondParticipant = MainActivity.getAllParticipants().get(i);
            if(msg.getRecipientStatus(firstParticipant) != msg.getRecipientStatus(secondParticipant)) {
                if(msg.isSent())
                    return Message.RecipientStatus.SENT;
                return Message.RecipientStatus.PENDING;
            }

            sameStatus = msg.getRecipientStatus(firstParticipant);
        }

        return sameStatus;
    }

    private String craftMsgText(Message msg){
        String msgText = "";

        List<MessagePart> parts = msg.getMessageParts();
        for(int i = 0; i < msg.getMessageParts().size(); i++){
            try {
                msgText += new String(parts.get(i).getData(), "UTF-8") + "\n";
            } catch (UnsupportedEncodingException e) {

            }
        }

        return msgText;
    }
}
