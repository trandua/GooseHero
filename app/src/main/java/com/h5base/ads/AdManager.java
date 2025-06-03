package com.h5base.ads;


import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
//import com.facebook.ads.AdSettings;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.h5base.BillingManager;
import com.h5base.DialogUtils;
import com.h5base.TimeChecker;
import com.h5base.ads.adapter.AdmobAdapter;
import com.h5base.ads.adapter.MAXAdapter;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
import com.h5base.ads.adapter.AdAdapter;
import com.h5base.ads.adapter.IronsourceAdapter;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;
import com.unity3d.services.banners.api.BannerListener;

import java.util.Arrays;
import java.util.List;

public class AdManager {
    private static BillingManager mBillingManager;
    private static TimeChecker timeChecker;
    public static AdAdapter adAdapter;
    private static boolean hasRemoveAds;
    public static boolean IsRewardAvaiable() {
        return adAdapter.IsRewardAvaiable();
    }
    private static Activity mMainActivity;
    private static boolean isNormalBanner;
    public static boolean HasInitSDK(){
        return adAdapter.IsInitSDK();
    }

    public static void init(Activity mainActivity, BillingManager billingManager, FrameLayout frameLayout) {
        mBillingManager = billingManager;
        timeChecker = new TimeChecker();
        mMainActivity = mainActivity;
        isNormalBanner = true;
        hasRemoveAds = mBillingManager != null && mBillingManager.isPurchased("remove_ads");
        timeChecker = new TimeChecker();
//        adAdapter = new AdmobAdapter();
        adAdapter = new MAXAdapter();
//        adAdapter = new IronsourceAdapter();

        ViewParent parent = frameLayout.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup parentViewGroup = (ViewGroup) parent;
            if(isNormalBanner){
                if (parentViewGroup.getChildCount() > 1) {
                    View firstChild = parentViewGroup.getChildAt(1);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) firstChild.getLayoutParams();
                    params.weight = 0;
                    firstChild.setLayoutParams(params);
                }
            }
            parentViewGroup.setAlpha(isNormalBanner ? 1 : 0); // Thiết lập độ trong suốt 70%
        }
        adAdapter.initAds(mainActivity, frameLayout, isNormalBanner, hasRemoveAds);
    }

    public static void showIntersAd(Activity activity){
        adAdapter.showInterstitial(activity, new AdAdapter.IntersCloseCallback(){
            @Override
            public void onInterClose() {
                startTime = System.currentTimeMillis();
//                    System.out.println("TAGG-Inters Close");
            }
        });
    }

    public static void showRewardedAd(Activity activity, AdAdapter.RewardedAdCallback callback){
        if(adAdapter.IsRewardAvaiable()){
            adAdapter.showRewardedVideo(callback, new AdAdapter.IntersCloseCallback(){
                @Override
                public void onInterClose() {
                    startTime = System.currentTimeMillis();
//                System.out.println("TAGG-Inters Close by Video");
                }
            });
        }else{
            DialogUtils.showNoRewardVideoDialog(mMainActivity);
        }
    }
    public static boolean Is30Min() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime >= 30.000; // 30.000 milliseconds = nửa phút
    }

    private static long startTime;
}