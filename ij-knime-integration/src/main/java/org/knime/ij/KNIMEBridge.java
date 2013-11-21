package org.knime.ij;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class KNIMEBridge {

	private static IOSGiStarter starter = null;

	@SuppressWarnings("deprecation")
	public static synchronized IOSGiStarter starter()
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, MalformedURLException {

		if (starter == null) {
			File osgiBundle = new File("C:\\knime_2.8.2\\plugins\\org.eclipse.osgi_3.7.2.v20120110-1415.jar");
			File knimeHelperBundle = new File("C:\\Users\\knime-ij-bundle");

			ClassLoader loader = KNIMERecorder.class.getClassLoader();
//			loader = new URLClassLoader(new URL[] { osgiBundle.toURL() }, loader);
//			loader = new URLClassLoader(new URL[] { knimeHelperBundle.toURL() }, loader);
			loader = new URLClassLoader(new URL[] { osgiBundle.toURL(), knimeHelperBundle.toURL() }, loader);

			starter = ((IOSGiStarter) loader.loadClass("OSGiStarter")
					.newInstance());
		}
		return starter;
	}
}
