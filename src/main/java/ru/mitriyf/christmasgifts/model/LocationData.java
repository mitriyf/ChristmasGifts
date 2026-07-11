package ru.mitriyf.christmasgifts.model;

import lombok.Getter;

@Getter
public class LocationData {
    private final double addX, addY, addZ;

    public LocationData(double addX, double addY, double addZ) {
        this.addX = addX;
        this.addY = addY;
        this.addZ = addZ;
    }
}
