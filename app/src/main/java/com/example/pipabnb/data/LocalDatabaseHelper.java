package com.example.pipabnb.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocalDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pipabnb_local.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_FAVORITES = "favoritos";
    private static final String COLUMN_USER_ID = "usuario_id";
    private static final String COLUMN_ACCOMMODATION_ID = "alojamiento_id";
    private static final String TABLE_SEARCHES = "busquedas_recientes";
    private static final String COLUMN_SEARCH_TEXT = "texto";
    private static final String COLUMN_SEARCH_DATE = "fecha";
    private final Context context;

    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_USER_ID + " TEXT NOT NULL, " +
                COLUMN_ACCOMMODATION_ID + " TEXT NOT NULL, " +
                "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_ACCOMMODATION_ID + "))");
        db.execSQL("CREATE TABLE " + TABLE_SEARCHES + " (" +
                COLUMN_USER_ID + " TEXT NOT NULL, " +
                COLUMN_SEARCH_TEXT + " TEXT NOT NULL, " +
                COLUMN_SEARCH_DATE + " INTEGER NOT NULL, " +
                "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_SEARCH_TEXT + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String userId = "datos_anteriores";
            db.execSQL("CREATE TABLE favoritos_nuevos (" +
                    COLUMN_USER_ID + " TEXT NOT NULL, " +
                    COLUMN_ACCOMMODATION_ID + " TEXT NOT NULL, " +
                    "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_ACCOMMODATION_ID + "))");
            db.execSQL("INSERT INTO favoritos_nuevos (" + COLUMN_USER_ID + ", " +
                            COLUMN_ACCOMMODATION_ID + ") SELECT ?, " +
                            COLUMN_ACCOMMODATION_ID + " FROM " + TABLE_FAVORITES,
                    new Object[]{userId});
            db.execSQL("DROP TABLE " + TABLE_FAVORITES);
            db.execSQL("ALTER TABLE favoritos_nuevos RENAME TO " + TABLE_FAVORITES);

            db.execSQL("CREATE TABLE busquedas_nuevas (" +
                    COLUMN_USER_ID + " TEXT NOT NULL, " +
                    COLUMN_SEARCH_TEXT + " TEXT NOT NULL, " +
                    COLUMN_SEARCH_DATE + " INTEGER NOT NULL, " +
                    "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_SEARCH_TEXT + "))");
            db.execSQL("INSERT INTO busquedas_nuevas (" + COLUMN_USER_ID + ", " +
                            COLUMN_SEARCH_TEXT + ", " + COLUMN_SEARCH_DATE + ") SELECT ?, " +
                            COLUMN_SEARCH_TEXT + ", " + COLUMN_SEARCH_DATE + " FROM " + TABLE_SEARCHES,
                    new Object[]{userId});
            db.execSQL("DROP TABLE " + TABLE_SEARCHES);
            db.execSQL("ALTER TABLE busquedas_nuevas RENAME TO " + TABLE_SEARCHES);
        }
    }

    public boolean isFavorite(String accommodationId) {
        if (accommodationId == null || accommodationId.trim().isEmpty()) return false;
        String userId = getCurrentUserId();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_FAVORITES, new String[]{COLUMN_ACCOMMODATION_ID},
                COLUMN_USER_ID + " = ? AND " + COLUMN_ACCOMMODATION_ID + " = ?",
                new String[]{userId, accommodationId},
                null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public void setFavorite(String accommodationId, boolean favorite) {
        if (accommodationId == null || accommodationId.trim().isEmpty()) return;
        String userId = getCurrentUserId();
        SQLiteDatabase db = getWritableDatabase();
        if (favorite) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_ACCOMMODATION_ID, accommodationId);
            db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        } else {
            db.delete(TABLE_FAVORITES,
                    COLUMN_USER_ID + " = ? AND " + COLUMN_ACCOMMODATION_ID + " = ?",
                    new String[]{userId, accommodationId});
        }
    }

    public void saveRecentSearch(String text) {
        String normalizedText = text == null ? "" : text.trim();
        if (normalizedText.isEmpty()) return;
        String userId = getCurrentUserId();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SEARCH_TEXT, normalizedText);
        values.put(COLUMN_SEARCH_DATE, System.currentTimeMillis());
        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(TABLE_SEARCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.delete(TABLE_SEARCHES,
                COLUMN_USER_ID + " = ? AND " + COLUMN_SEARCH_TEXT +
                        " NOT IN (SELECT " + COLUMN_SEARCH_TEXT + " FROM " + TABLE_SEARCHES +
                        " WHERE " + COLUMN_USER_ID + " = ? ORDER BY " +
                        COLUMN_SEARCH_DATE + " DESC LIMIT 5)",
                new String[]{userId, userId});
    }

    public List<String> getRecentSearches(int limit) {
        ArrayList<String> searches = new ArrayList<>();
        String userId = getCurrentUserId();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_SEARCHES, new String[]{COLUMN_SEARCH_TEXT},
                COLUMN_USER_ID + " = ?", new String[]{userId},
                null, null, COLUMN_SEARCH_DATE + " DESC", String.valueOf(limit))) {
            while (cursor.moveToNext()) searches.add(cursor.getString(0));
        }
        return searches;
    }

    private String getCurrentUserId() {
        return context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
                .getString("id", "sin_usuario");
    }
}
