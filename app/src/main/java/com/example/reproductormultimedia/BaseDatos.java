package com.example.reproductormultimedia;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;
public class BaseDatos extends  SQLiteOpenHelper {

        public static final int DB_VERSION = 1;
        public static final String DB_NOMBRE = "BASEDATOS_REPRODUCTORMULTI";
        public static final String TABLA_USUARIOS = "CREATE TABLE USUARIOS( id INTEGER PRIMARY KEY AUTOINCREMENT ,nombre TEXT NOT NULL UNIQUE,avatar BLOB,fechaNac TEXT,sexo TEXT NOT NULL)";
        public static final String TABLA_CANCIONES = "CREATE TABLE CANCIONES( id INTEGER PRIMARY KEY AUTOINCREMENT ,titulo TEXT NOT NULL, nombreArtista TEXT NOT NULL)";
        public static final String TABLA_USUARIO_CANCION = "CREATE TABLE USUARIO_CANCION (" +
            "id_usuario_cancion INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "id_usuario INTEGER NOT NULL," +
            "id_cancion INTEGER NOT NULL," +
            " FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE," +
            " FOREIGN KEY (id_cancion) REFERENCES canciones(id_cancion) ON DELETE CASCADE ON UPDATE CASCADE)";

        public BaseDatos(@Nullable Context context) {
            super(context, DB_NOMBRE, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase BD) {

            BD.execSQL(TABLA_USUARIOS);
            BD.execSQL(TABLA_CANCIONES);
            BD.execSQL(TABLA_USUARIO_CANCION);


        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USUARIOS");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS CANCIONES");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TABLA_USUARIO_CANCION");
            onCreate(sqLiteDatabase);


        }

    }

