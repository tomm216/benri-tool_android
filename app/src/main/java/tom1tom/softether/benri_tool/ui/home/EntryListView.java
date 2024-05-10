package tom1tom.softether.benri_tool.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import tom1tom.softether.benri_tool.R;



import java.util.ArrayList;
import java.util.Calendar;

public class EntryListView {

    private final Context mContext;
    private final View mRoot;
    private final ListView mListView;
    private final CustomArrayAdapter mAdapter;
    private final ArrayList<Entry> mDataList;
    private final DatabaseHelper mDbHelper;

    public EntryListView(Context context, View root) {
        // コンテキストとルートビューを保持
        mContext = context;
        mRoot = root;

        // データベースヘルパーの初期化
        mDbHelper = new DatabaseHelper(mContext);

        // リストビューとアダプターの初期化
        mListView = mRoot.findViewById(R.id.listView);
        mDataList = new ArrayList<>();
        mAdapter = new CustomArrayAdapter(mContext, R.layout.list_item_entry, mDataList);
        mListView.setAdapter(mAdapter);
    }

    public void loadEntries() {
        // エントリーをロードしてデータリストに追加し、アダプターを更新
        mDataList.clear();
        mDataList.addAll(mDbHelper.getAllEntries());
        mAdapter.notifyDataSetChanged();
    }

    public void showNewEntryDialog(int year, int month, int dayOfMonth) {
        // 新しいエントリーダイアログを表示
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_new_entry, null);
        EditText editText = dialogView.findViewById(R.id.editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        builder.setTitle(year + "/" + (month + 1) + "/" + dayOfMonth + "のToDo作成");
        builder.setPositiveButton("保存", (dialog, which) -> {
            // エントリーをデータベースに追加し、リストにも追加してアダプターを更新
            String content = editText.getText().toString();
            mDbHelper.insertEntry(new Entry(-1, year + "/" + (month + 1) + "/" + dayOfMonth, content));
            mDataList.add(new Entry(-1, year + "/" + (month + 1) + "/" + dayOfMonth, content));
            mAdapter.notifyDataSetChanged();
            Toast.makeText(mContext, "保存しました", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
