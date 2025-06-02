package com.h5base;
import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAnalytics firebaseAnalytics;

    private FirebaseManager(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    // Log khi game bắt đầu
    public void logGameStart(String gameId) {
        Bundle bundle = new Bundle();
        bundle.putString("game_id", gameId);
        firebaseAnalytics.logEvent("game_start", bundle);
        firebaseAnalytics.logEvent("game_start_"+gameId, new Bundle());
    }

    // Log khi game kết thúc
    public void logGameEnd(String gameId, long duration) {
        Bundle bundle = new Bundle();
        bundle.putString("game_id", gameId);
        bundle.putLong("duration", duration);
        firebaseAnalytics.logEvent("game_end", bundle);
    }

    // Log khi người chơi thoát game sớm
    public void logGameQuitEarly(String gameId, long duration) {
        Bundle bundle = new Bundle();
        bundle.putString("game_id", gameId);
        bundle.putLong("early_quit_duration", duration);
        firebaseAnalytics.logEvent("game_end", bundle);

        long block = duration%10;
        firebaseAnalytics.logEvent("game_end_block_"+block, new Bundle());
    }

    // Log khi người chơi chuyển từ game này sang game khác
    public void logGameSwitch(String fromGameId, String toGameId) {
        Bundle bundle = new Bundle();
        bundle.putString("from_game", fromGameId);
        bundle.putString("to_game", toGameId);
        firebaseAnalytics.logEvent("game_switch", bundle);
    }
    public void logEvent(String event, Bundle bundle){
        firebaseAnalytics.logEvent(event, bundle);
    }
}
