package com.h5base.ads.adapter;

import android.app.Activity;
import android.widget.FrameLayout;

public abstract class AdAdapter {
    public interface RewardedAdCallback {
        void onAdResult(boolean success);
    }
    public interface IntersCloseCallback{
        void onInterClose();
    }
    public abstract void initAds(Activity activity, FrameLayout frameLayout, boolean isNormalBanner, boolean hasNoAds);
    public abstract void initBanner();
    public abstract void showInterstitial(Activity activity, IntersCloseCallback _callback);

    public abstract void showRewardedVideo(RewardedAdCallback callback, IntersCloseCallback _callback);
    public abstract boolean IsRewardAvaiable();
    public abstract boolean IsInitSDK();
}