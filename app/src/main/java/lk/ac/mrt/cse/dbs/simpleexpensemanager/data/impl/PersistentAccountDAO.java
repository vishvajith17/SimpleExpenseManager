package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBConstants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private DBHelper dbHelper;

    public PersistentAccountDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //query to get all account numbers from account table
        Cursor cursor = db.rawQuery(
                "SELECT " + DBConstants.ACCOUNT_ACCOUNT_NO + " FROM " + DBConstants.ACCOUNT_TABLE,
                null
        );

        ArrayList<String> accountNumbersList = new ArrayList<>();

        //loop through the results and add to the list
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNumbersList.add(cursor.getString(0));
        }

        cursor.close();
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //query to select all rows of account table
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DBConstants.ACCOUNT_TABLE,
                null
        );

        ArrayList<Account> accountsList = new ArrayList<>();

        //loop through the results and create account objects and add to list
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
            accountsList.add(account);
        }

        cursor.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //query to get the row from the account table with relevant account number
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DBConstants.ACCOUNT_TABLE + " WHERE " + DBConstants.ACCOUNT_ACCOUNT_NO + "=?;"
                , new String[]{accountNo});

        //if a result exist create an account object else throw error
        Account account;
        if (cursor != null && cursor.moveToFirst()) {
            account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
        } else {
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account){

        //check if an already an account with this no exist
        Account alreadyExistingAccount = null;
        try {
            alreadyExistingAccount = getAccount(account.getAccountNo());
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        if (alreadyExistingAccount!=null){
            System.out.println("Account already exists.");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentvalues = new ContentValues();
        contentvalues.put(DBConstants.ACCOUNT_ACCOUNT_NO, account.getAccountNo());
        contentvalues.put(DBConstants.ACCOUNT_BANK_NAME, account.getBankName());
        contentvalues.put(DBConstants.ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        contentvalues.put(DBConstants.ACCOUNT_BALANCE, account.getBalance());

        //insert new row to account table
        db.insert(DBConstants.ACCOUNT_TABLE, null, contentvalues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //query for the account to check whether such an account exist
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DBConstants.ACCOUNT_TABLE + " WHERE " + DBConstants.ACCOUNT_ACCOUNT_NO + "=?;"
                , new String[]{accountNo}
                );

        //if such one exist remove it else throw exception
        if (cursor.moveToFirst()) {
            db.delete(
                    DBConstants.ACCOUNT_TABLE,
                    DBConstants.ACCOUNT_ACCOUNT_NO + " = ?",
                    new String[]{accountNo}
                    );
        } else {
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid");
        }
        cursor.close();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //if no account is specified to update
        if (accountNo==null) {
            throw new InvalidAccountException("Account is not specified.");
        }

        //get the account related using getAccount method in this call
        Account account = this.getAccount(accountNo);

        //if such account exist
        if (account != null) {
            double updatedBalance;

            //update the balance of account according to transaction type
            if (expenseType == ExpenseType.INCOME) {
                updatedBalance = account.getBalance() + amount;
            } else if (expenseType == ExpenseType.EXPENSE) {
                updatedBalance = account.getBalance() - amount;
            } else {
                throw new InvalidAccountException("Invalid Expense Type");
            }

            //if the account does not have enough balance throw error
            if (updatedBalance < 0){
                throw  new InvalidAccountException("Balance of " + account.getBalance() + " is insufficient for the transaction.");
            }

            // if ok query to update the balance in the account table
            db.execSQL(
                    "UPDATE " + DBConstants.ACCOUNT_TABLE +
                            " SET " + DBConstants.ACCOUNT_BALANCE + " = ?" +
                            " WHERE " + DBConstants.ACCOUNT_ACCOUNT_NO + " = ?",
                    new String[]{Double.toString(updatedBalance), accountNo});

        } else { //if such account does not exist throw error
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid");
        }



    }
}
