package com.shareexpenses.app.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.shareexpenses.app.MainApplication;
import com.shareexpenses.app.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jscreve on 22/09/2014.
 */
public class ExpenseDAO {

    public static ArrayList<Expense> loadExpenses(SQLiteDatabase db, Account account) {
        Cursor cursor;
        ArrayList<Expense> result;

        result = new ArrayList<Expense>();
        cursor = db.rawQuery("select id, name, value, account_id, currency, payer_id, date from expense where account_id=? order by date asc", new String[]{String.valueOf(account.getId())});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Double value = cursor.getDouble(cursor.getColumnIndex("value"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String currencyString = cursor.getString(cursor.getColumnIndex("currency"));
                Long participantId = cursor.getLong(cursor.getColumnIndex("payer_id"));
                Currency currency = new Currency(currencyString);

                //fetch payer
                Participant participant = MainApplication.getInstance().getData().getParticipant(participantId);
                if(participant != null) {
                    participant.setAccount(account);
                }
                Expense expense = new Expense(value, name, participant, Util.getDate(date), currency);
                expense.setId(id);

                //get participants
                List<ParticipantForExpense> participantsForExpenses = ParticipantForExpenseDAO.loadParticipants(db, expense);
                expense.setParticipantForExpenseList(participantsForExpenses);

                //get categories
                List<CategoryForExpense> categoryForExpenses = CategoryForExpenseDAO.loadCategories(db, expense);
                expense.setCategoriesForExpense(categoryForExpenses);

                result.add(expense);
            }
        }
        cursor.close();
        Collections.reverse(result);
        return result;
    }

    public static void addExpenseToAccount(SQLiteDatabase db, Expense expense) {

        ContentValues values;
        long rowId;

        values = new ContentValues();
        values.put("account_id", expense.getAccount().getId());
        values.put("payer_id", expense.getPayer().getId());
        values.put("name", expense.getName());
        values.put("value", expense.getValue());
        values.put("date", Util.getDate(expense.getDate()));
        values.put("currency", expense.getCurrency().getName());
        rowId = db.insert("expense", null, values);
        expense.setId(rowId);

        //manage participants
        List<ParticipantForExpense> participants = expense.getParticipantForExpenseList();
        for (ParticipantForExpense participantForExpense : participants) {
            ParticipantForExpenseDAO.saveParticipant(db, expense, participantForExpense);
        }

        //manage categories
        List<CategoryForExpense> categories = expense.getCategoriesForExpense();
        CategoryForExpenseDAO.saveCategories(db, expense, categories);
    }

    public static void updateExpenseToAccount(SQLiteDatabase db, Expense expense) {

        ContentValues values;
        long rowId;

        values = new ContentValues();
        values.put("account_id", expense.getAccount().getId());
        values.put("payer_id", expense.getPayer().getId());
        values.put("name", expense.getName());
        values.put("value", expense.getValue());
        values.put("date", Util.getDate(expense.getDate()));
        values.put("currency", expense.getCurrency().getName());
        db.update("expense", values, "id = ?", new String[]{Long.toString(expense.getId())});

        //manage participants
        ParticipantForExpenseDAO.removeParticipants(db, expense);
        List<ParticipantForExpense> participants = expense.getParticipantForExpenseList();
        for (ParticipantForExpense participantForExpense : participants) {
            ParticipantForExpenseDAO.saveParticipant(db, expense, participantForExpense);
        }

        //manage categories
        CategoryForExpenseDAO.removeCategories(db ,expense);
        List<CategoryForExpense> categories = expense.getCategoriesForExpense();
        CategoryForExpenseDAO.saveCategories(db, expense, categories);
    }

    public static void removeExpense(SQLiteDatabase db, Expense expense) {
        CategoryForExpenseDAO.removeCategories(db, expense);
        ParticipantForExpenseDAO.removeParticipants(db, expense);
        db.delete("expense", "id = ?", new String[]{Long.toString(expense.getId())});
    }
}
