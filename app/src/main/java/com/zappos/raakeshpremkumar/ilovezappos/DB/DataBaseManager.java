package com.zappos.raakeshpremkumar.ilovezappos.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.util.ArrayList;

/**
 * Created by raakeshpremkumar on 1/29/17.
 */

public class DataBaseManager extends SQLiteOpenHelper {

    private String createTables[] = {DataBaseQuery.CREATE_TABLE_PRODUCT_DETAILS};
    private static String databaseName = "ILOVEZAPPOS";
    private static int dataBaseVersion=1;
    private static Context context=null;
    private static SQLiteDatabase sqliteDatabase=null;
    private static DataBaseManager databaseManagerInstance=null;

    public DataBaseManager(Context context){
        super(context, DataBaseManager.databaseName, null, DataBaseManager.dataBaseVersion);
    }

    public DataBaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (String createTable : createTables){
            sqLiteDatabase.execSQL(createTable);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public synchronized static SQLiteDatabase getDatabase(){
        if(sqliteDatabase==null){
            sqliteDatabase=DataBaseManager.getInstance(DataBaseManager.context).getWritableDatabase();
        }
        return DataBaseManager.sqliteDatabase;
    }

    public synchronized static void setContext(Context context){
        DataBaseManager.context=context;
    }

    public static DataBaseManager getInstance(Context context){
        if(databaseManagerInstance==null && DataBaseManager.context!=null){
            databaseManagerInstance=new DataBaseManager(DataBaseManager.context);
        }
        return databaseManagerInstance;
    }

    public synchronized void insertProductDetails(ContentValues contentValues){
        Cursor cursor=getDatabase().rawQuery("SELECT * FROM " + DataBaseQuery.TABLE_PRODUCT_DETAILS + " WHERE " +
                DataBaseQuery.PRODUCT_ID + " =? ", new String[]{String.valueOf(contentValues.get(DataBaseQuery.PRODUCT_ID))});
        if(cursor.getCount()>0){
            long update_result=getDatabase().update(DataBaseQuery.TABLE_PRODUCT_DETAILS, contentValues,
                    DataBaseQuery.PRODUCT_ID+"=?", new String[]{String.valueOf(contentValues.get(DataBaseQuery.PRODUCT_ID))});
        }
        else{
            long insert_result=getDatabase().insert(DataBaseQuery.TABLE_PRODUCT_DETAILS, null, contentValues);
        }
    }

    public synchronized <T> ArrayList<T> retrieveTablerows(String tableName, String where, String[] args){
        ArrayList<T> table_rows=new ArrayList<T>();
        Cursor cursor=null;

        try{
            if(where==null){
                cursor=getDatabase().rawQuery("SELECT * FROM " + tableName, null); //NO I18N
            }
            else{
                cursor=getDatabase().rawQuery("SELECT * FROM "+tableName+" WHERE "+where+"=?",args); //NO I18N
            }
            table_rows=retrieve_rows(tableName,cursor);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return table_rows;
    }

    private synchronized <T> ArrayList<T> retrieve_rows(String tableName, Cursor cursor){
        ArrayList<T> rows=new ArrayList<T>();
        if(cursor.moveToFirst()){
            do{
                Products pojo_object=new Products();
                pojo_object.setProductId(cursor.getInt(1));
                pojo_object.setBrandName(cursor.getString(2));
                pojo_object.setThumbnailImageUrl(cursor.getString(3));
                pojo_object.setOriginalPrice(cursor.getString(4));
                pojo_object.setStyleId(Long.parseLong(cursor.getString(5)));
                pojo_object.setColorId(Long.parseLong(cursor.getString(6)));
                pojo_object.setPrice(cursor.getString(7));
                pojo_object.setPercentOff(cursor.getString(8));
                pojo_object.setProductUrl(cursor.getString(9));
                pojo_object.setProductName(cursor.getString(10));
                rows.add((T)pojo_object);
            }while(cursor.moveToNext());
        }
        return rows;
    }

}
