package tom1tom.softether.benri_tool.ui.work;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import tom1tom.softether.benri_tool.R;
import android.os.Handler;


public class WorkFragmentController {

    private final Context mContext;
    private final View mRoot;
    private final TextView mTimeTextView; // 時間のテキストビューを追加
    private final TextView mDateTextView;
    private final Handler mHandler;
    private final Runnable mUpdateTimeRunnable;

    public WorkFragmentController(Context context, View root) {
        mContext = context;
        mRoot = root;
        mTimeTextView = root.findViewById(R.id.timeTextView); // 時間のテキストビューを取得
        mDateTextView = root.findViewById(R.id.dateTextView);
        mHandler = new Handler(); // Handlerを初期化
        mUpdateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                displayCurrentDateTime();
                mHandler.postDelayed(this, 1000); // 1秒ごとに更新
            }
        };
        initialize(); // WorkFragment内からアクセスできるようになりました
    }

    protected void initialize() {
        displayCurrentDateTime();
        mHandler.postDelayed(mUpdateTimeRunnable, 1000); // 最初の実行と1秒ごとの更新を開始
    }

    private void displayCurrentDateTime() {
        DateTimeUtil dateTimeUtil = new DateTimeUtil();
        String currentDateTime = dateTimeUtil.getCurrentDateTime();
        String[] dateTimeArray = currentDateTime.split("\n");

        // 時間と日付を別々のテキストビューにセット
        mTimeTextView.setText(dateTimeArray[0]); // 時間のテキストビューに時間をセット
        mDateTextView.setText(dateTimeArray[1]); // 日付のテキストビューに日付をセット
    }
}