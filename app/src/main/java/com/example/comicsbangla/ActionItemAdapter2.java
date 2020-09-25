package com.example.comicsbangla;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ActionItemAdapter2 extends RecyclerView.Adapter {
    ArrayList<Pair<String,StorageReference>> comic_id_storageref=new ArrayList<>();
    Context context;
    public ActionItemAdapter2(Context context,ArrayList<Pair<String,StorageReference>> comic_id_storageref) {
        this.context=context;
        this.comic_id_storageref=comic_id_storageref;
    }
    public class ActionItemHolder extends RecyclerView.ViewHolder{
        private ImageView cover_image;
        private ActionItemHolder(View view) {
            super(view);
            cover_image=view.findViewById(R.id.new_upload_image);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comic= LayoutInflater.from(parent.getContext()).inflate(R.layout.new_uploads,parent,false);
        return new ActionItemHolder(comic);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  holder, final int position) {

        ActionItemAdapter2.ActionItemHolder ActionItemHolder=(ActionItemAdapter2.ActionItemHolder) holder;
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .dontAnimate()
                .dontTransform()
                .placeholder(R.drawable.category_load_landscape)
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT);
        Glide.with(context)
                .load(comic_id_storageref.get(position).second)
                .apply(requestOptions)
                .thumbnail(Glide.with(context).load(R.raw.category_load_gif_landscape))
                .dontTransform()
                .into(ActionItemHolder.cover_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,OverView.class);
                intent.putExtra("ComicId",comic_id_storageref.get(position).first);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comic_id_storageref.size();
    }

}
