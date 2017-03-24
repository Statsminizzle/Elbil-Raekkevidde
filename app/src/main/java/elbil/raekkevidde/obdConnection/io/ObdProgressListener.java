package elbil.raekkevidde.obdConnection.io;

public interface ObdProgressListener {

    void stateUpdate(final ObdCommandJob job);

}