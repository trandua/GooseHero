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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.h5base.Utils;
import com.h5base.WebViewActivity;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
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
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;

import java.util.Arrays;
import java.util.List;

public class IronsourceAdapter extends AdAdapter {

    private String IRON_KEY = "1c546f54d";//"85460dcd"; test key, realKey: 1c546f54d
    public final String INTERSTITIAL_AD_UNIT_ID = "aeyqi3vqlv6o8sh9";//"aeyqi3vqlv6o8sh9"; test id, real id:
    public final String BANNER_AD_UNIT_ID = "thnfvcsog13bhn08";//"thnfvcsog13bhn08"; test id, real id:

    private LevelPlayBannerAdView levelPlayBanner;
    private LevelPlayInterstitialAd mInterstitialAd;
    private RewardedAdCallback max_VideoCallback;
    private AdAdapter.IntersCloseCallback onIntersCloseCallback;
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

        IntegrationHelper.validateIntegration(mMainActivity);
        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
            @Override
            public void onAdAvailable(AdInfo adInfo) {
                isRewardAvaiable = true;
            }

            @Override
            public void onAdUnavailable() {
                isRewardAvaiable = false;
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {

            }

            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                max_VideoCallback.onAdResult(false);
            }

            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) {

            }

            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                max_VideoCallback.onAdResult(true);
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                onIntersCloseCallback.onInterClose();
            }
        });
        IronSource.shouldTrackNetworkState(mMainActivity, true);
        IronSource.addImpressionDataListener(new ImpressionDataListener() {
            @Override
            public void onImpressionSuccess(ImpressionData impressionData) {
                double revenue = impressionData.getRevenue(); // In USD
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "ironsource");
                params.putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.getAdNetwork());
                params.putString(FirebaseAnalytics.Param.AD_FORMAT, impressionData.getAdFormat());
                params.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.getMediationAdUnitName());
                params.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                params.putString(FirebaseAnalytics.Param.CURRENCY, "USD"); // All Applovin revenue is sent in USD
                WebViewActivity.analysticManager.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params);
            }
        });
        // Init the SDK when implementing the Multiple Ad Units Interstitial and Banner API, and Rewarded using legacy APIs
        List<LevelPlay.AdFormat> legacyAdFormats = Arrays.asList(LevelPlay.AdFormat.REWARDED, LevelPlay.AdFormat.BANNER);

        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(IRON_KEY)
                .withLegacyAdFormats(legacyAdFormats)
                .build();
        LevelPlay.init(mMainActivity, initRequest, new LevelPlayInitListener() {
            @Override
            public void onInitSuccess(@NonNull LevelPlayConfiguration levelPlayConfiguration) {
                initIntersAd();//initMAXIntersAd();
//                initVideoReward();//initMAXVideoReward();
//                initBanner();// Banner cua LevelPlay
                loadHiddenBanner(frameLayout, activity);//Banner Admob
                hasInitSDK = true;
            }

            @Override
            public void onInitFailed(@NonNull LevelPlayInitError levelPlayInitError) {

            }
        });
    }

    @Override
    public void initBanner() {
        adContainerView.setAlpha(isNormalBanner ? 1 : 0);

        levelPlayBanner = new LevelPlayBannerAdView(mMainActivity, BANNER_AD_UNIT_ID);
        levelPlayBanner.setAdSize(LevelPlayAdSize.BANNER);

        levelPlayBanner.setBannerListener(new LevelPlayBannerAdViewListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                levelPlayBanner.setAlpha(isNormalBanner ? 1 : 0);
                System.out.println("TAGG-Banner Loaded");
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {
                System.out.println("TAGG-Banner Load fail: " + levelPlayAdError.getAdUnitId() + " vs " + levelPlayAdError.getErrorCode() + " vs " + levelPlayAdError.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                onBannerClick();
            }
        });


        adContainerView.removeAllViews();
        adContainerView.addView(levelPlayBanner);
        // Load the banner ad
        levelPlayBanner.loadAd();
        adContainerView.bringToFront();
    }

    public void loadHiddenBanner(FrameLayout frmAdContainerView, Activity mActivity){
//        adContainerView = frmAdContainerView;

        // Create a new ad view.
        AdView adAdmobHiddenView = new AdView(mActivity);
        adAdmobHiddenView.setAdUnitId(Utils.ID_HBANNER);
        adAdmobHiddenView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, 360));
        adAdmobHiddenView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                onBannerClick();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
//                System.out.println("TAGG-Banner Admob fail to load");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
//                System.out.println("TAGG-Banner Admob Loaded");
                adAdmobHiddenView.setAlpha(0);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
            }
        });

        frmAdContainerView.removeAllViews();
        frmAdContainerView.addView(adAdmobHiddenView);


        AdRequest adRequest = new AdRequest.Builder().build();
        adAdmobHiddenView.loadAd(adRequest);
        frmAdContainerView.bringToFront();
    }

    @Override
    public void showInterstitial(Activity activity, AdAdapter.IntersCloseCallback closeCallback) {
        if (hasNoAds) return;
        if (mInterstitialAd.isAdReady()) {
            onIntersCloseCallback = closeCallback;
            // Show ad
            mInterstitialAd.showAd(activity);
        }else mInterstitialAd.loadAd();
    }

    @Override
    public void showRewardedVideo(AdAdapter.RewardedAdCallback callback, AdAdapter.IntersCloseCallback closeCallback) {
        max_VideoCallback = callback;
        onIntersCloseCallback = closeCallback;
        if (IronSource.isRewardedVideoAvailable())
            IronSource.showRewardedVideo();
        else {
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
        mInterstitialAd = new LevelPlayInterstitialAd(INTERSTITIAL_AD_UNIT_ID);
        mInterstitialAd.setListener(new LevelPlayInterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {

            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {

            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                mInterstitialAd.loadAd();
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                mInterstitialAd.loadAd();
                onIntersCloseCallback.onInterClose();
            }
        });
    }

    private void initVideoReward() {
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
