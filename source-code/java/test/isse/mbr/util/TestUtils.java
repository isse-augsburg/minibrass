package isse.mbr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestUtils {

	public static String readFile(File file) throws FileNotFoundException {		
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);		
			fis.close();

			String str = new String(data, "UTF-8");
			return str;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}
}
