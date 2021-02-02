package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

public class DBConstants {

    //table names
    public final static String ACCOUNT_TABLE = "account_table";
    public final static String TRANSACTION_TABLE = "transaction_table";

    //column names related to account table
    public final static String ACCOUNT_ACCOUNT_NO = "account_no";
    public final static String ACCOUNT_BANK_NAME = "bank_name";
    public final static String ACCOUNT_HOLDER_NAME = "holder_name";
    public final static String ACCOUNT_BALANCE = "balance";

    //column names related to transaction table
    public final static String TRANSACTION_ID = "id";
    public final static String TRANSACTION_ACCOUNT_NO = "account_no";
    public final static String TRANSACTION_TYPE = "type";
    public final static String TRANSACTION_DATE = "date";
    public final static String TRANSACTION_AMOUNT = "amount";

    //the two types of transactions
    public final static String TYPE_EXPENSE = "EXPENSE";
    public final static String TYPE_INCOME = "INCOME";
}
