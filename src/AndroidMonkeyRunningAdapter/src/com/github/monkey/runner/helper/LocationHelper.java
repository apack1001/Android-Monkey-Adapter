package com.github.monkey.runner.helper;

import java.io.File;

/**
 * Helper for fetch the os location of the files (including configure files, log
 * files etc.)
 */
public final class LocationHelper {

	/**
	 * Throw when the location of the file couldn't be found.
	 */
	public static final class FileLocationException extends Exception {

		private static final long serialVersionUID = 1L;

		public FileLocationException(String string) {
			super(string);
		}
	}

	private static String sHomeLocation = null;
	private static String sConfLocation = null;
	private static String sLogsLocation = null;
	private static String sScriptLocation = null;

	/**
	 * Returns the home location.
	 * 
	 * @return an OS specific path, terminated by a separator.
	 * 
	 * @throws FileLocationException
	 */
	public final static String getHomeLocation() throws FileLocationException {
		if (sHomeLocation == null) {
			String home = findValidPath("user.dir", "HOME");
			// if the above failed, we throw an exception.
			if (home == null) {
				throw new FileLocationException("Unable to get home directory.");
			} else {
				sHomeLocation = home;
			}
		}
		return sHomeLocation;
	}

	/**
	 * Returns the folder used to store preference related files.
	 * 
	 * @return an OS specific path, terminated by a separator.
	 * 
	 * @throws FileLocationException
	 */
	public final static String getConfLocation() throws FileLocationException {
		if (sConfLocation == null) {
			sConfLocation = getHomeLocation() + File.separator + "conf";
			checkFolder(sConfLocation);
		}
		return sConfLocation;
	}

	/**
	 * Returns the home folder used to store all log related files.
	 * 
	 * @return an OS specific path, terminated by a separator.
	 * 
	 * @throws FileLocationException
	 */
	public final static String getLogsLocation() throws FileLocationException {
		if (sLogsLocation == null) {
			sLogsLocation = getHomeLocation() + File.separator + "logs";
			checkFolder(sLogsLocation);
		}
		return sLogsLocation;
	}

	/**
	 * Returns the home folder used to store all script related files.
	 * 
	 * @return an OS specific path, terminated by a separator.
	 * 
	 * @throws FileLocationException
	 */
	public final static String getScriptLocation() throws FileLocationException {
		if (sScriptLocation == null) {
			sScriptLocation = getHomeLocation() + File.separator + "script";
			checkFolder(sScriptLocation);
		}
		return sScriptLocation;
	}

	/**
	 * Returns the folder used to store log related files for specific monkey
	 * test item.
	 * 
	 * @param id
	 *            the id of specific monkey test item
	 * 
	 * 
	 * @return an OS specific path, terminated by a separator.
	 * 
	 * @throws FileLocationException
	 */
	public final static String getLogsLocation(String id)
			throws FileLocationException {
		if (id == null) {
			return null;
		}
		String location = getLogsLocation() + File.separator + id;
		checkFolder(location);
		return location;
	}

	public final static String getZipLocation(String id)
			throws FileLocationException {
		return getLogsLocation() + File.separator + id + ".zip";
	}
	
	public final static String getMonkeyLogLocation(String id)
			throws FileLocationException {
		return getLogsLocation(id) + File.separator + "monkey_log.txt";
	}

	public final static String getLogcatLogLocation(String id)
			throws FileLocationException {
		return getLogsLocation(id) + File.separator + "logcat_log.txt";
	}
	
	public final static String getBugreportLocation(String id)
			throws FileLocationException {
		return getLogsLocation(id) + File.separator + "bugreport_log.txt";
	}
	
	public final static String getTracesLocation(String id)
			throws FileLocationException {
		return getLogsLocation(id) + File.separator + "traces_log.txt";
	}

	public final static String getPropertiesLocation(String id)
			throws FileLocationException {
		return getLogsLocation(id) + File.separator + "properties.txt";
	}

	public final static String getLaunchPackageLocation()
			throws FileLocationException {
		String location = getScriptLocation() + File.separator
				+ "getLaunchPackage";
		checkFile(location);
		return location;
	}

	public final static String getLaunchPackageJarLocation()
			throws FileLocationException {
		String location = getScriptLocation() + File.separator
				+ "getLaunchPackage.jar";
		checkFile(location);
		return location;
	}

	private static void checkFolder(String location)
			throws FileLocationException {
		File f = new File(location);
		// make sure the folder exists!
		if (f.exists() == false) {
			try {
				f.mkdir();
			} catch (SecurityException e) {
				FileLocationException e2 = new FileLocationException(
						String.format("Unable to create folder '%1$s'.",
								location));
				e2.initCause(e);
				throw e2;
			}
		} else if (f.isFile()) {
			throw new FileLocationException(String.format(
					"'%1$s' is not a directory!.", location));
		}
	}

	private static void checkFile(String location) throws FileLocationException {
		File f = new File(location);
		if (f.exists() == false) {
			throw new FileLocationException(String.format("'%1$s' not exist.",
					location));
		}
	}

	/**
	 * Checks a list of system properties and/or system environment variables
	 * for validity, and existing director, and returns the first one.
	 * 
	 * @param names
	 * @return the content of the first property/variable.
	 */
	private static String findValidPath(String... names) {
		for (String name : names) {
			String path;
			if (name.indexOf('.') != -1) {
				path = System.getProperty(name);
			} else {
				path = System.getenv(name);
			}

			if (path != null) {
				File f = new File(path);
				if (f.isDirectory()) {
					return path;
				}
			}
		}

		return null;
	}
}
