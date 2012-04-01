package com.pexat.habhub.chasecartracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;

public class Main extends Activity
{
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Location location;

	protected TextView txt_latitude, txt_longitude, txt_altitude, txt_speed;
	protected Button btn_toggle;
	protected EditText txt_callsign;
	
	protected Thread httpThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		txt_latitude = (TextView) findViewById(R.id.txt_latitude);
		txt_longitude = (TextView) findViewById(R.id.txt_longitude);
		txt_altitude = (TextView) findViewById(R.id.txt_altitude);
		txt_speed = (TextView) findViewById(R.id.txt_speed);
		btn_toggle = (Button) findViewById(R.id.btn_toggle);
		txt_callsign = (EditText) findViewById(R.id.txt_callsign);

		locationListener = new MainLocationListener();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, locationListener);
		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		updateFields();
		
		httpThread = new Thread(new Runnable() {
			public void run()
			{
				while (true)
				{					
					Time time = new Time(Time.TIMEZONE_UTC);
					time.setToNow();
					
					ListenerTelemetry telemetry = new ListenerTelemetry(txt_callsign.getText().toString(), location, time, getDevice(), getClient());
														
					HabitatInterface.sendListenerTelemetry(telemetry);
					
					try
					{
						Thread.sleep(30000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			
		});
		
		btn_toggle.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				if (!httpThread.isAlive())
				{
					httpThread.start();
					btn_toggle.setText("Running...");
				}
			}			
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(this, "Please enable your GPS!", 10000).show();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		locationManager.removeUpdates(locationListener);
	}

	protected class MainLocationListener implements LocationListener
	{
		public void onLocationChanged(Location l)
		{
			location = l;

			updateFields();
		}

		public void onProviderDisabled(String p)
		{
		}

		public void onProviderEnabled(String p)
		{
		}

		public void onStatusChanged(String p, int s, Bundle e)
		{
		}
	}
	
	protected String getDevice()
	{
		String id = "%s %s; Android %s";
		
		return String.format(id, android.os.Build.BRAND, android.os.Build.MODEL, android.os.Build.VERSION.RELEASE);
	}
	
	protected String getClient()
	{		
		return "HabHub Chase Car Tracker; Priyesh Patel";
	}
	
	protected void updateFields()
	{
		txt_latitude.setText(Double.toString(location.getLatitude()));
		txt_longitude.setText(Double.toString(location.getLongitude()));
		txt_altitude.setText(Double.toString(location.getAltitude()));
		txt_speed.setText(Double.toString(location.getSpeed()));
	}
}