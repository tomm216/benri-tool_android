package tom1tom.softether.benri_tool;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import tom1tom.softether.benri_tool.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BottomNavigationViewのイベント設定
        setNavViewEvent();

        setSupportActionBar(binding.appBarMain.toolbar);

        // FAB (Floating Action Button) の関連部分を削除します

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    /**
     * BottomNavigationViewのイベント設定
     */
    private void setNavViewEvent(){
        BottomNavigationView navView = findViewById(R.id.bottom_navigation); // ボトムナビゲーションビューのIDを修正
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 各メニューアイテムのIDを取得
                int itemId = item.getItemId();

                // ナビゲーションアクションを実行する
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);

                // IDに基づいて処理を実行
                if (itemId == R.id.navigation_home) {
                    // ホームに移動する処理をここに追加
                    navController.navigate(R.id.nav_home);
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    // 勤怠管理に移動する処理をここに追加
                    navController.navigate(R.id.nav_work);
                    return true;
                } else if (itemId == R.id.navigation_notifications) {
                    // 未実装に移動する処理をここに追加
                    Toast.makeText(MainActivity.this, "機能はこれから考えます", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }


}
