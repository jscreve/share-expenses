package com.shareexpenses.app;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.CategoryForExpense;
import com.shareexpenses.app.model.Expense;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by jess on 20/10/2014.
 */
public class QIFUtils {

    /**
     *
     !Option:AutoSwitch
     !Account
     NCompte courant
     D
     X
     TBank
     B0,00Â â‚¬
     ^
     !Clear:AutoSwitch
     !Type:Cat
     NAbonnements
     DMagazines, journaux, sites, etc.
     E
     ^
     NAlimentation
     D
     E
     ^
     !Account
     NCompte courant
     D
     X
     TBank
     ^
     !Type:Bank
     D12/06/2003
     PSNCF
     M
     T-44,9
     CX
     LTransports
     ^
     */

    public static void writeAccount(String accountName, PrintWriter outputFile) {
        writeAccount(accountName, "Bank", outputFile);
    }

    public static void writeAccount(String accountName, String accountType, PrintWriter outputFile) {
        outputFile.println("!Option:AutoSwitch");
        outputFile.println("!Account");
        outputFile.println("N" + accountName);
        outputFile.println("T" + accountType);
        outputFile.println("^");
        outputFile.println("!Clear:AutoSwitch");
    }

    public static void writeCategories(List<Category> categories, PrintWriter outputFile) {
        outputFile.println("!Type:Cat");
        for(Category category : categories) {
            outputFile.println("N" + category.getName());
            outputFile.println("D");
            outputFile.println("E");
            outputFile.println("");
        }
    }

    public static void writeExpenses(List<Expense> expenses, PrintWriter outputFile) {
        writeExpenses(expenses, "Bank", outputFile);
    }

    public static void writeExpenses(List<Expense> expenses, String accountType, PrintWriter outputFile) {
        outputFile.println("!Type:" + accountType);
        for(Expense expense : expenses) {
            //write date
            if(expense.getDate() != null) {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("DD/MM/yyyy");
                String sDate = simpleDateFormat.format(expense.getDate());
                outputFile.println("D" + sDate);
            }
            //write name
            outputFile.println("P" + expense.getName());

            //write memo
            outputFile.println("M");

            //write price
            DecimalFormat twoDecimals = new DecimalFormat("#.00");
            String sValue=twoDecimals.format(expense.getValue());
            outputFile.println("T-" + sValue);

            //write categories
            if(expense.getCategoriesForExpense().size()==0) {
                //nothing to do
            } else if(expense.getCategoriesForExpense().size()==1) {
                outputFile.println("L" + expense.getCategoriesForExpense().get(0).getCategory().getName());
            } else {
                //we split the expense
                int nbCategories = expense.getCategoriesForExpense().size();
                String sValuePerCategory=twoDecimals.format(expense.getValue() / (double)nbCategories);
                for(CategoryForExpense categoryForExpense : expense.getCategoriesForExpense()) {
                    outputFile.println("S" + categoryForExpense.getCategory().getName());
                    outputFile.println("E");
                    outputFile.println("$-" + sValuePerCategory);
                }
            }
            outputFile.println("^");
        }
    }

    public static void writeTail(PrintWriter outputFile) {
        outputFile.println("!Type:Security");
    }

    public static void generateQIFFile(List<Expense> expenses, List<Category> categories, String accountName, PrintWriter outputFile) {
        //write account name
        writeAccount(accountName, outputFile);
        //write categories
        writeCategories(categories, outputFile);
        //write expenses
        writeExpenses(expenses, outputFile);
        //write tail
        writeTail(outputFile);
        //flush
        outputFile.flush();
    }

    public static File generateQIFFile(List<Expense> expenses, List<Category> categories, String accountName, String filename, Context context) {
        // Get the directory for the app's public directory, don't know why DOCUMENTS does not work
        File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        try {
            if (!output.createNewFile()) {
                Log.e("QIF file generation", "File not created");
                output = null;
            } else {
                try {
                    FileOutputStream outputStream = new FileOutputStream(output);
                    PrintWriter printWriter = new PrintWriter(outputStream);
                    generateQIFFile(expenses, categories, accountName, printWriter);
                    printWriter.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e("QIF file generation", "File not created");
            output = null;
        }
        return output;
    }
 }
