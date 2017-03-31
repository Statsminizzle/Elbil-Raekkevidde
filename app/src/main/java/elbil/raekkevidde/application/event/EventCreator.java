package elbil.raekkevidde.application.event;

import org.greenrobot.eventbus.EventBus;

import elbil.raekkevidde.application.event.events.ItemInsertedEvent;

/**
 * Created by Yoghurt Jr on 24-03-2017.
 */

public class EventCreator {
    private EventBus bus = EventBus.getDefault();

    public void ItemInsertedEvent(String item){
        ItemInsertedEvent event = new ItemInsertedEvent(item);
        bus.post(event);
    }

}
