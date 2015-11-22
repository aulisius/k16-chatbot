package com.kurukshetra.k16;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	ArrayList<Message> arrayList;
	ChatAdapter chatAdapter;
	ListView mListView;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		String help = "Hey! Welcome to K! 16";
		Message welcome = new Message ();
		welcome.setType ("bot");
		welcome.setMessage (help);
		arrayList = new ArrayList<> ();
		arrayList.add (welcome);

		mListView = (ListView) findViewById (R.id.chat);
		chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);

		mListView.setAdapter (chatAdapter);


	}

	public void addChatMessage(View v) {
		EditText editText = (EditText) findViewById (R.id.new_msg);

		String msg = editText.getEditableText ().toString ();

		if(msg.replaceAll (" ", "").length () > 0) {

			Message message = new Message ();
			message.setType ("user");
			message.setMessage (msg);
			arrayList.add (message);

			//chatAdapter.notifyDataSetChanged ();

			Message botReply = new Message ();
			botReply.setType ("bot");
			botReply.setMessage ("This is the bot");

			arrayList.add(botReply);

			chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);
			//chatAdapter.notifyDataSetChanged ();
			mListView.setAdapter (chatAdapter);
		}

		editText.setText ("");
	}
}



class ChatAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Message> chatData;

	ChatAdapter(Context context, ArrayList<Message> stringList) {
		mContext = context;
		chatData = stringList;
	}

	@Override
	public boolean areAllItemsEnabled () {
		return true;
	}

	@Override
	public boolean isEnabled (int position) {
		return true;
	}

	@Override
	public void registerDataSetObserver (DataSetObserver observer) {

	}

	@Override
	public void unregisterDataSetObserver (DataSetObserver observer) {

	}

	@Override
	public int getCount () {
		return chatData.size ();
	}

	@Override
	public Object getItem (int position) {
		return chatData.get (position).getMessage ();
	}

	@Override
	public long getItemId (int position) {
		return position;
	}

	@Override
	public boolean hasStableIds () {
		return false;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent) {

		if(convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);

			View rView;
			if(chatData.get (position).getType ().equals ("user"))
				rView = layoutInflater.inflate (R.layout.user_message, null);
			else
				rView = layoutInflater.inflate (R.layout.bot_reply, null);

			TextView textView = (TextView) rView.findViewById (R.id.message);

			textView.setText (chatData.get (position).getMessage ());

			return rView;
		}
		else return convertView;
	}

	@Override
	public int getItemViewType (int position) {
		return chatData.get (position).getType ().equals ("user") ? 0 : 1;
	}

	@Override
	public int getViewTypeCount () {
		return 2;
	}

	@Override
	public boolean isEmpty () {
		return false;
	}
}