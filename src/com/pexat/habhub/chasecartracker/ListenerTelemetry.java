package com.pexat.habhub.chasecartracker;

import java.text.DecimalFormat;

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
	public String application;

	public ListenerTelemetry() {
	}

	public ListenerTelemetry(String c, Location l, Time t, String device, String application) {
		this.setCallsign(c);
		this.setLocationData(l);
		this.setTimeData(t);
		this.setClient(device, application);
	}

	public void setLocationData(Location l) {
		this.latitude = Double.valueOf(new DecimalFormat("#.######").format(l.getLatitude()));
		this.longitude = Double.valueOf(new DecimalFormat("#.######").format(l.getLongitude()));
		this.speed = Double.valueOf(new DecimalFormat("#.##").format((l.getSpeed() / 1000) * 3600));
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

	public void setClient(String device, String application) {
		this.device = device;
		this.application = application;
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
			client.put("application", this.application);

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
