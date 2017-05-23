package elbil.raekkevidde.application;

import android.app.Application;

import java.util.ArrayList;

import elbil.raekkevidde.application.event.EventCreator;

/**
 * Created by Yoghurt Jr on 24-03-2017.
 */

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
