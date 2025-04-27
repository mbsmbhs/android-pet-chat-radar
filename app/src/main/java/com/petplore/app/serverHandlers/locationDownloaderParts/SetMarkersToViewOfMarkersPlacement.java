package com.petplore.app.serverHandlers.locationDownloaderParts;

import android.view.View;
import android.widget.ImageView;

import com.petplore.app.R;
import com.petplore.app.radarItems.MarkerObjects;
import com.petplore.app.radarItems.PulsingImage;

import java.util.ArrayList;

public class SetMarkersToViewOfMarkersPlacement {
    public SetMarkersToViewOfMarkersPlacement() {
    }

    public void setPagedMarkersToView(MarkerObjects markerObject, ArrayList listOfMarkers, Float[][] markersPlacement, PulsingImage rippleBackground, ImageView centerButton) {

        int index = listOfMarkers.indexOf(markerObject);
        if (index < markersPlacement.length) {
            markerObject.setHorizontalVerticalBias(markersPlacement[index][0], markersPlacement[index][1]);
            markerObject.mainContainerLayout.setVisibility(View.VISIBLE);
        }
        rippleBackground.disablePulse(R.drawable.ic_disabled_pulse_background);
        centerButton.setImageResource(R.drawable.ic_disabled_pulse_background);

    }
}