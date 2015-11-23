package com.kurukshetra.k16;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

	ArrayList<Message> arrayList;
	ChatAdapter chatAdapter;
	ListView mListView;
	HashMap<String, String[]> stringHashMap;
	JSONObject eventJSON;
	HashMap<String, String> queryType;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		stringHashMap = new HashMap<> ();
		queryType = new HashMap<> () ;
		arrayList = new ArrayList<> ();

		initJSONFile ();

		//JSONObject json  = new JSONObject ()
		String[] helpStrings = new String[] {
				"Hey! This is DexBot here to help you! <br/> You can use these commands to communicate with me<br/>",
				"when is ___? - e.g. coding<br/>",
				"about __? - e.g. Events <br/>",
				"help - To see this help message <br/>"
		};

		queryType.put ("help", "help");
		queryType.put ("about", "description");
		queryType.put ("when", "time");

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

	public void initJSONFile() {

		try {
			InputStream is = getAssets ().open ("events.json");
			BufferedReader br = new BufferedReader (new InputStreamReader (is));
			StringBuilder str = new StringBuilder ();
			String st = "";
			while((st = br.readLine ()) != null) {
				str.append (st);
			}
			JSONObject js = new JSONObject (str.toString ());
			JSONArray categoryJSONArray = js.getJSONArray ("events");

			String[] categories = new String[categoryJSONArray.length ()];
			for(int i = 0; i < categoryJSONArray.length (); i++ ) {
				categories[i] = categoryJSONArray.getJSONObject (i).getString ("name");
			}


			for(int i = 0; i < categoryJSONArray.length (); i++) {

				JSONArray tempArray = categoryJSONArray.getJSONObject (i).getJSONArray ("events");
				ArrayList<String> eventNameList = new ArrayList<> ();

				for(int j = 0; j < tempArray.length (); j++) {


					JSONObject tempObject = tempArray.getJSONObject (j);
					String key = tempObject.getString ("name").toLowerCase ();

					JSONArray tabs = tempObject.getJSONArray ("tabs");

					for(int k = 1; k < tabs.length (); k++) {
						eventNameList.add (tabs.getJSONObject (k).getString ("title"));
						eventNameList.add (tabs.getJSONObject (k).getString ("content"));
					}

					String[] description = new String[eventNameList.size ()];
					eventNameList.toArray (description);
					stringHashMap.put (key, description);
				}
			}

			stringHashMap.put ("events", categories);
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

		if (msg.replaceAll (" ", "").length () > 0) {

			Message message = new Message ();
			message.setType ("user");
			message.setMessage (msg);
			arrayList.add (message);

		}
		else return;

		Message botReply = new Message ();
		botReply.setType ("bot");
		botReply.setMessage ( parseMessage (msg) );
		arrayList.add (botReply);

		chatAdapter = new ChatAdapter (getApplicationContext (), arrayList);
		mListView.setAdapter (chatAdapter);
	}

	public String parseMessage(String message) {

		String mesg = "Please see 'help' to see the list of commands";

		message = message.toLowerCase ();

		String messageType = message.split ("\\s+")[0];

		String messageBody = message.substring (messageType.length ()).toLowerCase ().trim ();

		if (message.matches ("about(.*)")) {
			if(stringHashMap.get (messageType) != null) {
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