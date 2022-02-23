package com.sanj.teapot.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sanj.teapot.models.Tea;

import java.util.ArrayList;
import java.util.List;

public class TeapotDb  extends SQLiteOpenHelper {
    private static volatile SQLiteDatabase database;
    private static volatile TeapotDb teapotDb;

    private TeapotDb(@Nullable Context context) {
        super(context, DbSchema.dbName, null, DbSchema.dbVersion);
    }

    public SQLiteDatabase getDatabase(){
        if (database==null){
            database=this.getWritableDatabase();
        }
        return null;
    }
    public static TeapotDb getInstance(Context context){
        if (teapotDb==null){
            teapotDb=new TeapotDb(context);
        }
        return teapotDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE "+DbSchema.tableName+" (" +
                DbSchema.columnId+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSchema.columnName+" VARCHAR, " +
                DbSchema.columnDescription+" VARCHAR, " +
                DbSchema.columnType+" VARCHAR, " +
                DbSchema.columnOrigin+" VARCHAR, " +
                DbSchema.columnIngredients+" VARCHAR, " +
                DbSchema.columnCaffeineLevel+" VARCHAR, " +
                DbSchema.columnFavorite+" VARCHAR )" ;
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(Tea tea,SQLiteDatabase teaDb) {
        ContentValues insertionValues=new ContentValues();
        insertionValues.put(DbSchema.columnName,tea.getName());
        insertionValues.put(DbSchema.columnDescription,tea.getDescription());
        insertionValues.put(DbSchema.columnType,tea.getType());
        insertionValues.put(DbSchema.columnOrigin,tea.getOrigin());
        insertionValues.put(DbSchema.columnIngredients,tea.getIngredients());
        insertionValues.put(DbSchema.columnCaffeineLevel,tea.getCaffeineLevel());
        insertionValues.put(DbSchema.columnFavorite,tea.isFavorite()?"1":"0");

        return teaDb.insert(DbSchema.tableName,null,insertionValues)>0;

    }

    public List<Tea> getAllTeas(SQLiteDatabase teaDb){
        List<Tea> teas=new ArrayList<>();
        String sql="SELECT * FROM "+DbSchema.tableName;

        @SuppressLint("Recycle") Cursor result=teaDb.rawQuery(sql,null);

        while (result.moveToNext()){
            String description,name,type,origin,ingredients,caffeineLevel;
            boolean favorite;
            int id;

            id=result.getInt(0);
            name=result.getString(1);
            description=result.getString(2);
            type=result.getString(3);
            origin=result.getString(4);
            ingredients=result.getString(5);
            caffeineLevel=result.getString(6);
            favorite = result.getString(7).equals("1");

            Tea tea= new Tea(id,description,name,type,origin,ingredients,caffeineLevel,favorite);
            teas.add(tea);
        }

        return teas;
    }
    public Tea getRandomTea(SQLiteDatabase teaDb){
        Tea tea=null;
        String sql="";

        @SuppressLint("Recycle") Cursor result=teaDb.rawQuery(sql,null);

        while (result.moveToNext()){
            String description,name,type,origin,ingredients,caffeineLevel;
            boolean favorite;
            int id;

            id=result.getInt(0);
            name=result.getString(1);
            description=result.getString(2);
            type=result.getString(3);
            origin=result.getString(4);
            ingredients=result.getString(5);
            caffeineLevel=result.getString(6);
            favorite = result.getString(7).equals("1");

            tea= new Tea(id,description,name,type,origin,ingredients,caffeineLevel,favorite);
        }
        return tea;
    }

    public Tea getRecentlyAddedTea(SQLiteDatabase teaDb){
        Tea tea=null;
        String sql="SELECT * FROM "+DbSchema.tableName+" ORDER BY "+DbSchema.columnId+" DESC LIMIT 1";

        @SuppressLint("Recycle") Cursor result=teaDb.rawQuery(sql,null);

        while (result.moveToNext()){
            String description,name,type,origin,ingredients,caffeineLevel;
            boolean favorite;

            int id;

            id=result.getInt(0);
            name=result.getString(1);
            description=result.getString(2);
            type=result.getString(3);
            origin=result.getString(4);
            ingredients=result.getString(5);
            caffeineLevel=result.getString(6);
            favorite = result.getString(7).equals("1");

            tea= new Tea(id,description,name,type,origin,ingredients,caffeineLevel,favorite);
        }
        return tea;
    }

    public boolean deleteTea(int teaId,SQLiteDatabase teaDb){
        String[] args=new String[]{String.valueOf(teaId)};
        int result=teaDb.delete(DbSchema.tableName,DbSchema.columnId+"=?",args);
        return result>0;
    }

    public Tea findTeaById(int teaId,SQLiteDatabase teaDb){
        Tea tea=null;
        String sql="SELECT * FROM "+DbSchema.tableName+" WHERE "+DbSchema.columnId+" = '"+teaId+"'";

        @SuppressLint("Recycle") Cursor result=teaDb.rawQuery(sql,null);

        while (result.moveToNext()){
            String description,name,type,origin,ingredients,caffeineLevel;
            boolean favorite;
            int id;

            id=result.getInt(0);
            name=result.getString(1);
            description=result.getString(2);
            type=result.getString(3);
            origin=result.getString(4);
            ingredients=result.getString(5);
            caffeineLevel=result.getString(6);
            favorite = result.getString(7).equals("1");

            tea= new Tea(id,description,name,type,origin,ingredients,caffeineLevel,favorite);
        }
        return null;
    }
    public boolean updateFavoriteTea(int teaId,boolean isFavorite,SQLiteDatabase teaDb){
        String[] args=new String[]{String.valueOf(teaId)};
        String favoriteCode=isFavorite?"1":"0";
        ContentValues updateValues=new ContentValues();
        updateValues.put(DbSchema.columnFavorite,favoriteCode);
        int result=teaDb.update(DbSchema.tableName,updateValues,DbSchema.columnId+"=?",args);
        return result>0;
    }

}