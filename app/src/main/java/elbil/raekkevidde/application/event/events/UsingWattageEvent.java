package elbil.raekkevidde.application.event.events;

/**
 * Created by Yoghurt Jr on 21-05-2017.
 */

public class UsingWattageEvent {

    private double usingWattage;

    public UsingWattageEvent(double usingWattage) {
        this.usingWattage = usingWattage;
    }

    public double getUsingWattage() {
        return usingWattage;
    }
}
