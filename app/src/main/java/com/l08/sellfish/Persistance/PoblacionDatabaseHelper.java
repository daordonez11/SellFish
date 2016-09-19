package com.l08.sellfish.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.l08.sellfish.Models.Pez;
import com.l08.sellfish.Models.Poblacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielordonez on 9/18/16.
 */
public class PoblacionDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DATABASELOG";
    private static PoblacionDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "poblacionDatabase";
    private static final int DATABASE_VERSION = 2;


    // Table Names
    private static final String TABLE_POBLACION = "poblacion";
    private static final String TABLE_PEZ = "pez";

    // Poblacion Table Columns
    private static final String KEY_POBLACION_ID = "id";

    private static final String KEY_POBLACION_ESPECIE = "especie";
    private static final String KEY_POBLACION_TAMAÑO = "tamano";
    private static final String KEY_POBLACION_ESTANQUE = "estanque";
    private static final String KEY_POBLACION_PERIODICIDAD= "periodicidad";

    // Pez Table Columns
    private static final String KEY_PEZ_ID = "id";
    private static final String KEY_PEZ_LENGTH = "pezLength";
    private static final String KEY_PEZ_WEIGHT = "pezWeight";
    private static final String KEY_PEZ_SEMANA = "semana";
    private static final String KEY_PEZ_IMAGE = "picture";
    private static final String KEY_POBLACION_PEZ_ID_FK = "poblacionId";


    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POBLACION_TABLE = "CREATE TABLE " + TABLE_POBLACION +
                "(" +
                KEY_POBLACION_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_POBLACION_ESPECIE + " TEXT," +
                KEY_POBLACION_TAMAÑO + " INTEGER," +
                KEY_POBLACION_ESTANQUE + " TEXT," +
                KEY_POBLACION_PERIODICIDAD + " TEXT" +
                ")";

        String CREATE_PEZ_TABLE = "CREATE TABLE " + TABLE_PEZ +
                "(" +
                KEY_PEZ_ID + " INTEGER PRIMARY KEY," +
                KEY_PEZ_LENGTH + " REAL," +
                KEY_PEZ_WEIGHT+ " REAL," +
                KEY_PEZ_IMAGE + " BLOB," +
                KEY_PEZ_SEMANA + " INTEGER,"+
        KEY_POBLACION_PEZ_ID_FK + " INTEGER REFERENCES " + TABLE_POBLACION + // Define a foreign key
                ")";

        db.execSQL(CREATE_POBLACION_TABLE);
        db.execSQL(CREATE_PEZ_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POBLACION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEZ);
            onCreate(db);
        }
    }
    public static synchronized PoblacionDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new PoblacionDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private PoblacionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Query poblaciones
     * @return
     */
    public List<Poblacion> getAllPopulations() {
        List<Poblacion> poblacion = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String POBLACION_QUERY =
                String.format("SELECT * FROM %s ",
                        TABLE_POBLACION);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POBLACION_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Poblacion newPoblacion = new Poblacion();
                    newPoblacion.id= cursor.getLong(cursor.getColumnIndex(KEY_POBLACION_ID));
                    newPoblacion.especie= cursor.getString(cursor.getColumnIndex(KEY_POBLACION_ESPECIE));
                    newPoblacion.estanque= cursor.getString(cursor.getColumnIndex(KEY_POBLACION_ESTANQUE));
                    newPoblacion.periodicidad= cursor.getString(cursor.getColumnIndex(KEY_POBLACION_PERIODICIDAD));
                    newPoblacion.tamaño= cursor.getInt(cursor.getColumnIndex(KEY_POBLACION_TAMAÑO));


                    poblacion.add(newPoblacion);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get populations from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return poblacion;
    }
    // Insert a fish into the database
    public void addPez(Pez pez, long poblacionId) {
        Log.d(TAG, "LOOK AT THIS: --->"+pez.imagen+pez.longitud+pez.semana);

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).

            ContentValues values = new ContentValues();
            values.put(KEY_POBLACION_PEZ_ID_FK, poblacionId);
            values.put(KEY_PEZ_LENGTH, pez.longitud);
            values.put(KEY_PEZ_IMAGE, pez.imagen);
            values.put(KEY_PEZ_SEMANA, pez.semana);
            values.put(KEY_PEZ_WEIGHT, pez.peso);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_PEZ, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {

            db.endTransaction();
        }
    }

    // Insert or update a population in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // population already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addPoblacion(Poblacion poblacion) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long poblacionId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_POBLACION_ESPECIE, poblacion.especie);
            values.put(KEY_POBLACION_ESTANQUE, poblacion.estanque);
            values.put(KEY_POBLACION_PERIODICIDAD, poblacion.periodicidad );
            values.put(KEY_POBLACION_TAMAÑO, poblacion.tamaño );
                poblacionId = db.insertOrThrow(TABLE_POBLACION, null, values);
                db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return poblacionId;
    }
    public long deletePoblacion(long idPoblacion)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+TABLE_PEZ+" where "+KEY_POBLACION_PEZ_ID_FK+"=\'"+idPoblacion+"\'");
        db.execSQL("delete from  " + TABLE_POBLACION +" where id=\'" + idPoblacion+"\'" );
        return idPoblacion;
    }
    public int updatePesoPez(Pez pez) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PEZ_WEIGHT, pez.peso);

        // Updating profile picture url for user with that userName
        return db.update(TABLE_PEZ, values, KEY_PEZ_ID + " = ?",
                new String[] { String.valueOf(pez.id) });
    }


    public List<Pez> getFishesFromPopulation(long poblacionId) {
        List<Pez> poblacion = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
//
        String POBLACION_QUERY =
                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s WHERE %s.%s = "+poblacionId+" ORDER BY semana ASC",
                        TABLE_PEZ,
                        TABLE_POBLACION,
                        TABLE_PEZ, KEY_POBLACION_PEZ_ID_FK,
                        TABLE_POBLACION, KEY_POBLACION_ID,
                        TABLE_POBLACION,KEY_POBLACION_ID);
//        String POBLACION_QUERY =
//                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
//                        TABLE_PEZ,
//                        TABLE_POBLACION,
//                        TABLE_PEZ, KEY_POBLACION_PEZ_ID_FK,
//                        TABLE_POBLACION, KEY_POBLACION_ID
//                       );
        System.out.println(POBLACION_QUERY);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POBLACION_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Poblacion newPoblacion = new Poblacion();
                    newPoblacion.id= cursor.getLong(cursor.getColumnIndex(KEY_POBLACION_ID));
                    newPoblacion.especie= cursor.getString(cursor.getColumnIndex(KEY_POBLACION_ESPECIE));
                    newPoblacion.tamaño= cursor.getInt(cursor.getColumnIndex(KEY_POBLACION_TAMAÑO));

                    Pez newPez = new Pez();
                    newPez.id = cursor.getLong(cursor.getColumnIndex(KEY_PEZ_WEIGHT));
                    newPez.poblacion = newPoblacion;
                    newPez.longitud = cursor.getDouble(cursor.getColumnIndex(KEY_PEZ_LENGTH));
                    newPez.semana = cursor.getInt(cursor.getColumnIndex(KEY_PEZ_SEMANA));
                    newPez.imagen = cursor.getBlob(cursor.getColumnIndex(KEY_PEZ_IMAGE));


                    poblacion.add(newPez);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get populations from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return poblacion;
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_PEZ, null, null);
            db.delete(TABLE_POBLACION, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }
}