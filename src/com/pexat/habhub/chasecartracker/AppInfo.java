package com.pexat.habhub.chasecartracker;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppInfo {
	public static String getDevice() {
		String id = "%s %s";

		return String.format(id, android.os.Build.BRAND, android.os.Build.MODEL);
	}
	
	public static String getDeviceSoftware() {
		String id = "Android %s";

		return String.format(id, android.os.Build.VERSION.RELEASE);
	}

	public static String getApplication() {
		return "HabHub Chase Car Tracker (Android)";
	}
	
	public static String getApplicationVersion(Context c) {
		String v = "unknown";
		
		try {
			v = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return v;
	}
}
