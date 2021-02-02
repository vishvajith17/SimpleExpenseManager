package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBConstants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private DBHelper dbHelper;
    private DateFormat dateFormat;

    public PersistentTransactionDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db= dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.TRANSACTION_DATE, this.dateFormat.format(date));
        contentValues.put(DBConstants.TRANSACTION_ACCOUNT_NO, accountNo);
        contentValues.put(DBConstants.TRANSACTION_TYPE, expenseType.toString());
        contentValues.put(DBConstants.TRANSACTION_AMOUNT, amount);

        //insert new row to transaction table
        db.insert(DBConstants.TRANSACTION_TABLE, null, contentValues);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //query to get all transactions ordered by date newest at top
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DBConstants.TRANSACTION_TABLE + " ORDER BY " + DBConstants.TRANSACTION_DATE + " DESC ",
                null
        );

        ArrayList<Transaction> transactionsList = new ArrayList<>();

        //loop and add transactions to the list creating transaction objects
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {

                Transaction transaction = new Transaction(
                        this.dateFormat.parse(cursor.getString(1)),
                        cursor.getString(2),
                        ExpenseType.valueOf(cursor.getString(3).toUpperCase()),
                        cursor.getDouble(4)
                );

                transactionsList.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return transactionsList;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //select limited number of rows from transaction table
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DBConstants.TRANSACTION_TABLE + " ORDER BY " + DBConstants.TRANSACTION_DATE + " DESC " +
                " LIMIT ?;"
                , new String[]{Integer.toString(limit)}
                );


        ArrayList<Transaction> transactionsList = new ArrayList<>();

        //loop and add transactions to the list creating transaction objects
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {

                Transaction transaction = new Transaction(
                        this.dateFormat.parse(cursor.getString(1)),
                        cursor.getString(2),
                        ExpenseType.valueOf(cursor.getString(3).toUpperCase()),
                        cursor.getDouble(4)
                );

                transactionsList.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return transactionsList;

    }
}
