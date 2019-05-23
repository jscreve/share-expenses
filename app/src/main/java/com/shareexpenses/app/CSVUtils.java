package com.shareexpenses.app;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.CategoryForExpense;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.ParticipantForExpense;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 */
public class CSVUtils {

    private static final String SEPARATOR = ",";
    private static final String LIST_SEPARATOR = ";";

    public static void writeHeader(String accountName, PrintWriter outputFile) {
        outputFile.print("Date");
        outputFile.print(SEPARATOR);
        outputFile.print("Expense");
        outputFile.print(SEPARATOR);
        outputFile.print("Price");
        outputFile.print(SEPARATOR);
        outputFile.print("Payer");
        outputFile.print(SEPARATOR);
        outputFile.print("Participants");
        outputFile.print(SEPARATOR);
        outputFile.print("Categories");
        outputFile.print(SEPARATOR);

    }

    public static void writeExpenses(List<Expense> expenses, PrintWriter outputFile) {
        for (Expense expense : expenses) {
            //write date
            if (expense.getDate() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD/MM/yyyy");
                String sDate = simpleDateFormat.format(expense.getDate());
                outputFile.print(sDate);
            }
            outputFile.print(SEPARATOR);

            //write name
            outputFile.print(expense.getName());
            outputFile.print(SEPARATOR);

            //write price
            DecimalFormat twoDecimals = new DecimalFormat("#.00");
            String sValue = twoDecimals.format(expense.getValue()).replace(',', '.');
            outputFile.print(sValue);
            outputFile.print(SEPARATOR);

            //write payer
            outputFile.print(expense.getPayer().getName() + " " + expense.getPayer().getLastName());
            outputFile.print(SEPARATOR);

            //write participants
            int nbParticipants = expense.getParticipantForExpenseList().size();
            if (nbParticipants >= 0) {
                int participant = 0;
                for (ParticipantForExpense participant1 : expense.getParticipantForExpenseList()) {
                    outputFile.print(participant1.getParticipant().getName() + " " + participant1.getParticipant().getLastName());
                    if (participant < nbParticipants)
                        outputFile.print(LIST_SEPARATOR);
                    participant++;
                }
            }
            outputFile.print(SEPARATOR);

            //write categories
            int nbCategories = expense.getCategoriesForExpense().size();
            if (nbCategories >= 0) {
                int cat = 0;
                for (CategoryForExpense category : expense.getCategoriesForExpense()) {
                    outputFile.print(category.getCategory().getName());
                    if (cat < nbCategories)
                        outputFile.print(LIST_SEPARATOR);
                    cat++;
                }
            }
            outputFile.print(SEPARATOR);

            outputFile.println();
        }
    }

    public static void generateCSVFile(List<Expense> expenses, List<Category> categories, String accountName, PrintWriter outputFile) {
        //write account name
        writeHeader(accountName, outputFile);

        outputFile.println();

        //write expenses
        writeExpenses(expenses, outputFile);
        //flush
        outputFile.flush();
    }

    public static File generateCSVFile(List<Expense> expenses, List<Category> categories, String accountName, String filename, Context context) {
        // Get the directory for the app's public directory, don't know why DOCUMENTS does not work
        File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        try {
            if (!output.createNewFile()) {
                Log.e("CSV file generation", "File not created");
                output = null;
            } else {
                try {
                    FileOutputStream outputStream = new FileOutputStream(output);
                    PrintWriter printWriter = new PrintWriter(outputStream);
                    generateCSVFile(expenses, categories, accountName, printWriter);
                    printWriter.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e("CSV file generation", "File not created");
            output = null;
        }
        return output;
    }
}
