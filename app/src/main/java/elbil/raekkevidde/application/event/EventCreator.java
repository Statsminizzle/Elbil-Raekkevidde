package elbil.raekkevidde.application.event;

import org.greenrobot.eventbus.EventBus;

import elbil.raekkevidde.application.event.events.BatteryChargeEvent;
import elbil.raekkevidde.application.event.events.DrivedKilometersEvent;
import elbil.raekkevidde.application.event.events.ItemInsertedEvent;
import elbil.raekkevidde.application.event.events.UpdateUIEvent;
import elbil.raekkevidde.application.event.events.UsingWattageEvent;

/**
 * Created by Yoghurt Jr on 24-03-2017.
 */

public class EventCreator {
    private EventBus bus = EventBus.getDefault();

    public void ItemInsertedEvent(String item){
        ItemInsertedEvent event = new ItemInsertedEvent(item);
        bus.post(event);
    }

    public void BatteryChargeEvent() {
        BatteryChargeEvent event = new BatteryChargeEvent();
        bus.post(event);
    }

    public void UsingWattageEvent(double usingWattage) {
        UsingWattageEvent event = new UsingWattageEvent(usingWattage);
        bus.post(event);
    }

    public void DrivedKilometersEvent(int kilometers){
        DrivedKilometersEvent event = new DrivedKilometersEvent(kilometers);
        bus.post(event);
    }

    public void UpdateUIEvent() {
        UpdateUIEvent event = new UpdateUIEvent();
        bus.post(event);
    }
}
