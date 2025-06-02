package com.h5base.ads.adapter;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.h5base.WebViewActivity;

public class MAXAdapter extends AdAdapter {

    private String SDK_KEY = "uJa4sGbT3zSQ9zw2o_CzJrxzsUI682WXsNFOfDGoT0Z5SsqQSWN52AZi7P0UtoN13VRe_MgEZodmMuzDKQwf0d";
    private String ID_MAX_INTERS = "b4d4d239f6c492d1";
    private String ID_MAX_VIDEO = "1709030b8d587eca";
    private String ID_MAX_BANNER = "a1ac232efd410234";
    private MaxInterstitialAd max_InterstitialAd;
    private MaxRewardedAd max_RewardedAd;
    private MaxAdView maxAdView;
    private RewardedAdCallback max_VideoCallback;
    private Activity mMainActivity;
    private boolean isRewardAvaiable;
    private boolean hasNoAds;
    private FrameLayout adContainerView;
    private boolean isNormalBanner;

    private boolean hasInitSDK = false;

    @Override
    public void initAds(Activity activity, FrameLayout frameLayout, boolean isNormalBanner, boolean hasNoAds) {
        this.mMainActivity = activity;
        this.hasNoAds = hasNoAds;
        this.adContainerView = frameLayout;
        this.isNormalBanner = isNormalBanner;

        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.
                builder(SDK_KEY)
                .setMediationProvider(AppLovinMediationProvider.MAX)
                .build();

        // Initialize the SDK with the configuration
        AppLovinSdk.getInstance(mMainActivity).initialize(initConfig, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration sdkConfig) {
//                hasInitSDK = true;
                // Start loading ads
                initIntersAd();//initMAXIntersAd();
                initVideoReward();//initMAXVideoReward();
                initBanner();
                hasInitSDK = true;
            }
        });
    }

    @Override
    public void initBanner() {
        adContainerView.setAlpha(isNormalBanner ? 1 : 0);
        maxAdView = new MaxAdView(ID_MAX_BANNER);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdCollapsed(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdLoaded(@NonNull MaxAd maxAd) {
                maxAdView.setAlpha(isNormalBanner ? 1 : 0);
            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(@NonNull MaxAd maxAd) {
            }

            @Override
            public void onAdClicked(@NonNull MaxAd maxAd) {
                onBannerClick();
            }

            @Override
            public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
            }
        });

        // Stretch to the width of the screen for banners to be fully functional
        int width = MATCH_PARENT;

        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = AppLovinSdkUtils.dpToPx(mMainActivity, 50);//getResources().getDimensionPixelSize( R.dimen.banner_height );
        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));

        // Set background color for banners to be fully functional
        maxAdView.setBackgroundColor(Color.WHITE);

//        ViewGroup rootView = findViewById( android.R.id.content );
//        rootView.addView( adView );

        adContainerView.removeAllViews();
        adContainerView.addView(maxAdView);
        // Load the ad
        maxAdView.loadAd();
        adContainerView.bringToFront();
    }

    private AdAdapter.IntersCloseCallback onIntersCloseCallback;

    @Override
    public void showInterstitial(Activity activity, AdAdapter.IntersCloseCallback closeCallback) {
        if (hasNoAds) return;
        if (max_InterstitialAd.isReady()) {
            onIntersCloseCallback = closeCallback;
            // `this` is the activity that will be used to show the ad
            max_InterstitialAd.showAd(mMainActivity);
        } else max_InterstitialAd.loadAd();
    }

    @Override
    public void showRewardedVideo(AdAdapter.RewardedAdCallback callback, AdAdapter.IntersCloseCallback closeCallback) {
        max_VideoCallback = callback;
        onIntersCloseCallback = closeCallback;
        if (max_RewardedAd.isReady()) {
            // `this` is the activity that will be used to show the ad
            max_RewardedAd.showAd(mMainActivity);
        } else {
//            DialogUtils.showNoRewardVideoDialog(activity);
            callback.onAdResult(false);
        }
    }

    @Override
    public boolean IsRewardAvaiable() {
        return isRewardAvaiable;
    }

    @Override
    public boolean IsInitSDK() {
        return hasInitSDK;
    }

    private void initIntersAd() {
        if (hasNoAds) return;
        max_InterstitialAd = new MaxInterstitialAd(ID_MAX_INTERS);
        max_InterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(@NonNull MaxAd maxAd) {
            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(@NonNull MaxAd maxAd) {
                // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
                max_InterstitialAd.loadAd();
                onIntersCloseCallback.onInterClose();
            }

            @Override
            public void onAdClicked(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
//                MainActivity.mInstance.showOverlayPanel(false);
            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
                max_InterstitialAd.loadAd();
            }
        });

        max_InterstitialAd.setRevenueListener(new MaxAdRevenueListener() {
            @Override
            public void onAdRevenuePaid(@NonNull MaxAd maxAd) {
                double revenue = maxAd.getRevenue(); // In USD
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "appLovin");
                params.putString(FirebaseAnalytics.Param.AD_SOURCE, maxAd.getNetworkName());
                params.putString(FirebaseAnalytics.Param.AD_FORMAT, maxAd.getFormat().getLabel());
                params.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, maxAd.getAdUnitId());
                params.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                params.putString(FirebaseAnalytics.Param.CURRENCY, "USD"); // All Applovin revenue is sent in USD
                WebViewActivity.analysticManager.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params);
            }
        });

        // Load the first ad
        max_InterstitialAd.loadAd();
    }

    private void initVideoReward() {
        max_RewardedAd = MaxRewardedAd.getInstance(ID_MAX_VIDEO);
        max_RewardedAd.setListener(new MaxRewardedAdListener() {
            @Override
            public void onUserRewarded(@NonNull MaxAd maxAd, @NonNull MaxReward maxReward) {
                max_VideoCallback.onAdResult(true);
            }

            @Override
            public void onAdLoaded(@NonNull MaxAd maxAd) {
                isRewardAvaiable = true;
            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(@NonNull MaxAd maxAd) {
                // Rewarded ad failed to display. AppLovin recommends that you load the next ad.
                max_RewardedAd.loadAd();
                onIntersCloseCallback.onInterClose();
            }

            @Override
            public void onAdClicked(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                isRewardAvaiable = false;
            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                // Rewarded ad failed to display. AppLovin recommends that you load the next ad.
                max_RewardedAd.loadAd();
                max_VideoCallback.onAdResult(false);
            }
        });

        max_RewardedAd.setRevenueListener(new MaxAdRevenueListener() {
            @Override
            public void onAdRevenuePaid(@NonNull MaxAd maxAd) {
                double revenue = maxAd.getRevenue(); // In USD
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "appLovin");
                params.putString(FirebaseAnalytics.Param.AD_SOURCE, maxAd.getNetworkName());
                params.putString(FirebaseAnalytics.Param.AD_FORMAT, maxAd.getFormat().getLabel());
                params.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, maxAd.getAdUnitId());
                params.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                params.putString(FirebaseAnalytics.Param.CURRENCY, "USD"); // All Applovin revenue is sent in USD
                WebViewActivity.analysticManager.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params);
            }
        });

        max_RewardedAd.loadAd();
    }

    private void onBannerClick() {
        adContainerView.setVisibility(View.GONE);
        // Lên lịch hiển thị lại FrameLayout sau 30 giây
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                adContainerView.setVisibility(View.VISIBLE);
                // Đặt lại alpha để đảm bảo FrameLayout hiển thị rõ
//                adContainerView.setAlpha(1.0f);
//                adView.setAlpha(1.0f);
            }
        }, 30000); // 30 giây = 30000 milliseconds
    }
}
