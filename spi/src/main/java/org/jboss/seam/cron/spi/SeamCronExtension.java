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
package org.jboss.seam.cron.spi;

import org.jboss.seam.cron.spi.scheduling.CronSchedulingInstaller;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import org.jboss.logging.Logger;
import org.jboss.seam.cron.impl.scheduling.exception.SchedulerConfigurationException;
import org.jboss.seam.cron.spi.scheduling.CronSchedulingProvider;
import org.jboss.seam.cron.util.CdiUtils;

/**
 * The CDI Extention implementation which bootstraps Seam Cron. Not useful to service providers.
 * 
 * @author Peter Royle
 */
@ApplicationScoped
public class SeamCronExtension implements Extension {

    private final Set<ObserverMethod> allObservers = new HashSet<ObserverMethod>();
    private final Logger log = Logger.getLogger(SeamCronExtension.class);

    public void registerCronEventObserver(@Observes ProcessObserverMethod pom) {
        allObservers.add(pom.getObserverMethod());
    }

    public void initProviders(@Observes AfterDeploymentValidation afterValid, final BeanManager manager,
            final CronSchedulingInstaller cronSchedExt) {
        // init all service providers
        log.debug("Initialising service providers");
        final Set<CronProviderLifecycle> providerLifecycles = getProviderLifecycles(manager);
        for (CronProviderLifecycle providerLifecycle : providerLifecycles) {
            log.info("Initialising service provider: " + providerLifecycle.toString());
            providerLifecycle.initProvider();
        }
        // process scheduling observers if scheduling provider exists
        final CronSchedulingProvider schedulingProvider = CdiUtils.getInstanceByType(manager, CronSchedulingProvider.class);
        if (schedulingProvider != null) {
            cronSchedExt.initProviderScheduling(manager, schedulingProvider, allObservers);
        }
    }

    public void stopProviders(@Observes BeforeShutdown event, final BeanManager manager,
            final CronSchedulingInstaller cronSchedExt) {

        final Set<CronProviderLifecycle> providerLifecycles = getProviderLifecycles(manager);
        for (CronProviderLifecycle providerLifecycle : providerLifecycles) {
            providerLifecycle.destroyProvider();
        }
    }
    
    /**
     * 
     * @param manager
     * @param cronSchedExt
     * @param cronAsyncExt
     * @return Not null
     * @throws SchedulerConfigurationException 
     */
    private Set<CronProviderLifecycle> getProviderLifecycles(final BeanManager manager) throws SchedulerConfigurationException {
        final Set<CronProviderLifecycle> providerLifecycles = CdiUtils.getInstancesByType(manager, CronProviderLifecycle.class);
        return providerLifecycles;
    }

}
