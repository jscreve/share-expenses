package com.shareexpenses.app.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jess on 17/09/2014.
 */
public class CategoryDAO implements Serializable {

    private String name;
    private String id;
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public CategoryDAO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static ArrayList<Category> loadCategories(SQLiteDatabase db, Account account) {
        //get categories
        Cursor categoryCursor = db.rawQuery("select * from category a where a.id_account=?", new String[]{String.valueOf(account.getId())});
        ArrayList<Category> categoriesList = new ArrayList<Category>();
        if (categoryCursor.moveToFirst()) {
            do {
                Long id = categoryCursor.getLong(categoryCursor.getColumnIndex("id"));
                String name = categoryCursor.getString(categoryCursor.getColumnIndex("name"));;
                Category category = new Category(name);
                category.setName(name);
                category.setId(id);
                category.setAccount(account);
                categoriesList.add(category);
            } while (categoryCursor.moveToNext());
        }
        categoryCursor.close();
        return categoriesList;
    }

    public static Category loadCategory(SQLiteDatabase db, Long id) {
        //get categories
        Cursor categoriesCursor = db.rawQuery("select * from category a where a.id=?", new String[]{String.valueOf(id)});
        Category category = null;
        if (categoriesCursor.moveToFirst()) {
            String name = categoriesCursor.getString(categoriesCursor.getColumnIndex("name"));
            category = new Category(name);
            category.setId(id);
        }
        categoriesCursor.close();
        return category;
    }

    public static void updateCategory(SQLiteDatabase db, Category category) {
        ContentValues values;
        values = new ContentValues();
        values.put("name", category.getName());
        db.update("category", values, "id = ?", new String[]{Long.toString(category.getId())});
    }

    public static void addCategoryToAccount(SQLiteDatabase db, Category category) {
        ContentValues values;
        long rowId;

        values = new ContentValues();
        //manage categories
        values.put("name", category.getName());
        values.put("id_account", category.getAccount().getId());
        long categoryRowId = db.insert("category", null, values);
        category.setId(categoryRowId);
    }

    public static void removeCategory(SQLiteDatabase db, Category category) {
        db.delete("category", "id=?", new String[]{Long.toString(category.getId())});
    }

}
