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
package elbil.raekkevidde.application.utils;


import elbil.raekkevidde.application.commands.ObdCommand;

/**
 * This class represents a job that BluetoothConnectionService will have to execute and
 * maintain until the job is finished. It is, thereby, the application
 * representation of an ObdCommand instance plus a state that will be
 * interpreted and manipulated by BluetoothConnectionService.
 */
public class ObdCommandJob {

    private Long _id;
    private ObdCommand _command;
    private ObdCommandJobState _state;

    /**
     * Default ctor.
     *
     * @param command the ObCommand to encapsulate.
     */
    public ObdCommandJob(ObdCommand command) {
        _command = command;
        _state = ObdCommandJobState.NEW;
    }



    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public ObdCommand getCommand() {
        return _command;
    }

    /**
     * @return job current state.
     */
    public ObdCommandJobState getState() {
        return _state;
    }

    /**
     * Sets a new job state.
     *
     * @param state the new job state.
     */
    public void setState(ObdCommandJobState state) {
        _state = state;
    }

    /**
     * The state of the command.
     */
    public enum ObdCommandJobState {
        NEW,
        RUNNING,
        FINISHED,
        EXECUTION_ERROR,
        BROKEN_PIPE,
        QUEUE_ERROR,
        NOT_SUPPORTED
    }

}
