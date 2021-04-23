package com.edward_costache.stay_fitrpg;

/**
 * A class for managing a recorded progress day in the application. A progress day is simply the day
 * for which an exercise was completed and its progress documented.
 *
 * Created by Edward Costache
 */
public class ProgressDay {
    private int pushups, situps, squats;
    private double distance;

    /**
     * The default constructed for a ProgressDay, for simplicity in the Database, values are set to 0 and not null
     * to avoid nullPointerExceptions.
     */
    ProgressDay()
    {
        setPushups(0);
        setSitups(0);
        setSquats(0);
        setDistance(0.0);
    }

    public int getPushups() {
        return pushups;
    }

    public void setPushups(int pushups) {
        this.pushups = pushups;
    }

    public int getSitups() {
        return situps;
    }

    public void setSitups(int situps) {
        this.situps = situps;
    }

    public int getSquats() {
        return squats;
    }

    public void setSquats(int squats) {
        this.squats = squats;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
