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
package elbil.raekkevidde.application.commands;

import com.github.pires.obd.exceptions.*;

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
