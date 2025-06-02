package com.h5base;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TimeChecker {
    private long startTime;
    // Constructor để khởi tạo thời gian bắt đầu
    public TimeChecker() {
        this.startTime = System.currentTimeMillis();
    }

    // Phương thức kiểm tra xem đã đủ 1 phút chưa
    public boolean hasOneMinutePassed() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime >= 0; // 30.000 milliseconds = nửa phút
    }

    // Phương thức lấy thời gian đã trôi qua (tính bằng giây)
    public long getElapsedTimeInSeconds() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime / 1000; // Chuyển từ milliseconds sang giây
    }
    private static final String KEY_LAST_OPEN_TIME = "last_open_time";
    private static final String KEY_LAST_CLOSE_TIME = "last_close_time";
    private static String PREFS_NAME = "MyAppPrefs";
    public static void onQuitGame(Context context){
//        Log.e("TimeSave-", "Quit Game");
        // Lưu thời gian khi Activity bị hủy
        long closeTime = System.currentTimeMillis();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LAST_CLOSE_TIME, closeTime);
        editor.apply();

//        Log.e("TimeSave", "Lưu thời gian đóng: " + closeTime);
    }
    public static long checkValidTime(Context context){
        // Lấy thời gian trước đó
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        long lastCloseTime = prefs.getLong(KEY_LAST_CLOSE_TIME, 0);
//        long elapsedTime = 1;
//        // Kiểm tra nếu đã có lần đóng trước đó
//        if (lastCloseTime != 0) {
//            long currentTime = System.currentTimeMillis();
//            elapsedTime = (currentTime - lastCloseTime) / 1000; // Tính giây
//
//            Log.d("TimeDiff", "Thời gian giữa 2 lần mở ứng dụng: " + elapsedTime + " giây");
//        }
//        return elapsedTime;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastCloseTime = prefs.getLong(KEY_LAST_CLOSE_TIME, 0);

        if (lastCloseTime == 0) {
            return 0; // Chưa có dữ liệu lần trước
        }

        long currentTime = System.currentTimeMillis();
        return (currentTime - lastCloseTime) / 1000; // Trả về thời gian tính bằng giây
    }
    // Hàm lấy thời gian giữa hai lần mở ứng dụng
    private static long getElapsedTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastCloseTime = prefs.getLong(KEY_LAST_CLOSE_TIME, 0);

        if (lastCloseTime == 0) {
            return 0; // Chưa có dữ liệu lần trước
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - lastCloseTime) / 1000; // Trả về thời gian tính bằng giây

        resetLastCloseTime(context);
        return elapsedTime;
    }
    public static boolean checkTime(Context context){
//        Log.d("TimeSave", "Thời gian giữa 2 lần mở ứng dụng");
        // Kiểm tra thời gian giữa hai lần mở
        long elapsedTime = getElapsedTime(context);
        if (elapsedTime > 0) {
//            Log.d("TimeSave", "Thời gian giữa 2 lần mở ứng dụng: " + elapsedTime + " giây");

            if (elapsedTime > 30) {
                Log.d("TimeSave", "!");
            }else{
//                Log.d("TimeSave", "Bạn đã không mở ứng dụng dưới "+elapsedTime+" giâyyyyyyyyy!");
                return true;
            }
        }
        return false;
    }
    // Hàm reset thời gian sau khi kiểm tra
    private static void resetLastCloseTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LAST_CLOSE_TIME, System.currentTimeMillis());
        editor.apply();

//        Log.d("TimeReset", "Đã reset lại thời gian");
    }
//    private static void saveLastTime(Context context){
//        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
//        // Lấy thời gian hiện tại
//        long currentTime = System.currentTimeMillis();
//
//        // Lấy SharedPreferences
//        //SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        long lastOpenTime = prefs.getLong(KEY_LAST_OPEN_TIME, 0);
//
//        // Nếu đã có thời gian trước đó, tính khoảng cách
//        if (lastOpenTime != 0) {
//            long elapsedTime = currentTime - lastOpenTime;
//            Log.d("TimeDiff", "Thời gian giữa hai lần mở: " + (elapsedTime / 1000) + " giây");
//        }
//
//        // Cập nhật lại thời gian mở ứng dụng
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putLong(KEY_LAST_OPEN_TIME, currentTime);
//        editor.apply();
//    }
}
