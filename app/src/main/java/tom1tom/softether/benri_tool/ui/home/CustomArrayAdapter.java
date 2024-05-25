package tom1tom.softether.benri_tool.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import tom1tom.softether.benri_tool.R;

public class CustomArrayAdapter extends ArrayAdapter<Entry> {

    private Context mContext;
    private int mResource;
    private ArrayList<Entry> dataList;
    private CustomArrayAdapter adapter;
    private DatabaseHelper dbHelper;

    public CustomArrayAdapter(Context context, int resource, ArrayList<Entry> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        dataList = objects;
        adapter = this;
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        Entry entry = getItem(position);
        String date = entry.getDate();
        String content = entry.getContent();

        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);
        dateTextView.setText(date);
        contentTextView.setText(content);

        // 日付をクリックしたときの処理
        dateTextView.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                String newDate = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDayOfMonth;
                entry.setDate(newDate);
                notifyDataSetChanged();
                dbHelper.updateEntryDate(entry.getHomeId(), newDate);
                Toast.makeText(mContext, "日付を更新しました", Toast.LENGTH_SHORT).show();
            }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        // 内容をクリックしたときの処理
        contentTextView.setOnClickListener(v -> {
            View editDialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_new_entry, null);
            EditText editContentEditText = editDialogView.findViewById(R.id.editText);
            editContentEditText.setText(content);

            AlertDialog.Builder editBuilder = new AlertDialog.Builder(mContext);
            editBuilder.setView(editDialogView);
            editBuilder.setTitle("内容を編集");
            editBuilder.setPositiveButton("保存", (dialog, which) -> {
                String editedContent = editContentEditText.getText().toString();
                entry.setContent(editedContent);
                notifyDataSetChanged();
                dbHelper.updateEntryContent(entry.getHomeId(), editedContent);
                Toast.makeText(mContext, "内容を更新しました", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            editBuilder.setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss());

            // 削除ボタンを追加
            editBuilder.setNeutralButton("削除", (dialog, which) -> {
                dataList.remove(entry);
                adapter.notifyDataSetChanged();
                dbHelper.deleteEntry(entry.getHomeId());
                Toast.makeText(mContext, "エントリーを削除しました", Toast.LENGTH_SHORT).show();
            });

            editBuilder.create().show();
        });

        return convertView;
    }
}
