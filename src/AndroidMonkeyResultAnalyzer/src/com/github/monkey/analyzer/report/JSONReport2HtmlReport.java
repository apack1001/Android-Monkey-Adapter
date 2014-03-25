package com.github.monkey.analyzer.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.monkey.analyzer.analyze.Constants;

/**
 * 
 * @author herongtian -- handle the major implementation
 * @author Alex Chen (apack1001@gmail.com) -- handle the refactoring tasks, make it more readable
 * 
 *         This class
 * 
 */
final public class JSONReport2HtmlReport {

	/**
	 * Construct the HTML content header.
	 */
	private final static String getHtmlHeader(final String encoding) {
		final String headStr = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><HTML>"
				+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+ encoding  +"\"><style>"
				+ "body {background: transparent;}\n"
				+ "ul {border: solid 1px #dedede;margin: 0;padding: 15px;font-family: \"Times New Roman\";font-size: 15px;}\n"
				+ "li {width: auto;margin-left: 10px;margin-right: 15px;cursor: pointer;display: inline-block;padding: 5px;zoom: 1; *display: inline;font-size: 13px;}\n"
				+ "td.left {border-left: 1px solid #c6c6c6;border-bottom: 1px solid #c6c6c6;background: #fff;}\n"
				+ "td.right {border-left: 1px solid #c6c6c6;border-bottom: 1px solid #c6c6c6;border-right: 1px solid #c6c6c6;border-bottom: 1px solid #c6c6c6;background: #fff;border-bottom: 1px solid #c6c6c6;}\n"
				+ ".active {background-color: #dcf1fe;border: solid 1px #ccccccc;color: black;border: solid 1px #c6c6c6;}\n"
				+ ".over {border: solid 1px #c6c6c6;}\n"
				+ ".t_1{line-height:23px;height:23px;background:#fbf2b0;border-bottom:1px solid #eeb154; color:#98580f; font-size:14px; font-weight:bold; padding-left:13px}\n"
				+ ".t_2{background:url(../img/t_1.gif) no-repeat center top; line-height:24px;font-size:14px;font-weight:bold; padding-left:13px; margin-top:10px}\n"
				+ ".mar_2{margin-bottom:10px}\n"
				+ "</style></head><title>Android Monkey Report</title><BODY onload=\"changeStyle()\"><br>";
		return headStr;
	}

	/**
	 * Convert a JSON-format String report to a HTML-format report file.
	 * 
	 * @param jSONFormatStringReport
	 *            a JSON-format String
	 * @param htmlReportFilePath
	 *            the path of target HTML report file
	 * @param encoding 
	 *            the coding of target HTML report file
	 */
	public static void toHTMLReport(String jSONFormatStringReport,
			String htmlReportFilePath, String encoding) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(htmlReportFilePath), encoding);
			JSONObject monkeyResultJSONObject = new JSONObject(
					jSONFormatStringReport);
			String tab1 = getHtmlHeader(encoding);
			String nametd = "<td class=\"left\" width=\"50%\" style=\"font-weight: bold;\">";
			String valuetd = "</td><td class=\"right\" width=\"50%\">";

			tab1 += "<div><div class=\"t_2\">执行结果信息汇总</div><div class=\"t_1\">基本信息</div>"
					+ "<div><table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"1\" cellspacing=\"1\" class=\"mar_2\" id=\"etc\">\n";
			String executor = "-";
			if (monkeyResultJSONObject.has(Constants.JSONReport.KEY_EXECUTOR))
				executor = monkeyResultJSONObject
						.getString(Constants.JSONReport.KEY_EXECUTOR);
			tab1 += "<tr>" + nametd + "执行人" + valuetd + executor
					+ "</td></tr>\n";
			// tab1 += "<tr>" + nametd + "手机类型" + valuetd
			// +
			// monkeyResult.getString(JSONReportConstants.JSONReport.KEY_MOBILE_PHONE_TYPE)
			// + "</td></tr>\n";
			// tab1 += "<tr>" + nametd + "开始时间" + valuetd
			// + monkeyResult.getString(JSONReportConstants.JSONReport.KEY_START_TIME)
			// + "</td></tr>\n";
			// tab1 += "<tr>" + nametd + "结束时间" + valuetd
			// + monkeyResult.getString(JSONReportConstants.JSONReport.KEY_END_TIME)
			// + "</td></tr>\n";
			String duration = "-";
			if (monkeyResultJSONObject.has(Constants.JSONReport.KEY_DURATION))
				duration = monkeyResultJSONObject
						.getString(Constants.JSONReport.KEY_DURATION);
			tab1 += "<tr>" + nametd + "总执行时间" + valuetd + duration
					+ "</td></tr>\n";
			String average = "-";
			if (monkeyResultJSONObject.has(Constants.JSONReport.KEY_AVERAGE))
				average = monkeyResultJSONObject
						.getString(Constants.JSONReport.KEY_AVERAGE);
			tab1 += "<tr>" + nametd + "平均时长(出现Crash/ANR取所有异常停止执行时间之平均值)" + valuetd + average
					+ "</td></tr>\n";
			String productName = "-";
			if (monkeyResultJSONObject
					.has(Constants.JSONReport.KEY_PRODUCT_NAME))
				productName = monkeyResultJSONObject
						.getString(Constants.JSONReport.KEY_PRODUCT_NAME);
			tab1 += "<tr>" + nametd + "产品名称" + valuetd + productName
					+ "</td></tr>\n";
			String productVer = "-";
			if (monkeyResultJSONObject.has(Constants.JSONReport.KEY_VERSION))
				productVer = monkeyResultJSONObject
						.getString(Constants.JSONReport.KEY_VERSION);
			tab1 += "<tr>" + nametd + "产品版本" + valuetd + productVer
					+ "</td></tr>\n";
			String phoneInfo = "-";
			if (monkeyResultJSONObject
					.has(Constants.JSONReport.KEY_MOBILE_PLATFORM))
				phoneInfo = monkeyResultJSONObject
						.getString(Constants.JSONReport.KEY_MOBILE_PLATFORM);
			tab1 += "<tr>" + nametd + "手机信息" + valuetd + phoneInfo
					+ "</td></tr>\n";
			int anrCount = 0;
			if (monkeyResultJSONObject.has(Constants.JSONReport.KEY_ANR_NUMBER))
				anrCount = monkeyResultJSONObject
						.getInt(Constants.JSONReport.KEY_ANR_NUMBER);
			tab1 += "<tr>" + nametd + "ANR数量" + valuetd + anrCount
					+ "</td></tr>\n";
			int crashCount = 0;
			if (monkeyResultJSONObject
					.has(Constants.JSONReport.KEY_CRASH_NUMBER))
				crashCount = monkeyResultJSONObject
						.getInt(Constants.JSONReport.KEY_CRASH_NUMBER);
			tab1 += "<tr>" + nametd + "CRASH数量" + valuetd + crashCount
					+ "</td></tr>";
			
			int nativeCrashCount = 0;
			if (monkeyResultJSONObject
					.has(Constants.JSONReport.KEY_NATIVE_CRASH_NUMBER))
				nativeCrashCount = monkeyResultJSONObject
						.getInt(Constants.JSONReport.KEY_NATIVE_CRASH_NUMBER);
			tab1 += "<tr>" + nametd + "Native CRASH数量" + valuetd + nativeCrashCount
					+ "</td></tr>" + "</table></div>\n";

			tab1 += "<div class=\"t_1\">详细信息</div><div>\n"
					+ "<table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"1\" cellspacing=\"1\" class=\"mar_2\" id=\"etc_detail\">"
					+ "<tr><td class=\"left\" width=\"5%\" style=\"font-weight: bold;\">Type</td>"
					+ "<td class=\"left\" width=\"10%\" style=\"font-weight: bold;\">Short Message</td>"
					+ "<td class=\"left\" width=\"10%\" style=\"font-weight: bold;\">Num</td>"
					+ "<td class=\"left\" width=\"10%\" style=\"font-weight: bold;\">Duration</td>"
					+ "<td class=\"left\" width=\"55%\" style=\"font-weight: bold;\">Message</td>" 
					+ "<td class=\"right\" width=\"10%\" style=\"font-weight: bold;\">Log</td>"
					+ "</tr>"
			// +
			// "<td class=\"left\" width=\"10%\" class=\"hd\" style=\"font-weight: bold;\">Bug Report</td>"
			// +
			// "<td class=\"left\" width=\"8%\" class=\"hd\" style=\"font-weight: bold;\">Logcat</td>"
			// +
			// "<td class=\"right\" width=\"8%\" class=\"hd\" style=\"font-weight: bold;\">Trace</td></tr>\n"
			;
			if (monkeyResultJSONObject
					.has(Constants.JSONReport.KEY_ABNORMALITIS)) {

				JSONArray abnormalities = monkeyResultJSONObject
						.getJSONArray(Constants.JSONReport.KEY_ABNORMALITIS);
				// String startUrl1 =
				// "<td width=\"8%\" class=\"left\" ><a href=\"";
				// String startUrl2 =
				// "<td width=\"8%\" class=\"right\" ><a href=\"";
				// String endUrl = "\">go</a></td>";
				for (int i = 0; i < abnormalities.length(); i++) {

					JSONObject json = abnormalities.getJSONObject(i);

					String type = "-";
					if (json.has(Constants.JSONReport.KEY_ABNORMALITIS_TYPE))
						type = json
								.getString(Constants.JSONReport.KEY_ABNORMALITIS_TYPE);
					String shortMsg = "-";

					if (json.has(Constants.JSONReport.KEY_ABNORMALITIS_SHORT_MSG))
						shortMsg = json
								.getString(Constants.JSONReport.KEY_ABNORMALITIS_SHORT_MSG);
					String msg = "-";
					if (json.has(Constants.JSONReport.KEY_ABNORMALITIS_MSG))
						msg = json
								.getString(Constants.JSONReport.KEY_ABNORMALITIS_MSG);
					int count = 0;
					if (json.has(Constants.JSONReport.KEY_ABNORMALITIS_COUNT))
						count = json
								.getInt(Constants.JSONReport.KEY_ABNORMALITIS_COUNT);
					String durations = "-";
					if (json.has(Constants.JSONReport.KEY_ABNORMALITIS_DURATIONS))
						durations = json
								.getString(Constants.JSONReport.KEY_ABNORMALITIS_DURATIONS);
					// String traceUrl =
					// json.getString(JSONReportConstants.JSONReport.KEY_ABNORMALITIS_TRACE_URL);
					// String logcatUrl =
					// json.getString(JSONReportConstants.JSONReport.KEY_ABNORMALITIS_LOGCAT_URL);
					// String bugUrl =
					// json.getString(JSONReportConstants.JSONReport.KEY_ABNORMALITIS_BUGREPORT_URL);
					String zip = "-";
					if (json.has(Constants.JSONReport.KEY_ABNORMALITIES_ZIP_URL)) {
						zip = json
								.getString(Constants.JSONReport.KEY_ABNORMALITIES_ZIP_URL);
					}
					tab1 += "<tr><td class=\"left\"  width=\"5%\" >" + type
							+ "</td><td class=\"left\" width=\"10%\" >"
							+ shortMsg
							+ "</td><td class=\"left\" width=\"10%\" >"
							+ count
							+ "</td><td class=\"left\" width=\"10%\" >"
							+ durations
							+ "</td><td class=\"left\" width=\"55%\" >" + msg
							+ "</td><td class=\"right\" width=\"10%\" >" + zip
							// + startUrl1 + bugUrl + endUrl + startUrl1 +
							// traceUrl
							// + endUrl + startUrl2 + logcatUrl + endUrl
							+ "</td></tr>\n";
				}
			}
			tab1 += "</table></div></div></body></html>";
			out.write(tab1);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("invalid JSON file type,please check definition of your JSONArrays!");
		}
	}

	/**
	 * Read JSON-format String from
	 * 
	 * @param path
	 *            the path of the JSON report
	 */
	@SuppressWarnings("unused")
	private static String getJSONString(final String path) {
		String jsonString = "";
		try {
			File f = new File(path);
			if (f.exists() && f.isFile()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f));
				BufferedReader bReader = new BufferedReader(read);
				String line = null;

				while ((line = bReader.readLine()) != null) {
					jsonString += line;
				}

				bReader.close();
				read.close();

			} else
				System.out.println("invalid JSON file path!");

		} catch (Exception e) {
			System.out.println("error accures while reading the JSON file!");
		}

		return jsonString;
	}
}
