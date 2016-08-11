package edu.iit.cs.xsu11.watertracking;

import android.os.Environment;
import android.text.format.Time;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtil {
    // private final static String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/watertracking";
    private final static String FILE_PATH = "/mnt/sdcard/digu";

    // Constructor
    public FileUtil() {

    }

    public String setFileName() {
        Time time = new Time("GMT-5");
        time.setToNow();

        int year = time.year;
        int month = time.month;
        int day = time.monthDay;
        int hour = time.hour;
        int minute = time.minute;
        int sec = time.second;

        return year + "-" + month + "-" + day + "_" + hour + "-" + minute + "-" + sec + ".txt";
    }

    public void writeSdcardFile(String fileName, String writeStr) throws IOException {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File path = new File(FILE_PATH);

                File f = new File(FILE_PATH + "/" + fileName);

                if (!path.exists()) {
                    path.mkdirs();
                }

                if (!f.exists()) {
                    f.createNewFile();
                }

                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f, true));

                osw.write(writeStr);
                osw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
