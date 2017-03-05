package com.example.user.thatsapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by User on 2/28/2017.
 */

public class InboxListAdapter extends BaseAdapter {


    private Context mContext;
    private List<InboxItem> mInboxItenList;

    public InboxListAdapter(Context mContext, List<InboxItem> mInboxItenList) {
        this.mContext = mContext;
        this.mInboxItenList = mInboxItenList;
    }

    @Override
    public int getCount() {
        return mInboxItenList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInboxItenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.inbox_items, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.inbox_layout);
        TextView email = (TextView)v.findViewById(R.id.inbox_email_text);
        TextView message = (TextView)v.findViewById(R.id.inbox_chat_text);
        email.setText(mInboxItenList.get(position).getEmail());
        message.setText(mInboxItenList.get(position).getMessage());
        return v;
    }
}
