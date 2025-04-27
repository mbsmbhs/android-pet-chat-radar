package com.petplore.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mbsmbhs.nearbyresultsactivity.Journey;
import com.mbsmbhs.nearbyresultsactivity.Mag;
import com.mbsmbhs.nearbyresultsactivity.Profile;
import com.petplore.app.LocationHandlers.GpsLocationGetter;
import com.petplore.app.radarItems.PulsingImage;
import com.petplore.app.serverHandlers.LocationSaverOnServer;

public abstract class MainActivity extends AppCompatActivity {
    public static int radiusOfSearch = 20;
    final Location[] lastSavedLocation = new Location[1];
    ImageView centerButton;
    FirebaseAuth firebaseAuth;
    // current gps location getter object
    GpsLocationGetter gpsLocationGetter;
    // location saver object
    LocationSaverOnServer locationSaverOnServer;
    DatabaseReference fireBaseUser;
    ImageView arrowImage;
    PulsingImage rippleBackground;
    // markers place holder
    ConstraintLayout markersHolderConstraintLayout;

    // setting navigation bar
    protected void setNavigationBar() {
        final BottomNavigationView mainButtomNavigationView = findViewById(R.id.buttomNavigationView);
        mainButtomNavigationView.setSelectedItemId(R.id.radarPage);
        mainButtomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.magPage:
                        startActivity(new Intent(getApplicationContext(), Mag.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.journeyPage:
                        startActivity(new Intent(getApplicationContext(), Journey.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.radarPage:
                        return true;

                    case R.id.notificationsPage:
//                        startActivity(new Intent(getApplicationContext(), Notifications.class));
//                        overridePendingTransition(0, 0);
//                        finish();
                        return true;

                    case R.id.profilePage:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }

                return false;
            }
        });
    }

    protected void setRadarNavigationButtonsAndAnimation() {
        final Button placesNavigationRadarButton = findViewById(R.id.placesRadarButton);
        final Button peopleNavigationRadarButton = findViewById(R.id.peopleRadarButton);
        final Button urgentNavigationRadarButton = findViewById(R.id.urgentRadarButton);

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(android.R.id.navigationBarBackground), true);
        fade.excludeTarget(decor.findViewById(android.R.id.statusBarBackground), true);
        fade.excludeTarget(decor.findViewById(R.id.buttomNavigationView), true);
        fade.excludeTarget(decor.findViewById(R.id.linearRadarPageButtons), true);
        fade.excludeTarget(decor.findViewById(R.id.logoTopLeft), true);
        fade.excludeTarget(decor.findViewById(R.id.placesRadarButton), true);
        fade.excludeTarget(decor.findViewById(R.id.peopleRadarButton), true);
        fade.excludeTarget(decor.findViewById(R.id.urgentRadarButton), true);


        getWindow().setEnterTransition(fade);

        View buttonViewToSetAnimation = peopleNavigationRadarButton;
        if (ViewCompat.getTransitionName(urgentNavigationRadarButton) != null) {
            buttonViewToSetAnimation = urgentNavigationRadarButton;
        } else if (ViewCompat.getTransitionName(placesNavigationRadarButton) != null) {
            buttonViewToSetAnimation = placesNavigationRadarButton;
        } else {
            buttonViewToSetAnimation = peopleNavigationRadarButton;
        }

        Log.d("checkPage", "" + String.valueOf(ViewCompat.getTransitionName(buttonViewToSetAnimation) == null));

        final View finalButtonViewToSetAnimation = buttonViewToSetAnimation;
        final Pair[] pairShareItems = new Pair[2];
        pairShareItems[0] = new Pair<View, String>(finalButtonViewToSetAnimation, ViewCompat.getTransitionName(finalButtonViewToSetAnimation));
        pairShareItems[1] = new Pair<View, String>(centerButton, ViewCompat.getTransitionName(centerButton));
        if (finalButtonViewToSetAnimation != peopleNavigationRadarButton) {
            peopleNavigationRadarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PeopleActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pairShareItems);
                    startActivity(intent, options.toBundle());
                }
            });
        }

        if (finalButtonViewToSetAnimation != placesNavigationRadarButton) {
            placesNavigationRadarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PlacesActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pairShareItems);
                    startActivity(intent, options.toBundle());
                }
            });
        }

        if (finalButtonViewToSetAnimation != urgentNavigationRadarButton) {
            urgentNavigationRadarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), UrgentActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pairShareItems);
                    startActivity(intent, options.toBundle());
                }
            });
        }
    }

    protected void saveCurrentUserProcess(String UserLocationPath, String UserDetailsPath) {

        UserDetailsObject currentUserDetails;

        String username = (firebaseAuth.getCurrentUser().getDisplayName() == null) ? ("no name") : (firebaseAuth.getCurrentUser().getDisplayName().trim());

        currentUserDetails = new UserDetailsObject(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, lastSavedLocation[0].getLatitude(), lastSavedLocation[0].getLongitude());

        locationSaverOnServer = new LocationSaverOnServer(this, lastSavedLocation[0], currentUserDetails, UserDetailsPath, UserLocationPath);

        GeoFire.CompletionListener geofireSaveCompleteListener = new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "geolocation not saved :" + error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "geolocation saved!!!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        OnCompleteListener<Void> databaseSaveCompleteListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "user details not saved :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "user details saved!!!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        locationSaverOnServer.saveUserDetails(geofireSaveCompleteListener, databaseSaveCompleteListener);
    }

    protected void gettingCurrentLocation() {

        Runnable afterGpsLockRunnable = new Runnable() {
            @Override
            public void run() {
                lastSavedLocation[0] = gpsLocationGetter.getCurrentLocation();
                Log.d("last location", String.valueOf(lastSavedLocation[0].getLatitude()));
            }
        };

        // running background gps listener
        if (gpsLocationGetter.isGpsAccessible()) {
            gpsLocationGetter.getLocationWithPermission(afterGpsLockRunnable);
        } else {
            //TODO what happen if gps was not accessible
            gpsLocationGetter.getAllLocationAccess();
        }

    }

    protected void directUserToProfilePageIfNotPreRegistered() {
        final String tempCurrentUserUid = firebaseAuth.getCurrentUser().getUid();
        fireBaseUser = FirebaseDatabase.getInstance().getReference();

        final SharedPreferences userDetailsPref = getSharedPreferences("userDetails", MODE_PRIVATE);


        String lastRunUid = userDetailsPref.getString(getResources().getString(R.string.last_run_uid_username), "");
        Log.d("inittt user data", lastRunUid);
        if (lastRunUid.equals("") || !tempCurrentUserUid.equals(lastRunUid)) {

            // set uid on server
            fireBaseUser.child("UserDetails").child(tempCurrentUserUid).child("Uid").setValue(tempCurrentUserUid).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        Log.d("inittt user data", "success");
                        Intent registerIntent = new Intent(getApplicationContext(), RegisterDetailFormActivity.class);
                        startActivity(registerIntent);
                        finish();

                    }
                    Log.d("init user data", "sucess2failed");
                }
            });
        }
    }
}
