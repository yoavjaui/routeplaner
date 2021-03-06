package de.templum.routeplaner.model;

import android.location.Location;

/**
 * This class represents an list route.
 * It contains the location and the address.
 */
public class RoutePoint {
    private Location mLocation;
    private String mAddress;

    public RoutePoint(Location location, String address) {
        mLocation = location;
        mAddress = address;
    }

    public RoutePoint(RoutePoint routePoint) {
        mLocation = new Location(routePoint.getLocation());
        mAddress = routePoint.toString();
    }

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public String toString() {
        return mAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoutePoint) {
            return mAddress.equals(((RoutePoint) obj).mAddress);
        } else {
            return super.equals(obj);
        }
    }
}
