import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;


public class OSGiStarterHookConfigurator implements HookConfigurator, ClassLoaderDelegateHook {

    // resolve class loader of parent framework (see KNIMEBridge)
    private static ClassLoader loader = OSGiStarterHookConfigurator.class.getClassLoader().getParent();

    @Override
    public void addHooks(final HookRegistry registry) {
        registry.addClassLoaderDelegateHook(this);
    }

    @Override
    public Class<?> preFindClass(final String name, final BundleClassLoader classLoader, final BundleData data)
            throws ClassNotFoundException {
        try {
            return loader.loadClass(name);
        } catch (Throwable t) {
//            System.err.println("Not loading " + name + " here");
//            t.printStackTrace();
            return null;
        }
    }


    @Override
    public Class<?> postFindClass(final String name, final BundleClassLoader classLoader, final BundleData data)
            throws ClassNotFoundException {
        return null;
    }

    @Override
    public URL preFindResource(final String name, final BundleClassLoader classLoader, final BundleData data)
            throws FileNotFoundException {
        return null; // TODO!
    }

    @Override
    public URL postFindResource(final String name, final BundleClassLoader classLoader, final BundleData data)
            throws FileNotFoundException {
        return null;
    }

    @Override
    public Enumeration<URL> preFindResources(final String name, final BundleClassLoader classLoader,
                                             final BundleData data) throws FileNotFoundException {
        return null; // TODO!
    }

    @Override
    public Enumeration<URL> postFindResources(final String name, final BundleClassLoader classLoader,
                                              final BundleData data) throws FileNotFoundException {
        return null;
    }

    @Override
    public String preFindLibrary(final String name, final BundleClassLoader classLoader, final BundleData data)
            throws FileNotFoundException {
        return null;
    }

    @Override
    public String postFindLibrary(final String name, final BundleClassLoader classLoader, final BundleData data) {
        return null;
    }
}
