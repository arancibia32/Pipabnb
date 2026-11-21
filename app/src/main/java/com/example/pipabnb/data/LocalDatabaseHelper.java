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
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVORITES = "favoritos";
    private static final String COLUMN_ACCOMMODATION_ID = "alojamiento_id";
    private static final String TABLE_SEARCHES = "busquedas_recientes";
    private static final String COLUMN_SEARCH_TEXT = "texto";
    private static final String COLUMN_SEARCH_DATE = "fecha";

    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ACCOMMODATION_ID + " TEXT PRIMARY KEY)");
        db.execSQL("CREATE TABLE " + TABLE_SEARCHES + " (" +
                COLUMN_SEARCH_TEXT + " TEXT PRIMARY KEY, " +
                COLUMN_SEARCH_DATE + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHES);
        onCreate(db);
    }

    public boolean isFavorite(String accommodationId) {
        if (accommodationId == null || accommodationId.trim().isEmpty()) return false;
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_FAVORITES, new String[]{COLUMN_ACCOMMODATION_ID},
                COLUMN_ACCOMMODATION_ID + " = ?", new String[]{accommodationId},
                null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public void setFavorite(String accommodationId, boolean favorite) {
        if (accommodationId == null || accommodationId.trim().isEmpty()) return;
        SQLiteDatabase db = getWritableDatabase();
        if (favorite) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ACCOMMODATION_ID, accommodationId);
            db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        } else {
            db.delete(TABLE_FAVORITES, COLUMN_ACCOMMODATION_ID + " = ?",
                    new String[]{accommodationId});
        }
    }

    public void saveRecentSearch(String text) {
        String normalizedText = text == null ? "" : text.trim();
        if (normalizedText.isEmpty()) return;

        ContentValues values = new ContentValues();
        values.put(COLUMN_SEARCH_TEXT, normalizedText);
        values.put(COLUMN_SEARCH_DATE, System.currentTimeMillis());
        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(TABLE_SEARCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.execSQL("DELETE FROM " + TABLE_SEARCHES + " WHERE " + COLUMN_SEARCH_TEXT +
                " NOT IN (SELECT " + COLUMN_SEARCH_TEXT + " FROM " + TABLE_SEARCHES +
                " ORDER BY " + COLUMN_SEARCH_DATE + " DESC LIMIT 5)");
    }

    public List<String> getRecentSearches(int limit) {
        ArrayList<String> searches = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_SEARCHES, new String[]{COLUMN_SEARCH_TEXT}, null, null,
                null, null, COLUMN_SEARCH_DATE + " DESC", String.valueOf(limit))) {
            while (cursor.moveToNext()) searches.add(cursor.getString(0));
        }
        return searches;
    }
}
