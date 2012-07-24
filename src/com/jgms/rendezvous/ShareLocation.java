package com.jgms.rendezvous;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jgms.rendezvous.MyLocation.LocationResult;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ShareLocation extends Activity {

	final String PREFS_NAME = "RendezvousPreferences";
	final String TAG = "ShareLocationActivity";
	final int LENGTHOFPIN = 8;
	public Location myLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_location);
		//Set up Shared Preferences to store persistent data
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		//Is this launch the first run of the app?
		boolean isFirstRun = settings.getBoolean("isFirstRun", true);
		//Set up preference to store the user's pin.
		String storedPin = settings.getString("pin", "");

		// Declare all the widgets
		TextView forename_label = (TextView) findViewById(R.id.forename_label);
		EditText forename = (EditText) findViewById(R.id.forename);
		TextView surname_label = (TextView) findViewById(R.id.surname_label);
		EditText surname = (EditText) findViewById(R.id.surname);
		TextView pinDisplay = (TextView) findViewById(R.id.pinDisplay);

		//If it is the first run, show the text fields to enter a name.
		//Hide the textview to display the user's pin.
		if (isFirstRun) {
			forename_label.setVisibility(0);
			forename.setVisibility(0);
			surname_label.setVisibility(0);
			surname.setVisibility(0);
			pinDisplay.setVisibility(View.INVISIBLE);
			Log.d(TAG, "This is the first run");
		} else {
			//Else, get rid of the setup
			forename_label.setVisibility(View.GONE);
			forename.setVisibility(View.GONE);
			surname_label.setVisibility(View.GONE);
			surname.setVisibility(View.GONE);
			//Display the pin to the user
			pinDisplay.setVisibility(0);
			//Pull the pin from the SharedPreferences
			pinDisplay.setText("Your PIN is: " + storedPin);
			Log.d(TAG, "This is not the first run");

		}

		// Initialize Parse
		Parse.initialize(this, "PARSE_APPLICATION_ID",
				"PARSE_CLIENT_KEY");
		// Get the current location
		Log.d(TAG, "Getting location");
		getLocation();

	}

	//Share the user's location
	public void shareLocation(View view) {
		Log.d(TAG, "clicked!");
		// Declare widgets
		EditText forename = (EditText) findViewById(R.id.forename);
		EditText surname = (EditText) findViewById(R.id.surname);
		TextView pinDisplay = (TextView) findViewById(R.id.pinDisplay);

		// Initiate the Shared Preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean isFirstRun = settings.getBoolean("isFirstRun", true);
		SharedPreferences.Editor editor = settings.edit();
		Log.d(TAG, "Set up preferences");

		// Get text from widgets
		if (isFirstRun) {
			String forenameText = forename.getText().toString();
			String surnameText = surname.getText().toString();
			String name = forenameText + " " + surnameText;
			editor.putString("username", name);
			editor.commit();
			Log.d(TAG, "name generated ok");
			if ((forenameText.length() < 1) || (surnameText.length() < 1)) {
				Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_LONG)
						.show();
			} else {
				Log.d(TAG, "Generating pin");
				// Generate the user pin
				String pin = generateUserPin(forenameText, surnameText);
				// Mark it as no longer first run, setup options will disappear
				editor.putBoolean("isFirstRun", false);
				editor.putString("pin", pin);
				editor.commit();
				pinDisplay.setText(pin);
				getAndStoreLocation(pin, name, isFirstRun);
			}
		}

		else { // It is not the first run of the app
				// Pull the user's pin out from SharedPreferences
			String pin = settings.getString("pin", "");
			String username = settings.getString("username", "");
			pinDisplay.setText(pin);
			Log.d(TAG, "Running!");
			getAndStoreLocation(pin, username, isFirstRun);

		}

	}

	// Method to generate the pin when passed the name and the surname (summary)
	public String generateUserPin(String name, String summary) {
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

	public void getAndStoreLocation(String pin, String name, boolean isFirstRun) {
		//OLD METHOD of getting user's last known location
		// Get the last known location
		/*
		 * Log.d(TAG, "Starting to find location"); LocationManager lm =
		 * (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 * Criteria criteria = new Criteria(); String bestProvider =
		 * lm.getBestProvider(criteria, false); Location location =
		 * lm.getLastKnownLocation(bestProvider); Log.d(TAG, "Location Found");
		 */

		Location location = myLocation;

		// Get last know latitude and longitude as Strings
		String latitude = Double.toString(location.getLatitude());
		Log.d(TAG, latitude);
		String longditude = Double.toString(location.getLongitude());
		Log.d(TAG, longditude);

		if (!(isFirstRun)) {
			// Find the existing Parse object
			ParseQuery query = new ParseQuery("Waypoints");
			query.whereEqualTo("pin", pin);
			query.getFirstInBackground(new GetCallback() {
				public void done(ParseObject object, ParseException e) {
					if (object == null) {
						//If the object could not be found
						Log.d(TAG, "The request failed.");
					} else {
						//Delete the object so it can be overwritten with new data.
						object.deleteInBackground();

					}
				}
			});
		}
		// Upload the data to Parse
		ParseObject waypoint = new ParseObject("Waypoints");
		waypoint.put("pin", pin);
		waypoint.put("Title", name);
		waypoint.put("type", "individual");
		waypoint.put("longitude", longditude);
		waypoint.put("latitude", latitude);
		waypoint.saveInBackground();
	}

	//Get the user's current location.
	//See MakeWaypoint.java for full commentary.
	public void getLocation() {
		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				Log.d(TAG, "Got location!");
				myLocation = location;
			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}

}
