package com.kurukshetra.k16.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.kurukshetra.k16.adapter.ChatAdapter;
import com.kurukshetra.k16.utility.*;
import com.kurukshetra.k16.R;

import java.util.ArrayList;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class ChatHead extends Service {

	public ChatHead () {
	}

	private WindowManager windowManager;
	private Button chatHead;
	private View mView;
	private WindowManager.LayoutParams params, nparams;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		final Context context = this;

		chatHead = new Button (context);
		chatHead.setBackgroundResource (R.drawable.ic_send);

		params = new WindowManager.LayoutParams (
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;


		nparams = new WindowManager.LayoutParams (
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAGS_CHANGED | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				PixelFormat.TRANSLUCENT);

		nparams.gravity = Gravity.CENTER;
		nparams.x = 0;
		nparams.y = 250;

		windowManager.addView (chatHead, params);

		chatHead.setOnTouchListener (new View.OnTouchListener () {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch (View view, MotionEvent motionEvent) {

				switch (motionEvent.getAction ()) {
					case ACTION_DOWN:
						initialX = params.x;
						initialY = params.y;
						initialTouchX = motionEvent.getRawX ();
						initialTouchY = motionEvent.getRawY ();

						return false;
					case ACTION_UP:
						if (!((initialTouchX == motionEvent.getRawX ()) && (initialTouchY == motionEvent.getRawY ()))) {

							params.y = (int) motionEvent.getRawY ();
							params.x = 0;
							windowManager.updateViewLayout (chatHead, params);
							return true;
						} else {
							params.x = 0;
							params.y = 100;
							windowManager.updateViewLayout (chatHead, params);
							return false;
						}

					case ACTION_MOVE:
						params.x = initialX + (int) (motionEvent.getRawX () - initialTouchX);
						params.y = initialY + (int) (motionEvent.getRawY () - initialTouchY);
						windowManager.updateViewLayout (chatHead, params);
						return true;
				}
				return false;
			}
		});

		chatHead.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				if (mView != null && mView.isShown ())
					windowManager.removeView (mView);
				else
					startChat (context);
			}
		});


	}

	public void startChat(Context ctx) {
		LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		mView = layoutInflater.inflate (R.layout.chat, null);
		final JSONFile jsonFile = new JSONFile (getAssets ());

		final ArrayList<Message> arrayList = new ArrayList<> ();
		String[] welcomeM = jsonFile.getStringArray ("help");

		String welcomeMessage = "";

		for (String s : welcomeM) welcomeMessage += s;

		arrayList.add(new Message("bot", welcomeMessage));
		final ListView lView = (ListView)  mView.findViewById (R.id.chat);
		lView.setAdapter (new ChatAdapter (getApplicationContext (), arrayList));

		Button btn = (Button) mView.findViewById (R.id.btn);

		EditText ed = (EditText) mView.findViewById (R.id.new_msg);

		btn.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {

				EditText editText = (EditText) mView.findViewById (R.id.new_msg);

				String msg = editText.getEditableText ().toString ();
				editText.setText ("");

				if (msg.replaceAll ("\\s+", "").length () > 0)
					arrayList.add (new Message ("user", msg));
				else return;

				arrayList.add (new Message ("bot", jsonFile.parseMessage (msg)));

				lView.setAdapter (new ChatAdapter (getApplicationContext (), arrayList));
			}
		});

		windowManager.addView (mView, nparams);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
		if (mView != null) {
			windowManager.removeView(mView);
			mView = null;
		}
	}
}
