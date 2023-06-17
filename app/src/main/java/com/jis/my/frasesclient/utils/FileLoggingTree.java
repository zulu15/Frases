package com.jis.my.frasesclient.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class FileLoggingTree extends Timber.Tree {
    private final Context context;
    private final String logFileName = "log.txt";
    private Uri logUri;

    public FileLoggingTree(Context context) {
        this.context = context;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        String formattedMessage = formatMessage(priority, tag, message);

        Uri logUri = createLogUri();
        if (logUri != null) {
            try {
                ContentResolver contentResolver = context.getContentResolver();
                OutputStream outputStream = contentResolver.openOutputStream(logUri, "wa");
                if (outputStream != null) {
                    outputStream.write(formattedMessage.getBytes());
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Uri createLogUri() {
        // Verificar si ya existe la URI del archivo de log
        if (logUri != null) {
            return logUri;
        }

        // Verificar si ya existe un archivo "app_logs.txt" dentro de "Documents"
        File logFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "app_logs.txt");
        if (logFile.exists()) {
            logUri = Uri.fromFile(logFile);
            return logUri;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "app_logs.txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        ContentResolver contentResolver = context.getContentResolver();
        Uri collectionUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        logUri = contentResolver.insert(collectionUri, values); // Asignar la URI a una variable de instancia


        return logUri;
    }


    private String formatMessage(int priority, String tag, String message) {
        String priorityString;
        switch (priority) {
            case Log.DEBUG:
                priorityString = "DEBUG";
                break;
            case Log.INFO:
                priorityString = "INFO";
                break;
            case Log.WARN:
                priorityString = "WARN";
                break;
            case Log.ERROR:
                priorityString = "ERROR";
                break;
            default:
                priorityString = "UNKNOWN";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());

        return String.format(Locale.getDefault(), "[%s][%s][%s] %s%n", timestamp, priorityString, tag, message);
    }
}
