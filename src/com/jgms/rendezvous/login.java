package com.jgms.rendezvous;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

//import com.facebook.android.*;
//import com.facebook.android.Facebook.*;

public class login extends Activity {
	/** Called when the activity is first created. */
	//-----------------------------------------------------------------------
	//FACEBOOK AUTHENTICATION IS INCLUDED IN THIS CLASS. UN-COMMENT TO ENABLE
	//-----------------------------------------------------------------------

	// Create Facebook stuff for login
	// Facebook facebook = new Facebook("345800862159943");
	// private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/*
		 * Login to Facebook and stuff mPrefs = getPreferences(MODE_PRIVATE);
		 * String access_token = mPrefs.getString("access_token", null); long
		 * expires = mPrefs.getLong("access_expires", 0); if (access_token !=
		 * null) { facebook.setAccessToken(access_token); } if (expires != 0) {
		 * facebook.setAccessExpires(expires); }
		 * 
		 * if (!facebook.isSessionValid()) {
		 * 
		 * facebook.authorize(this, new String[] { "email", "publish_checkins"
		 * }, new DialogListener() {
		 * 
		 * @Override public void onComplete(Bundle values) {
		 * SharedPreferences.Editor editor = mPrefs.edit();
		 * editor.putString("access_token", facebook.getAccessToken());
		 * editor.putLong("access_expires", facebook.getAccessExpires());
		 * editor.commit(); }
		 * 
		 * @Override public void onFacebookError(FacebookError error) { }
		 * 
		 * @Override public void onError(DialogError e) { }
		 * 
		 * @Override public void onCancel() { } });
		 * 
		 * }
		 */

	}

	/*
	 * Authorize with Facebook and log facebook.authorizeCallback(requestCode,
	 * resultCode, data); Log.d("Rendezvous", "Facebook Login Completed");
	 */

	//Create the options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	//Return which item was clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

	//Create the menu programmatically
	private void CreateMenu(Menu menu) {
		//Add an "About Rendezvous item"
		MenuItem mnu1 = menu.add(0, 0, 0, getResources().getString(R.string.about));
		mnu1.setIcon(R.drawable.about);
		//If running on a compatible device, show the About option in the Action Bar
		if (Build.VERSION.SDK_INT >= 14) {
			mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
	}

	//Event handling for which item was selected in the menu
	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			//If "About Rendezvous" was selected, open the About Activity
			startActivity(new Intent("com.jgms.rendezvous.About"));
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Extend access token as instructed on Facebook dev site.
		// facebook.extendAccessTokenIfNeeded(this, null);
	}

	// Switch to the make waypoint activity when the button is pressed
	public void makeWaypointButton(View view) {
		startActivity(new Intent("com.jgms.rendezvous.MakeWaypoint"));
	}

	// Switch to the find waypoint activity when the button is pressed
	public void findWaypointButton(View view) {
		startActivity(new Intent("com.jgms.rendezvous.FindExisting"));
	}

	// Switch to the share location activity when the button is pressed
	public void shareLocationButton(View view) {
		startActivity(new Intent("com.jgms.rendezvous.ShareLocation"));
	}

	// Switch to the find friend activity when the button is pressed
	public void findFriendButton(View view) {
		startActivity(new Intent("com.jgms.rendezvous.FindFriend"));
	}

}