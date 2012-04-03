package com.pexat.habhub.chasecartracker;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.text.format.Time;

public class ListenerTelemetry {
	public long time_created;
	public long time_uploaded;
	public String callsign;
	public int hour;
	public int minute;
	public int second;
	public double latitude;
	public double longitude;
	public double speed;
	public long altitude;
	public String device;
	public String device_software;
	public String application;
	public String application_version;

	public ListenerTelemetry() {
	}

	public ListenerTelemetry(String c, Location l, Time t, String d, String ds, String a, String av) {
		this.setCallsign(c);
		this.setLocationData(l);
		this.setTimeData(t);
		this.setClient(d, ds, a, av);
	}

	public void setLocationData(Location l) {
		this.latitude = (double) Math.round(l.getLatitude() * 1000000) / 1000000;
		this.longitude = (double) Math.round(l.getLongitude() * 1000000) / 1000000;
		this.speed = (double) Math.round(((l.getSpeed() / 1000) * 3600) * 100) / 100;
		this.altitude = Math.round(l.getAltitude());
	}

	public void setTimeData(Time t) {
		this.time_created = Long.valueOf(t.format("%s"));
		this.time_uploaded = time_created;
		this.hour = t.hour;
		this.minute = t.minute;
		this.second = t.second;
	}

	public void setCallsign(String c) {
		this.callsign = c;
	}

	public void setClient(String d, String ds, String a, String av) {
		this.device = d;
		this.device_software = ds;
		this.application = a;
		this.application_version = av;
	}

	public String getJSON() {
		JSONObject output = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject time = new JSONObject();
		JSONObject client = new JSONObject();

		try {
			output.put("type", "listener_telemetry");
			output.put("time_created", this.time_created);
			output.put("time_uploaded", this.time_created);

			data.put("latitude", this.latitude);
			data.put("longitude", this.longitude);
			data.put("speed", this.speed);
			data.put("altitude", this.altitude);
			data.put("callsign", this.callsign);

			client.put("device", this.device);
			client.put("device_software", this.device_software);
			client.put("application", this.application);
			client.put("application_version", this.application_version);

			time.put("hour", this.hour);
			time.put("minute", this.minute);
			time.put("second", this.second);

			data.put("client", client);
			data.put("time", time);
			output.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return output.toString();
	}
}
