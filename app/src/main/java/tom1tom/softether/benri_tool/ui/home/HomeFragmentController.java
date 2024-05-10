package tom1tom.softether.benri_tool.ui.home;

import android.content.Context;
import android.view.View;
import android.widget.CalendarView;
import tom1tom.softether.benri_tool.R;

public class HomeFragmentController {

    private final Context mContext;
    private final View mRoot;
    private final CalendarView mCalendarView;
    private final EntryListView mEntryListView;

    public HomeFragmentController(Context context, View root) {
        // コンテキストとルートビューを保持
        mContext = context;
        mRoot = root;

        // カレンダービューとエントリーリストビューを取得
        mCalendarView = root.findViewById(R.id.calendarView);
        mEntryListView = new EntryListView(context, root);
    }

    public void initialize() {
        // カレンダービューの日付変更リスナーを設定し、新しいエントリーダイアログを表示
        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            mEntryListView.showNewEntryDialog(year, month, dayOfMonth);
        });

        // エントリーリストをロード
        mEntryListView.loadEntries();
    }
}
