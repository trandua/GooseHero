package com.h5base;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewAssetLoader;

import com.example.h5gamebase.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.h5base.ads.AdManager;
import com.ironsource.mediationsdk.IronSource;
import com.kochava.tracker.Tracker;
import com.h5base.ads.adapter.AdAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
public class WebViewActivity extends Activity {
    public static WebView webView;
    private RelativeLayout loadingOverlay;
    private Timer networkCheckTimer; // Timer để kiểm tra kết nối liên tục
    private boolean hasInternetAtStart; // Biến để lưu trạng thái kết nối khi vào activity
    public static boolean hasShownNoInternetDialog = false; // Biến để tránh hiển thị dialog nhiều lần

    public static BillingManager billingManager;
    public static AnalysticManager analysticManager;

    private LocalWebServer webServer;
    private static final int PORT = 8080;
    int countToShowAd = 0;
    FrameLayout adContainerView;
    LinearLayout top_bar_layout;
    public static boolean shouldShowDialog = true;

    private static final int[] PORT_CANDIDATES = {
            8081, 8082, 8083, 8084, 8085,
            8881, 8882, 8883, 8884, 8885,
            9091, 9092, 9093, 9094, 9095,
            10087, 10088, 10089, 10090, 10091
    };
    private static final int MAX_RETRY = 3;
    private static final int TIMEOUT_MS = 15000; // Timeout 15 giây
    private static final int RANDOM_PORT_MIN = 1024;
    private static final int RANDOM_PORT_MAX = 65535;

    private int currentPort = -1;
    private int retryCount = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private final Map<Integer, Runnable> timeoutRunnables = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.parseColor("#2C1B3C"));
//        LinearLayout rootLayout = new LinearLayout(this);
//        rootLayout.setBackgroundColor(Color.parseColor("#2C1B3C"));

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |  // Ẩn StatusBar
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Giữ các cử chỉ, tránh ẩn lại thanh điều hướng
        );
//        int gameConfig = getIntent().getIntExtra("LANSCAPE", -1);
//        if (gameConfig == 1) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        setContentView(R.layout.activity_webview);

        // Khởi tạo BillingManager
        billingManager = new BillingManager(this);
        analysticManager = new AnalysticManager(this);
        BillingManager.webviewactivity = this;
        adContainerView = findViewById(R.id.ad_view_container);
        top_bar_layout = findViewById(R.id.top_bar);
        // Init Ads
        AdManager.init(this, billingManager, adContainerView);

        loadingOverlay = findViewById(R.id.loadingOverlay);

        countToShowAd = 0;
//        WebViewSetup();
        initH5Web();
    }
    private void initH5Web(){
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        // Giả lập server nội bộ từ assets
        WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://appassets.androidplatform.net/assets/" + Utils.game_name + "/index.html");
        webView.addJavascriptInterface(this, "Android");
        Dissmiss();
    }
    public void Dissmiss() {
        hideLoadingOverlay();
    }
    private void WebViewSetup() {
        webView = findViewById(R.id.webView);

        // Set full screen margin
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) webView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        webView.setLayoutParams(params);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setInitialScale(100);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());

        // Set WebViewClient để xử lý onPageFinished
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("WebViewAAA", "Page loaded: " + url);
                cancelTimeout(currentPort);
                Log.d("WebViewAAA", "Timeout callback canceled for port " + currentPort);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("WebViewAAA", "WebView error: " + description + " (" + errorCode + ") for URL: " + failingUrl);
                cancelTimeout(currentPort);
                retryLoadWebView("WebView error: " + description);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        startLocalWebServerAndLoad();
    }
    private void startLocalWebServerAndLoad() {
        // Dừng server cũ và hủy timeout
        stopServerAndCancelTimeouts();

        try {
            // Kiểm tra thư mục tồn tại
            AssetManager assetManager = getAssets();
            //String[] files = assetManager.list("1_Defense");
//            String[] files = assetManager.list("1_HeroGooseDefense");
//            if (files == null || files.length == 0) {
//                throw new FileNotFoundException("Folder 1_Defense is empty or missing.");
//            }

            // Tìm port khả dụng
            currentPort = findAvailablePort();
            if (currentPort == -1) {
                throw new IOException("No available port for LocalWebServer.");
            }

            // Khởi động LocalWebServer
//            webServer = new LocalWebServer(this, currentPort, "1_Defense");
            webServer = new LocalWebServer(this, currentPort, Utils.game_name);
            webServer.start();

            // Đợi server sẵn sàng (tối đa 2 giây)
            long startTime = System.currentTimeMillis();
            while (!webServer.isAlive() && System.currentTimeMillis() - startTime < 2000) {
                Thread.sleep(100);
            }

            if (!webServer.isAlive()) {
                throw new IOException("LocalWebServer failed to start on port " + currentPort);
            }

            Log.i("WebViewAAA", "WebServer started at port " + currentPort);

            // Load URL
            String localUrl = "http://127.0.0.1:" + currentPort + "/";
            //webView.loadUrl(localUrl);
            webView.loadUrl("https://appassets.androidplatform.net/assets/" + Utils.game_name + "/index.html");
            webView.addJavascriptInterface(this, "Android");

            // Đặt timeout
            Runnable timeoutRunnable = () -> {
                Log.w("WebViewAAA", "Timeout callback triggered for port " + currentPort);
                String currentUrl = webView.getUrl();
                if (currentUrl == null || !currentUrl.startsWith("http://127.0.0.1:" + currentPort)) {
                    retryLoadWebView("Timeout loading WebView on port " + currentPort);
                } else {
                    Log.d("WebViewAAA", "Timeout ignored: page already loaded on port " + currentPort);
                }
            };
            timeoutRunnables.put(currentPort, timeoutRunnable);
            handler.postDelayed(timeoutRunnable, TIMEOUT_MS);
            Log.d("WebViewAAA", "Timeout scheduled for port " + currentPort);

        } catch (IOException e) {
            cancelTimeout(currentPort);
            retryLoadWebView("IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            cancelTimeout(currentPort);
            retryLoadWebView("InterruptedException: " + e.getMessage());
        } catch (Exception e) {
            cancelTimeout(currentPort);
            retryLoadWebView("Unexpected error: " + e.getMessage());
        }
    }

    private void stopServerAndCancelTimeouts() {
        // Hủy tất cả timeout
        for (Runnable runnable : timeoutRunnables.values()) {
            handler.removeCallbacks(runnable);
        }
        timeoutRunnables.clear();
        Log.d("WebViewAAA", "All timeout callbacks canceled");

        // Dừng server
        if (webServer != null && webServer.isAlive()) {
            try {
                webServer.stop();
                Log.i("WebViewAAA", "Stopped previous WebServer on port " + currentPort);
            } catch (Exception e) {
                Log.w("WebViewAAA", "Error stopping previous WebServer: " + e.getMessage());
            }
        }

        // Đảm bảo port được giải phóng
        if (currentPort != -1) {
            try (ServerSocket socket = new ServerSocket(currentPort)) {
                socket.setReuseAddress(true);
            } catch (IOException e) {
                Log.w("WebViewAAA", "Port " + currentPort + " still in use, but proceeding");
            }
        }
    }

    private void cancelTimeout(int port) {
        Runnable runnable = timeoutRunnables.remove(port);
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            Log.d("WebViewAAA", "Timeout canceled for port " + port);
        }
        // Hủy tất cả callback để an toàn
        handler.removeCallbacksAndMessages(null);
    }

    private int findAvailablePort() {
        // Thử các port trong PORT_CANDIDATES
        for (int port : PORT_CANDIDATES) {
            if (isPortAvailable(port)) {
                Log.d("WebViewAAA", "Port " + port + " is available");
                return port;
            }
            Log.w("WebViewAAA", "Port " + port + " is in use");
        }

        // Thử port ngẫu nhiên nếu PORT_CANDIDATES hết
        Random random = new Random();
        int maxAttempts = 10; // Giới hạn số lần thử port ngẫu nhiên
        for (int i = 0; i < maxAttempts; i++) {
            int randomPort = RANDOM_PORT_MIN + random.nextInt(RANDOM_PORT_MAX - RANDOM_PORT_MIN + 1);
            if (isPortAvailable(randomPort)) {
                Log.d("WebViewAAA", "Random port " + randomPort + " is available");
                return randomPort;
            }
            Log.w("WebViewAAA", "Random port " + randomPort + " is in use");
        }

        return -1; // Không tìm được port
    }

    private boolean isPortAvailable(int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.w("WebViewAAA", "Error closing socket: " + e.getMessage());
                }
            }
        }
    }

    private void retryLoadWebView(String reason) {
        Log.e("WebViewAAA", "Retrying (" + retryCount + "/" + MAX_RETRY + ") due to: " + reason);

        if (retryCount < MAX_RETRY) {
            retryCount++;
            stopServerAndCancelTimeouts();
            handler.postDelayed(this::startLocalWebServerAndLoad, 1000);
        } else {
            Toast.makeText(this, "Lỗi load WebView: " + reason, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        shouldShowDialog = true;
        String jsCode = "javascript:applyPreservedVisibleSize2()";
        webView.evaluateJavascript(jsCode, null);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (networkCheckTimer != null){
            networkCheckTimer.cancel();
        }

        shouldShowDialog = false;

        webView.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        TimeChecker.onQuitGame(this);
    }
    @Override
    protected void onDestroy() {
        if (webServer != null && webServer.isAlive()) {
            try {
                webServer.stop();
                Log.i("WebViewAAA", "WebServer stopped on destroy");
            } catch (Exception e) {
                Log.w("WebViewAAA", "Error stopping WebServer on destroy: " + e.getMessage());
            }
        }
        super.onDestroy();
        TimeChecker.onQuitGame(this);
//        bannerHandler.removeCallbacksAndMessages(null);
        // Dừng timer khi activity bị hủy
        if (networkCheckTimer != null) {
            networkCheckTimer.cancel();
            networkCheckTimer = null;
        }
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.onPause();
            webView.destroy();
            webView = null;
        }
        System.gc();
    }
    public void showLoadingOverlay() {
        runOnUiThread(() -> {
            if (loadingOverlay != null) {
                loadingOverlay.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideLoadingOverlay() {
        runOnUiThread(() -> {
            if (loadingOverlay != null) {
                loadingOverlay.setVisibility(View.GONE);
            }
        });
    }
    @JavascriptInterface
    public final void showInterstitial() {
        System.out.println("TAGG-chromium-CallShow Inters");

        long time = 0;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                AdManager.showIntersAd(WebViewActivity.this);
            }
        }, time);
    }
    @JavascriptInterface
    public final void showRewardAd() {
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
                if (success) {
                    // Người dùng đã xem hết quảng cáo và nhận thưởng
                    // Gửi kết quả ngược lại cho JavaScript
                    webView.post(() -> webView.evaluateJavascript("javascript:window.rewardCallback();", null));
                } else {
                    webView.post(() -> webView.evaluateJavascript("javascript:window.rewardFailCallback();", null));
                }
            }
        });
    }
    @JavascriptInterface
    public final void callShowRewardAd() {
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
            }
        });
    }
    @JavascriptInterface
    public final void callCheckVideo(){
        if(AdManager.IsRewardAvaiable()){
            webView.post(() -> webView.evaluateJavascript("javascript:onRewardAdLoaded()", null));
        }else
            webView.post(() -> webView.evaluateJavascript("javascript:onRewardAdLFailedToLoad()", null));
    }
    @JavascriptInterface
    public final void onNoVideoAvaiable(){
        DialogUtils.showNoRewardVideoDialog(this);
    }
}