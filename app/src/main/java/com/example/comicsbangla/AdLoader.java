package com.example.comicsbangla;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.List;

public class AdLoader {
    FrameLayout adcontainerView;
    Context activity_contex;
    AdLoader(Context context,FrameLayout layout) {
        adcontainerView=layout;
        activity_contex=context;
        loadBanner();

    }

    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdView mAdView = new AdView(activity_contex);
        mAdView.setAdUnitId("ca-app-pub-3110096151197337/8788896873");
        adcontainerView.addView(mAdView);
        //List<String> devices= Arrays.asList("AFAE4F4EF1660D968802FCDB2D8A40CE","9FFEC22EBBE3DD3E0672D229ECB10FA6","A528B56A5D7A05B41A358C15672BB5A5");
        //RequestConfiguration configuration =
                //new RequestConfiguration.Builder().setTestDeviceIds(devices).build();
        //MobileAds.setRequestConfiguration(configuration);
        AdRequest adRequest = new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        mAdView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        
    }
    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.


        float widthPixels = activity_contex.getResources().getDisplayMetrics().widthPixels;
        float density = activity_contex.getResources().getDisplayMetrics().density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity_contex, adWidth);
    }
}
