package com.kurukshetra.k16;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

	ArrayList<Message> arrayList;
	ChatAdapter chatAdapter;
	ListView mListView;
	HashMap<String, String[]> stringHashMap;
	HashMap<String, String> queryType;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		stringHashMap = new HashMap<> ();
		queryType = new HashMap<> () ;
		arrayList = new ArrayList<> ();

		initJSONFile ();

		String[] helpStrings = new String[] {
				"Hey! This is DexBot here to help you! <br/> You can use these commands to communicate with me <br/>",
				"when is ___? - e.g. when coding <br/>",
				"about __? - e.g. about categories <br/>",
				"help - To see this help message <br/>"
		};

		queryType.put ("help", "help");
		queryType.put ("about", "description");
		queryType.put ("when", "time");

		stringHashMap.put ("help", helpStrings);

		String[] temp = stringHashMap.get ("help");
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
		Toast.makeText (this, "onPause()", Toast.LENGTH_SHORT).show ();
	}

	@Override
	protected void onDestroy () {
		super.onDestroy ();
		Toast.makeText (this, "onDestroy()", Toast.LENGTH_SHORT).show ();
		stopService (new Intent (this, ChatHead.class));
	}

	@Override
	protected void onStop () {
		super.onStop ();
		//Toast.makeText (this, "onStop()", Toast.LENGTH_SHORT).show ();
		//startService (new Intent (this, ChatHead.class));
	//	stopService (new Intent (this, ChatHead.class));
	}

	@Override
	public void onBackPressed () {
		super.onBackPressed ();
		//Toast.makeText (this, "onBackPressed()", Toast.LENGTH_SHORT).show ();
		//startService (new Intent (this, ChatHead.class));
	}

	@Override
	protected void onResume () {
		super.onResume ();
		Toast.makeText (this, "onResume()", Toast.LENGTH_SHORT).show ();
		stopService (new Intent (this, ChatHead.class));
	}

	public void initJSONFile() {

		try {
			BufferedReader br = new BufferedReader (new InputStreamReader (getAssets ().open ("events.json")));

			StringBuilder builder = new StringBuilder ();
			String JSONString = "";
			while((JSONString = br.readLine ()) != null) builder.append (JSONString);

			JSONArray categoryJSONArray = new JSONObject (builder.toString ()).getJSONArray ("events");

			String[] categories = new String[categoryJSONArray.length ()];

			for(int i = 0; i < categoryJSONArray.length (); i++) {
				categories[i] = categoryJSONArray.getJSONObject (i).getString ("name");
				JSONArray tempArray = categoryJSONArray.getJSONObject (i).getJSONArray ("events");

				String[] eventNames = new String[tempArray.length ()];

				for(int j = 0; j < tempArray.length (); j++) {

					ArrayList<String> contentList = new ArrayList<> ();

					JSONObject tempObject = tempArray.getJSONObject (j);
					eventNames[j] = tempObject.getString ("name").toLowerCase ();

					JSONArray tabs = tempObject.getJSONArray ("tabs");

					for(int k = 1; k < tabs.length (); k++) {
						contentList.add (tabs.getJSONObject (k).getString ("title"));
						contentList.add (tabs.getJSONObject (k).getString ("content"));
					}

					String[] description = new String[contentList.size ()];
					contentList.toArray (description);
					stringHashMap.put (eventNames[j], description);
				}
				stringHashMap.put (categories[i].toLowerCase (), eventNames);
			}

			stringHashMap.put ("categories", categories);
		}
		catch(IOException ie) {
			Log.d ("JSONFile", "Read error");
		}
		catch (JSONException je) {
			Log.d ("JSONArray", "Malformed JSON");
		}

	}
	public void addChatMessage (View v) {

		EditText editText = (EditText) findViewById (R.id.new_msg);

		String msg = editText.getEditableText ().toString ();
		editText.setText ("");

		if (msg.replaceAll (" ", "").length () > 0)
			arrayList.add (new Message ("user", msg));
		else return;

		arrayList.add (new Message ("bot", parseMessage (msg)));

		chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);
		mListView.setAdapter (chatAdapter);
	}

	public String parseMessage(String message) {

		String mesg = "Please see 'help' to see the list of commands";

		message = message.toLowerCase ();

		String messageType = message.split ("\\s+")[0];

		String messageBody = message.substring (messageType.length ()).toLowerCase ().trim ();

		if (message.matches ("about(.*)")) {
			if(stringHashMap.get (messageBody) != null) {
				mesg = "";
				String[] wordParts = stringHashMap.get (messageBody);
				for(String i : wordParts) mesg = mesg + "<br/>" + i;
			}
		}
		else if(message.matches ("help(.*)")) {
			String[] wordParts = stringHashMap.get ("help");
			mesg = "";
			for(String i : wordParts) mesg = mesg + i;
		}

		return mesg;
	}

	//@Override
	//public void onPause
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
	public void registerDataSetObserver (DataSetObserver observer) {}

	@Override
	public void unregisterDataSetObserver (DataSetObserver observer) {}

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
			rView = layoutInflater.inflate (R.layout.message, null);
		else
			rView = layoutInflater.inflate (R.layout.reply, null);

		TextView textView = (TextView) rView.findViewById (R.id.message);

		textView.setText (Html.fromHtml (chatData.get (position).getMessage ()));

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
