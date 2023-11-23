package com.example.aaronsandroidlab;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessage {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name="messages")
    String message;
    @ColumnInfo(name = "TimeSent")
    String timeSent;

    @ColumnInfo(name = "SentOrReceive")
    boolean isSentButton;

    ChatMessage(){}

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
