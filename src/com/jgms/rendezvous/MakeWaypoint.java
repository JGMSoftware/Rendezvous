package com.jgms.rendezvous;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.jgms.rendezvous.MyLocation.LocationResult;
import com.parse.Parse;
import com.parse.ParseObject;

public class MakeWaypoint extends Activity {

	final String TAG = "Make a Waypoint";
	final int LENGTHOFPIN = 8;
	public Location myWaypoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_waypoint);

		// Initialize Parse
		Parse.initialize(this, "PARSE_APPLICATION_ID",
				"PARSE_CLIENT_KEY");

		// Set up the tab host
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();

		// Set up tab 1
		TabSpec spec1 = tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("My Location",
				getResources().getDrawable(R.drawable.my_location_tab));

		// Set up tab 2
		TabSpec spec2 = tabHost.newTabSpec("Tab 2");
		spec2.setContent(R.id.tab2);
		spec2.setIndicator("Address",
				getResources().getDrawable(R.drawable.address_tab));

		// Set up tab 3
		TabSpec spec3 = tabHost.newTabSpec("Tab 3");
		spec3.setContent(R.id.tab3);
		spec3.setIndicator("Coordinate",
				getResources().getDrawable(R.drawable.coords_tab));

		// Add all of the tabs to the tab host
		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);

		// Get the current location
		Log.d(TAG, "Getting location");
		getLocation();

	}

	// MY LOCATION TAB ---------------------------------------------
	public void myLocationMake(View view) {
		// Set up widgets
		EditText name = (EditText) findViewById(R.id.loc_name);
		EditText summary = (EditText) findViewById(R.id.loc_summary);
		TextView displayPin = (TextView) findViewById(R.id.loc_pin);

		// Define the text as strings
		String waypointName = name.getText().toString();
		String waypointSummary = summary.getText().toString();

		// Check that both are filled
		if (checkAllFields(waypointName, waypointSummary)) {
			// If not, display error.
			Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_LONG)
					.show();
		}
		// Else, they are filled, generate pin
		else {
			String pin = generateWaypointPin(waypointName, waypointSummary);
			// OLD METHOD of getting last known location
			// Get current GPS location
			// Get the last known location
			/*
			 * Log.d(TAG, "Starting to find location"); LocationManager lm =
			 * (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			 * Criteria criteria = new Criteria(); String bestProvider =
			 * lm.getBestProvider(criteria, false); Location location =
			 * lm.getLastKnownLocation(bestProvider); Log.d(TAG,
			 * "Location Found");
			 */

			// Get the location from the global location variable declared at
			// the top.
			Location location = myWaypoint;

			// Get last know latitude and longitude as Strings from the location
			String latitude = Double.toString(location.getLatitude());
			Log.d(TAG, latitude);
			String longditude = Double.toString(location.getLongitude());
			Log.d(TAG, longditude);

			// Upload to Parse
			ParseObject waypoint = new ParseObject("Waypoints");
			waypoint.put("pin", pin);
			waypoint.put("Title", waypointName);
			waypoint.put("Summary", waypointSummary);
			waypoint.put("type", "waypoint");
			waypoint.put("longitude", longditude);
			waypoint.put("latitude", latitude);
			waypoint.saveInBackground();

			// Display pin to user
			displayPin.setText("PIN: " + pin);
			displayPin.setVisibility(0);

		}

	}

	// ADDRESS TAB---------------------------------------------
	public void addressMake(View view) {
		// Declare the widgets
		EditText name = (EditText) findViewById(R.id.adr_name);
		EditText summary = (EditText) findViewById(R.id.adr_summary);
		EditText address = (EditText) findViewById(R.id.adr_address);
		TextView displayPin = (TextView) findViewById(R.id.adr_pin);

		// Define the text as strings
		String waypointName = name.getText().toString();
		String waypointSummary = summary.getText().toString();
		String waypointAddress = address.getText().toString();

		// Check that both are filled
		if (checkAllFields(waypointName, waypointSummary)
				|| (waypointAddress.length() < 1)) {
			// If not, display error.
			Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_LONG)
					.show();
		}
		// Else, they are filled, generate pin
		else {
			String pin = generateWaypointPin(waypointName, waypointSummary);
			// Get location from address
			Geocoder coder = new Geocoder(this);
			List<Address> addressList;
			String strAddress = waypointAddress;

			try {
				// Attempt to get a location from the input address
				addressList = coder.getFromLocationName(strAddress, 5);
				if (address == null) {
					Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
				}
				// Get the coordinates from this address
				Address location = addressList.get(0);
				location.getLatitude();
				location.getLongitude();

				String lat = Double.toString(location.getLatitude());
				String lon = Double.toString(location.getLongitude());

				Log.d(TAG, lat);
				Log.d(TAG, lon);

				// Upload to Parse
				ParseObject waypoint = new ParseObject("Waypoints");
				waypoint.put("pin", pin);
				waypoint.put("Title", waypointName);
				waypoint.put("Summary", waypointSummary);
				waypoint.put("type", "waypoint");
				waypoint.put("longitude", lon);
				waypoint.put("latitude", lat);
				waypoint.saveInBackground();

				// Display this pin to the user
				displayPin.setText("PIN: " + pin);
				displayPin.setVisibility(0);
				Toast.makeText(this, R.string.save_confirm, Toast.LENGTH_LONG)
						.show();

				// Catch exception
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Problem finding the address.",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	// COORDINATES TAB------------------------------------------
	public void coordsMake(View view) {
		// Declare the widgets
		EditText name = (EditText) findViewById(R.id.cor_name);
		EditText summary = (EditText) findViewById(R.id.cor_summary);
		EditText latitude = (EditText) findViewById(R.id.cor_lat);
		EditText longditude = (EditText) findViewById(R.id.cor_lon);
		TextView displayPin = (TextView) findViewById(R.id.cor_pin);

		// Define the text as strings
		String waypointName = name.getText().toString();
		String waypointSummary = summary.getText().toString();
		String waypointLat = latitude.getText().toString();
		String waypointLon = longditude.getText().toString();

		// Check that both are filled
		if (checkAllFields(waypointName, waypointSummary)
				|| (waypointLat.length() < 1) || (waypointLon.length() < 1)) {
			// If not, display error.
			Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_LONG)
					.show();
		}

		// Else, they are filled, generate pin
		else { // Check latitude
			if (Double.parseDouble(waypointLat) < -90
					|| Double.parseDouble(waypointLat) > 90) {
				Toast.makeText(this, R.string.lat_error, Toast.LENGTH_LONG)
						.show();
			}// Check longditude
			else if ((Double.parseDouble(waypointLon) < -180)
					|| (Double.parseDouble(waypointLon) > 180)) {
				Toast.makeText(this, R.string.lon_error, Toast.LENGTH_LONG)
						.show();
			} else {
				String pin = generateWaypointPin(waypointName, waypointSummary);
				displayPin.setText("PIN: " + pin);
				displayPin.setVisibility(0);
				Toast.makeText(this, R.string.save_confirm, Toast.LENGTH_LONG)
						.show();
				// Upload to Parse
				ParseObject waypoint = new ParseObject("Waypoints");
				waypoint.put("pin", pin);
				waypoint.put("Title", waypointName);
				waypoint.put("Summary", waypointSummary);
				waypoint.put("type", "Waypoint");
				waypoint.put("longitude", waypointLon);
				waypoint.put("latitude", waypointLat);
				waypoint.saveInBackground();

			}
		}

	}

	// Method to generate the pin when passed the name and the summary
	public String generateWaypointPin(String name, String summary) {
		// Hash the name and description together
		int integerPin = (name.hashCode() + summary.hashCode());
		String stringPin = Integer.toString(integerPin);
		Log.d(TAG, stringPin);
		int pinLength = stringPin.length();
		String pin = "PIN";
		int salt = 0;
		if (pinLength > 4) {
			// If the pin length is greater than 4, truncate it to the first 4
			// characters
			Character one = stringPin.charAt(0);
			Character two = stringPin.charAt(1);
			Character three = stringPin.charAt(2);
			Character four = stringPin.charAt(3);
			String truncatedHash = "" + one + two + three + four;
			Log.d(TAG, truncatedHash);
			// Generate a random 4 digit salt
			salt = (int) ((Math.random() * 9999) + 1);
			// Concatenate these to form the pin
			pin = truncatedHash + "" + salt;
			Log.d(TAG, pin);
		}
		if (pinLength <= 4) {
			// Find out how long the salt needs to be
			int saltLength = ((LENGTHOFPIN - pinLength) - 1);
			// String to hold the number of digits to multiply math.random by
			String saltyString = "";
			for (int i = 0; i <= (saltLength); i++) {
				saltyString += "9";
			}
			Log.d(TAG, saltyString);
			// Generate the random salt
			salt = (int) ((Math.random() * Integer.parseInt(saltyString)) + 1);
			pin = stringPin + "" + salt;
			Log.d(TAG, pin);
		}

		return pin;
	}

	// A method to check if the fields are all filled
	public boolean checkAllFields(String name, String summary) {
		if (name.length() < 1 || (summary.length() < 1)) {
			return true;
		}
		return false;
	}

	// Called to get the current location
	public void getLocation() {
		LocationResult locationResult = new LocationResult() {
			// Called when MyLocation class gets location
			@Override
			public void gotLocation(Location location) {
				Log.d(TAG, "Got location!");
				// Set the global location variable to the user's current
				// location
				myWaypoint = location;
			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}

}
