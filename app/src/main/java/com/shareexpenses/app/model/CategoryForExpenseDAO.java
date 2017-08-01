package com.shareexpenses.app.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jscreve on 22/09/2014.
 */
public class CategoryForExpenseDAO {

    public static ArrayList<CategoryForExpense> loadCategories(SQLiteDatabase db, Expense expense) {
        //get categories
        Cursor categoriesCursor = db.rawQuery("select a.id AS id_category_for_expense, b.name AS name_category, b.id AS id_category from category_for_expense a, category b where a.id_category=b.id and a.id_expense=?", new String[]{String.valueOf(expense.getId())});
        ArrayList<CategoryForExpense> categoryList = new ArrayList<CategoryForExpense>();
        if (categoriesCursor.moveToFirst()) {
            do {
                String categoryName = categoriesCursor.getString(categoriesCursor.getColumnIndex("name_category"));
                Long categoryId = categoriesCursor.getLong(categoriesCursor.getColumnIndex("id_category"));
                Long categoryForExpenseId = categoriesCursor.getLong(categoriesCursor.getColumnIndex("id_category_for_expense"));
                Category category = new Category(categoryName);
                category.setId(categoryId);
                CategoryForExpense categoryForExpense = new CategoryForExpense(category, expense);
                categoryForExpense.setId(categoryForExpenseId);
                categoryList.add(categoryForExpense);
            } while (categoriesCursor.moveToNext());
        }
        categoriesCursor.close();
        return categoryList;
    }

    public static void saveCategories(SQLiteDatabase db, Expense expense, List<CategoryForExpense> categories) {

        ContentValues values;
        long rowId;

        values = new ContentValues();

        //manage categories
        if (categories != null) {
            for (CategoryForExpense categoryForExpense : categories) {
                values.put("id_category", categoryForExpense.getCategory().getId());
                values.put("id_expense", expense.getId());
                long categoryRowId = db.insert("category_for_expense", null, values);
            }
        }
    }

    public static void updateCategories(SQLiteDatabase db, Expense expense, List<CategoryForExpense> categories) {

        ContentValues values;
        values = new ContentValues();

        //manage categories
        if (categories != null) {
            for (CategoryForExpense categoryForExpense : categories) {
                values.put("id_category", categoryForExpense.getCategory().getId());
                values.put("id_expense", expense.getId());
                db.update("category_for_expense", values, "id_expense = ?", new String[]{Long.toString(expense.getId())});
            }
        }
    }

    public static void removeCategories(SQLiteDatabase db, Expense expense) {
        db.delete("category_for_expense", "id_expense = ?", new String[]{Long.toString(expense.getId())});
    }
}
