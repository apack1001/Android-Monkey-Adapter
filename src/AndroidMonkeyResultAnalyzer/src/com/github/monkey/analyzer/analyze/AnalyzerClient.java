package com.github.monkey.analyzer.analyze;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.model.AbnormalityType;

/**
 * An facade of this analyzing engine, make it easier to use.<br/>
 * <br/>
 * Responsibilities are as below.<br/>
 * 1.&nbsp; define all the analyzing configuration <br/>
 * 2.&nbsp; using all the configurations to generate pure or unprocessed
 * abnormalities <br/>
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
public class AnalyzerClient {
	/**
	 * handle of AnalyzerInvokr, which represent it's invoked or not
	 */
	boolean invokeHandle = false;
	
	/**
	 * handle of AnalyzerInvoker
	 */
	AnalyzerInvoker invoker = null;
	
	private static ArrayList<Abnormality> EMPTY_ABNORMALITIES = new ArrayList<Abnormality>();


	/**
	 * initialized
	 */
	public void init() {
		invokeHandle = false;
	}

	/**
	 * Uninitialized
	 */
	public void uninit() {
		invokeHandle = false;
	}

	/**
	 * 根据传入的log文件的路径，遍历其中的log文件，筛选出已知类型的异常文件
	 * 
	 * @param invoker
	 *            instance of an {@link AnalyzerInvoker}
	 * 
	 * @return an {@link ArrayList<Abnormality>} which store all the
	 *         known abnormalities
	 */
	public ArrayList<Abnormality> getKnownAbnormalities() {

		if (invokeHandle == false)
			throw new IllegalStateException(
					"you must call this method after analyzer");

		if (invoker != null)
			return invoker.getKnownAbnormalities();

		return EMPTY_ABNORMALITIES;
	}
	/**
	 * Call this method will return all the unknown abnormalities.<br/>
	 * This method must be called after {@link #invokeAnalyze}
	 * 
	 * @return an {@link ArrayList} of {@link Abnormality}
	 */
	public ArrayList<Abnormality> getUnknownAbnormalities() {
		if (invokeHandle == false)
			throw new IllegalStateException(
					"you must call this method after analyzer");

		if (invoker != null)
			return invoker.getUnknownAbnormalities();
		return EMPTY_ABNORMALITIES;
	}

	
	public void analyze(String logFilesPath,
			String monkeyLogFilename, String bugreportLogFilename,
			String tracesFilename, String logcatFilename,
			String propertiesFilename, String pkgName) {
		invoker = invokeAnalyzer(logFilesPath, monkeyLogFilename,
				bugreportLogFilename, tracesFilename, logcatFilename,
				propertiesFilename, pkgName);
	}
	
	private AnalyzerInvoker invokeAnalyzer(String logFilesPath,
			String monkeyLogFilename, String bugreportLogFilename,
			String tracesFilename, String logcatFilename,
			String propertiesFilename, String pkgName) {
		// ANR
		AnalyzerConfiguration anrConfig = getANRAnalyzingConfiguration(
				monkeyLogFilename, bugreportLogFilename, tracesFilename,
				logcatFilename, propertiesFilename, pkgName);
		IAnalyzer anrAnalyzer = new CrashOrAnrAnalyzer(anrConfig);

		// CRASH
		AnalyzerConfiguration crashConfig = getCrashAnalyzingConfiguration(
				monkeyLogFilename, bugreportLogFilename, tracesFilename,
				logcatFilename, propertiesFilename, pkgName);
		IAnalyzer crashAnalyzer = new CrashOrAnrAnalyzer(crashConfig);

		// Native CRASH
		AnalyzerConfiguration nativeCrashConfig = getNativeCrashAnalyzingConfiguration(
				monkeyLogFilename, bugreportLogFilename, tracesFilename,
				logcatFilename, propertiesFilename, pkgName);
		IAnalyzer nativeCrashAnalyzer = new NativeCrashAnalyzer(nativeCrashConfig);
		
		AnalyzerInvoker invoker = new AnalyzerInvoker();

		File[] files = getAbnormalitiesDirectories(logFilesPath);

		for (int i = 0; files != null && i < files.length; i++) {
			invoker.addPath(files[i].getAbsolutePath() + File.separator);
		}
		invoker.registerAnalyzer(anrAnalyzer);
		invoker.registerAnalyzer(crashAnalyzer);
		invoker.registerAnalyzer(nativeCrashAnalyzer);
		invoker.analyze();

		invokeHandle = true;

		return invoker;
	}

	/**
	 * Default implementation of the configuration to analyze CRASH-type
	 * abnormality
	 * 
	 * @param monkeyLogFilename
	 *            android monkey file name pattern
	 * @param bugreportLogFilename
	 *            android bugreport file name pattern
	 * @param tracesFilename
	 *            android trace file name pattern, usually used to analyze ANR
	 * @param logcatFilename
	 *            android logcat file name pattern
	 * @param propertiesFilename
	 *            monkey running results and device info
	 * @param pkgName
	 *            the package name of the target package
	 * @return instance of a configuration
	 */
	private AnalyzerConfiguration getCrashAnalyzingConfiguration(
			String monkeyLogFilename, String bugreportLogFilename,
			String tracesFilename, String logcatFilename,
			String propertiesFilename, String pkgName) {
		AnalyzerConfiguration crashConfig = new AnalyzerConfiguration.Builder()
				.setStartPattern("// CRASH: ").setEndPattern("^// $")
				.setType(AbnormalityType.CRASH)
				.setMonkeyLogFileName(monkeyLogFilename)
				.setBugReportFileName(bugreportLogFilename)
				.setTracesFileName(tracesFilename)
				.setLogcatFileName(logcatFilename)
				.setPropertiesFileName(propertiesFilename)
				.setContentLengthBeforeStartPattern(10)
				.setContentLengthAfterEndPattern(10)
				.setPackageName(pkgName)
				.build();
		return crashConfig;
	}

	/**
	 * Default implementation of the configuration to analyze ANR-type
	 * abnormality
	 * 
	 * @param monkeyLogFilename
	 *            android monkey file name pattern
	 * @param bugreportLogFilename
	 *            android bugreport file name pattern
	 * @param tracesFilename
	 *            android trace file name pattern, usually used to analyze ANR
	 * @param logcatFilename
	 *            android logcat file name pattern
	 * @param propertiesFilename
	 *            monkey running results and device info
	 * @param pkgName
	 *            the package name of the target package
	 * @return instance of a configuration
	 */
	private AnalyzerConfiguration getANRAnalyzingConfiguration(
			String monkeyLogFilename, String bugreportLogFilename,
			String tracesFilename, String logcatFilename,
			String propertiesFilename, String pkgName) {
		AnalyzerConfiguration anrConfig = new AnalyzerConfiguration.Builder()
				.setStartPattern("// NOT RESPONDING")
				.setEndPattern("// NOT RESPONDING")
				.setType(AbnormalityType.ANR)
				.setContentLengthBeforeStartPattern(10)
				.setContentLengthAfterEndPattern(10)
				.setMonkeyLogFileName(monkeyLogFilename)
				.setBugReportFileName(bugreportLogFilename)
				.setTracesFileName(tracesFilename)
				.setLogcatFileName(logcatFilename)
				.setPropertiesFileName(propertiesFilename)
				.setPackageName(pkgName)
				.build();
		return anrConfig;
	}
	
	
	/**
	 * Default implementation of the configuration to analyze ANR-type
	 * abnormality
	 * 
	 * @param monkeyLogFilename
	 *            android monkey file name pattern
	 * @param bugreportLogFilename
	 *            android bugreport file name pattern
	 * @param tracesFilename
	 *            android trace file name pattern, usually used to analyze ANR
	 * @param logcatFilename
	 *            android logcat file name pattern
	 * @param propertiesFilename
	 *            monkey running results and device info
	 * @param pkgName
	 *            the package name of the target package
	 * @return instance of a configuration
	 */
	private AnalyzerConfiguration getNativeCrashAnalyzingConfiguration(
			String monkeyLogFilename, String bugreportLogFilename,
			String tracesFilename, String logcatFilename,
			String propertiesFilename, String pkgName) {
		AnalyzerConfiguration nativeCrashConfig = new AnalyzerConfiguration.Builder()
				.setStartPattern("\\*\\* New native crash detected")
				.setEndPattern("\\*\\* New native crash detected.")
				.setType(AbnormalityType.NATIVE)
				.setMonkeyLogFileName(monkeyLogFilename)
				.setBugReportFileName(bugreportLogFilename)
				.setTracesFileName(tracesFilename)
				.setLogcatFileName(logcatFilename)
				.setPropertiesFileName(propertiesFilename)
				.setPackageName(pkgName)
				.build();
		return nativeCrashConfig;
	}
	

	/**
	 * Get All the abnormality directories count
	 * 
	 * @param path
	 *            abnormalities file path
	 * @return all directories count
	 */
	public int getAbnormalitiesDirectoriesCount(final String path) {
		File[] paths = getAbnormalitiesDirectories(path);
		return paths == null ? 0 : paths.length;
	}

	/**
	 * Get All the abnormality directories
	 * 
	 * @param path
	 *            abnormalities file path
	 * @return all directories
	 */
	private File[] getAbnormalitiesDirectories(final String path) {
		if (path == null || path == "")
			throw new RuntimeException("Illegal File Path");

		File logDir = new File(path);
		if (logDir == null || !logDir.isDirectory() || !logDir.exists())
			return null;
		File[] dirs = logDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory())
					return true;

				return false;
			}
		});
		return dirs;
	}
}
