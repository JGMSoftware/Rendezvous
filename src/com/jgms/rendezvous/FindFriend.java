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

public class FindFriend extends Activity {

	final String TAG = "Find a Friend";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_friend);

		// Initialize Parse
		Parse.initialize(this, "mVi4YTWyPwowDMi65MNilKy6YWJoYrHF5th2CsIY",
				"sH9EaQuylyjOpMbp20WHkGUImkwXef8nX75Cf9ZW");
	}

	// Method to show the position of a friend on Google Maps.
	// Same process as in FindExisting.java, see full commentary there.
	public void showFriendOnMap(View view) {
		Log.d(TAG, "clicked");
		TextView waypointTitle = (TextView) findViewById(R.id.friend_header);
		TextView latText = (TextView) findViewById(R.id.friend_lat);
		TextView lonText = (TextView) findViewById(R.id.friend_lon);

		Log.d(TAG, "Setting lat and lon");
		String title = waypointTitle.getText().toString();
		String latitude = latText.getText().toString();
		String longitude = lonText.getText().toString();

		Intent i = new Intent("com.jgms.rendezvous.ShowMap");
		i.putExtra("title", title);
		i.putExtra("latitude", latitude);
		i.putExtra("longitude", longitude);

		startActivityForResult(i, 1);
	}

	// Same process as in FindExisting.java, see full commentary there.
	public void findFriend(View view) {
		// Declare the text field
		EditText waypointPin = (EditText) findViewById(R.id.friend_pin);

		// Declare the string from the text field
		String waypoint = waypointPin.getText().toString();

		if (waypointPin.length() < 1) {
			Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_LONG)
					.show();
		} else {
			ParseQuery query = new ParseQuery("Waypoints");
			query.whereEqualTo("pin", waypoint);
			query.getFirstInBackground(new GetCallback() {
				public void done(ParseObject object, ParseException e) {
					if (object == null) {
						objectReturnFailed();
						Log.d(TAG, "The request failed.");
					} else {
						objectReturnedSuccessfully(object);
						Log.d(TAG, "Finished successfully");

					}
				}
			});
		}
	}

	public void objectReturnFailed() {
		Toast.makeText(this, R.string.retrieval_fail, Toast.LENGTH_LONG).show();
	}

	public void objectReturnedSuccessfully(ParseObject object) {
		// Get strings from the Parse object
		Log.d(TAG, "Getting values from Parse");
		String title = object.get("Title").toString();
		String lat = object.get("latitude").toString();
		String lon = object.get("longitude").toString();
		Log.d(TAG, "Values retrieved from Parse");

		// Declare the widgets
		TextView Title = (TextView) findViewById(R.id.friend_header);
		Button showFriend = (Button) findViewById(R.id.showOnMap);
		TextView friendLat = (TextView) findViewById(R.id.friend_lat);
		TextView friendLon = (TextView) findViewById(R.id.friend_lon);

		// Display the TextViews with data
		Title.setVisibility(View.VISIBLE);
		Title.setText(title);

		friendLat.setText(lat);
		friendLat.setVisibility(View.VISIBLE);
		friendLon.setText(lon);
		friendLon.setVisibility(View.VISIBLE);
		Log.d(TAG, "Lat and long successful");

		showFriend.setVisibility(View.VISIBLE);
		Log.d(TAG, "Button shown");

	}
}
