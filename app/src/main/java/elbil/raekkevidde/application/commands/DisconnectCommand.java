package elbil.raekkevidde.application.commands;

import elbil.raekkevidde.obdJavaApi.commands.protocol.ObdProtocolCommand;
import elbil.raekkevidde.obdJavaApi.exceptions.BusInitException;
import elbil.raekkevidde.obdJavaApi.exceptions.MisunderstoodCommandException;
import elbil.raekkevidde.obdJavaApi.exceptions.NoDataException;
import elbil.raekkevidde.obdJavaApi.exceptions.ResponseException;
import elbil.raekkevidde.obdJavaApi.exceptions.StoppedException;
import elbil.raekkevidde.obdJavaApi.exceptions.UnableToConnectException;
import elbil.raekkevidde.obdJavaApi.exceptions.UnknownErrorException;
import elbil.raekkevidde.obdJavaApi.exceptions.UnsupportedCommandException;

/**
 * Created by Yoghurt Jr on 19-05-2017.
 */

public class DisconnectCommand extends ObdProtocolCommand{
        private final Class[] ERROR_CLASSES = {
                UnableToConnectException.class,
                BusInitException.class,
                MisunderstoodCommandException.class,
                NoDataException.class,
                StoppedException.class,
                UnknownErrorException.class,
                UnsupportedCommandException.class
        };

    public DisconnectCommand() {
        super("AT PC");
    }

    @Override
    public String getFormattedResult() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

        void checkForErrors() {
            for (Class<? extends ResponseException> errorClass : ERROR_CLASSES) {
                ResponseException messageError;

                try {
                    messageError = errorClass.newInstance();
                    messageError.setCommand(this.cmd);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if (messageError.isError(rawData)) {
                    if (false)
                        throw messageError;
                }
            }
        }
}
