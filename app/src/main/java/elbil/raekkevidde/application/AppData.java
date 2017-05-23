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
package elbil.raekkevidde.application;

import android.app.Application;

import java.util.ArrayList;

import elbil.raekkevidde.application.event.EventCreator;

public class AppData extends Application {
    public static EventCreator event = new EventCreator();

    public static ArrayList<String> obdResponseList = new ArrayList<>();
    public static ArrayList<String> obdQueryList = new ArrayList<>();

    public static String currentID = "";
    public int count = 0;

    public static double batteryCharge = 0;
    public static ArrayList<Double> usingWattage = new ArrayList<>();
    public static int drivedKilometers = 0;

    public static ArrayList<Double> range = new ArrayList<>();
    public static double rangeAverage = 0;
    //iON battery 14,5 kWh


}
