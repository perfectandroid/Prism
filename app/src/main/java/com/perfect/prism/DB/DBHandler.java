package com.perfect.prism.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.perfect.prism.Model.AgentDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "prismManager";

    private static final String TABLE_LocationPeport = "LocationPeport";
    private static final String KEY_AgentName= "AgentName";
    private static final String KEY_ID_Client = "ID_Client";
    private static final String KEY_Site = "Site";
    private static final String KEY_Location= "Location";
    private static final String KEY_AssignedTicket= "AssignedTicket";
    private static final String KEY_SoftwarePending = "SoftwarePending";
    private static final String KEY_ClosedTicket = "ClosedTicket";
    private static final String KEY_Balance= "Balance";
    private static final String KEY_ID_Agent = "ID_Agent";

     public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRISM_TABLE = "CREATE TABLE " + TABLE_LocationPeport + "("
                + KEY_AgentName + " TEXT,"
                + KEY_ID_Client + " TEXT,"
                + KEY_Site + " TEXT,"
                + KEY_Location + " TEXT,"
                + KEY_AssignedTicket + " TEXT,"
                + KEY_SoftwarePending + " TEXT,"
                + KEY_ClosedTicket + " TEXT,"
                + KEY_Balance + " TEXT,"
                + KEY_ID_Agent + " TEXT"
                + ")";
        db.execSQL(CREATE_PRISM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LocationPeport);
        onCreate(db);
    }

    /**
     * Generic method to insert values to a table.
     *
     * @param tableName     of the table to be inserted.
     * @param contentValues containing table values.
     * @return inserted row id of the table.
     */
    public long insert(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(tableName, null, contentValues);
    }

    public void update(ContentValues cv, String tableName,
                       String fieldName, String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long s = db.update(tableName, cv, fieldName + "=" + ID, null);
    }

    public Cursor select(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM '" + tableName + "'", null);
        return cursor;
    }

    public Cursor selectc(String tableName, String iditem) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName +" WHERE ID_Items = " + iditem +"", null);
        return cursor;
    }

    public boolean updateCart (Integer id, String Count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Count", Count);
        db.update("cart", contentValues, "ID_Items = ? ", new String[] { Integer.toString(id) } );
        return true;
    }



    public void addLocationReport(AgentDetailsModel cart) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues civalues = new ContentValues();
        civalues.put(KEY_AgentName, cart.getAgentName());
        civalues.put(KEY_ID_Client, cart.getID_Client());
        civalues.put(KEY_Site, cart.getSite());
        civalues.put(KEY_Location, cart.getLocation());
        civalues.put(KEY_AssignedTicket, cart.getAssignedTicket());
        civalues.put(KEY_SoftwarePending, cart.getSoftwarePending());
        civalues.put(KEY_ClosedTicket, cart.getClosedTicket());
        civalues.put(KEY_Balance, cart.getBalance());
        civalues.put(KEY_ID_Agent, cart.getID_Agent());
        db.insert(TABLE_LocationPeport, null, civalues);
        db.close();
    }

    public List<AgentDetailsModel> getAllLocationReport(String ID_Client) {
        List<AgentDetailsModel> locationreportList = new ArrayList<AgentDetailsModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LocationPeport +" WHERE ID_Client = "+ID_Client/* +""*/, null);
        if (cursor.moveToFirst()) {
            do {
                AgentDetailsModel locationreport = new AgentDetailsModel();
                locationreport.setAgentName(cursor.getString(0));
                locationreport.setID_Client(cursor.getString(1));
                locationreport.setSite(cursor.getString(2));
                locationreport.setLocation(cursor.getString(3));
                locationreport.setAssignedTicket(cursor.getString(4));
                locationreport.setSoftwarePending(cursor.getString(5));
                locationreport.setClosedTicket(cursor.getString(6));
                locationreport.setBalance(cursor.getString(7));
                locationreport.setID_Agent(cursor.getString(8));
                locationreportList.add(locationreport);
            } while (cursor.moveToNext());
        }
        return locationreportList;
    }

    public boolean clearall() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM LocationPeport");
        db.close();
        return true;
    }

}
