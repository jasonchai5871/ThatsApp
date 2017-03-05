package com.example.user.thatsapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.user.thatsapp.MainActivity.currentUserEmail;

/**
 * Created by User on 2/20/2017.
 */

public class ChatListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChatItem> mChatItenList;
    private long sec = 0;

    public ChatListAdapter(Context mContext, List<ChatItem> mChatItenList) {
        this.mContext = mContext;
        this.mChatItenList = mChatItenList;
    }

    @Override
    public int getCount() {
        return mChatItenList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatItenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Declare and initiate the value to all items
        View v = View.inflate(mContext, R.layout.chat_items, null);
        LinearLayout chatlayout = (LinearLayout) v.findViewById(R.id.chat_layout);
        TextView message = (TextView) v.findViewById(R.id.chat_message);
        final TextView email = (TextView) v.findViewById(R.id.chat_email_text);
        String check = mChatItenList.get(position).getEmail();
        message.setText(mChatItenList.get(position).getMessage());
        String timer = mChatItenList.get(position).getTime();
        final String chatwith = mChatItenList.get(position).getEmail();
        //if message send by current user, move the gravity to the right and the sender name become you
        //if any timer is detected, preview the message expired duration
        if(check.equals(currentUserEmail)) {
            chatlayout.setGravity(Gravity.RIGHT);
            email.setText("You");
            if(timer.equals("1"))
                email.setText("Expired in 1 minute  " + "You");
            else if(timer.equals("3"))
                email.setText("Expired in 3 minutes  " + "You");
            else if(timer.equals("5"))
                email.setText("Expired in 5 minutes  " + "You");
        }
        //else if message is from chat with user, set gravity to left and show the chat with user email
        //if any timer is detected, preview the message expired duration
        else
        {
            chatlayout.setGravity(Gravity.LEFT);
            email.setText(chatwith);
            if(timer.equals("1"))
                email.setText(chatwith+"  Expired in 1 minute  ");
            else if(timer.equals("3"))
                email.setText(chatwith+"  Expired in 3 minutes  ");
            else if(timer.equals("5"))
                email.setText(chatwith+"  Expired in 5 minutes  ");
        }
        return v;
    }

}
