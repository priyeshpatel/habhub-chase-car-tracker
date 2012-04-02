package com.pexat.habhub.chasecartracker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main extends Activity {
	// Preferences
	public static final String PREFS_NAME = "ChaseCarTrackerPrefs";

	// Dialogs
	private static final int DIALOG_SET_CALLSIGN = 1;

	// UI Elements
	private TextView txt_latitude, txt_longitude, txt_altitude, txt_speed, txt_callsign, txt_lastupdated;
	private Button btn_toggle;

	// Service
	private Messenger tsOutgoing = null;
	private boolean isBound = false;
	private final Messenger tsIncoming = new Messenger(new TrackerServiceIncomingHandler());

	// Current callsign
	private String callsign;

	/**
	 * Activity Setup
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		txt_latitude = (TextView) findViewById(R.id.txt_latitude);
		txt_longitude = (TextView) findViewById(R.id.txt_longitude);
		txt_altitude = (TextView) findViewById(R.id.txt_altitude);
		txt_speed = (TextView) findViewById(R.id.txt_speed);
		txt_callsign = (TextView) findViewById(R.id.txt_callsign);
		txt_lastupdated = (TextView) findViewById(R.id.txt_last_updated);
		btn_toggle = (Button) findViewById(R.id.btn_toggle);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		setNewCallsign(settings.getString("callsign", ""));

		if (trackerServiceRunning()) {
			doBindService();
			btn_toggle.setText("Stop Tracker");
		}

		btn_toggle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!isBound) {
					Intent i = new Intent(Main.this, TrackerService.class);
					i.putExtra("callsign", callsign);
					startService(i);
					doBindService();
					btn_toggle.setText("Stop Tracker");
				} else {
					doUnbindService();
					stopService(new Intent(Main.this, TrackerService.class));
					btn_toggle.setText("Start Tracker");
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			doUnbindService();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Service Communications
	 */

	private class TrackerServiceIncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TrackerService.MSG_GPS_DATA :
					Bundle b = msg.getData();
					txt_latitude.setText(b.getString("longitude"));
					txt_longitude.setText(b.getString("latitude"));
					txt_altitude.setText(b.getString("altitude"));
					txt_speed.setText(b.getString("speed"));
					txt_lastupdated.setText(b.getString("time"));
					break;
				default :
					super.handleMessage(msg);
					break;
			}
		}
	}

	private ServiceConnection tsConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			tsOutgoing = new Messenger(service);
			try {
				Message msg = Message.obtain(null, TrackerService.MSG_REGISTER_CLIENT);
				msg.replyTo = tsIncoming;
				tsOutgoing.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			tsOutgoing = null;
		}
	};

	private void doBindService() {
		bindService(new Intent(this, TrackerService.class), tsConnection, Context.BIND_AUTO_CREATE);
		isBound = true;
	}

	private void doUnbindService() {
		if (!isBound)
			return;

		if (tsOutgoing != null) {
			try {
				Message msg = Message.obtain(null, TrackerService.MSG_DEREGISTER_CLIENT);
				msg.replyTo = tsIncoming;
				tsOutgoing.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		unbindService(tsConnection);
		isBound = false;
	}

	/**
	 * Dialog & Menu
	 */

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_SET_CALLSIGN :
				LayoutInflater inflater = LayoutInflater.from(this);
				final View layout = inflater.inflate(R.layout.callsign_dialog, null);
				final EditText txt_editCallsign = (EditText) layout.findViewById(R.id.txt_callsign_dialog);
				txt_editCallsign.setText(callsign);

				final AlertDialog d = new AlertDialog.Builder(this).setTitle("Set Callsign").setView(layout).setPositiveButton("OK", null).create();

				d.setOnDismissListener(new DialogInterface.OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						String nc = txt_editCallsign.getText().toString();
						if (nc.equals("")) {
							txt_editCallsign.setError("Please enter a callsign");
							d.show();
						} else {
							setNewCallsign(nc);
						}
					}
				});

				return d;

			default :
				return super.onCreateDialog(id);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.opt_callsign :
				showDialog(DIALOG_SET_CALLSIGN);
				return true;
			case R.id.opt_about :
				startActivity(new Intent(this, About.class));
				return true;
			default :
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Helper Functions
	 */

	// Check if tracker service is running
	private boolean trackerServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.pexat.habhub.chasecartracker.TrackerService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// Set a new callsign
	private void setNewCallsign(String c) {
		if (c.equals("")) {
			showDialog(DIALOG_SET_CALLSIGN);
			return;
		}

		callsign = c;
		txt_callsign.setText(callsign + "_chase");

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("callsign", callsign);
		editor.commit();

		if (isBound && tsOutgoing != null) {
			try {
				Bundle b = new Bundle();
				b.putString("callsign", callsign);
				Message msg = Message.obtain(null, TrackerService.MSG_SET_CALLSIGN);
				msg.setData(b);
				msg.replyTo = tsIncoming;
				tsOutgoing.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}