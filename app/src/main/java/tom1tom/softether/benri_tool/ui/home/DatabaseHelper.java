package tom1tom.softether.benri_tool.ui.home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import tom1tom.softether.benri_tool.ui.work.WorkStatus;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "entries.db"; // データベース名
    private static final int DATABASE_VERSION = 4; // データベースのバージョンを増やす

    // エントリーのテーブル（ToDoリスト用）
    private static final String TABLE_NAME_TODO = "entries";
    private static final String COLUMN_HOME_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_CONTENT = "content";

    // 勤怠管理のテーブル
    private static final String TABLE_NAME_WORK = "work_entries";
    private static final String COLUMN_WORK_ID = "_id";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_BREAK_TIME = "break_time";
    private static final String COLUMN_END_TIME = "end_time";

    // コンストラクタ
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // テーブルを作成するメソッド
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createToDoTableQuery = "CREATE TABLE " + TABLE_NAME_TODO +
                " (" + COLUMN_HOME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_CONTENT + " TEXT)";
        db.execSQL(createToDoTableQuery);

        String createWorkTableQuery = "CREATE TABLE " + TABLE_NAME_WORK +
                " (" + COLUMN_WORK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_BREAK_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT)";
        db.execSQL(createWorkTableQuery);
    }

    // データベースをアップグレードするメソッド
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME_TODO + " ADD COLUMN " + COLUMN_START_TIME + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NAME_TODO + " ADD COLUMN " + COLUMN_BREAK_TIME + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NAME_TODO + " ADD COLUMN " + COLUMN_END_TIME + " TEXT");
        }
        if (oldVersion < 3) {
            String createWorkTableQuery = "CREATE TABLE " + TABLE_NAME_WORK +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_START_TIME + " TEXT, " +
                    COLUMN_BREAK_TIME + " TEXT, " +
                    COLUMN_END_TIME + " TEXT)";
            db.execSQL(createWorkTableQuery);
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_NAME_WORK + " ADD COLUMN " + COLUMN_WORK_ID + " INTEGER");
        }
    }


    // エントリーを追加するメソッド（ToDoリスト用）
    public void insertEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, entry.getDate());
        values.put(COLUMN_CONTENT, entry.getContent());
        db.insert(TABLE_NAME_TODO, null, values);
        db.close();
    }

    // 勤怠エントリーを追加するメソッド
    public long insertWorkEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, entry.getDate());
        values.put(COLUMN_START_TIME, entry.getStartTime());
        values.put(COLUMN_BREAK_TIME, entry.getBreakTime());
        values.put(COLUMN_END_TIME, entry.getEndTime());

        long workId = db.insert(TABLE_NAME_WORK, null, values);
        db.close();
        return workId;
    }

    // エントリーの日付を更新するメソッド（ToDoリスト用）
    public void updateEntryDate(long entryId, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, newDate);
        db.update(TABLE_NAME_TODO, values, COLUMN_HOME_ID + " = ?", new String[]{String.valueOf(entryId)});
        db.close();
    }

    // エントリーの内容を更新するメソッド（ToDoリスト用）
    public void updateEntryContent(long entryId, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, newContent);
        db.update(TABLE_NAME_TODO, values, COLUMN_HOME_ID + " = ?", new String[]{String.valueOf(entryId)});
        db.close();
    }

    // エントリーを削除するメソッド（ToDoリスト用）
    public void deleteEntry(long entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_TODO, COLUMN_HOME_ID + " = ?", new String[]{String.valueOf(entryId)});
        db.close();
    }

    // 全てのエントリーを取得するメソッド（ToDoリスト用）
    public ArrayList<Entry> getAllEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_TODO, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long homeId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                entries.add(new Entry(homeId, date, content));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }


    // 全ての勤怠エントリーを取得するメソッド
    public ArrayList<Entry> getAllWorkEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_WORK, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long workId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WORK_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME));
                String breakTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREAK_TIME));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME));

                // ログで確認
                Log.d("DatabaseHelper", "Loaded Break Time: " + breakTime);

                if (breakTime != null) {
                    entries.add(new Entry(workId, date, startTime, breakTime, endTime));
                } else {
                    entries.add(new Entry(workId, date, startTime, "", endTime)); // 休憩時間がnullの場合の処理
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public void deleteWorkEntry(long workId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_WORK, COLUMN_WORK_ID + " = ?", new String[]{String.valueOf(workId)});
        db.close();
    }


    // ワークステータスを保存するメソッド
    public void saveWorkStatus(boolean isWorking, boolean isOnBreak, String startTime, String endTime, ArrayList<String[]> breakTimes) {
        // 実装内容省略
    }

    // ワークステータスを取得するメソッド
    public WorkStatus getWorkStatus() {
        // 実装内容省略
        return null;
    }

    // 休憩時間を文字列に変換するメソッド
    private String breakTimesToString(ArrayList<String[]> breakTimes) {
        StringBuilder sb = new StringBuilder();
        for (String[] breakTime : breakTimes) {
            sb.append(breakTime[0]).append("-").append(breakTime[1]).append(";");
        }
        return sb.toString();
    }

    // 文字列を休憩時間に変換するメソッド
    private ArrayList<String[]> stringToBreakTimes(String breakTimesString) {
        ArrayList<String[]> breakTimes = new ArrayList<>();
        String[] breakTimePairs = breakTimesString.split(";");
        for (String breakTimePair : breakTimePairs) {
            String[] breakTime = breakTimePair.split("-");
            if (breakTime.length == 2) {
                breakTimes.add(breakTime);
            }
        }
        return breakTimes;
    }

    // 勤怠エントリーを更新するメソッド
    public void updateWorkEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, entry.getDate());
        values.put(COLUMN_START_TIME, entry.getStartTime());
        values.put(COLUMN_BREAK_TIME, entry.getBreakTime());
        values.put(COLUMN_END_TIME, entry.getEndTime());

        db.update(TABLE_NAME_WORK, values, COLUMN_WORK_ID + " = ?", new String[]{String.valueOf(entry.getWorkId())});
        db.close();
    }

}
