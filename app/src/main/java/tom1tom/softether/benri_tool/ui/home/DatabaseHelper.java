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
    private static final int DATABASE_VERSION = 5; // データベースのバージョンを増やす

    // エントリーのテーブル（ToDoリスト用）
    private static final String TABLE_NAME_TODO = "entries";
    private static final String COLUMN_HOME_ID = "_id";
    private static final String COLUMN_TODO_DATE = "date"; // 名前変更
    private static final String COLUMN_CONTENT = "content";

    // 勤怠管理のテーブル
    private static final String TABLE_NAME_WORK = "work_entries";
    private static final String COLUMN_WORK_ID = "_id";
    private static final String COLUMN_WORK_DATE = "date"; // 名前変更
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_BREAK_TIME = "break_time";
    private static final String COLUMN_END_TIME = "end_time";

    // ワークステータステーブルのカラム名
    private static final String TABLE_NAME_WORK_STATUS = "work_status";
    private static final String COLUMN_WORK_STATUS_ID = "id";
    private static final String COLUMN_IS_WORKING = "is_working";
    private static final String COLUMN_IS_ON_BREAK = "is_on_break";
    private static final String COLUMN_WORK_STATUS_START_TIME = "start_time";
    private static final String COLUMN_WORK_STATUS_END_TIME = "end_time";
    private static final String COLUMN_BREAK_TIMES = "break_times";
    private static final String COLUMN_WORK_STATUS_WORK_ID = "work_id";


    // コンストラクタ
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // テーブルを作成するメソッド
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createToDoTableQuery = "CREATE TABLE " + TABLE_NAME_TODO +
                " (" + COLUMN_HOME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TODO_DATE + " TEXT, " +
                COLUMN_CONTENT + " TEXT)";
        db.execSQL(createToDoTableQuery);

        String createWorkTableQuery = "CREATE TABLE " + TABLE_NAME_WORK +
                " (" + COLUMN_WORK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORK_DATE + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_BREAK_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT)";
        db.execSQL(createWorkTableQuery);

        // work_status テーブルを作成
        String createWorkStatusTableQuery = "CREATE TABLE " + TABLE_NAME_WORK_STATUS +
                " (" + COLUMN_WORK_STATUS_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_IS_WORKING + " INTEGER, " +
                COLUMN_IS_ON_BREAK + " INTEGER, " +
                COLUMN_WORK_STATUS_START_TIME + " TEXT, " +
                COLUMN_WORK_STATUS_END_TIME + " TEXT, " +
                COLUMN_BREAK_TIMES + " TEXT, " +
                COLUMN_WORK_STATUS_WORK_ID + " INTEGER)";
        db.execSQL(createWorkStatusTableQuery);

        // 初期状態を挿入
        ContentValues initialValues = new ContentValues();
        initialValues.put("id", 1);
        initialValues.put("is_working", 0);
        initialValues.put("is_on_break", 0);
        initialValues.put("start_time", "");
        initialValues.put("end_time", "");
        initialValues.put("break_times", "");
        initialValues.put("work_id", -1);
        db.insert("work_status", null, initialValues);
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
                    COLUMN_WORK_DATE + " TEXT, " +
                    COLUMN_START_TIME + " TEXT, " +
                    COLUMN_BREAK_TIME + " TEXT, " +
                    COLUMN_END_TIME + " TEXT)";
            db.execSQL(createWorkTableQuery);
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_NAME_WORK + " ADD COLUMN " + COLUMN_WORK_ID + " INTEGER");
        }
        if (oldVersion < 5) {
            String createWorkStatusTableQuery = "CREATE TABLE work_status (" +
                    "id INTEGER PRIMARY KEY, " +
                    "is_working INTEGER, " +
                    "is_on_break INTEGER, " +
                    "start_time TEXT, " +
                    "end_time TEXT, " +
                    "break_times TEXT, " +
                    "work_id INTEGER)";
            db.execSQL(createWorkStatusTableQuery);
        }
    }

    // エントリーを追加するメソッド（ToDoリスト用）
    public void insertEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TODO_DATE, entry.getDate());
        values.put(COLUMN_CONTENT, entry.getContent());
        db.insert(TABLE_NAME_TODO, null, values);
        db.close();
    }

    // 勤怠エントリーを追加するメソッド
    public long insertWorkEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORK_DATE, entry.getDate());
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
        values.put(COLUMN_TODO_DATE, newDate);
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
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_DATE));
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
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORK_DATE));
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
    public void saveWorkStatus(boolean isWorking, boolean isOnBreak, String startTime, String endTime, ArrayList<String[]> breakTimes, long workId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_WORKING, isWorking ? 1 : 0);
        values.put(COLUMN_IS_ON_BREAK, isOnBreak ? 1 : 0);
        values.put(COLUMN_WORK_STATUS_START_TIME, startTime);
        values.put(COLUMN_WORK_STATUS_END_TIME, endTime);
        values.put(COLUMN_BREAK_TIMES, breakTimesToString(breakTimes));
        values.put(COLUMN_WORK_STATUS_WORK_ID, workId);

        // デバッグ用ログ出力
        Log.d("DatabaseHelper", "Before saving: " + values.toString());

        int rowsAffected = db.update(TABLE_NAME_WORK_STATUS, values, COLUMN_WORK_STATUS_ID + " = ?", new String[]{"1"});
        if (rowsAffected == 0) {
            // 初回保存の場合はinsert
            values.put(COLUMN_WORK_STATUS_ID, 1);
            db.insertWithOnConflict(TABLE_NAME_WORK_STATUS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        db.close();

        // デバッグ用ログ出力
        Log.d("DatabaseHelper", "saveWorkStatus: isWorking=" + isWorking + ", isOnBreak=" + isOnBreak);
        Log.d("DatabaseHelper", "saveWorkStatus: startTime=" + startTime + ", endTime=" + endTime + ", breakTimes=" + breakTimesToString(breakTimes) + ", workId=" + workId);
    }

    // ワークステータスを取得するメソッド
    public WorkStatus getWorkStatus() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_WORK_STATUS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // 列名が正しいか確認
            int isWorkingIndex = cursor.getColumnIndex(COLUMN_IS_WORKING);
            int isOnBreakIndex = cursor.getColumnIndex(COLUMN_IS_ON_BREAK);
            int startTimeIndex = cursor.getColumnIndex(COLUMN_WORK_STATUS_START_TIME);
            int endTimeIndex = cursor.getColumnIndex(COLUMN_WORK_STATUS_END_TIME);
            int breakTimesIndex = cursor.getColumnIndex(COLUMN_BREAK_TIMES);
            int workIdIndex = cursor.getColumnIndex(COLUMN_WORK_STATUS_WORK_ID);

            // 列名が見つからない場合、ログにエラーを出力
            if (isWorkingIndex == -1 || isOnBreakIndex == -1 || startTimeIndex == -1 || endTimeIndex == -1 || breakTimesIndex == -1 || workIdIndex == -1) {
                Log.e("DatabaseHelper", "Column index not found. isWorkingIndex: " + isWorkingIndex +
                        ", isOnBreakIndex: " + isOnBreakIndex + ", startTimeIndex: " + startTimeIndex +
                        ", endTimeIndex: " + endTimeIndex + ", breakTimesIndex: " + breakTimesIndex +
                        ", workIdIndex: " + workIdIndex);
                cursor.close();
                return null;
            }

            boolean isWorking = cursor.getInt(isWorkingIndex) == 1;
            boolean isOnBreak = cursor.getInt(isOnBreakIndex) == 1;
            String startTime = cursor.getString(startTimeIndex);
            String endTime = cursor.getString(endTimeIndex);
            String breakTimesString = cursor.getString(breakTimesIndex);
            long workId = cursor.getLong(workIdIndex);
            ArrayList<String[]> breakTimes = stringToBreakTimes(breakTimesString);

            Log.d("DatabaseHelper", "Loaded status - isWorking: " + isWorking + ", isOnBreak: " + isOnBreak + ", startTime: " + startTime + ", endTime: " + endTime + ", workId: " + workId);

            for (String[] breakTime : breakTimes) {
                Log.d("DatabaseHelper", "Loaded Break Time: " + breakTime[0] + " - " + breakTime[1]);
            }

            cursor.close();
            return new WorkStatus(isWorking, isOnBreak, startTime, endTime, breakTimes, workId);
        } else {
            if (cursor != null) {
                cursor.close();
            }
            Log.d("DatabaseHelper", "No status found in DB");
            return null;
        }
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
        if (breakTimesString != null && !breakTimesString.isEmpty()) {
            String[] breakTimePairs = breakTimesString.split(";");
            for (String breakTimePair : breakTimePairs) {
                String[] breakTime = breakTimePair.split("-");
                if (breakTime.length == 2) {
                    breakTimes.add(breakTime);
                }
            }
        }
        return breakTimes;
    }

    // 勤怠エントリーを更新するメソッド
    public void updateWorkEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORK_DATE, entry.getDate());
        values.put(COLUMN_START_TIME, entry.getStartTime());
        values.put(COLUMN_BREAK_TIME, entry.getBreakTime());
        values.put(COLUMN_END_TIME, entry.getEndTime());

        db.update(TABLE_NAME_WORK, values, COLUMN_WORK_ID + " = ?", new String[]{String.valueOf(entry.getWorkId())});
        db.close();
    }
}
