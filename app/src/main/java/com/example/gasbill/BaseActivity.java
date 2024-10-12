package com.example.gasbill;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String MUSIC_PLAYING_KEY = "music_playing";

    @Override
    protected void onResume() {
        super.onResume();

        // Khởi động dịch vụ nhạc
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent); // Bắt đầu dịch vụ âm nhạc

        // Đảm bảo rằng âm nhạc sẽ tiếp tục phát
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isMusicPlaying = preferences.getBoolean(MUSIC_PLAYING_KEY, true); // Kiểm tra trạng thái âm nhạc

        if (isMusicPlaying) {
            // Truyền trạng thái nhạc
            serviceIntent.putExtra(MUSIC_PLAYING_KEY, true);
            startService(serviceIntent); // Khởi động dịch vụ âm nhạc
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Không dừng dịch vụ âm nhạc ở đây
        // Để dịch vụ âm nhạc tiếp tục chạy khi chuyển đổi giữa các Activity
    }
}
