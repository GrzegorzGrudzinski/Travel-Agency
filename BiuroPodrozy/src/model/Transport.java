package model;

import i18n.*;

public enum Transport {
    OWN, BUS, TRAIN, PLANE;
    
    @Override 
    public String toString() {
        return Messages.get("transport." + name());
    }
}
