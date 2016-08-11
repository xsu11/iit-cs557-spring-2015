package ca.uwaterloo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.http.util.EncodingUtils;

import android.os.Environment;

@SuppressWarnings("deprecation")
public class FileUtil {

	// Global variables
	// file path, get it from the environment variables of the phone
	public final static String PATH = Environment.getExternalStorageDirectory()
			.toString() + "/WaterTracking/wt_li_" + WekaTest.NUM_POINT + "p";

	// test file name
	public final static String TEST_FILENAME = "wt_li_" + WekaTest.NUM_POINT
			+ "p_test_data.arff";

	// training file name
	public final static String TRAINING_FILENAME = "wt_li_"
			+ WekaTest.NUM_POINT + "p_training_data.arff";

	// log file name
	public final static String LOG_FILENAME = "wt_li_" + WekaTest.NUM_POINT
			+ "p_logfile.txt";

	// backup file name
	public final static String BACKUP_FILENAME = "wt_li_" + WekaTest.NUM_POINT
			+ "p_backupfile.txt";

	// write into sdcard file
	public static void writeSdcardFile(String dir, String fileName,
			String write_str) throws IOException {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				File path = new File(dir);
				File f = new File(dir + "/" + fileName);

				if (!path.exists()) {
					path.mkdirs();
				}

				if (!f.exists()) {
					f.createNewFile();
				}

				OutputStreamWriter osw = new OutputStreamWriter(
						new FileOutputStream(f, true));

				osw.write(write_str);
				osw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// read from sdcard file
	public static String readSdcardFile(String dir, String fileName)
			throws IOException {
		String res = "";

		try {
			FileInputStream fin = new FileInputStream(dir + "/" + fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}

	// delete file in sdcard
	public static void deleteSdcardFile(String dir, String fileName) {
		// delete TEST_FILENAME
		File f = new File(dir + "/" + fileName);

		if (f.isFile() && f.exists()) {
			f.delete();
		}

	}

	// write the header of .arff file
	public static void writeArffFileHeader(String dir, String fileName,
			int numAttr) {
		String header = "@RELATION waterTrackingData\n\n";
		try {
			writeSdcardFile(dir, fileName, header);

			for (int i = 0; i < numAttr; i++) {
				String attr = "@ATTRIBUTE attr" + i + " NUMERIC\n";
				writeSdcardFile(dir, fileName, attr);
			}
			String res = "@ATTRIBUTE result {1, 0}\n\n";
			writeSdcardFile(dir, fileName, res);

			String data = "@DATA\n";

			writeSdcardFile(dir, fileName, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
