package com.example.gasbill;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String MUSIC_PLAYING_KEY = "music_playing";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music); // Thay thế bằng tệp nhạc của bạn
        mediaPlayer.setLooping(true); // Đặt lặp lại cho phát liên tục
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isMusicPlaying = preferences.getBoolean(MUSIC_PLAYING_KEY, true); // Mặc định là true

        // Khởi động nhạc nếu cho phép
        if (isMusicPlaying && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        return START_STICKY; // Đảm bảo dịch vụ tiếp tục chạy
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic(); // Gọi phương thức dừng nhạc
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // Tạm dừng nhạc
            }
            mediaPlayer.release(); // Giải phóng tài nguyên
            mediaPlayer = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Tắt nhạc khi ứng dụng bị thoát
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Dừng nhạc khi thoát ứng dụng
            editor.putBoolean(MUSIC_PLAYING_KEY, true); // Lưu trạng thái nhạc
        } else {
            editor.putBoolean(MUSIC_PLAYING_KEY, false); // Lưu trạng thái không phát
        }
        editor.apply(); // Lưu thay đổi
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Không ràng buộc
    }
}
