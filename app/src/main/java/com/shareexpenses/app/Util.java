package com.shareexpenses.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jscreve on 23/09/2014.
 */
public class Util {

    public static String datePattern = "yyyy-MM-dd";

    public static String monthDatePattern = "MM";

    public static DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

    public static DateFormat monthFormatter1 = new SimpleDateFormat("MM");


    public static Date getDate(String inputDate) {
        Date outputDate = null;
        SimpleDateFormat format = new SimpleDateFormat(datePattern);

        try {
            outputDate = format.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
    }

    public static String getDate(Date inputDate) {
        String outputString = null;
        SimpleDateFormat format = new SimpleDateFormat(datePattern);

        outputString = format.format(inputDate);

        return outputString;
    }

    public static String getMonth(Date inputDate) {
        String outputString = null;
        SimpleDateFormat format = new SimpleDateFormat(monthDatePattern);

        outputString = format.format(inputDate);

        return outputString;
    }

    public static Date getDateFromMonth(String inputDate) {
        Date outputDate = null;
        SimpleDateFormat format = new SimpleDateFormat(monthDatePattern);

        try {
            outputDate = format.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDate;
    }

    public static void showDatePickerDialog(View v, Context context, final Date[] date, final Button button) {
        // Create a new instance of DatePickerDialog and return it
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    date[0] = formatter1.parse(Integer.toString(year) + "-" + Integer.toString(monthOfYear+1) + '-' + Integer.toString(dayOfMonth));
                    //update date
                    button.setText(formatter1.format(date[0]));
                } catch (ParseException e) {

                }
            }
        }, year, month, day);
        dialog.show();
    }

    public static void displayWarningDialogBox(Context context, String message, String cancelText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    public static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
    }

    public static void copyFileStream(InputStream is, OutputStream os) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static void displayErrorDropbox(Activity activity, String message, String cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    public static void copyFileStream(String data, OutputStream os) throws IOException {
        try {
            os.write(data.getBytes("UTF-8"));
            os.flush();
        } finally {
            os.close();
        }
    }
}
