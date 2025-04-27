package com.petplore.app.serverHandlers.locationDownloaderParts;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.petplore.app.LocationCreatorClass;
import com.petplore.app.R;
import com.petplore.app.UserDetailsObject;
import com.petplore.app.radarItems.MarkerObjects;
import com.petplore.app.radarItems.PulsingImage;

import java.util.ArrayList;

public class CreateListOfViewableMarkersReadyToBeShown2 {
    public final SetMarkersToViewOfMarkersPlacement setMarkersToViewOfMarkersPlacement = new SetMarkersToViewOfMarkersPlacement();

    public CreateListOfViewableMarkersReadyToBeShown2() {
    }

    public void createListOfMarkersObject(Context context, UserDetailsObject userDetail, ArrayList listOfMarkers, Location currentLocation, ConstraintLayout markersHolderConstraintLayout, Float[][] markersPlacement1, PulsingImage rippleBackground, ImageView centerButton) {
        int markerId = 123445;
        markerId = markerId + listOfMarkers.size();

        // TODO star image
        boolean starred = true;
        // TODO marker imageId
        int markerImageId = R.drawable.ic_pin_vector;

        Location tempUserLocation1 = LocationCreatorClass.createLocation(userDetail.getLocationLatitude(), userDetail.getLocationLongitude());
        int distance = (int) currentLocation.distanceTo(tempUserLocation1);
        int kilometerDistance = distance / 1000;

        MarkerObjects marker = new MarkerObjects(context, markerId, markersHolderConstraintLayout, userDetail, kilometerDistance + "kilometers away", starred, markerImageId, 0, 0);
        marker.mainContainerLayout.setVisibility(View.GONE);
        listOfMarkers.add(marker);
        setMarkersToViewOfMarkersPlacement.setPagedMarkersToView(marker, listOfMarkers, markersPlacement1, rippleBackground, centerButton);

    }
}