package com.shareexpenses.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import com.dropbox.sync.android.*;
import com.shareexpenses.app.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by jscreve on 22/09/2014.
 */
public class Data {

    public static final String DB_NAME = "expense";

    private ArrayList<Account> allAccounts;
    private ArrayList<Expense> allExpenses;
    private ArrayList<Participant> allParticipants;
    private ArrayList<Category> allCategories;

    private Context context;
    private SQLiteAssetHelper databaseHelper;
    private BackupManager backupManager;
    private DbxAccountManager dbxAccountManager;
    private boolean isDropBoxActivated;
    private DbxFile sqlFile;
    private DbxFile.Listener listener = null;


    public Data(Context context) {
        this.context = context;
        databaseHelper = new SQLiteAssetHelper(context, DB_NAME, null, 2);
        backupManager = new BackupManager(context);
    }

    public static Comparator<Expense> ExpenseComparator = new Comparator<Expense>() {

        public int compare(Expense expense1, Expense expense2) {
            //descending order
            return expense2.getDate().compareTo(expense1.getDate());
        }

    };

    public boolean isDropBoxActivated() {
        return isDropBoxActivated;
    }

    public boolean setDropBoxActivated(boolean isDropBoxActivated, boolean reloadFile) throws DbxException {
        boolean fileReloaded = false;
        this.isDropBoxActivated = isDropBoxActivated;
        if(isDropBoxActivated && reloadFile) {
        //    fileReloaded = !loadDbFromDropBox();
        }
        return fileReloaded;
    }

    public DbxAccountManager getDbxAccountManager() {
        return dbxAccountManager;
    }

    public void setDbxAccountManager(DbxAccountManager dbxAccountManager) {
        this.dbxAccountManager = dbxAccountManager;
    }

    private void openSQLFile() throws DbxException {
        if(sqlFile == null) {
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(dbxAccountManager.getLinkedAccount());
            DbxPath path = new DbxPath("expense.sql");
            if (dbxFs.exists(path)) {
                sqlFile = dbxFs.open(path);
            } else {
                sqlFile = dbxFs.create(path);
            }
        }
    }

    private void closeSQLFile() {
        if(sqlFile != null) {
            sqlFile.close();
            sqlFile = null;
        }
    }

    private boolean saveDbToDropBox() throws DbxException {
        boolean failure = false;

        if(isDropBoxActivated) {
            openSQLFile();
            try {
                File input = context.getDatabasePath(Data.DB_NAME);

                //copy
                try {
                    //sqlFile.writeFromExistingFile(input, false);
                    Util.copyFileStream(new FileInputStream(input), sqlFile.getWriteStream());
                } catch (Exception ex) {
                    Log.e("copy file", "sql file copy failed", ex);
                    failure = true;
                }
            } finally {
                closeSQLFile();
            }
        }
        return failure;
    }

    private boolean loadDbFromDropBox() throws DbxException {

        boolean no_copy = false;

        //load if nothing defined yet
        Data data = MainApplication.getInstance().getData();

        DbxFileSystem dbxFs = DbxFileSystem.forAccount(dbxAccountManager.getLinkedAccount());
        //wait for drop box sync
        dbxFs.awaitFirstSync();

        DbxPath path = new DbxPath("expense.sql");
        if (dbxFs.exists(path)) {
            openSQLFile();
            DbxFileStatus status = sqlFile.getSyncStatus();
            try {
                if (status.isLatest) {
                    DbxFileInfo dbxFileInfo = sqlFile.getInfo();
                    File internalSQLFile = context.getDatabasePath(Data.DB_NAME);
                    //copy
                    try {
                        Date internalFileData = new Date(internalSQLFile.lastModified());
                        Date dropBoxLastModified = dbxFileInfo.modifiedTime;
                        Util.copyFileStream(sqlFile.getReadStream(), new FileOutputStream(internalSQLFile));
                    } catch (Exception ex) {
                        Log.e("copy file", "sql file copy failed", ex);
                        no_copy = true;
                    }
                } else {
                    no_copy = true;
                }
            } finally {
                closeSQLFile();
            }
        } else {
            no_copy = true;
        }
        return no_copy;
    }

    public void notifyDBchange() {
        if(backupManager != null) {
            backupManager.dataChanged();
        }
        if(isDropBoxActivated) {
            try {
                saveDbToDropBox();
            } catch (DbxException ex) {
                Log.e("Dropbox copy", "an error occured copying dropbox file", ex);
            }
        }

    }

    public String getDBPath() {
        return databaseHelper.getmDatabasePath();
    }

    public ArrayList<Account> getAccounts() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        allAccounts = AccountDAO.loadAccounts(db);
        return allAccounts;
    }

    public Account getAccount(Long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Account account = AccountDAO.loadAccount(db, id);
        return account;
    }

    public void removeAccount(Account account) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        allAccounts.remove(account);
        try {
            db.beginTransaction();
            AccountDAO.removeAccount(db, account);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }

    public void saveAccount(Account account) {
        SQLiteDatabase db;

        db = databaseHelper.getWritableDatabase();
        allAccounts.add(account);
        try {
            db.beginTransaction();
            AccountDAO.saveAccounts(db, Collections.singletonList(account));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }


    public ArrayList<Expense> getExpenses(Account account) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        allExpenses = ExpenseDAO.loadExpenses(db, account);
        return allExpenses;
    }

    public void saveExpense(Expense expense) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        allExpenses.add(0, expense);
        try {
            db.beginTransaction();
            ExpenseDAO.addExpenseToAccount(db, expense);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
        Collections.sort(allExpenses, ExpenseComparator);
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        //find Expense and replace it
        int i=0;
        for(Expense expense1 : allExpenses) {
            if(expense1.getId().equals(expense.getId())) {
                allExpenses.set(i, expense);
                break;
            }
            i++;
        }
        try {
            db.beginTransaction();
            ExpenseDAO.updateExpenseToAccount(db, expense);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
        Collections.sort(allExpenses, ExpenseComparator);
    }

    public void removeExpense(Expense expense) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        allExpenses.remove(expense);
        try {
            db.beginTransaction();
            ExpenseDAO.removeExpense(db, expense);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }

    public ArrayList<Participant> getParticipants(Account account) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        allParticipants = ParticipantDAO.loadParticipants(db, account);
        return allParticipants;
    }

    public Participant getParticipant(Long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Participant participant = ParticipantDAO.loadParticipant(db, id);
        return participant;
    }

    public void saveParticipant(Participant participant) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        allParticipants.add(participant);
        try {
            db.beginTransaction();
            ParticipantDAO.addParticipantToAccount(db, participant);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }

    public void removeParticipant(Participant participant) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        allParticipants.remove(participant);
        try {
            db.beginTransaction();
            ParticipantDAO.removeParticipant(db, participant);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }

    public ArrayList<Category> getCategories(Account account) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        allCategories = CategoryDAO.loadCategories(db, account);
        return allCategories;
    }

    public Category getCategory(Long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Category category = CategoryDAO.loadCategory(db, id);
        return category;
    }

    public void saveCategory(Category category) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        allCategories.add(category);
        try {
            db.beginTransaction();
            CategoryDAO.addCategoryToAccount(db, category);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }

    public void removeCategory(Category category) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        allCategories.remove(category);
        try {
            db.beginTransaction();
            CategoryDAO.removeCategory(db, category);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db;
        db = databaseHelper.getWritableDatabase();
        //find Category and replace it
        int i=0;
        for(Category category1 : allCategories) {
            if(category1.getId() == category.getId()) {
                allCategories.set(i, category);
                break;
            }
            i++;
        }

        try {
            db.beginTransaction();
            CategoryDAO.updateCategory(db, category);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            notifyDBchange();
        }
    }
}
