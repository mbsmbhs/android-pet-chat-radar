package com.petplore.app;

public class userLocationDetails {
    UserDetailsObject userDetailsObjectItem;

    public userLocationDetails() {
    }

    public userLocationDetails(UserDetailsObject userDetailsObjectItem) {
        this.userDetailsObjectItem = userDetailsObjectItem;
    }

    public UserDetailsObject getUserDetailsObjectItem() {
        return userDetailsObjectItem;
    }
}