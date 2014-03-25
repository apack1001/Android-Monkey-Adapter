package com.github.monkey.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.monkey.analyzer.analyze.AnalyzerClient;
import com.github.monkey.analyzer.analyze.Constants;
import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.report.Abnormalities2JSONReport;
import com.github.monkey.analyzer.report.JSONReport2HtmlReport;
import com.github.monkey.analyzer.statistics.AbnormalitiesAnalyzerWrapper;

/**
 * 
 * @author Alex Chen (apack1001@gmail.com)
 *
 */
public class Main {
	public static void main(String[] args) {
		CLIParser cli = new CLIParser();
		boolean success = cli.parse(args);
		if (false == success)
			return;
		
		for (String dir : cli.workspaces) {
			AnalyzerClient client = new AnalyzerClient();
			
			client.analyze(dir,
					cli.monkeyLogFileName, 
					cli.bugreportFileName, 
					cli.tracesFileName,
					cli.logcatFileName, 
					cli.propertiesName, 
					cli.pkgName
				);
			
			final ArrayList<Abnormality> knownAbnormalities = client.getKnownAbnormalities();
			final ArrayList<Abnormality> unknownAbnormalities = client
					.getUnknownAbnormalities();
			
			final int count = client.getAbnormalitiesDirectoriesCount(dir);
			int duration = 0;
			try {
				duration = Integer.parseInt(cli.duration);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			double avg = AbnormalitiesAnalyzerWrapper.getAverage(
					knownAbnormalities, 
					unknownAbnormalities, 
					duration
				);
			HashMap<String, String> info = fillTestInfo(avg, cli.duration);

			String result = Abnormalities2JSONReport.toJSONFormatStringReport(
					knownAbnormalities, 
					unknownAbnormalities, 
					info, 
					duration,
					count);

			JSONReport2HtmlReport.toHTMLReport(result, 
					dir + File.separator + "index.html", "gbk");
			JSONReport2HtmlReport.toHTMLReport(result, 
					dir + File.separator + "index_utf8.html", "utf-8");
		}

	}

	private static HashMap<String, String> fillTestInfo(double average, String duration) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Constants.JSONReport.KEY_DURATION, duration);
		hm.put(Constants.JSONReport.KEY_AVERAGE, "" + average);
		return hm;
	}
}