package com.h5base;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;

public class AnalysticManager {
    private static AnalysticManager instance;
    private HashMap<String, Long> gameStartTimes = new HashMap<>();
    private FirebaseManager firebaseManager;

    public AnalysticManager(Context context) {
        firebaseManager = FirebaseManager.getInstance(context);
    }

    public static synchronized AnalysticManager getInstance(Context context) {
        if (instance == null) {
            instance = new AnalysticManager(context);
        }
        return instance;
    }

    // Bắt đầu chơi game
    public void startGame(String gameId) {
        long startTime = SystemClock.elapsedRealtime();
        gameStartTimes.put(gameId, startTime);
        firebaseManager.logGameStart(gameId);
    }

    // Kết thúc game
    public void endGame(String gameId) {
        if (gameStartTimes.containsKey(gameId)) {
            long startTime = gameStartTimes.get(gameId);
            long endTime = SystemClock.elapsedRealtime();
            long duration = (endTime - startTime) / 1000; // Đổi ra giây
            Log.e("duration","duration "+duration);
            firebaseManager.logGameEnd(gameId, duration);

            // Nếu thời gian chơi < 5 giây => Log game quit early
            if (duration < 5) {
                firebaseManager.logGameQuitEarly(gameId, duration);
            }

            gameStartTimes.remove(gameId);
        }
    }

    // Chuyển game
    public void switchGame(String fromGameId, String toGameId) {
        endGame(fromGameId);
        startGame(toGameId);
        firebaseManager.logGameSwitch(fromGameId, toGameId);
    }
    public void logEvent(String event, Bundle bundle){
        firebaseManager.logEvent(event, bundle);
    }
}
