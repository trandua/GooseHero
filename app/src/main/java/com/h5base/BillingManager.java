package com.h5base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import com.android.billingclient.api.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingManager {
    public static final String TAG = "BillingManager";
    public static final String PREF_NAME = "MyAppPrefs";

    // Danh sách sản phẩm
    public static final Map<String, String> PRODUCT_IDS = new HashMap<String, String>() {{
        put("remove_ads", "remove_ads");
        put("pack_coin_1", "pack_coin_1");
        put("pack_coin_2", "pack_coin_2");
        put("pack_coin_3", "pack_coin_3");
        put("pack_coin_4", "pack_coin_4");
        put("pack_coin_5", "pack_coin_5");
        put("pack_coin_6", "pack_coin_6");
    }};

    public static int[] rewardAmount = new int[]{10000, 21000, 55000, 115000, 240000, 625000};

    private final BillingClient billingClient;
    private final Context context;
    private final Map<String, String> productPrices = new HashMap<>(); // Lưu giá của các sản phẩm

    public static WebViewActivity webviewactivity;

    public BillingManager(Context context) {
        this.context = context;
        billingClient = BillingClient.newBuilder(context)
                .setListener(this::onPurchaseUpdate)
                .enablePendingPurchases()
                .build();
        startConnection();
    }

    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing setup successful");
                    checkPurchasedItems();
                    loadProductPrices(); // Load giá sản phẩm ngay khi kết nối thành công
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected");
            }
        });
    }

    // Phương thức để load giá của tất cả các sản phẩm
    public void loadProductPrices() {
        List<String> skuList = Arrays.asList(
                PRODUCT_IDS.get("wing_clash_coin_pack_1"),
                PRODUCT_IDS.get("wing_clash_coin_pack_2"),
                PRODUCT_IDS.get("wing_clash_coin_pack_3"),
                PRODUCT_IDS.get("wing_clash_coin_pack_4"),
                PRODUCT_IDS.get("wing_clash_coin_pack_5"),
                PRODUCT_IDS.get("wing_clash_coin_pack_6")
        );

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Lỗi khi lấy thông tin giá sản phẩm: " + billingResult.getDebugMessage());
                return;
            }

            if (skuDetailsList == null || skuDetailsList.isEmpty()) {
                Log.e(TAG, "Không tìm thấy thông tin giá sản phẩm");
                return;
            }

            // Lưu giá của các sản phẩm vào productPrices
            for (SkuDetails skuDetails : skuDetailsList) {
                String productId = skuDetails.getSku();
                String price = skuDetails.getPrice(); // Giá đã được định dạng theo địa phương (localized price)
                productPrices.put(productId, price);
                Log.d(TAG, "Loaded price for " + productId + ": " + price);
            }
        });
    }

    // Phương thức để lấy giá của một sản phẩm
    public String getProductPrice(String productKey) {
        if (!PRODUCT_IDS.containsKey(productKey)) {
            return "N/A"; // Trả về giá mặc định nếu không tìm thấy sản phẩm
        }
        String productId = PRODUCT_IDS.get(productKey);
        return productPrices.getOrDefault(productId, "N/A");
    }

    public void startPurchase(Activity activity, String productKey) {
        if (!PRODUCT_IDS.containsKey(productKey)) {
            Log.e(TAG, "Sản phẩm không hợp lệ: " + productKey);
            return;
        }

        if (billingClient == null || !billingClient.isReady()) {
            Log.e(TAG, "BillingClient chưa sẵn sàng!");
            return;
        }

        String productId = PRODUCT_IDS.get(productKey);
        List<String> skuList = Arrays.asList(productId);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Lỗi khi lấy thông tin sản phẩm: " + billingResult.getDebugMessage());
                return;
            }

            if (skuDetailsList == null || skuDetailsList.isEmpty()) {
                Log.e(TAG, "Không tìm thấy sản phẩm: " + productId);
                return;
            }

            SkuDetails skuDetails = skuDetailsList.get(0);
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();

            int responseCode = billingClient.launchBillingFlow(activity, flowParams).getResponseCode();
            if (responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Lỗi khi bắt đầu giao dịch: " + responseCode);
            } else {
                Log.i(TAG, "Bắt đầu giao dịch thành công: " + productId);
            }
        });
    }

    private void onPurchaseUpdate(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                String productId = purchase.getSkus().get(0);

                if (PRODUCT_IDS.containsValue(productId) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    if (productId.equals("remove_ads")) {
                        acknowledgePurchase(purchase);
                    } else {
                        consumePurchase(purchase);
                    }
                }
            }
        }else {
            if (webviewactivity != null){
                webviewactivity.hideLoadingOverlay();
            }
        }
    }

    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(params, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                savePurchaseState(purchase.getSkus().get(0), true);
                Toast.makeText(context, "Gỡ quảng cáo thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consumePurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.consumeAsync(consumeParams, (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                savePurchaseState(purchase.getSkus().get(0), true);
            }
        });
    }

    private void savePurchaseState(String productId, boolean purchased) {
        Log.e("savePurchaseState", "Giao dịch thành công: " + productId);

        if (purchased) {
            switch (productId) {
                case "remove_ads":
                    SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(productId, true).apply();
                    Log.d(TAG, "Người dùng đã mua: Gỡ quảng cáo");
                    break;

                default:
                    grantReward(productId);
                    break;
            }

            Toast.makeText(context, "Mua thành công: " + productId, Toast.LENGTH_SHORT).show();
        }
    }

    private void grantReward(String productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(productId, true).apply();
        int amount = 0;
        switch (productId) {
            case "pack_coin_1":
                amount = rewardAmount[0];
                break;
            case "pack_coin_2":
                amount = rewardAmount[1];
                break;
            case "pack_coin_3":
                amount = rewardAmount[2];
                break;
            case "pack_coin_4":
                amount = rewardAmount[3];
                break;
            case "pack_coin_5":
                amount = rewardAmount[4];
                break;
            case "pack_coin_6":
                amount = rewardAmount[5];
                break;
        }

        if (amount > 0) {
            int finalAmount1 = amount;
            ((Activity)context).runOnUiThread(() -> {
                String jsCode= "(function() {" +
                        "   if (typeof addKhiemCoin === 'function') {" +
                        "       addKhiemCoin(" + finalAmount1 + ");" +
                        "   }" +
                        "})();";

                WebViewActivity.webView.evaluateJavascript(jsCode, null);
                webviewactivity.Dissmiss();
                Toast.makeText(context, "You have received " + finalAmount1 + " coin!", Toast.LENGTH_LONG).show();
            });
        }
    }

    public boolean isPurchased(String productKey) {
        if (!PRODUCT_IDS.containsKey(productKey)) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PRODUCT_IDS.get(productKey), false);
    }

    private void checkPurchasedItems() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    if (purchase.getSkus().get(0).equals("remove_ads")){
                        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putBoolean(purchase.getSkus().get(0), true).apply();
                    }
                }
            }
        });
    }
}