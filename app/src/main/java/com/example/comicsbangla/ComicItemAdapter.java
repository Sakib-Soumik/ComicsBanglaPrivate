package com.example.comicsbangla;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.storage.StorageReference;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.material.internal.ContextUtils.getActivity;

public class ComicItemAdapter extends RecyclerView.Adapter {
    ArrayList<StorageReference> comic_images=new ArrayList<>();
    Context context;
    int AD_TYPE=2;
    int CONTENT_TYPE=1;
    public ComicItemAdapter(Context context, ArrayList<StorageReference> comic_images) {
        this.context=context;
        this.comic_images=comic_images;
    }
    public class ComicItemHolder extends RecyclerView.ViewHolder{
        private ImageView comic_image;
        private ComicItemHolder(View view) {
            super(view);
            comic_image=view.findViewById(R.id.comic_image);

        }
    }
    public class AdItemHolder extends RecyclerView.ViewHolder{
        AdView adView;
        private AdItemHolder(View view) {
            super(view);
            adView=view.findViewById(R.id.adView);


        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == AD_TYPE)
        {
            View ad= LayoutInflater.from(parent.getContext()).inflate(R.layout.comic_ad_layout,parent,false);
            return new AdItemHolder(ad);

        }
        else {

            View comic= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_row,parent,false);
            return new ComicItemHolder(comic);
        }

    }
    @Override
    public int getItemViewType(int position) {
        if(comic_images.get(position)==null)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  holder, int position) {
        //Toast.makeText(context,Integer.toString(ReadComic.current_page),Toast.LENGTH_SHORT).show();
        if(getItemViewType(position)==AD_TYPE) {
            AdItemHolder adItemHolder=(AdItemHolder) holder;
            //List<String> devices= Arrays.asList("AFAE4F4EF1660D968802FCDB2D8A40CE","9FFEC22EBBE3DD3E0672D229ECB10FA6","A528B56A5D7A05B41A358C15672BB5A5");
            //RequestConfiguration configuration =
               //     new RequestConfiguration.Builder().setTestDeviceIds(devices).build();
            //MobileAds.setRequestConfiguration(configuration);
            AdRequest adRequest = new AdRequest.Builder().build();
            adItemHolder.adView.loadAd(adRequest);


        }
        else {
            ComicItemHolder comicItemHolder=(ComicItemHolder) holder;
            int comic_size=comic_images.size();

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .centerCrop()
                    .placeholder(R.drawable.comic_gif_start)
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT);

            Glide.with(context)
                    .load(comic_images.get(position))
                    .apply(requestOptions)
                    .thumbnail(Glide.with(context).load(R.raw.comic_load_gif))
                    .dontTransform()
                    .into(comicItemHolder.comic_image);
            if(position<2) {
                if (position + 1 < comic_size) {
                    Glide.with(context)
                            .load(comic_images.get(position + 1))
                            .apply(requestOptions)
                            .preload();
                    if (position + 2 < comic_size) {
                        Glide.with(context)
                                .load(comic_images.get(position + 2))
                                .apply(requestOptions)
                                .preload();
                        if (position + 3 < comic_size) {
                            Glide.with(context)
                                    .load(comic_images.get(position + 3))
                                    .apply(requestOptions)
                                    .preload();
                        }
                    }
                }
            }
        }
    }
    @Override
    public int getItemCount() {
        return comic_images.size();
    }


}
