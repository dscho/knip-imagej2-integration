import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;


/**
 * Adds our custom class loader delegator hook.
 * <p>
 * In order to be able to interact with callers that have been started outside of OSGi's control, we need to re-use that
 * class loader for the classes whose instances we want to pass back and forth.
 * </p>
 * <p>
 * The idea to solve this conundrum is that the outside caller constructs a custom class loader to load this project
 * (and the OSGi base .jar), with its own class loader as parent class loader. Then, this {@link HookConfigurator}
 * implementation installs itself as a {@link ClassLoaderDelegateHook} that tries first of all to load the classes in
 * the original class loader and prevents OSGi from resolving those classes differently.
 * </p>
 *
 * @author Johannes Schindelin
 */
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
