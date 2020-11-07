package com.example.sunmoonbus;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileIO {

    FileIO(Context context, String fileName, String items) {
        saveItemsToFile(context, fileName, items);
    }

    private void saveItemsToFile(Context context, String fileName, String items) {
        File file = new File(context.getFilesDir(), fileName) ;
        FileWriter fw = null ;
        BufferedWriter bufwr = null ;

        try {
            // open file.
            fw = new FileWriter(file) ;
            bufwr = new BufferedWriter(fw) ;

            //for (String str : items) {
                bufwr.write(items) ;
                bufwr.newLine() ;
            //}

            // write data to the file.
            bufwr.flush() ;

        } catch (Exception e) {
            e.printStackTrace() ;
        }

        try {
            // close file.
            if (bufwr != null) {
                bufwr.close();
            }

            if (fw != null) {
                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace() ;
        }
    }
}
