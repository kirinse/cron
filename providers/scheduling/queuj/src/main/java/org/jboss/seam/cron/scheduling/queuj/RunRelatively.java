/**
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.cron.scheduling.queuj;

import com.workplacesystems.queuj.Occurrence;
import com.workplacesystems.queuj.Schedule;
import java.text.ParseException;
import org.jboss.seam.cron.spi.scheduling.trigger.IntervalTriggerDetail;
import org.jboss.seam.cron.spi.scheduling.trigger.ScheduledTriggerDetail;

/**
 *
 * @author Dave Oxley
 */
public class RunRelatively extends Occurrence {

    // Increase the number when an incompatible change is made
    private static final long serialVersionUID = RunRelatively.class.getName().hashCode() + 1;

    private Schedule schedule;

    RunRelatively(ScheduledTriggerDetail schedTriggerDetails) throws ParseException {
        schedule = new CronSchedule(schedTriggerDetails);
    }

    RunRelatively(IntervalTriggerDetail intervalTriggerDetails) {
        schedule = new RelativeSchedule(intervalTriggerDetails);
    }

    @Override
    public Schedule getSchedule(int i) {
        return schedule;
    }

    @Override
    public Schedule[] getSchedules() {
        return new Schedule[] {schedule}; 
    }

    private final static String new_line = System.getProperty("line.separator");

    @Override
    protected String getSelfString() {
        return new_line +
            INDENT + "  {" + new_line +
            INDENT + "    " + schedule.toString() + new_line +
            INDENT + "  }" + new_line;
    }
}
