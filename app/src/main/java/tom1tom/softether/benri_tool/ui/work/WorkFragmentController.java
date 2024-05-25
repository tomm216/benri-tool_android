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
import android.os.Handler;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import tom1tom.softether.benri_tool.R;
import tom1tom.softether.benri_tool.ui.home.DatabaseHelper;
import tom1tom.softether.benri_tool.ui.home.Entry;

public class WorkFragmentController {

    private final Context mContext;
    private final View mRoot;
    private final TextView mTimeTextView;
    private final TextView mDateTextView;
    private final Handler mHandler;
    private final Runnable mUpdateTimeRunnable;
    private final TableLayout mTableLayout;
    private Button mButtonAttendance;
    private Button mButtonBreak;
    private boolean isWorking = false;
    private boolean isOnBreak = false;
    private TableRow currentRow;
    private final ArrayList<String[]> breakTimes = new ArrayList<>();
    private final DatabaseHelper dbHelper;

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
        initialize();
    }

    protected void initialize() {
        displayCurrentDateTime();
        mHandler.postDelayed(mUpdateTimeRunnable, 1000);
        setupButtons();
        loadWorkStatus();
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
        if (isWorking) {
            if (isOnBreak) {
                mButtonBreak.setText("休憩終了");
                mButtonBreak.setEnabled(true);
                mButtonAttendance.setEnabled(false);
            } else {
                mButtonBreak.setText("休憩");
                mButtonBreak.setEnabled(true);
                mButtonAttendance.setEnabled(true);
            }
        } else {
            mButtonBreak.setEnabled(false);
            mButtonAttendance.setEnabled(true);
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
        mButtonAttendance.setText("退勤");
        isWorking = true;
        breakTimes.clear(); // 休憩リストを初期化
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
        }
        mButtonAttendance.setText("出勤");
        isWorking = false;
        if (currentRow != null) {
            currentRow.setTag(R.id.break_times_tag, new ArrayList<>(breakTimes));
        }
        breakTimes.clear();
        saveWorkEntry();
        setButtonState();
        saveWorkStatus();
    }

    private void startBreakEntry() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        if (!breakTimes.isEmpty() && breakTimes.get(breakTimes.size() - 1)[1].isEmpty()) {
        } else {
            breakTimes.add(new String[]{currentTime, ""});
        }
        mButtonBreak.setText("休憩終了");
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
        mButtonBreak.setText("休憩");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = View.inflate(mContext, R.layout.work_edit_dialog, null);
        builder.setView(dialogView);

        EditText editStartTime = dialogView.findViewById(R.id.editStartTime);
        EditText editEndTime = dialogView.findViewById(R.id.editEndTime);

        LinearLayout breakTimesLayout = dialogView.findViewById(R.id.breakTimesLayout);
        breakTimesLayout.removeAllViews();

        ArrayList<String[]> currentBreakTimes = new ArrayList<>();
        if (row != null && row.getTag(R.id.break_times_tag) != null) {
            currentBreakTimes = (ArrayList<String[]>) row.getTag(R.id.break_times_tag);
        } else {
            currentBreakTimes = new ArrayList<>();
        }

        // 休憩時間が登録されていない場合でも空行を追加
        if (currentBreakTimes.isEmpty()) {
            addBreakTimeRow(breakTimesLayout, "", "");
        } else {
            for (String[] breakTime : currentBreakTimes) {
                addBreakTimeRow(breakTimesLayout, breakTime[0], breakTime[1]);
            }
        }

        editStartTime.setText(startTimeText.getText());
        editEndTime.setText(endTimeText.getText());

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (!isValidTime(editStartTime.getText().toString()) || !isValidTime(editEndTime.getText().toString())) {
                showToast("無効な時間が入力されました。再度入力してください。");
                return;
            }

            startTimeText.setText(editStartTime.getText());
            endTimeText.setText(editEndTime.getText());

            ArrayList<String[]> newBreakTimes = new ArrayList<>();
            for (int i = 0; i < breakTimesLayout.getChildCount(); i++) {
                LinearLayout breakTimeRow = (LinearLayout) breakTimesLayout.getChildAt(i);
                EditText breakStart = (EditText) breakTimeRow.getChildAt(0);
                EditText breakEnd = (EditText) breakTimeRow.getChildAt(1);

                if (!isValidTime(breakStart.getText().toString()) || !isValidTime(breakEnd.getText().toString())) {
                    showToast("無効な時間が入力されました。再度入力してください。");
                    return;
                }

                newBreakTimes.add(new String[]{breakStart.getText().toString(), breakEnd.getText().toString()});
            }

            breakTimes.clear();
            breakTimes.addAll(newBreakTimes);

            if (row != null) {
                row.setTag(R.id.break_times_tag, new ArrayList<>(breakTimes));
            }
            updateBreakTimes();
            saveWorkEntry();
            saveWorkStatus();
        });

        builder.setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("削除", (dialog, which) -> {
            if (row != null) {
                long workId = (row.getTag(R.id.work_id_tag) != null) ? (long) row.getTag(R.id.work_id_tag) : -1;
                if (workId != -1) {
                    mTableLayout.removeView(row);
                    currentRow = null;
                    isWorking = false;
                    isOnBreak = false;
                    breakTimes.clear();
                    setButtonState();
                    saveWorkStatus();
                    deleteWorkEntry(workId);
                    showToast("削除に成功しました");
                } else {
                    showToast("エントリを削除できませんでした。IDが見つかりません。");
                }
            }
        });

        builder.create().show();
    }

    private void addBreakTimeRow(LinearLayout layout, String start, String end) {
        LinearLayout breakTimeRow = new LinearLayout(mContext);
        breakTimeRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        breakTimeRow.setOrientation(LinearLayout.HORIZONTAL);

        EditText breakStart = new EditText(mContext);
        breakStart.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        breakStart.setText(start);
        breakStart.setHint("休憩開始時間");

        EditText breakEnd = new EditText(mContext);
        breakEnd.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        breakEnd.setText(end);
        breakEnd.setHint("休憩終了時間");

        breakTimeRow.addView(breakStart);
        breakTimeRow.addView(breakEnd);

        layout.addView(breakTimeRow);
    }


    private boolean isValidTime(String time) {
        return time != null && time.matches("([01]?\\d|2[0-3]):([0-5]?\\d)");
    }

    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void saveWorkStatus() {
        dbHelper.saveWorkStatus(isWorking, isOnBreak,
                (currentRow != null && currentRow.getChildAt(1) instanceof TextView) ? ((TextView) currentRow.getChildAt(1)).getText().toString() : "",
                (currentRow != null && currentRow.getChildAt(3) instanceof TextView) ? ((TextView) currentRow.getChildAt(3)).getText().toString() : "",
                breakTimes);
    }

    private void loadWorkStatus() {
        WorkStatus status = dbHelper.getWorkStatus();
        if (status != null) {
            isWorking = status.isWorking;
            isOnBreak = status.isOnBreak;
            String startTime = status.startTime;
            String endTime = status.endTime;
            ArrayList<String[]> savedBreakTimes = status.breakTimes;

            if (isWorking) {
                currentRow = new TableRow(mContext);

                TextView dateText = new TextView(mContext);
                dateText.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()));
                dateText.setPadding(8, 8, 8, 8);
                dateText.setGravity(android.view.Gravity.CENTER);
                currentRow.addView(dateText);

                TextView startTimeText = new TextView(mContext);
                startTimeText.setText(startTime);
                startTimeText.setPadding(8, 8, 8, 8);
                startTimeText.setGravity(android.view.Gravity.CENTER);
                currentRow.addView(startTimeText);

                TextView breakTimeText = new TextView(mContext);
                breakTimeText.setText("");
                breakTimeText.setPadding(8, 8, 8, 8);
                breakTimeText.setGravity(android.view.Gravity.CENTER);
                currentRow.addView(breakTimeText);

                TextView endTimeText = new TextView(mContext);
                endTimeText.setText(endTime);
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
                currentRow.setTag(status.getWorkId()); // 現在の行にworkIdを設定
                currentRow.setTag(savedBreakTimes);
                updateBreakTimes();
            }
            setButtonState();
        }
    }

    private void loadWorkEntries() {
        mTableLayout.removeAllViews(); // 既存の行をクリア
        ArrayList<Entry> entries = dbHelper.getAllWorkEntries();
        for (Entry entry : entries) {
            TableRow row = new TableRow(mContext);

            TextView dateText = new TextView(mContext);
            dateText.setText(entry.getDate());
            dateText.setPadding(8, 8, 8, 8);
            dateText.setGravity(android.view.Gravity.CENTER);
            row.addView(dateText);

            TextView startTimeText = new TextView(mContext);
            startTimeText.setText(entry.getStartTime());
            startTimeText.setPadding(8, 8, 8, 8);
            startTimeText.setGravity(android.view.Gravity.CENTER);
            row.addView(startTimeText);

            TextView breakTimeText = new TextView(mContext);
            breakTimeText.setText(entry.getBreakTime());
            breakTimeText.setPadding(8, 8, 8, 8);
            breakTimeText.setGravity(android.view.Gravity.CENTER);
            row.addView(breakTimeText);

            TextView endTimeText = new TextView(mContext);
            endTimeText.setText(entry.getEndTime());
            endTimeText.setPadding(8, 8, 8, 8);
            endTimeText.setGravity(android.view.Gravity.CENTER);
            row.addView(endTimeText);

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
                    showEditDialog(startTimeText, breakTimeText, endTimeText, row);
                }
            });
            row.addView(editButton);

            row.setTag(R.id.work_id_tag, entry.getWorkId());
            row.setTag(R.id.break_times_tag, entry.getBreakTimes()); // breakTimesを設定

            mTableLayout.addView(row);
        }
    }

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

    private void saveWorkEntry() {
        if (currentRow != null) {
            TextView dateText = (TextView) currentRow.getChildAt(0);
            TextView startTimeText = (TextView) currentRow.getChildAt(1);
            TextView breakTimeText = (TextView) currentRow.getChildAt(2);
            TextView endTimeText = (TextView) currentRow.getChildAt(3);

            // workIdを取得（nullの場合は-1を設定）
            long workId = (currentRow.getTag(R.id.work_id_tag) != null) ? (long) currentRow.getTag(R.id.work_id_tag) : -1;

            Entry entry = new Entry(workId, dateText.getText().toString(), startTimeText.getText().toString(), breakTimeText.getText().toString(), endTimeText.getText().toString());
            entry.setBreakTimes((ArrayList<String[]>) currentRow.getTag(R.id.break_times_tag)); // breakTimesを設定

            if (workId == -1) {
                // 新規エントリの場合、挿入してworkIdを取得
                workId = dbHelper.insertWorkEntry(entry);
                currentRow.setTag(R.id.work_id_tag, workId); // 新しいworkIdを設定
            } else {
                dbHelper.updateWorkEntry(entry);
            }
        }
    }

    private void deleteWorkEntry(long workId) {
        dbHelper.deleteWorkEntry(workId);
    }
}
