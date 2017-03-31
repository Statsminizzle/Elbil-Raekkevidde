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
}
