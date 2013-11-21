import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.equinox.app.IApplicationContext;
import org.knime.ij.IOSGiStarter;
import org.knime.ij.WorkflowAccess;
import org.knime.ij.wfrecorder.WorkflowAccessService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * Created on 20.11.2013 by Christian Dietz
 */

/**
 * Starts OSGi for use in projects not linking directly to OSGi.
 * <p>
 * The idea is to load this class in a custom class loader, exposing the IOSGiStarter interface.
 * </p>
 * <p>
 * This implementation then starts up OSGi such that the classes found in the calling class loader (i.e. the custom
 * class loader's parent class loader) are *not* handled by OSGi but instead by the existing class loader.
 * </p>
 * <p>
 * That way, we can interact directly with bundles implementing interfaces defined in the calling class loader and
 * passing objects back and forth whose classes are defined in the same class loader, too.
 * </p>
 *
 * @author Christian Dietz & Johannes Schindelin
 */
public class OSGiStarter implements IOSGiStarter {

    private WorkflowAccess access = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAccess workflowAccess() {
        if (access == null) {
            try {
                Framework startFramework = startFramework();
                BundleContext context = startFramework.getBundleContext();
                ServiceReference<?> serviceReference =
                        context.getServiceReference(WorkflowAccessService.class.getName());
                access = (WorkflowAccess)context.getService(serviceReference);
                return access;

            } catch (BundleException e) {
                e.printStackTrace();
            }

            return null;
        }

        return access;
    }

    private Framework startFramework() throws BundleException {
        // Here, we assume that the OSGi Framework class was loaded from the org.eclipse.osgi .jar file contained in KNIME_HOME/plugins/
        String frameworkClassLocation = Framework.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File knimeHome = new File(frameworkClassLocation).getParentFile().getParentFile();

        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class, getClass().getClassLoader()).iterator().next();
        Map<String, String> config = new HashMap<String, String>();
        config.put(IApplicationContext.APPLICATION_ARGS, "-consoleLog");
        config.put("osgi.console", "");
        config.put("eclipse.consoleLog", "true");
        config.put("osgi.compatibility.bootdelegation", "true");
        config.put("osgi.hook.configurators.include", OSGiStarterHookConfigurator.class.getName());
        config.put("osgi.configuration.area", new File(knimeHome, "/configuration").toString());

        Framework framework = frameworkFactory.newFramework(config);
        framework.start();

        installAllPlugins(knimeHome, framework.getBundleContext());
        installThisBundle(framework);

        return framework;
    }

    /*
    public static void main(final String[] args) {
        final String url = OSGiStarter.class.getProtectionDomain().getCodeSource()
                .getLocation().toString();
        System.err.println(url);
    }
    */

    /**
     * @param framework
     * @throws BundleException
     */
    private void installThisBundle(final Framework framework) throws BundleException {
        // install this bundle
        final URL location = getClass().getProtectionDomain().getCodeSource().getLocation();

        final BundleContext context = framework.getBundleContext();
        Bundle bundle = context.installBundle(location.toString());

        //TODO only call this in case you use the developer version of KNIME (check for dedicated bundle to be installed)
        bundle.update();
        bundle.start();
    }

    protected void installAllPlugins(final File knimeHome, final BundleContext context) {
        new File(knimeHome, "/plugins").listFiles(new FileFilter() {
            //
            @Override
            public boolean accept(final File file) {
                String name = file.getName();
                if (file.isDirectory()) {
                    if ("configuration".equals(name)) {
                        return false;
                    }
                } else if (!name.endsWith(".jar")) {
                    return false;
                }
                if (name.startsWith("org.eclipse.osgi_")) {
                    return false;
                }
                String url = file.toURI().toString();
                try {
                    context.installBundle(url);
                } catch (BundleException e) {
                    e.printStackTrace();
                }
                return true;
            }

        });
    }
}
