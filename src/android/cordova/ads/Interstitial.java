package admob.plus.cordova.ads;

import androidx.annotation.NonNull;

import android.util.Log;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.ResponseInfo;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.ads.mediation.admob.AdMobAdapter;

import admob.plus.cordova.ExecuteContext;
import admob.plus.cordova.Generated.Events;
import admob.plus.core.Context;

public class Interstitial extends AdBase {
    private InterstitialAd mAd = null;

    public Interstitial(ExecuteContext ctx) {
        super(ctx);
    }

    @Override
    public void onDestroy() {
        clear();

        super.onDestroy();
    }

    @Override
    public void load(Context ctx) {
        clear();
        AdRequest adReequest = new AdRequest.Builder().build();
        InterstitialAd.load(getActivity(), adUnitId, adReequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                ResponseInfo responseInfo = interstitialAd.getAdapterResponses();
                Log.i("olli", responseInfo.toString());

                mAd = interstitialAd;
                mAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        emit(Events.AD_DISMISS);
                        emit(Events.INTERSTITIAL_DISMISS);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        emit(Events.AD_SHOW_FAIL, adError);
                        emit(Events.INTERSTITIAL_SHOW_FAIL, adError);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mAd = null;
                        emit(Events.AD_SHOW);
                        emit(Events.INTERSTITIAL_SHOW);
                    }

                    @Override
                    public void onAdImpression() {
                        emit(Events.AD_IMPRESSION);
                        emit(Events.INTERSTITIAL_IMPRESSION);
                    }
                });

                emit(Events.AD_LOAD);
                emit(Events.INTERSTITIAL_LOAD);
                ctx.resolve();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mAd = null;
                emit(Events.AD_LOAD_FAIL, loadAdError);
                emit(Events.INTERSTITIAL_LOAD_FAIL, loadAdError);
                ctx.reject(loadAdError.toString());
            }
        });
    }

    @Override
    public boolean isLoaded() {
        return mAd != null;
    }

    @Override
    public void show(Context ctx) {
        if (isLoaded()) {
            mAd.show(getActivity());
            ctx.resolve();
        } else {
            ctx.reject("Ad is not loaded");
        }
    }

    private void clear() {
        if (mAd != null) {
            mAd.setFullScreenContentCallback(null);
            mAd = null;
        }
    }
}
