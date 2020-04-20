package com.example.comicsbangla;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ComicItemAdapter extends RecyclerView.Adapter {
    ArrayList<StorageReference> comic_images=new ArrayList<>();
    Context context;
    public ComicItemAdapter(Context context,ArrayList<StorageReference> comic_images) {
        this.context=context;
        this.comic_images=comic_images;
    }
    public class ComicItemHolder extends RecyclerView.ViewHolder{
        private PhotoView comic_image;
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
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .centerCrop()
                .dontAnimate()
                .dontTransform()
                .centerCrop()
                .placeholder(R.drawable.comic_load)
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT);
        Log.d("linik", "onBindViewHolder: "+comic_images.get(position).toString());
        Glide.with(context)
                .load(comic_images.get(position))
                .apply(requestOptions)
                .dontTransform()
                .into(comicItemHolder.comic_image);
    }

    @Override
    public int getItemCount() {
        return comic_images.size();
    }

}
