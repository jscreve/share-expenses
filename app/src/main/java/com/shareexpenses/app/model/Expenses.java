package com.shareexpenses.app.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.shareexpenses.app.SQLiteAssetHelper;

/**
 * Created by jess on 17/09/2014.
 */
public class Expenses {

    private boolean dataLoaded;

    private Context context;
    private SQLiteAssetHelper databaseHelper;

    public Expenses(Context context)
    {
        dataLoaded=false;
        this.context = context;
        //databaseHelper=new SQLiteAssetHelper(context,"expenses",null,1);
        //loadFromDB();
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void setDataLoaded(boolean dataLoaded) {
        this.dataLoaded = dataLoaded;
    }

    public void loadFromDB()
    {
        SQLiteDatabase db;

        db=databaseHelper.getReadableDatabase();
        //allChevaux=ChevalDAO.loadChevaux(db);
        //setDataLoaded(allChevaux.size()>0);
    }

    public void saveToDB()
    {
        SQLiteDatabase db;

        db=databaseHelper.getWritableDatabase();
        try
        {
            db.beginTransaction();
            //ChevalDAO.removeAllChevaux(db);
            //ChevalDAO.saveChevaux(db,allChevaux);
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }


}
