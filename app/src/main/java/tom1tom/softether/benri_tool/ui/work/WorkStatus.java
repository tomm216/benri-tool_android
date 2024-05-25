package tom1tom.softether.benri_tool.ui.work;

import java.util.ArrayList;

public class WorkStatus {
    public boolean isWorking;
    public boolean isOnBreak;
    public String startTime;
    public String endTime;
    public ArrayList<String[]> breakTimes;
    public long workId; // workIdを追加

    public WorkStatus(boolean isWorking, boolean isOnBreak, String startTime, String endTime, ArrayList<String[]> breakTimes, long workId) {
        this.isWorking = isWorking;
        this.isOnBreak = isOnBreak;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakTimes = breakTimes;
        this.workId = workId; // workIdを設定
    }

    public long getWorkId() {
        return workId;
    }
}
