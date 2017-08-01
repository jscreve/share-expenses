package com.shareexpenses.app.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by jscreve on 22/09/2014.
 */
public class ParticipantDAO {

    public static ArrayList<Participant> loadParticipants(SQLiteDatabase db, Account account) {
        //get categories
        Cursor participantsCursor = db.rawQuery("select * from participant a where a.account_id=?", new String[]{String.valueOf(account.getId())});
        ArrayList<Participant> participantsList = new ArrayList<Participant>();
        if (participantsCursor.moveToFirst()) {
            do {
                Long id = participantsCursor.getLong(participantsCursor.getColumnIndex("id"));
                String name = participantsCursor.getString(participantsCursor.getColumnIndex("firstName"));;
                String lastName = participantsCursor.getString(participantsCursor.getColumnIndex("lastName"));
                Participant participant = new Participant(name, lastName);
                participant.setName(name);
                participant.setLastName(lastName);
                participant.setId(id);
                participant.setAccount(account);
                participantsList.add(participant);
            } while (participantsCursor.moveToNext());
        }
        participantsCursor.close();
        return participantsList;
    }

    public static Participant loadParticipant(SQLiteDatabase db, Long id) {
        //get categories
        Cursor participantsCursor = db.rawQuery("select * from participant a where a.id=?", new String[]{String.valueOf(id)});
        Participant participant = null;
        if (participantsCursor.moveToFirst()) {
            String name = participantsCursor.getString(participantsCursor.getColumnIndex("firstName"));
            String lastName = participantsCursor.getString(participantsCursor.getColumnIndex("lastName"));
            Long account_id = participantsCursor.getLong(participantsCursor.getColumnIndex("account_id"));
            participant = new Participant(name, lastName);
            participant.setName(name);
            participant.setLastName(lastName);
            participant.setId(id);
        }
        participantsCursor.close();
        return participant;
    }

    public static void addParticipantToAccount(SQLiteDatabase db, Participant participant) {
        ContentValues values;
        long rowId;

        values = new ContentValues();
        //manage categories
        values.put("firstName", participant.getName());
        values.put("lastName", participant.getLastName());
        values.put("account_id", participant.getAccount().getId());
        long participantRowId = db.insert("participant", null, values);
        participant.setId(participantRowId);
    }

    public static void removeParticipant(SQLiteDatabase db, Participant participant) {
        db.delete("participant", "id=?", new String[]{Long.toString(participant.getId())});
    }
}
