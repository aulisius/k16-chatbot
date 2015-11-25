package com.kurukshetra.k16;

import android.app.ActionBar;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class ChatHead extends Service {
	public ChatHead () {
	}

	private WindowManager windowManager;
	private Button chatHead;
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
		chatHead.setBackgroundResource (R.mipmap.send);

		params = new WindowManager.LayoutParams (
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		windowManager.addView(chatHead, params);


		nparams = new WindowManager.LayoutParams (
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		nparams.gravity = Gravity.CENTER;
		nparams.x = 100;
		nparams.y = 400;

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
						return false;
					case ACTION_MOVE:
						params.x = initialX + (int) (motionEvent.getRawX () - initialTouchX);
						params.y = initialY + (int) (motionEvent.getRawY () - initialTouchY);
						windowManager.updateViewLayout (chatHead, params);
						return false;
				}
				return false;
			}
		});

		chatHead.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				startChat (context);
				Toast.makeText (context, "in click", Toast.LENGTH_LONG).show ();
			}
		});


	}

	public void startChat(Context ctx) {
		LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		View mView = layoutInflater.inflate (R.layout.activity_main, null);
		ListView lView = new ListView (this);
		//PopupWindow popupWindow = new PopupWindow (mView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		//popupWindow.showAsDropDown (chatHead, 50 ,-30);
		//popupWindow.setWidth (mView.getWidth ());
		//popupWindow.setHeight (mView.getHeight ());
		//popupWindow.showAtLocation (chatHead, Gravity.TOP, 200, 100);
		windowManager.addView (mView, nparams);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
	}
}
