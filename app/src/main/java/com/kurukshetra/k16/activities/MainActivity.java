package com.kurukshetra.k16.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kurukshetra.k16.adapter.ChatAdapter;
import com.kurukshetra.k16.utility.*;
import com.kurukshetra.k16.service.ChatHead;
import com.kurukshetra.k16.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	ChatAdapter chatAdapter;
	ListView mListView;

	ArrayList<Message> arrayList;
	JSONFile jsonFile;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		arrayList = new ArrayList<> ();

		jsonFile = new JSONFile (getAssets ());

		String[] temp = jsonFile.getStringArray ("help");
		String help = "";
		for (String i : temp) help = help + "\n" + i;

		arrayList.add (new Message ("bot", help));

		chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);

		mListView = (ListView) findViewById (R.id.chat);
		mListView.setAdapter (chatAdapter);
	}

	@Override
	protected void onPause () {
		super.onPause ();
		startService (new Intent (this, ChatHead.class));
	}

	@Override
	protected void onDestroy () {
		super.onDestroy ();
		Toast.makeText (this, "onDestroy()", Toast.LENGTH_SHORT).show ();
		stopService (new Intent (this, ChatHead.class));
	}

	@Override
	protected void onResume () {
		super.onResume ();
		Toast.makeText (this, "onResume()", Toast.LENGTH_SHORT).show ();
		stopService (new Intent (this, ChatHead.class));
	}

	public void addChatMessage (View v) {

		EditText editText = (EditText) findViewById (R.id.new_msg);

		String msg = editText.getEditableText ().toString ();
		editText.setText ("");

		if (msg.replaceAll (" ", "").length () > 0)
			arrayList.add (new Message ("user", msg));
		else return;

		arrayList.add (new Message ("bot", jsonFile.parseMessage (msg)));

		chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);
		mListView.setAdapter (chatAdapter);
	}
}

