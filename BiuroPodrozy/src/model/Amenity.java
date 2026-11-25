package model;

import i18n.Messages;

public enum Amenity {
    POOL, WIFI, BEACH, SPA, SAUNA, PARKING, ENTERTAINMENT;

    @Override
    public String toString() {
        return Messages.get("amenity." + name());
    }
}