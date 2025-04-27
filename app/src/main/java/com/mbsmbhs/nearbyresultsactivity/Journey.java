package com.mbsmbhs.nearbyresultsactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.petplore.app.PeopleActivity;
import com.petplore.app.R;

public class Journey extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        // start setting navigation bar
        BottomNavigationView mainButtomNavigationView = findViewById(R.id.buttomNavigationView);
        mainButtomNavigationView.setSelectedItemId(R.id.journeyPage);
        mainButtomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.magPage:
                        startActivity(new Intent(getApplicationContext(), Mag.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.journeyPage:
                        return true;

                    case R.id.radarPage:
                        startActivity(new Intent(getApplicationContext(), PeopleActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
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
        // end setting navigation bar
    }
}