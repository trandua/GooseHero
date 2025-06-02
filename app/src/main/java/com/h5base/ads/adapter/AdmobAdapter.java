package com.h5base.ads.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
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

public class AdmobAdapter extends AdAdapter {
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;
    private AdView adAdmobHiddenView;
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
        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(mMainActivity, initializationStatus -> {
                    });
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initIntersAd();
                            initVideoReward();
//                            initBanner();
                            hasInitSDK = true;
                        }
                    });
                })
                .start();
    }

    @Override
    public void initBanner() {
        adContainerView.setAlpha(isNormalBanner ? 1 : 0);
        // Create a new ad view.
        adAdmobHiddenView = new AdView(mMainActivity);
        adAdmobHiddenView.setAdUnitId(BANNER_AD_UNIT_ID);
        adAdmobHiddenView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mMainActivity, 360));
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
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adAdmobHiddenView.setAlpha(isNormalBanner ? 1 : 0);
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

        adContainerView.removeAllViews();
        adContainerView.addView(adAdmobHiddenView);


        AdRequest adRequest = new AdRequest.Builder().build();
        adAdmobHiddenView.loadAd(adRequest);
        adContainerView.bringToFront();
    }

    private IntersCloseCallback onIntersCloseCallback;

    @Override
    public void showInterstitial(Activity activity, IntersCloseCallback closeCallback) {
        if (hasNoAds) return;
        if (mInterstitialAd != null) {
            onIntersCloseCallback = closeCallback;
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mInterstitialAd.show(activity);
                }
            });
        } else {
            initIntersAd();
        }
    }

    @Override
    public void showRewardedVideo(RewardedAdCallback callback, IntersCloseCallback closeCallback) {
        if (rewardedAd != null) {
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
//                            onIntersCloseCallback = closeCallback;
                            rewardedAd = null;
                            initVideoReward(); // Tải quảng cáo mới sau khi đóng
//                            onIntersCloseCallback.onInterClose();
                            closeCallback.onInterClose();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            rewardedAd = null;
                            callback.onAdResult(false);
                        }
                    });

                    rewardedAd.show(mMainActivity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            callback.onAdResult(true);
                        }
                    });
                }
            });
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

        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InterstitialAd.load(mMainActivity, INTERSTITIAL_AD_UNIT_ID, getRequest(),
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                mInterstitialAd = interstitialAd;
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdClicked() {
                                        // Called when a click is recorded for an ad.
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when ad is dismissed.
                                        // Set the ad reference to null so you don't show the ad a second time.
                                        onIntersCloseCallback.onInterClose();
                                        mInterstitialAd = null;
                                        mMainActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                initIntersAd();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when ad fails to show.
                                        mInterstitialAd = null;
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        // Called when an impression is recorded for an ad.
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when ad is shown.
                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                mInterstitialAd = null;
                            }
                        });
            }
        });
    }

    private void initVideoReward() {
        RewardedAd.load(mMainActivity, REWARDED_AD_UNIT_ID,
                getRequest(), new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        isRewardAvaiable = false;
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        isRewardAvaiable = true;
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                rewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                rewardedAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }
                        });
                    }
                });
    }

    private AdRequest getRequest() {
        return new AdRequest.Builder().build();
    }

    //    private void onInterClose() {
//        startTime = System.currentTimeMillis();
//    }
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