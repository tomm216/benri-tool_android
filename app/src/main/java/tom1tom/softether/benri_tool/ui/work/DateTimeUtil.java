package tom1tom.softether.benri_tool.ui.work;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public String getCurrentDateTime() {
        // 現在の日付と時刻を取得
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 日付と時刻を文字列にフォーマット
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm\nyyyy/MM/dd");
        return currentDateTime.format(formatter);
    }

}
