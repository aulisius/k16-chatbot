package com.kurukshetra.k16;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	ArrayList<Message> arrayList;
	ChatAdapter chatAdapter;
	ListView mListView;
	HashMap<String, String[]> stringHashMap;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		stringHashMap = new HashMap<> ();
		arrayList = new ArrayList<> ();

		String[] eventStrings = new String[] {
				"Engineering",
				"Robotics",
				"Quizzes",
				"Management",
				"Coding",
				"Online",
				"General"
		};

		String[] helpStrings = new String[] {
				"Hey! This is DexBot here to help you!\n You can use these commands to communicate with me",
				"when is ___? - To know when some event will occur e.g. coding",
				"what are __? - To know about something e.g. Events",
				"help - To see this help message"
		};

		stringHashMap.put ("events", eventStrings);
		stringHashMap.put ("help", helpStrings);

		String[] temp = stringHashMap.get ("help");
		String help = "";
		for (String i : temp) help = help + "\n" + i;

		Message welcome = new Message ();
		welcome.setType ("bot");
		welcome.setMessage (help);

		arrayList.add (welcome);

		chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);

		mListView = (ListView) findViewById (R.id.chat);
		mListView.setAdapter (chatAdapter);
	}

	public void addChatMessage (View v) {
		EditText editText = (EditText) findViewById (R.id.new_msg);

		String msg = editText.getEditableText ().toString ();

		editText.setText ("");

		if (msg.replaceAll (" ", "").length () > 0) {

			Message message = new Message ();
			message.setType ("user");
			message.setMessage (msg);
			arrayList.add (message);

		}
		else return;

		Message botReply = new Message ();
		botReply.setType ("bot");

		String mesg = "";
		if (msg.toLowerCase ().matches ("what(.*)")) {
			if(msg.toLowerCase().matches ("(.*)events(.*)")) {
				String[] wordParts = stringHashMap.get ("events");
				for(String i : wordParts) mesg = mesg + "\n" + i;
			}
		}
		else if(msg.toLowerCase ().matches ("(.*)help(.*)")) {
			String[] wordParts = stringHashMap.get ("help");

			for(String i : wordParts) mesg = mesg + "\n" + i;
		}
		else mesg = "Please see 'help' to see the list of commands";

		botReply.setMessage (mesg);

		arrayList.add (botReply);

		chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);
		mListView.setAdapter (chatAdapter);
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