package com.petplore.app.serverHandlers;

import android.content.Context;
import android.location.Location;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.petplore.app.UserDetailsObject;

import java.util.Map;

public class LocationSaverOnServer {

    Context context;
    Location currentLocation;
    UserDetailsObject userDetails;
    DatabaseReference userLocationDatabase;
    FirebaseUser currentUser;
    String userUid;
    String UserDatailsPath, UserLocationPath;
    DatabaseReference locationOfUsersGeoRefDatabase;
    GeoFire userLocationGeoFire;


    public LocationSaverOnServer() {
    }

    public LocationSaverOnServer(Context context, Location currentLocation, UserDetailsObject userDetails, String UserDetailsPath, String UserLocationPath) {

        // getting logged in firebase user details
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.userUid = currentUser.getUid();

        this.context = context;
        this.currentLocation = currentLocation;
        this.userDetails = userDetails;

        this.UserDatailsPath = UserDetailsPath;
        this.UserLocationPath = UserLocationPath;

        this.userLocationDatabase = FirebaseDatabase.getInstance().getReference(UserDetailsPath).child(userUid);


        this.locationOfUsersGeoRefDatabase = FirebaseDatabase.getInstance().getReference(UserLocationPath);
        this.userLocationGeoFire = new GeoFire(locationOfUsersGeoRefDatabase);
    }

    public void saveUserDetails(GeoFire.CompletionListener geoFireCompletionListener, OnCompleteListener<Void> databaseRefrenceCompletionListener) {

        GeoLocation userGeoLocation = new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

        FirebaseDatabase.getInstance().getReference("UserDetails").child(userUid).updateChildren((Map<String, Object>) userDetails.getMappedUserDetails()).addOnCompleteListener(databaseRefrenceCompletionListener);
        userLocationGeoFire.setLocation(userUid, userGeoLocation, geoFireCompletionListener);

    }

    public void saveOnlyDetailsWithoutLocation(DatabaseReference.CompletionListener completionListener) {


        userLocationDatabase.updateChildren(userDetails.getMappedUserDetails(), completionListener);
    }


//     TODO new way of saving
//         set a function that add each child
//            too many error listener
//
//            fun addItem(userId: String, shopItemId: String) {
//                firebaseData
//                        .child(USERS)
//                        .child(userId)
//                        .child(CART)
//                        .child(shopItemId)
//                        .setValue(true)
//            }
//
//            fun removeItem(userId: String, shopItemId: String) {
//                firebaseData
//                        .child(USERS)
//                        .child(userId)
//                        .child(CART)
//                        .child(shopItemId)
//                        .setValue(null)
//            }

//    TODO or get last saved detail and combine

}