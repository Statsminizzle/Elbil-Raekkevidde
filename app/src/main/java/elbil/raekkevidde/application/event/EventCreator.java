/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package elbil.raekkevidde.application.event;

import org.greenrobot.eventbus.EventBus;

import elbil.raekkevidde.application.event.events.BatteryChargeEvent;
import elbil.raekkevidde.application.event.events.DrivedKilometersEvent;
import elbil.raekkevidde.application.event.events.ItemInsertedEvent;
import elbil.raekkevidde.application.event.events.UpdateUIEvent;
import elbil.raekkevidde.application.event.events.UsingWattageEvent;

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
