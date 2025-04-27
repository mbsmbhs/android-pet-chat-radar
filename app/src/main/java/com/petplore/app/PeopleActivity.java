package com.petplore.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.petplore.app.LocationHandlers.GpsLocationGetter;
import com.petplore.app.radarItems.MarkerObjects;
import com.petplore.app.radarItems.PulsingImage;
import com.petplore.app.serverHandlers.LocationArrayDownloader;

import java.util.ArrayList;

public class PeopleActivity extends MainActivity {

    // location downloader object
    LocationArrayDownloader locationArrayDownloader;

    // marker placement for view
    Float[][] markersPlacement1 = {{0.81f, 0.389f}, {0.73f, 0.672f}, {0.31f, 0.245f}, {0.27f, 0.806f}};

    ArrayList<String> arrayOfUsersAroundIDs;
    ArrayList<UserDetailsObject> arrayOfUsersAroundWithDetails;
    ArrayList<MarkerObjects> listOfMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNavigationBar();

        centerButton = findViewById(R.id.centerButton);

        setRadarNavigationButtonsAndAnimation();

        firebaseAuth = FirebaseAuth.getInstance();
        markersHolderConstraintLayout = findViewById(R.id.staticMarkersPlaceHolder1);

        // setting pulse animation
        rippleBackground = new PulsingImage(this, R.id.rippleCenter, R.drawable.ic_people_radar_pulse, 3, 83, 83, 7f, 2100);
        // get arrow image
        arrowImage = findViewById(R.id.arrowImage);
        // set gps location getter
        gpsLocationGetter = new GpsLocationGetter(this);
        // set Item downloader object;
        locationArrayDownloader = new LocationArrayDownloader(this, lastSavedLocation[0], radiusOfSearch, centerButton, rippleBackground, markersHolderConstraintLayout, listOfMarkers, arrayOfUsersAroundIDs, arrayOfUsersAroundWithDetails);

        // set only one time run code and pulse effect
        final int[] howManyTimesClicked = {0};
        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastSavedLocation[0] != null) {
                    if (howManyTimesClicked[0] == 0) {
                        rippleBackground.startAnimation();
                        howManyTimesClicked[0]++;
                    }
                    rippleBackground.enablePulse();
                    gpsLocationGetter.getAllLocationAccess();
                    saveCurrentUserProcess("locationOfUsers", "UserDetails");
                    searchButtonClickedForSearch();
                } else {
                    //TODO what happens if search button clicked while searching
                    Toast.makeText(PeopleActivity.this, "what to do when gps is still loading??!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO delete or move this later
        Button tempSignOutButton = findViewById(R.id.tempSignOutButton);
        tempSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                firebaseAuth.signOut();
                Intent intentRegisterLogin = new Intent(getApplicationContext(), RegisterLoginActivity.class);
                startActivity(intentRegisterLogin);
                finish();
            }
        });
    }

    void searchButtonClickedForSearch() {
        arrowImage.setVisibility(View.INVISIBLE);
        centerButton.setImageResource(R.drawable.ic_people_radar_pulse);
        locationArrayDownloader.setCurrentLocation(lastSavedLocation[0]);
        locationArrayDownloader.downloadOrShowNextPage(PeopleActivity.this, lastSavedLocation[0], radiusOfSearch, "locationOfUsers", "UserDetails", arrayOfUsersAroundIDs, arrayOfUsersAroundWithDetails, listOfMarkers, markersHolderConstraintLayout, markersPlacement1, rippleBackground, centerButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gettingCurrentLocation();

        directUserToProfilePageIfNotPreRegistered();
    }

}