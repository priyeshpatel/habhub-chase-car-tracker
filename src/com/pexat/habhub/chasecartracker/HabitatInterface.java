package com.pexat.habhub.chasecartracker;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.pexat.net.SimpleHttp;

public class HabitatInterface {
	private static String _habitat_url = "http://habitat.habhub.org/";
	private static String _habitat_db = "habitat";

	protected static String getUUID() {
		String page = SimpleHttp.get(_habitat_url + "_uuids");

		try {
			JSONObject j = new JSONObject(page);
			page = j.getJSONArray("uuids").getString(0);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

		return page;
	}

	protected static void sendListenerTelemetry(ListenerTelemetry l) {
		String putURL = _habitat_url + _habitat_db + "/" + HabitatInterface.getUUID();
		StringEntity stringdata = null;
		try {
			stringdata = new StringEntity(l.getJSON());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpEntity data = (HttpEntity) stringdata;

		SimpleHttp.put(putURL, data);
	}
}
