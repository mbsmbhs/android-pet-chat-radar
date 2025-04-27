package com.petplore.app.serverHandlers.locationDownloaderParts;

import android.content.Context;
import android.location.Location;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petplore.app.UserDetailsObject;
import com.petplore.app.radarItems.PulsingImage;

import java.util.ArrayList;

public class GetEachMarkrIdsDetail3 {
    public final CreateListOfViewableMarkersReadyToBeShown2 createListOfViewableMarkersReadyToBeShown2 = new CreateListOfViewableMarkersReadyToBeShown2();

    public GetEachMarkrIdsDetail3() {
    }

    public void startSearchUserArrays(final Context context, final ArrayList<String> usersIdsAround, String UserDatasPath, final ArrayList usersAroundWithDetails, final ArrayList listOfMarkers, final Location currentLocation, final ConstraintLayout markersHolderConstraintLayout, final Float[][] markersPlacement1, final PulsingImage rippleBackground, final ImageView centerButton) {

        for (String key : usersIdsAround) {
            if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(key)) {
                DatabaseReference addedUserDatas = FirebaseDatabase.getInstance().getReference(UserDatasPath).child(key);
                addedUserDatas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // TODO filters can be done here if not remove key
                        UserDetailsObject singleUserDetails = dataSnapshot.getValue(UserDetailsObject.class);

                        // TODO Filters Will Be Applied Here If Not Do Not Add
                        if (singleUserDetails.getShowOnRadar() != null && singleUserDetails.getShowOnRadar()) {
                            usersAroundWithDetails.add(singleUserDetails);

                            createListOfViewableMarkersReadyToBeShown2.createListOfMarkersObject(context, singleUserDetails, listOfMarkers, currentLocation, markersHolderConstraintLayout, markersPlacement1, rippleBackground, centerButton);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        // TODO handle this error
                        Toast.makeText(context, "error getting from server " + databaseError, Toast.LENGTH_SHORT);

                    }
                });
            }
        }
    }
}