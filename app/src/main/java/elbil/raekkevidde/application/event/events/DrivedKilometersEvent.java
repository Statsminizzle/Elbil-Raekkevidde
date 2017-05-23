package elbil.raekkevidde.application.event.events;

/**
 * Created by Yoghurt Jr on 21-05-2017.
 */

public class DrivedKilometersEvent {

    private int kilometers;

    public DrivedKilometersEvent(int kilometers) {
        this.kilometers = kilometers;
    }

    public int getKilometers() {
        return kilometers;
    }
}
