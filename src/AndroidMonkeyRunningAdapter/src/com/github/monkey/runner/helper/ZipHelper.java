package com.github.monkey.runner.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHelper {

	public static boolean zip(String src, String des) throws Exception {
		boolean result;
		ZipOutputStream out = null;
		try {
			File f = new File(src);
			out = new ZipOutputStream(new FileOutputStream(des));
			zip(out, f, "");
			f.delete();
			result = true;
		} catch (Exception ex) {
			result = false;
		} finally {
			out.close();
		}
		return result;
	}

	static void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			out.putNextEntry(new ZipEntry(base + File.separator));
			base = base.length() == 0 ? "" : base + File.separator;
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int b;
			System.out.println(base);
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}
}
