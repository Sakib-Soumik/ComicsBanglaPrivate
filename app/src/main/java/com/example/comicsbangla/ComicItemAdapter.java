package com.example.comicsbangla;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ComicItemAdapter extends RecyclerView.Adapter {
    ArrayList<StorageReference> comic_images=new ArrayList<>();
    Context context;
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comic= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_row,parent,false);
        return new ComicItemHolder(comic);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  holder, int position) {
        ComicItemHolder comicItemHolder=(ComicItemHolder) holder;
        int comic_size=comic_images.size();
        ReadComic.current_page=position;
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
    @Override
    public int getItemCount() {
        return comic_images.size();
    }


}
