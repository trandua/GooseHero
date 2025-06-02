window.h5Ubi = {
    isAndroidWebView: function () {
        const ua = navigator.userAgent || '';
        const isAndroid = /Android/i.test(ua);
        const isWebView = ua.includes('wv') || (ua.includes('Version/') && !ua.includes('Chrome'));
        return isAndroid && isWebView;
      },
    
    onCheckCallAndroid: function ({ onAndroidWebView, onBrowser }) {
        if (this.isAndroidWebView()) {
          onAndroidWebView?.();
        } else {
          onBrowser?.();
        }
      },
      onCallShowInters: function ({ onAndroidWebView, onBrowser }) {
        if (this.isAndroidWebView() && typeof Android !== 'undefined') {
          onAndroidWebView?.();
          Android.showInterstitial();
        } else {
          onBrowser?.();
        }
      },
      onCallShowImageInters: function ({ onAndroidWebView, onBrowser }) {
        if (this.isAndroidWebView() && typeof Android !== 'undefined') {
          onAndroidWebView?.();
          Android.showIntersImage();
        } else {
          onBrowser?.();
        }
      },
    onCallShowReward: function ({ onAndroidWebView, onBrowser }) {
        if (this.isAndroidWebView() && typeof Android !== 'undefined') {
          onAndroidWebView?.();
          Android.showRewardAd();
        } else {
          onBrowser?.();
        }
      },
      onCheckAndShowRewardAd: function ({ onAndroidWebView, onBrowser, onError }) {
        if (this.isAndroidWebView()) {
            // Define callbacks for reward ad status
            window.onRewardAdLoaded = function () {
                onAndroidWebView?.();
                Android.callShowRewardAd();
            };
            window.onRewardAdLFailedToLoad = function () {
                console.error("Reward ad failed to load");
                Android.onNoVideoAvaiable();
                onError?.("Reward ad failed to load");
            };
            // Call Android to check video reward status
            if (typeof Android !== 'undefined' && typeof Android.callCheckVideo === 'function') {
                Android.callCheckVideo();
            } else {
                console.error("Android interface not available");
                onError?.("Android interface not available");
            }
        } else {
            onBrowser?.();
        }
    }
};