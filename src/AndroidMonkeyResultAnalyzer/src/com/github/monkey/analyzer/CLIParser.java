package com.github.monkey.analyzer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * 
 * @author Alex Chen (apack1001@gmail.com)
 *
 */
public class CLIParser {
	public String[] workspaces;
	public String monkeyLogFileName;
	public String bugreportFileName;
	public String tracesFileName;
	public String logcatFileName;
	public String propertiesName;
	public String pkgName;
	public String duration;
	
	public boolean parse(String [] args) {
		Options options = new Options();
		
		@SuppressWarnings("static-access")
		Option workspaces = OptionBuilder.withArgName("w")
								.withLongOpt("workspaces")
								.hasArgs()
								.withDescription("Workspace of monkey running directoy.")
								.isRequired()
								.create("w");
		options.addOption(workspaces);
		options.addOption("d", "duration", true, "Expected uration of single monkey job(8 hours or 4.5 hours).");
		options.addOption("m", "monkey-log-file-name", true, "File name of monkey log.");
		options.addOption("l", "logcat-log-file-name", true, "File name of logcat log.");
		options.addOption("t", "traces-log-file-name", true, "File name of traces log.");
		options.addOption("b", "bugreport-log-file-name", true, "File name of bugreport log.");
		options.addOption("p", "properties-file-name", true, "File name of each monkey running summary.");
		options.addOption("n", "package-name", true, "Package name of an Android Application.");
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
		if (cmd.hasOption("w")) {
			this.workspaces = cmd.getOptionValues("w");
		}
		if (cmd.hasOption("d")) {
			this.duration = cmd.getOptionValue("d");
		}
		if (cmd.hasOption("m")) {
			this.monkeyLogFileName = cmd.getOptionValue("m");
		}
		if (cmd.hasOption("l")) {
			this.logcatFileName = cmd.getOptionValue("l");
		}
		if (cmd.hasOption("t")) {
			this.tracesFileName = cmd.getOptionValue("t");
		}
		if (cmd.hasOption("p")) {
			this.propertiesName = cmd.getOptionValue("p");
		}
		if (cmd.hasOption("n")) {
			this.pkgName = cmd.getOptionValue("n");
		}
		return true;	
	}
}
