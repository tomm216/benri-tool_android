package tom1tom.softether.benri_tool.ui.home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "entries.db"; // データベース名
    private static final int DATABASE_VERSION = 1; // データベースのバージョン
    private static final String TABLE_NAME = "entries"; // エントリーのテーブル名
    private static final String COLUMN_DATE = "date"; // 日付列
    private static final String COLUMN_CONTENT = "content"; // 内容列

    // コンストラクタ
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // テーブルを作成するメソッド
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_CONTENT + " TEXT)";
        db.execSQL(createTableQuery);
    }

    // データベースをアップグレードするメソッド
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // エントリーを追加するメソッド
    public void insertEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, entry.getDate());
        values.put(COLUMN_CONTENT, entry.getContent());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // エントリーの日付を更新するメソッド
    public void updateEntryDate(long entryId, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, newDate);
        db.update(TABLE_NAME, values, "_id = ?", new String[]{String.valueOf(entryId)});
        db.close();
    }

    // エントリーの内容を更新するメソッド
    public void updateEntryContent(long entryId, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, newContent);
        db.update(TABLE_NAME, values, "_id = ?", new String[]{String.valueOf(entryId)});
        db.close();
    }

    // エントリーを削除するメソッド
    public void deleteEntry(long entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "_id = ?", new String[]{String.valueOf(entryId)});
        db.close();
    }

    // 全てのエントリーを取得するメソッド
    public ArrayList<Entry> getAllEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                entries.add(new Entry(id, date, content));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }
}
