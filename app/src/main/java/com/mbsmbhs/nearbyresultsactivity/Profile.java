package com.mbsmbhs.nearbyresultsactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petplore.app.MainActivity;
import com.petplore.app.PeopleActivity;
import com.petplore.app.R;
import com.petplore.app.RegisterDetailFormActivity;
import com.petplore.app.UserDetailsObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Profile extends AppCompatActivity {

    private DatabaseReference userDatabase;
    String currentUserUid;
    static UserDetailsObject currentUserDetails;
    SimpleDateFormat sdf;

    LinearLayout profileLinearLayout;
    TextView petNameTextView, emailUserTextView, phoneUserTextView, petDetailTextView;

    ToggleButton showOnRadarToggle;

    RadioGroup rangeRadioGroup;
    Integer[] ranges = {5, 10, 20, 50, 100};

    public static String radarOn = "RADAR_ON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // set simple date format
        sdf = new SimpleDateFormat("MM/dd/yyyy");

        // start setting navigation bar
        BottomNavigationView mainButtomNavigationView = findViewById(R.id.buttomNavigationView);
        mainButtomNavigationView.setSelectedItemId(R.id.profilePage);
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
                        return true;
                }

                return false;
            }
        });
        // end setting navigation bar

        // set firebase database
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // setting items from view
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        petNameTextView = findViewById(R.id.petNameText);
        emailUserTextView = findViewById(R.id.emailUserText);
        phoneUserTextView = findViewById(R.id.phoneUserText);
        petDetailTextView = findViewById(R.id.petDetailText);
        showOnRadarToggle = findViewById(R.id.toggleShowOnRadar);
        rangeRadioGroup = findViewById(R.id.radioRangeGroup);

        // setting register and edit page navigation
        profileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(getApplicationContext(), RegisterDetailFormActivity.class);
                if (showOnRadarToggle.isChecked()) {
                    registerIntent.putExtra(radarOn, true);
                }
                startActivity(registerIntent);

            }
        });

        // get current user details
        userDatabase = FirebaseDatabase.getInstance().getReference("UserDetails");
        userDatabase.child(currentUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUserDetails = dataSnapshot.getValue(UserDetailsObject.class);

                setUserDetails(currentUserDetails);

                Boolean isShowingOnRadar = (currentUserDetails.getShowOnRadar() != null) ? (currentUserDetails.getShowOnRadar()) : (false);
                showOnRadarToggle.setChecked(isShowingOnRadar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Could not get user details", Toast.LENGTH_LONG);

            }
        });

        // set show on radar
        showOnRadarToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (TextUtils.isEmpty(currentUserDetails.getPet_breed()) || TextUtils.isEmpty(currentUserDetails.getPet_gender()) || TextUtils.isEmpty(currentUserDetails.getPet_name())) {
                        compoundButton.setChecked(false);
                        userDatabase.child(currentUserUid).child("showOnRadar").setValue(false);

                        new AlertDialog.Builder(Profile.this)
                                .setMessage("Complete pet name, pet gender, pet breed")
                                .setPositiveButton("Complete profile", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent registerIntent = new Intent(getApplicationContext(), RegisterDetailFormActivity.class);
                                        registerIntent.putExtra(radarOn, true);
                                        startActivity(registerIntent);
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .setCancelable(false)
                                .show();
                    } else {
                        userDatabase.child(currentUserUid).child("showOnRadar").setValue(b);
                    }
                } else {
                    userDatabase.child(currentUserUid).child("showOnRadar").setValue(b);
                }
            }
        });
        // set show on radar


        // set default selected range
        int indexOfSelectedRangesItem = ArrayUtils.toArrayList(ranges).indexOf(MainActivity.radiusOfSearch);
        rangeRadioGroup.check(rangeRadioGroup.getChildAt(indexOfSelectedRangesItem).getId());

        // set range changer
        rangeRadioGroup.setOnCheckedChangeListener(onRadioCheckChangedFunc());

    }

    private RadioGroup.OnCheckedChangeListener onRadioCheckChangedFunc() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int distanceSelected = 20;
                switch (i) {
                    case R.id.radioButton5km:
                        distanceSelected = ranges[0];
                        break;

                    case R.id.radioButton10km:
                        distanceSelected = ranges[1];
                        break;

                    case R.id.radioButton20km:
                        distanceSelected = ranges[2];
                        break;

                    case R.id.radioButton50km:
                        distanceSelected = ranges[3];
                        break;

                    case R.id.radioButton100km:
                        distanceSelected = ranges[4];
                        break;

                    default:
                        distanceSelected = ranges[5];
                        break;
                }
                MainActivity.radiusOfSearch = distanceSelected;
                Log.d("radio group changed", "distance = " + MainActivity.radiusOfSearch);
            }
        };
    }

    private void setUserDetails(UserDetailsObject downloadedUserDetails) {
        petNameTextView.setText(currentUserDetails.getPet_nickname());
        emailUserTextView.setText(currentUserDetails.getAccount_email());
        phoneUserTextView.setText(currentUserDetails.getAccount_phone());

        Calendar petBirthday = Calendar.getInstance();
        try {
            if (currentUserDetails.getPet_birthday() != null) {
                petBirthday.setTime(sdf.parse(currentUserDetails.getPet_birthday()));
                Log.d("pet day set:", String.valueOf(petBirthday.toString()));
            }
        } catch (ParseException ex) {
            Log.d("Exception", ex.getLocalizedMessage());
        }
        String petAge;
        if (petBirthday != Calendar.getInstance()) {
            petAge = calculateAge(petBirthday);
        } else {
            petAge = "";
        }

        String petBreed = currentUserDetails.getPet_breed();
        if (petBreed == null) {
            petBreed = "";
        }

        // setting for petDetailString ", "
        String petDetailsString;
        Log.d("pet detail", "pet breed: " + petBreed + " pet age: " + petAge);
        if (!petBreed.equals("") && !petAge.equals("")) {
            petDetailsString = petBreed + ", " + petAge;
        } else {
            petDetailsString = petBreed + petAge;
        }
        petDetailTextView.setText(petDetailsString);
    }

    private String calculateAge(Calendar petBirthday) {
        Calendar today = Calendar.getInstance();

        int ageYear = today.get(Calendar.YEAR) - petBirthday.get(Calendar.YEAR);
        int ageMonth = today.get(Calendar.MONTH) - petBirthday.get(Calendar.MONTH);
        int ageDay = today.get(Calendar.DATE) - petBirthday.get(Calendar.DATE);

        if (ageYear > 0) {
            if (ageYear == 1) {
                return (ageYear + " year old");
            } else {
                return (ageYear + " years old");
            }
        } else if (ageMonth > 0) {
            if (ageMonth == 1) {
                return (ageMonth + " month old");
            } else {
                return (ageMonth + " months old");
            }
        } else {
            if (ageDay == 1) {
                return (ageDay + " day old");
            }
            return (ageDay + " days old");
        }
    }
}