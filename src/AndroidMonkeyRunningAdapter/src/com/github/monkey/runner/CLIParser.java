package com.github.monkey.runner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 
 * @author Alex Chen (apack1001@gmail.com)
 *
 */
public class CLIParser {

	public String [] deivcesId;
	public String singleDuration;
	public String seriesDuration;
	public String pkgName;
	public String user;
	public String pkgPath;
	public String pkgVersion;
	public String unlockCmd;
	
	public boolean parse(String [] args) {
		Options options = new Options();
		
		options.addOption("d", "device-id", true, "the id list of the devices which is need to run monkey test");
		options.addOption("r", "user-name", true, "user name of this job owner");
		options.addOption("v", "pkg-version", true, "version of this application");
		options.addOption("n", "pkg-name", true, "package name of this appliacation");
		options.addOption("p", "pkg-path", true, "point to an Android application path in the storage");
		options.addOption("t", "series-duration", true, "expected total monkey jobs duration (hour)");
		options.addOption("s", "single-duration", true, "expected one monkey job duration (hour)");
		options.addOption("u", "unlock-cmd-path", true, "point to an unlock script path which must be standalone executable");
		options.addOption("h", "help", false, "Output help information!");
		
		String formatstr = "java -jar jarfile [-options/ --options]...\n";
		String headerstr = "options are as below:";

		CommandLineParser parser = new GnuParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		
		try {
			cmd = parser.parse( options, args );
		} catch (ParseException e) {
			formatter.printHelp(formatstr, headerstr, options, "");
			return false;
		}	
		
		if (cmd == null || cmd.hasOption("h") || cmd.getOptions().length == 0) {
			formatter.printHelp(formatstr, options);
			return false;
		}
		if (cmd.hasOption("d")) {
			this.deivcesId = cmd.getOptionValues("d");
		}
		if (cmd.hasOption("r")) {
			this.user = cmd.getOptionValue("r");
		}
		if (cmd.hasOption("u")) {
			this.unlockCmd = cmd.getOptionValue("u");
		}
		if (cmd.hasOption("v")) {
			this.pkgVersion = cmd.getOptionValue("v");
		}
		if (cmd.hasOption("n")) {
			this.pkgName = cmd.getOptionValue("n");
		}
		if (cmd.hasOption("p")) {
			this.pkgPath = cmd.getOptionValue("p");
		}
		if (cmd.hasOption("t")) {
			this.seriesDuration = cmd.getOptionValue("t");
		}
		if (cmd.hasOption("s")) {
			this.singleDuration = cmd.getOptionValue("s");
		}
		if (cmd.hasOption("u")) {
			this.unlockCmd = cmd.getOptionValue("u");
		}
		return true;	
	}
}
