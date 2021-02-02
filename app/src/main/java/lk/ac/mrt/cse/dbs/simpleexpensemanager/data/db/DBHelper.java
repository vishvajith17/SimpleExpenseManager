package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "180670C";
    private final static int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //create statement for account table
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + DBConstants.ACCOUNT_TABLE + "(" +
                    DBConstants.ACCOUNT_ACCOUNT_NO + " TEXT PRIMARY KEY," +
                    DBConstants.ACCOUNT_BANK_NAME + " TEXT NOT NULL," +
                    DBConstants.ACCOUNT_HOLDER_NAME + " TEXT NOT NULL," +
                    DBConstants.ACCOUNT_BALANCE + " REAL" +
                ");"
        );

        //create statement for transaction table
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + DBConstants.TRANSACTION_TABLE + "(" +
                        DBConstants.TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBConstants.TRANSACTION_DATE + " TEXT NOT NULL," +
                        DBConstants.TRANSACTION_ACCOUNT_NO + " TEXT NOT NULL," +
                        DBConstants.TRANSACTION_TYPE + " TEXT NOT NULL," +
                        DBConstants.TRANSACTION_AMOUNT + " REAL NOT NULL," +
                        "FOREIGN KEY (" + DBConstants.TRANSACTION_ACCOUNT_NO + ") REFERENCES "
                        + DBConstants.ACCOUNT_TABLE + "(" + DBConstants.ACCOUNT_ACCOUNT_NO + ")," +
                        "CHECK ("+DBConstants.TRANSACTION_TYPE+"==\""+DBConstants.TYPE_EXPENSE+"\" OR "+DBConstants.TRANSACTION_TYPE+"==\""+DBConstants.TYPE_INCOME+"\")"+
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBConstants.TRANSACTION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBConstants.ACCOUNT_TABLE);
        onCreate(sqLiteDatabase);
    }
}
