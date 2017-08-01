package com.shareexpenses.app.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jscreve on 22/09/2014.
 */
public class AccountDAO {

    public static ArrayList<Account> loadAccounts(SQLiteDatabase db) {
        Cursor cursor;
        ArrayList<Account> result;

        result = new ArrayList<Account>();
        cursor = db.rawQuery("select id, name from account", new String[]{});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Long id = cursor.getLong(cursor.getColumnIndex(("id")));
                Account account = new Account(name);
                account.setId(id);
                result.add(account);
            }
        }
        cursor.close();
        return result;
    }

    public static Account loadAccount(SQLiteDatabase db, long id) {
        Cursor cursor;
        Account result=null;
        cursor = db.rawQuery("select * from account where id=?", new String[]{Long.toString(id)});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                result = new Account(name);
                result.setId(id);
            }
        }
        cursor.close();
        return result;
    }

    public static void removeAllAccounts(SQLiteDatabase db) {
        db.delete("account", null, null);
    }

    public static void removeAccount(SQLiteDatabase db, Account account) {
        db.delete("account", "id = ?", new String[]{Long.toString(account.getId())});
        db.delete("expense", "account_id = ?", new String[]{Long.toString(account.getId())});
        db.delete("participant", "account_id = ?", new String[]{Long.toString(account.getId())});
    }

    public static void saveAccounts(SQLiteDatabase db, List<Account> allAccounts) {
        for (Account account : allAccounts) {
            ContentValues values;
            long rowId;

            values = new ContentValues();
            values.put("name", account.getAccountName());
            rowId = db.insert("account", null, values);
            account.setId(rowId);
        }
    }
}
