package com.example.aaronsandroidlab;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.aaronsandroidlab.databinding.ActivityChatRoomBinding;
import com.example.aaronsandroidlab.databinding.RecieveMessageBinding;
import com.example.aaronsandroidlab.databinding.SendMessageBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;

    ArrayList<ChatMessage> messages = new ArrayList<>();

    ChatRoomViewModel chatModel;

    ChatMessageDAO mDAO;


    private RecyclerView.Adapter myAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);







        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Retrieve the arrayList<>
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        messages = chatModel.messages.getValue();

        if (messages == null) {
            chatModel.messages.postValue(messages = new ArrayList<>());
        }


        binding.sendButton.setOnClickListener(click -> {
            //Get Text from input
            String msg = binding.textInput.getText().toString();

            //Get Date in time in Simple Format
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateAndTime = sdf.format(new Date());


            //New ChatMessage Object
            ChatMessage chatmsg = new ChatMessage(msg, currentDateAndTime, true);

            //Add new Chat Object to the ArrayList
            messages.add(chatmsg);

//            myAdapter.notifyItemInserted(messages.size()-1);

            //Clear previous text
            binding.textInput.setText("");

            myAdapter.notifyDataSetChanged();

        });

        binding.recieveButton.setOnClickListener(click -> {
            //Get Text from input
            String msg = binding.textInput.getText().toString();

            //Get Date in time in Simple Format
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());

            //New ChatMessage Object
            ChatMessage chatmsg = new ChatMessage(msg, currentDateandTime, false);

            //Add new Chat Object to the ArrayList
            messages.add(chatmsg);

//            myAdapter.notifyItemInserted(messages.size()-1);

            //Clear previous text
            binding.textInput.setText("");

            myAdapter.notifyDataSetChanged();




        });


        binding.recycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                if (viewType == 0) {
                    SendMessageBinding binding = SendMessageBinding.inflate(getLayoutInflater());
                    return new MyRowHolder(binding.getRoot());

                } else {
                    RecieveMessageBinding binding = RecieveMessageBinding.inflate(getLayoutInflater());
                    return new MyRowHolder(binding.getRoot());
                }

            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {

                ChatMessage cht = messages.get(position);

                holder.messageText.setText(cht.getMessage());
                holder.timeText.setText(cht.getTimeSent());

            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

            @Override
            public int getItemViewType(int position) {
                ChatMessage cht = messages.get(position);

                if (cht.isSentButton) {
                    return 0;
                } else {
                    return 1;
                }
            }

        });

        //Initialize the chat room model object
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        mDAO = db.cmDAO();

        if(messages == null)
        {
            chatModel.messages.setValue(messages = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database

                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }



    }


    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;


        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(click -> {
                int position = getAbsoluteAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete the message " + messageText.getText())
                        .setTitle("Question:")
                        .setNegativeButton("No", (Dialog, Click) -> {})
                        .setPositiveButton("Yes", (Dialog, Click) ->{

                            ChatMessage removedMessage = messages.get(position);

//                                Executor thread1 = Executors.newSingleThreadExecutor();
//                                        thread1.execute(() -> {
//
//                                        //delete from database
//                                        mDAO.deleteMessages(removedMessage);//which chat message to delete?
//                                });

                                messages.remove(position);
                                myAdapter.notifyItemRemoved(position);


                                Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk ->{



                                        messages.add(position, removedMessage);
                                        myAdapter.notifyItemRemoved(position);
                                    })
                                    .show();

                    }).create().show();

            });



            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);
        }
    }

}
