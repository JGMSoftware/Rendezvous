package com.jgms.rendezvous;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FindExisting extends Activity {

	final String TAG = "FindExisting";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_waypoint);

		// Initialize Parse
		Parse.initialize(this, "PARSE_APPLICATION_ID",
				"PARSE_CLIENT_KEY");
	}

	public void findWaypointBtnClicked(View view) {
		// Declare the text field
		EditText waypointPin = (EditText) findViewById(R.id.waypoint_pin);

		// Declare the string from the text field
		String waypoint = waypointPin.getText().toString();

		//Check that the field has text in it
		if (waypointPin.length() < 1) {
			//If it doesn't, display an error message to the user
			Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_LONG)
					.show();
		} else {
			//If it does, query Parse for the waypoint
			ParseQuery query = new ParseQuery("Waypoints");
			query.whereEqualTo("pin", waypoint);
			query.getFirstInBackground(new GetCallback() {
				public void done(ParseObject object, ParseException e) {
					if (object == null) {
						//There is no match on Parse for the specified Waypoint
						objectReturnFailed();
						Log.d(TAG, "The request failed.");
					} else {
						//Parse found the waypoint
						Log.d(TAG, "Object returned successfully");
						objectReturnedSuccessfully(object);

					}
				}
			});
		}
	}

	//Method to show the waypoint on a map
	public void showOnMap(View view) {
		//Declare the widgets
		TextView waypointTitle = (TextView) findViewById(R.id.waypoint_title);
		TextView latText = (TextView) findViewById(R.id.find_lat);
		TextView lonText = (TextView) findViewById(R.id.find_lon);

		//Get the title, latitude ang longitude of the waypoint from the screen
		String title = waypointTitle.getText().toString();
		String latitude = latText.getText().toString();
		String longitude = lonText.getText().toString();

		//Declare a new intent that passes title, latitude and longitude of the waypoint
		Intent i = new Intent("com.jgms.rendezvous.ShowMap");
		i.putExtra("title", title);
		i.putExtra("latitude", latitude);
		i.putExtra("longitude", longitude);

		//Show the map with a pin on the waypoint
		startActivityForResult(i, 1);
	}

	public void objectReturnedSuccessfully(ParseObject object) {
		Log.d(TAG, "Getting strings from Parse Object");
		// Get strings from the Parse object
		String title = object.get("Title").toString();
		String summary = object.get("Summary").toString();
		String lat = object.get("latitude").toString();
		String lon = object.get("longitude").toString();
		Log.d(TAG, "Got strings successfully");
		Log.d(TAG, title);
		Log.d(TAG, summary);
		Log.d(TAG, lat);
		Log.d(TAG, lon);

		// Declare the widgets
		Log.d(TAG, "Declaring widgets");
		TextView waypointTitle = (TextView) findViewById(R.id.waypoint_title);
		TextView waypointSummary = (TextView) findViewById(R.id.waypoint_summary);
		Button showOnMap = (Button) findViewById(R.id.show_on_map);
		TextView latText = (TextView) findViewById(R.id.find_lat);
		TextView lonText = (TextView) findViewById(R.id.find_lon);
		Log.d(TAG, "Declared widgets successfully");

		// Display the TextViews with data
		waypointTitle.setVisibility(0);
		Log.d(TAG, "Set Visibility ok");
		waypointTitle.setText(title);

		waypointSummary.setVisibility(View.VISIBLE);
		waypointSummary.setText(summary);
		Log.d(TAG, "Waypoint OK");

		latText.setVisibility(View.VISIBLE);
		latText.setText(lat);
		Log.d(TAG, "Lat ok");

		lonText.setVisibility(View.VISIBLE);
		lonText.setText(lon);

		//Show the "Show On Map" button
		showOnMap.setVisibility(View.VISIBLE);

	}

	public void objectReturnFailed() {
		//Display an error message to the user if the object was not returned successfully from Parse
		Toast.makeText(this, R.string.retrieval_fail, Toast.LENGTH_LONG).show();
	}

}
