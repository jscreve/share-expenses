package com.shareexpenses.app.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by jscreve on 22/09/2014.
 */
public class ParticipantForExpenseDAO {

    public static ArrayList<ParticipantForExpense> loadParticipants(SQLiteDatabase db, Expense expense) {
        //get categories
        Cursor participantsCursor = db.rawQuery("select a.id AS id_ep, a.percentage AS percentage_ep, b.id AS id_participant, b.firstName AS firstName_participant, b.lastName AS lastName_participant from expense_participant a, participant b where a.id_participant=b.id and a.id_expense=?", new String[]{String.valueOf(expense.getId())});
        ArrayList<ParticipantForExpense> participantsList = new ArrayList<ParticipantForExpense>();
        if (participantsCursor.moveToFirst()) {
            do {
                Double participation = participantsCursor.getDouble(participantsCursor.getColumnIndex("percentage_ep"));
                String name = participantsCursor.getString(participantsCursor.getColumnIndex("firstName_participant"));
                String lastName = participantsCursor.getString(participantsCursor.getColumnIndex("lastName_participant"));
                Long participantId = participantsCursor.getLong(participantsCursor.getColumnIndex("id_participant"));
                Long participantForExpenseId = participantsCursor.getLong(participantsCursor.getColumnIndex("id_ep"));
                Participant participant = new Participant(name, lastName);
                participant.setName(name);
                participant.setLastName(lastName);
                participant.setAccount(expense.getAccount());
                participant.setId(participantId);
                ParticipantForExpense participantForExpense = new ParticipantForExpense(participant, expense, participation);
                participantForExpense.setId(participantForExpenseId);
                participantsList.add(participantForExpense);
            } while (participantsCursor.moveToNext());
        }
        participantsCursor.close();
        return participantsList;
    }

    public static void saveParticipant(SQLiteDatabase db, Expense expense, ParticipantForExpense participant) {

        ContentValues values;
        long rowId;

        values = new ContentValues();
        values.put("id_expense", expense.getId());
        values.put("id_participant", participant.getParticipant().getId());
        values.put("percentage", participant.getParticipation());
        long participantRowId = db.insert("expense_participant", null, values);
        participant.setId(participantRowId);
    }

    public static void updateParticipant(SQLiteDatabase db, Expense expense, ParticipantForExpense participant) {

        ContentValues values;
        long rowId;
        values = new ContentValues();
        values.put("id_expense", expense.getId());
        values.put("id_participant", participant.getParticipant().getId());
        values.put("percentage", participant.getParticipation());
        db.update("expense_participant", values, "id_expense = ?", new String[]{Long.toString(expense.getId())});
    }

    public static void removeParticipants(SQLiteDatabase db, Expense expense) {
        db.delete("expense_participant", "id_expense = ?", new String[]{Long.toString(expense.getId())});
    }

}
