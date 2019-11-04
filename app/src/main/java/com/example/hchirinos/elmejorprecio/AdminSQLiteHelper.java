package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteHelper extends SQLiteOpenHelper {

    public static final int VERSION = 7;

    public AdminSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table compras (idProducto varchar primary key, cantidad int)");
        db.execSQL("create table tiendas (idTienda varchar primary key, sucursal varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS compras");
        db.execSQL("DROP TABLE IF EXISTS tiendas");
        onCreate(db);
    }
}
