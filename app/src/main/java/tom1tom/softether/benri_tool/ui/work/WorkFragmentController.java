package tom1tom.softether.benri_tool.ui.work;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;
import android.view.LayoutInflater;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tom1tom.softether.benri_tool.R;
import tom1tom.softether.benri_tool.ui.home.DatabaseHelper;
import tom1tom.softether.benri_tool.ui.home.Entry;

public class WorkFragmentController extends Fragment {

    private Context mContext;
    private View mRoot;
    private TextView mTimeTextView;
    private TextView mDateTextView;
    private Handler mHandler;
    private Runnable mUpdateTimeRunnable;
    private TableLayout mTableLayout;
    private Button mButtonAttendance;
    private Button mButtonBreak;
    private boolean isWorking = false;
    private boolean isOnBreak = false;
    private TableRow currentRow;
    private final ArrayList<String[]> breakTimes = new ArrayList<>();
    private DatabaseHelper dbHelper;

    // コンストラクタ
    public WorkFragmentController(Context context, View root) {
        mContext = context;
        mRoot = root;
        mTimeTextView = root.findViewById(R.id.timeTextView);
        mDateTextView = root.findViewById(R.id.dateTextView);
        mTableLayout = root.findViewById(R.id.tableLayout);
        mHandler = new Handler();
        dbHelper = new DatabaseHelper(context);

        mUpdateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                displayCurrentDateTime();
                mHandler.postDelayed(this, 1000);
            }
        };
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_work, container, false);
        mContext = getContext(); // mContext を設定
        mTimeTextView = mRoot.findViewById(R.id.timeTextView);
        mDateTextView = mRoot.findViewById(R.id.dateTextView);
        mTableLayout = mRoot.findViewById(R.id.tableLayout);
        mHandler = new Handler();
        dbHelper = new DatabaseHelper(mContext);

        mUpdateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                displayCurrentDateTime();
                mHandler.postDelayed(this, 1000);
            }
        };

        return mRoot;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isWorking", isWorking);
        outState.putBoolean("isOnBreak", isOnBreak);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            isWorking = savedInstanceState.getBoolean("isWorking", false);
            isOnBreak = savedInstanceState.getBoolean("isOnBreak", false);
        } else {
            loadWorkStatus(); // データベースから作業状態を読み込む
        }
        initialize();
    }
    @Override
    public void onStart() {
        super.onStart();
        // onStart での loadWorkStatus の呼び出しを削除
    }

    public void initialize() {
        displayCurrentDateTime();
        mHandler.postDelayed(mUpdateTimeRunnable, 1000);
        setupButtons();
        loadWorkEntries();
    }
    private void displayCurrentDateTime() {
        DateTimeUtil dateTimeUtil = new DateTimeUtil();
        String currentDateTime = dateTimeUtil.getCurrentDateTime();
        String[] dateTimeArray = currentDateTime.split("\n");

        mTimeTextView.setText(dateTimeArray[0]);
        mDateTextView.setText(dateTimeArray[1]);
    }

    private void setupButtons() {
        mButtonAttendance = mRoot.findViewById(R.id.buttonAttendance);
        mButtonBreak = mRoot.findViewById(R.id.buttonBreak);

        mButtonAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWorking) {
                    addEndWorkEntry();
                } else {
                    addStartWorkEntry();
                }
            }
        });

        mButtonBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnBreak) {
                    endBreakEntry();
                } else {
                    startBreakEntry();
                }
            }
        });

        setButtonState();
    }

    private void setButtonState() {
        Log.d("WorkFragmentController", "Setting button state: isWorking=" + isWorking + ", isOnBreak=" + isOnBreak);
        if (!isWorking) {
            mButtonAttendance.setText("出勤");
            mButtonAttendance.setEnabled(true);
            mButtonBreak.setText("休憩");
            mButtonBreak.setEnabled(false);
        } else {
            mButtonAttendance.setText("退勤");
            mButtonAttendance.setEnabled(true);
            if (isOnBreak) {
                mButtonBreak.setText("休憩終了");
                mButtonBreak.setEnabled(true);
                mButtonAttendance.setEnabled(false);
            } else {
                mButtonBreak.setText("休憩");
                mButtonBreak.setEnabled(true);
            }
        }
    }

    private void addStartWorkEntry() {
        String currentDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        currentRow = new TableRow(mContext);

        TextView dateText = new TextView(mContext);
        dateText.setText(currentDate);
        dateText.setPadding(8, 8, 8, 8);
        dateText.setGravity(android.view.Gravity.CENTER);
        currentRow.addView(dateText);

        TextView startTimeText = new TextView(mContext);
        startTimeText.setText(currentTime);
        startTimeText.setPadding(8, 8, 8, 8);
        startTimeText.setGravity(android.view.Gravity.CENTER);
        currentRow.addView(startTimeText);

        TextView breakTimeText = new TextView(mContext);
        breakTimeText.setText("");
        breakTimeText.setPadding(8, 8, 8, 8);
        breakTimeText.setGravity(android.view.Gravity.CENTER);
        currentRow.addView(breakTimeText);

        TextView endTimeText = new TextView(mContext);
        endTimeText.setText("");
        endTimeText.setPadding(8, 8, 8, 8);
        endTimeText.setGravity(android.view.Gravity.CENTER);
        currentRow.addView(endTimeText);

        ImageButton editButton = new ImageButton(mContext);
        editButton.setImageResource(R.drawable.edit);

        // Background resource setup
        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        editButton.setBackgroundResource(outValue.resourceId);

        // Adjust the size to match other cells
        TableRow.LayoutParams params = new TableRow.LayoutParams(100, 100);
        editButton.setLayoutParams(params);
        editButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        editButton.setPadding(8, 8, 8, 8);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(startTimeText, breakTimeText, endTimeText, currentRow);
            }
        });
        currentRow.addView(editButton);

        mTableLayout.addView(currentRow);
        isWorking = true;
        breakTimes.clear(); // 休憩リストを初期化

        // 即座にボタンの状態を更新
        setButtonState();
        saveWorkStatus();

        // workIdをタグに設定
        currentRow.setTag(R.id.work_id_tag, -1L);
    }

    private void addEndWorkEntry() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        if (currentRow != null) {
            TextView endTimeText = (TextView) currentRow.getChildAt(3);
            endTimeText.setText(currentTime);
            // 必要な引数を取得
            TextView startTimeText = (TextView) currentRow.getChildAt(1);
            TextView breakTimeText = (TextView) currentRow.getChildAt(2);

            // 作業エントリを保存
            saveWorkEntry(currentRow, startTimeText.getText().toString(), endTimeText.getText().toString(), breakTimes);
        }
        isWorking = false;
        if (currentRow != null) {
            currentRow.setTag(R.id.break_times_tag, new ArrayList<>(breakTimes));
        }
        breakTimes.clear();
        setButtonState();
        saveWorkStatus();
    }

    private void startBreakEntry() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        if (!breakTimes.isEmpty() && breakTimes.get(breakTimes.size() - 1)[1].isEmpty()) {
        } else {
            breakTimes.add(new String[]{currentTime, ""});
        }
        isOnBreak = true;
        setButtonState();
        saveWorkStatus();
    }

    private void endBreakEntry() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        if (!breakTimes.isEmpty()) {
            breakTimes.get(breakTimes.size() - 1)[1] = currentTime;
        }
        updateBreakTimes();
        isOnBreak = false;
        setButtonState();
        saveWorkStatus();
    }

    private void updateBreakTimes() {
        if (currentRow != null) {
            TextView breakTimeText = (TextView) currentRow.getChildAt(2);
            StringBuilder breakTimesString = new StringBuilder();
            for (String[] breakTime : breakTimes) {
                if (!breakTime[1].isEmpty()) {
                    breakTimesString.append(breakTime[0]).append(" - ").append(breakTime[1]).append("\n");
                } else {
                    breakTimesString.append(breakTime[0]).append(" - ").append("...\n");
                }
            }
            breakTimeText.setText(breakTimesString.toString().trim());
        }
    }

    private void showEditDialog(TextView startTimeText, TextView breakTimeText, TextView endTimeText, TableRow row) {
        // ダイアログのビルダーを作成し、レイアウトを設定
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = View.inflate(mContext, R.layout.work_edit_dialog, null);
        builder.setView(dialogView);

        // ダイアログ内のエディットテキストを取得
        EditText editStartTime = dialogView.findViewById(R.id.editStartTime);
        EditText editEndTime = dialogView.findViewById(R.id.editEndTime);

        // 休憩時間を表示するレイアウトを取得し、既存のビューをクリア
        LinearLayout breakTimesLayout = dialogView.findViewById(R.id.breakTimesLayout);
        breakTimesLayout.removeAllViews();

        // 現在の出勤時間と退勤時間を取得
        String originalStartTime = startTimeText.getText().toString();
        String originalEndTime = endTimeText.getText().toString();

        // テキストビューから休憩時間リストを取得し、レイアウトに表示
        String breakTimesText = breakTimeText.getText().toString();
        ArrayList<String[]> breakTimesFromText = stringToBreakTimes(breakTimesText);
        if (breakTimesFromText != null) {
            for (String[] breakTime : breakTimesFromText) {
                addBreakTimeRow(breakTimesLayout, breakTime[0], breakTime[1]);
            }
        }

        // 現在の出勤時間と退勤時間をダイアログのエディットテキストに設定
        editStartTime.setText(originalStartTime);
        editEndTime.setText(originalEndTime);

        // OKボタンのクリックリスナーを設定
        builder.setPositiveButton("OK", (dialog, which) -> {
            // 入力された出勤時間と退勤時間が有効かチェック
            if (!isValidTime(editStartTime.getText().toString()) || !isValidTime(editEndTime.getText().toString())) {
                showToast("無効な時間が入力されました。再度入力してください。");
                return;
            }

            // 入力が有効な場合、出勤時間と退勤時間を更新
            String newStartTime = editStartTime.getText().toString();
            String newEndTime = editEndTime.getText().toString();
            startTimeText.setText(newStartTime);
            endTimeText.setText(newEndTime);

            // 変更箇所をチェックし、トーストで表示
            StringBuilder changes = new StringBuilder("変更された箇所:\n");
            if (!originalStartTime.equals(newStartTime)) {
                changes.append("出勤時間: ").append(originalStartTime).append(" -> ").append(newStartTime).append("\n");
            }
            if (!originalEndTime.equals(newEndTime)) {
                changes.append("退勤時間: ").append(originalEndTime).append(" -> ").append(newEndTime).append("\n");
            }

            // 新しい休憩時間リストを作成
            ArrayList<String[]> newBreakTimes = new ArrayList<>();
            for (int i = 0; i < breakTimesLayout.getChildCount(); i++) {
                // 休憩時間の各行を取得
                LinearLayout breakTimeRow = (LinearLayout) breakTimesLayout.getChildAt(i);
                EditText breakStart = (EditText) breakTimeRow.getChildAt(0);
                EditText breakEnd = (EditText) breakTimeRow.getChildAt(1);

                // 入力された休憩時間が有効かチェック
                if (!isValidTime(breakStart.getText().toString()) || !isValidTime(breakEnd.getText().toString())) {
                    showToast("無効な時間が入力されました。再度入力してください。");
                    return;
                }

                // 新しい休憩時間をリストに追加
                newBreakTimes.add(new String[]{breakStart.getText().toString(), breakEnd.getText().toString()});
            }

            // 休憩時間リストを更新
            boolean breakTimesChanged = false;
            for (int i = 0; i < newBreakTimes.size(); i++) {
                String[] originalBreakTime = i < breakTimes.size() ? breakTimes.get(i) : new String[]{"", ""};
                String[] newBreakTime = newBreakTimes.get(i);
                if (!originalBreakTime[0].equals(newBreakTime[0]) || !originalBreakTime[1].equals(newBreakTime[1])) {
                    changes.append("休憩時間").append(i + 1).append(": ")
                            .append(originalBreakTime[0]).append(" - ").append(originalBreakTime[1])
                            .append(" -> ").append(newBreakTime[0]).append(" - ").append(newBreakTime[1]).append("\n");
                    breakTimesChanged = true;
                }
            }

            breakTimes.clear();
            breakTimes.addAll(newBreakTimes);

            // 変更があった場合、トーストで表示
            if (!changes.toString().equals("変更された箇所:\n")) {
                showToast(changes.toString().trim());
            }

            // 現在の行に新しい休憩時間リストをタグとして設定
            if (row != null) {
                row.setTag(R.id.break_times_tag, new ArrayList<>(breakTimes));
            }

            // 休憩時間表示を更新
            updateBreakTimes();

            // 作業エントリを保存
            saveWorkEntry(row, newStartTime, newEndTime, breakTimes);

            // 作業状態を保存
            saveWorkStatus();
        });

        // キャンセルボタンのクリックリスナーを設定
        builder.setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss());

        // 削除ボタンのクリックリスナーを設定
        builder.setNeutralButton("削除", (dialog, which) -> {
            if (row != null) {
                // 作業エントリのIDを取得
                long workId = (row.getTag(R.id.work_id_tag) != null) ? (long) row.getTag(R.id.work_id_tag) : -1;
                if (workId != -1) {
                    // テーブルから行を削除
                    mTableLayout.removeView(row);
                    currentRow = null;
                    isWorking = false;
                    isOnBreak = false;
                    breakTimes.clear();
                    setButtonState();

                    // 作業状態を保存
                    saveWorkStatus();

                    // データベースから作業エントリを削除
                    deleteWorkEntry(workId);
                    showToast("削除に成功しました");
                } else {
                    showToast("エントリを削除できませんでした。IDが見つかりません。");
                }
            }
        });

        // ダイアログを表示
        builder.create().show();
    }

    private void addBreakTimeRow(LinearLayout layout, String start, String end) {
        // 新しい休憩時間の行を作成
        LinearLayout breakTimeRow = new LinearLayout(mContext);
        breakTimeRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        breakTimeRow.setOrientation(LinearLayout.HORIZONTAL);

        // 休憩開始時間のエディットテキストを作成
        EditText breakStart = new EditText(mContext);
        breakStart.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        breakStart.setText(start);
        breakStart.setHint("休憩開始時間");

        // 休憩終了時間のエディットテキストを作成
        EditText breakEnd = new EditText(mContext);
        breakEnd.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        breakEnd.setText(end);
        breakEnd.setHint("休憩終了時間");

        // 行にエディットテキストを追加
        breakTimeRow.addView(breakStart);
        breakTimeRow.addView(breakEnd);

        // レイアウトに行を追加
        layout.addView(breakTimeRow);
    }

    private boolean isValidTime(String time) {
        return time != null && time.matches("([01]?\\d|2[0-3]):([0-5]?\\d)");
    }

    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void saveWorkStatus() {
        long workId = (currentRow != null && currentRow.getTag(R.id.work_id_tag) != null) ? (long) currentRow.getTag(R.id.work_id_tag) : -1;
        String startTime = (currentRow != null && currentRow.getChildAt(1) instanceof TextView) ? ((TextView) currentRow.getChildAt(1)).getText().toString() : "";
        String endTime = (currentRow != null && currentRow.getChildAt(3) instanceof TextView) ? ((TextView) currentRow.getChildAt(3)).getText().toString() : "";

        Log.d("WorkFragmentController", "Saving status: isWorking=" + isWorking + ", isOnBreak=" + isOnBreak + ", startTime=" + startTime + ", endTime=" + endTime + ", breakTimes=" + breakTimes + ", workId=" + workId);

        dbHelper.saveWorkStatus(isWorking, isOnBreak, startTime, endTime, breakTimes, workId);

        // 状態を保存した直後にデータベースの内容を確認する
        WorkStatus savedStatus = dbHelper.getWorkStatus();
        Log.d("WorkFragmentController", "After save - isWorking=" + savedStatus.isWorking + ", isOnBreak=" + savedStatus.isOnBreak);
    }
    // データベースから作業状態を読み込むメソッド
    private void loadWorkStatus() {
        // データベースから WorkStatus を取得
        WorkStatus status = dbHelper.getWorkStatus();

        // WorkStatus が取得できた場合
        if (status != null) {
            Log.d("WorkFragmentController", "Status from DB - isWorking: " + status.isWorking + ", isOnBreak: " + status.isOnBreak);

            isWorking = status.isWorking;
            isOnBreak = status.isOnBreak;

            // isWorking が true の場合（出勤状態の場合）
            if (isWorking) {
                Log.d("WorkFragmentController", "Restoring current row from status");

                currentRow = new TableRow(mContext);

                TextView dateText = new TextView(mContext);
                dateText.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()));
                dateText.setPadding(8, 8, 8, 8);
                dateText.setGravity(android.view.Gravity.CENTER);
                currentRow.addView(dateText);

                TextView startTimeText = new TextView(mContext);
                startTimeText.setText(status.startTime);
                startTimeText.setPadding(8, 8, 8, 8);
                startTimeText.setGravity(android.view.Gravity.CENTER);
                currentRow.addView(startTimeText);

                TextView breakTimeText = new TextView(mContext);
                breakTimeText.setText("");
                breakTimeText.setPadding(8, 8, 8, 8);
                breakTimeText.setGravity(android.view.Gravity.CENTER);
                currentRow.addView(breakTimeText);

                TextView endTimeText = new TextView(mContext);
                endTimeText.setText(status.endTime);
                endTimeText.setPadding(8, 8, 8, 8);
                endTimeText.setGravity(android.view.Gravity.CENTER);
                currentRow.addView(endTimeText);

                ImageButton editButton = new ImageButton(mContext);
                editButton.setImageResource(R.drawable.edit);

                TypedValue outValue = new TypedValue();
                mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                editButton.setBackgroundResource(outValue.resourceId);

                TableRow.LayoutParams params = new TableRow.LayoutParams(100, 100);
                editButton.setLayoutParams(params);
                editButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
                editButton.setPadding(8, 8, 8, 8);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(startTimeText, breakTimeText, endTimeText, currentRow);
                    }
                });
                currentRow.addView(editButton);

                mTableLayout.addView(currentRow);

                currentRow.setTag(R.id.work_id_tag, status.workId);
                currentRow.setTag(R.id.break_times_tag, status.breakTimes);

                updateBreakTimes();
            }
        } else {
            Log.d("WorkFragmentController", "Status from DB is null");
        }

        Log.d("WorkFragmentController", "After loading from DB - isWorking: " + isWorking + ", isOnBreak: " + isOnBreak);

        // setButtonStateはここで呼び出さず、必要な箇所でのみ呼び出す
        setButtonState();
    }

    private void recreateCurrentRow(WorkStatus status) {
        // 現在の作業情報をもとに TableRow を再構築するメソッド
        currentRow = new TableRow(mContext);
        // その他の行の設定、状態の復元など
    }

    private void loadWorkEntries() {
        // テーブルレイアウトから既存の行をすべて削除
        mTableLayout.removeAllViews();

        // データベースヘルパーからすべての作業エントリを取得
        ArrayList<Entry> entries = dbHelper.getAllWorkEntries();

        // 各作業エントリについてループ
        for (Entry entry : entries) {
            // 新しいテーブル行を作成
            TableRow row = new TableRow(mContext);

            // 日付のテキストビューを作成し、テーブル行に追加
            TextView dateText = new TextView(mContext);
            dateText.setText(entry.getDate());
            dateText.setPadding(8, 8, 8, 8);
            dateText.setGravity(android.view.Gravity.CENTER);
            row.addView(dateText);

            // 出勤時間のテキストビューを作成し、テーブル行に追加
            TextView startTimeText = new TextView(mContext);
            startTimeText.setText(entry.getStartTime());
            startTimeText.setPadding(8, 8, 8, 8);
            startTimeText.setGravity(android.view.Gravity.CENTER);
            row.addView(startTimeText);

            // 休憩時間のテキストビューを作成し、テーブル行に追加
            TextView breakTimeText = new TextView(mContext);
            breakTimeText.setText(entry.getBreakTime());
            breakTimeText.setPadding(8, 8, 8, 8);
            breakTimeText.setGravity(android.view.Gravity.CENTER);
            row.addView(breakTimeText);

            // 退勤時間のテキストビューを作成し、テーブル行に追加
            TextView endTimeText = new TextView(mContext);
            endTimeText.setText(entry.getEndTime());
            endTimeText.setPadding(8, 8, 8, 8);
            endTimeText.setGravity(android.view.Gravity.CENTER);
            row.addView(endTimeText);

            // 編集ボタンを作成し、テーブル行に追加
            ImageButton editButton = new ImageButton(mContext);
            editButton.setImageResource(R.drawable.edit);

            // 背景リソースを設定
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            editButton.setBackgroundResource(outValue.resourceId);

            // サイズを他のセルに合わせて調整
            TableRow.LayoutParams params = new TableRow.LayoutParams(100, 100);
            editButton.setLayoutParams(params);
            editButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            editButton.setPadding(8, 8, 8, 8);

            // 編集ボタンのクリックリスナーを設定
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(startTimeText, breakTimeText, endTimeText, row);
                }
            });
            row.addView(editButton);

            // 行にworkIdと休憩時間をタグとして設定
            row.setTag(R.id.work_id_tag, entry.getWorkId());
            row.setTag(R.id.break_times_tag, entry.getBreakTimes());

            // テーブルレイアウトに行を追加
            mTableLayout.addView(row);
        }
    }

    private ArrayList<String[]> stringToBreakTimes(String breakTimesString) {
        ArrayList<String[]> breakTimes = new ArrayList<>();
        if (breakTimesString != null && !breakTimesString.isEmpty()) {
            String[] breakTimePairs = breakTimesString.split("\n");
            for (String breakTimePair : breakTimePairs) {
                String[] breakTime = breakTimePair.split(" - ");
                if (breakTime.length == 2) {
                    breakTimes.add(breakTime);
                }
            }
        }
        return breakTimes;
    }

    private void saveWorkEntry(TableRow row, String startTime, String endTime, ArrayList<String[]> breakTimes) {
        if (row != null) {
            TextView dateText = (TextView) row.getChildAt(0);
            TextView breakTimeText = (TextView) row.getChildAt(2);

            // workIdを取得（nullの場合は-1を設定）
            long workId = (row.getTag(R.id.work_id_tag) != null) ? (long) row.getTag(R.id.work_id_tag) : -1;

            StringBuilder breakTimesString = new StringBuilder();
            for (String[] breakTime : breakTimes) {
                breakTimesString.append(breakTime[0]).append(" - ").append(breakTime[1]).append("\n");
            }

            breakTimeText.setText(breakTimesString.toString().trim());

            Entry entry = new Entry(workId, dateText.getText().toString(), startTime, breakTimeText.getText().toString(), endTime);
            entry.setBreakTimes((ArrayList<String[]>) row.getTag(R.id.break_times_tag)); // breakTimesを設定

            if (workId == -1) {
                // 新規エントリの場合、挿入してworkIdを取得
                workId = dbHelper.insertWorkEntry(entry);
                row.setTag(R.id.work_id_tag, workId); // 新しいworkIdを設定
            } else {
                dbHelper.updateWorkEntry(entry);
            }
        }
    }

    private void deleteWorkEntry(long workId) {
        dbHelper.deleteWorkEntry(workId);
    }
}
