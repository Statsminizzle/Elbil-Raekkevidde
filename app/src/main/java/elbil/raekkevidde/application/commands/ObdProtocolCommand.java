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

public abstract class ObdProtocolCommand extends ObdCommand {

    public ObdProtocolCommand(String command) {
        super(command);
    }

    public ObdProtocolCommand(ObdProtocolCommand other) {
        this(other.cmd);
    }

    protected void performCalculations() {
    }

    protected void fillBuffer() {
    }

    @Override
    public String getCalculatedResult() {
        return String.valueOf(getResult());
    }
}

