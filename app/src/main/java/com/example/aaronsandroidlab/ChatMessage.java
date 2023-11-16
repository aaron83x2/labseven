package com.example.aaronsandroidlab;

public class ChatMessage {

    String message;
    String timeSent;
    boolean isSentButton;

    ChatMessage(String m, String t, boolean sent){
        message = m;
        timeSent = t;
        isSentButton = sent;
    }


    public String getMessage(){
        return message;
    }


    public String getTimeSent(){
        return timeSent;
    }

    public boolean getSentButton(){
        return isSentButton;
    }


}
