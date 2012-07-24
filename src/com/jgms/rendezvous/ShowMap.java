package com.jgms.rendezvous;

import android.os.Bundle;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.graphics.*;

import com.google.android.maps.MapActivity;

public class ShowMap extends MapActivity {
	GeoPoint point;

	private class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);
			// Change GeoPoint to pixels
			Point screenPts = new Point();
			mapView.getProjection().toPixels(point, screenPts);
			// Set up the pushpin image as a bitmap that can be used as an
			// overlay.
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.pushpin);

			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
			return true;
		}
	}

	// Necessary override, no route is displayed on the map
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		//Set up the MapView
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		MapController mc = mapView.getController();

		//Get the title, latitude and longitude for 
		String title = getIntent().getStringExtra("Title");
		String latitude = getIntent().getStringExtra("latitude");
		String longitude = getIntent().getStringExtra("longitude");

		//Get the latitude and longitude as coordinates
		double lat = Double.parseDouble(latitude);
		double lon = Double.parseDouble(longitude);

		//Change these into a Geopoing by multiplying them by 1E6
		point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		//Center the map on this point
		mc.animateTo(point);
		//Zoom in
		mc.setZoom(18);

		//Declare a map overlay
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		//Add the map overlay with the pushpin to the MapView
		listOfOverlays.add(mapOverlay);
	}

}
