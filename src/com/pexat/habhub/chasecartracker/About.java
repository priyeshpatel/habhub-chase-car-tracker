package com.pexat.habhub.chasecartracker;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class About extends ListActivity {
	String application_version = "unknown";

	String data[][];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			application_version = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		data = new String[][]{
				{"Author", "Priyesh Patel"},
				{"Version", application_version},
				{"Release Date", getString(R.string.release_date)},
				{"Device Information", getDeviceInformation()}
		};

		setListAdapter(new TwoLineArrayAdapter(this, data));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0 :
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.author_url)));
				startActivity(i);
				break;
		}
		super.onListItemClick(l, v, position, id);
	}

	public class TwoLineArrayAdapter extends ArrayAdapter<String[]> {
		private int mListItemLayoutResId;

		public TwoLineArrayAdapter(Context context, String[][] ts) {
			this(context, android.R.layout.two_line_list_item, ts);
		}

		public TwoLineArrayAdapter(Context context, int listItemLayoutResourceId, String[][] ts) {
			super(context, listItemLayoutResourceId, ts);
			mListItemLayoutResId = listItemLayoutResourceId;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View listItemView = convertView;
			if (null == convertView) {
				listItemView = inflater.inflate(mListItemLayoutResId, parent, false);
			}

			listItemView.setPadding(20, 20, 20, 20);

			TextView lineOneView = (TextView) listItemView.findViewById(android.R.id.text1);
			TextView lineTwoView = (TextView) listItemView.findViewById(android.R.id.text2);

			String[] t = (String[]) getItem(position);
			lineOneView.setText(lineOneText(t));
			lineTwoView.setText(lineTwoText(t));

			return listItemView;
		}

		public String lineOneText(String[] t) {
			return t[0];
		}

		public String lineTwoText(String[] t) {
			return t[1];
		}
	}

	/**
	 * Helper Functions
	 */
	
	private String getDeviceInformation() {
		String id = "%s %s; Android %s";

		return String.format(id, android.os.Build.BRAND, android.os.Build.MODEL, android.os.Build.VERSION.RELEASE);
	}
}