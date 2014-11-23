/*
 * Copyright 2014 Max.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.maxschuster.vaadin.signaturefield.client.signaturepad;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 *
 * @author Max
 */
public class Debouncer {
    
    private final RepeatingCommand wrapperCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            RepeatingCommand command = getCommand();
            if (command != null) {
                command.execute();
                debouncing = false;
            }
            return false;
        }
    };
    
    private int delay;
    
    private RepeatingCommand command;
    
    private boolean debouncing;

    public Debouncer(RepeatingCommand command, int delay) {
        this.command = command;
        this.delay = delay;
    }
    
    public void execute() {
        Scheduler scheduler = Scheduler.get();
        if (!debouncing) {
            debouncing = true;
            scheduler.scheduleFixedDelay(wrapperCommand, delay);
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public RepeatingCommand getCommand() {
        return command;
    }

    public void setCommand(RepeatingCommand command) {
        this.command = command;
    }
    
}
