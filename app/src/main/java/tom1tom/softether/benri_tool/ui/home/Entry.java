package tom1tom.softether.benri_tool.ui.home;

import android.util.Log;

import java.util.ArrayList;

public class Entry {
    private long homeId;
    private long workId;
    private String date;
    private String startTime;
    private String breakTime;
    private String endTime;
    private String content;
    private ArrayList<String[]> breakTimes;

    // コンストラクタ（勤怠管理用）
    public Entry(long homeId, long workId, String date, String content, String startTime, String breakTime, String endTime) {
        this.homeId = homeId;
        this.workId = workId;
        this.date = date;
        this.content = content;
        this.startTime = startTime;
        this.breakTime = breakTime;
        this.endTime = endTime;
        this.breakTimes = stringToBreakTimes(breakTime);
        Log.d("Entry", "Parsed Break Times: " + breakTimesToString(this.breakTimes)); // デバッグログ追加
    }

    // コンストラクタ（勤怠管理用、contentなし）
    public Entry(long workId, String date, String startTime, String breakTime, String endTime) {
        this.workId = workId;
        this.date = date;
        this.startTime = startTime;
        this.breakTime = breakTime;
        this.endTime = endTime;
        this.breakTimes = stringToBreakTimes(breakTime);
    }

    // コンストラクタ（ToDoリスト用）
    public Entry(long homeId, String date, String content) {
        this.homeId = homeId;
        this.date = date;
        this.content = content;
        this.startTime = "";
        this.breakTime = "";
        this.endTime = "";
        this.workId = -1; // workIdを無効な値として設定
        this.breakTimes = new ArrayList<>();
    }

    // 必要なgetterとsetterを追加
    public long getHomeId() {
        return homeId;
    }

    public long getWorkId() {
        return workId;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getBreakTime() {
        return breakTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setBreakTime(String breakTime) {
        this.breakTime = breakTime;
        this.breakTimes = stringToBreakTimes(breakTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ArrayList<String[]> getBreakTimes() {
        return breakTimes;
    }

    public void setBreakTimes(ArrayList<String[]> breakTimes) {
        this.breakTimes = breakTimes;
    }

    private ArrayList<String[]> stringToBreakTimes(String breakTimesString) {
        ArrayList<String[]> breakTimes = new ArrayList<>();
        if (breakTimesString != null && !breakTimesString.isEmpty()) {
            String[] breakTimePairs = breakTimesString.split(";");
            for (String breakTimePair : breakTimePairs) {
                String[] breakTime = breakTimePair.split("-");
                if (breakTime.length == 2) {
                    breakTimes.add(breakTime);
                } else if (breakTime.length == 1) {
                    breakTimes.add(new String[]{breakTime[0], ""});
                }
            }
        }
        return breakTimes;
    }

    private String breakTimesToString(ArrayList<String[]> breakTimes) {
        StringBuilder breakTimesString = new StringBuilder();
        for (String[] breakTime : breakTimes) {
            breakTimesString.append(breakTime[0]).append("-").append(breakTime[1]).append(";");
        }
        if (breakTimesString.length() > 0) {
            breakTimesString.setLength(breakTimesString.length() - 1); // 最後のセミコロンを削除
        }
        return breakTimesString.toString();
    }
}
