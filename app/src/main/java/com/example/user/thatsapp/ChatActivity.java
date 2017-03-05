package com.example.user.thatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


import static com.example.user.thatsapp.MainActivity.contactList;
import static com.example.user.thatsapp.MainActivity.currentUserEmail;

public class ChatActivity extends AppCompatActivity {


    //Declare items
    private EditText mMessageText;
    ListView mChatList;
    ChatListAdapter aChatAdapter;
    ArrayList<ChatItem> mChatItem;
    //Declare int variable
    int item_selection = 0 , timmer = 0;
    //Declare firebase item
    private Firebase mSelfRef, mChatWithRef;
    //Declare UserDatabasename class
    private UserDatabaseName dbname = new UserDatabaseName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(UserChatDetails.chatWith);
        //Declare button with id
        Button mSendButton;
        mSendButton = (Button) findViewById(R.id.send_button);
        //Initiate id
        mMessageText = (EditText) findViewById(R.id.message_text);
        mChatList = (ListView) findViewById(R.id.chat_list);
        //Initiate new Arraylist
        mChatItem = new ArrayList<>();

        //Initiate adapter with mChatItem list
        aChatAdapter = new ChatListAdapter(getApplicationContext(), mChatItem);
        //set context for firebase
        Firebase.setAndroidContext(this);
        //set offline database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //set firebase database url
        mSelfRef = new Firebase("https://thatsapp-86aef.firebaseio.com/UserDetails/"+dbname.convertEmailToDbName(currentUserEmail)+"/message");
        mChatWithRef = new Firebase("https://thatsapp-86aef.firebaseio.com/UserDetails/"+dbname.convertEmailToDbName(UserChatDetails.chatWith)+"/message");
        //keep synced the url data to local storage
        mSelfRef.keepSynced(true);

        //set OnClickListener to send button
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = mMessageText.getText().toString();
                //if the edit text boz input is not empty
                if(!messageText.isEmpty())
                {
                    //Declare map variable to store message, user, chatWith and timer data
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", currentUserEmail);
                    map.put("chatWithUser", UserChatDetails.chatWith);
                    map.put("timer", String.valueOf(timmer));
                    //push the map value to firebase database url of current user and chat with user
                    mSelfRef.push().setValue(map);
                    mChatWithRef.push().setValue(map);
                    //clear the edit text box
                    mMessageText.setText("");


                }
            }
        });
        //Set listener to current user message firebase database url to catch the real time chat message data
        mSelfRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //retrieve all the data and start analysing
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String chatwithuser = map.get("chatWithUser").toString();
                String timer = map.get("timer").toString();
                final String key = dataSnapshot.getKey().toString();
                //if the message is send by current user and match with the chatWith user
                if(userName.equals(currentUserEmail) && chatwithuser.equals(UserChatDetails.chatWith)){
                    //add new value to chatitem list
                   mChatItem.add(new ChatItem(message, currentUserEmail,key,timer));
                    aChatAdapter.notifyDataSetChanged();
                    //check if any timer is provided
                    if(!timer.isEmpty() && !timer.equals("0")){
                        long sec = 0;
                        //if timer is provided (1minute, 3minutes and 5 minutes) set value to sec
                        if (timer.equals("1")){
                            sec = 60000;
                        }else if(timer.equals("3")){
                            sec = 180000;
                        }
                        else if(timer.equals("5")){
                            sec = 300000;
                        }
                        //start a count down timer with the sec value
                        new CountDownTimer(sec, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }
                            //when count down finish, remove the chat details from that chat list
                            @Override
                            public void onFinish() {
                                for(ChatItem item : mChatItem){
                                    if(key.equals(item.getKey())){
                                        mChatItem.remove(item);
                                        aChatAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                mSelfRef.child(key).removeValue();
                            }
                        }.start();
                    }


                }
                //else if the message is send by the chat with user
                else if(chatwithuser.equals(currentUserEmail) && userName.equals(UserChatDetails.chatWith)){
                    //perform the same code with send by current user.
                   mChatItem.add(new ChatItem(message, UserChatDetails.chatWith,key,timer));
                    aChatAdapter.notifyDataSetChanged();

                    if(!timer.isEmpty() && !timer.equals("0")){
                        long sec = 0;
                        if (timer.equals("1")){
                            sec = 60000;
                        }else if(timer.equals("3")){
                            sec = 180000;
                        }
                        else if(timer.equals("5")){
                            sec = 300000;
                        }
                        new CountDownTimer(sec, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                for(ChatItem item : mChatItem){
                                    if(key.equals(item.getKey())){
                                        mChatItem.remove(item);
                                        aChatAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                mSelfRef.child(key).removeValue();
                            }
                        }.start();
                    }

                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


            //reset the adapter to get the latest data
            aChatAdapter = new ChatListAdapter(getApplicationContext(), mChatItem);
            aChatAdapter.notifyDataSetChanged();
            mChatList.setAdapter(aChatAdapter);






    }
    //set timmer value that selected by user the timer they wan (1, 3, 5 or none) minutes
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedid = item.getItemId();
        switch (selectedid){
            case R.id.one_minute_item:
                item.setChecked(true);
                item_selection = 1;
                timmer = 1;
                return true;
            case R.id.three_minute_item:
                item.setChecked(true);
                item_selection = 2;
                timmer = 3;
                return true;
            case R.id.five_minute_item:
                item.setChecked(true);
                item_selection = 3;
                timmer = 5;
                return true;
            case R.id.none_item:
                item.setChecked(true);
                item_selection = 0;
                timmer = 0;
                return true;
        }
        return true;
    }
    //create the item to menu bar to allow user to select the timer they wan (1, 3, 5 or none) minutes
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_chat, menu);
        MenuItem item_one_minute = menu.findItem(R.id.one_minute_item);
        MenuItem item_three_minute = menu.findItem(R.id.three_minute_item);
        MenuItem item_five_minute = menu.findItem(R.id.five_minute_item);
        MenuItem item_none = menu.findItem(R.id.none_item);
            if(item_selection == 1) {
                item_one_minute.setChecked(true);


            }else if(item_selection == 2) {
                item_three_minute.setChecked(true);

            }else if(item_selection == 3) {
                item_five_minute.setChecked(true);

            }else if (item_selection == 0) {
                item_none.setChecked(true);

            }else {
                item_none.setChecked(true);
            }

        return true;
    }


}
