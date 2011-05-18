/**
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual
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
package org.jboss.seam.cron.provider.quartz.jobs;

import org.jboss.seam.cron.provider.spi.trigger.AbstractTriggerHelper;
import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.logging.Logger;
import org.jboss.seam.cron.provider.quartz.QuartzScheduleProvider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Base class for firing a Trigger via a Quartz Job via the wrapped #{@link AbstractTriggerHelper}.
 *
 * @author Peter Royle
 */
public abstract class AbstractTimeEventJob
        implements Job {

    /**
     * Implement this to create an instance of the appropriate helper.
     */
    protected abstract AbstractTriggerHelper createTriggerHelper();

    /**
     * Executes the firing of the trigger payload via the delegate #{@link AbstractTriggerHelper}
     * when told to do so by the Quartz scheduler.
     * 
     * @param context
     * @throws JobExecutionException
     */
    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        final BeanManager manager = (BeanManager) context.getJobDetail().getJobDataMap().get(QuartzScheduleProvider.MANAGER_NAME);
        final Annotation qualifier = (Annotation) context.getJobDetail().getJobDataMap().get(QuartzScheduleProvider.QUALIFIER);
        AbstractTriggerHelper delegate = createTriggerHelper();
        delegate.configure(manager, qualifier);
        delegate.fireTrigger();

    }
}
