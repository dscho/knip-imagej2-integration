package org.knime.ij.wfrecorder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activates this OSGi bundle.
 *
 * @author Christian Dietz
 */
public class KNIMEOSGiHelperBundleActivator implements BundleActivator {
    //    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public void start(final BundleContext bc) {
        WorkflowAccessService service = new WorkflowAccessService();
        bc.registerService(WorkflowAccessService.class.getName(), service, null);
    }

    @Override
    public void stop(final BundleContext bc) {
        //        log.info("stopped.");
    }
}
