package elbil.raekkevidde.application.event.events;

/**
 * Created by Yoghurt Jr on 24-03-2017.
 */

public class ItemInsertedEvent {
    String item;

    public ItemInsertedEvent(String item){
        this.item = item;
    }

    public String getItem(){
        return item;
    }
}
