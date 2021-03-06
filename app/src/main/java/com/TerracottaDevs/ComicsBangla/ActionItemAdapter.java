package com.TerracottaDevs.ComicsBangla;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ActionItemAdapter extends RecyclerView.Adapter {
    ArrayList<Pair<String,StorageReference>> comic_id_storageref=new ArrayList<>();
    Context context;
    public ActionItemAdapter(Context context,ArrayList<Pair<String,StorageReference>> comic_id_storageref) {
        this.context=context;
        this.comic_id_storageref=comic_id_storageref;
    }
    public class ActionItemHolder extends RecyclerView.ViewHolder{
        private ImageView cover_image;
        private ActionItemHolder(View view) {
            super(view);
            cover_image=view.findViewById(R.id.action_image);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comic= LayoutInflater.from(parent.getContext()).inflate(R.layout.action_cover,parent,false);
        return new ActionItemHolder(comic);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  holder, final int position) {

        ActionItemAdapter.ActionItemHolder ActionItemHolder=(ActionItemAdapter.ActionItemHolder) holder;
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .dontAnimate()
                .dontTransform()
                .placeholder(R.drawable.category_load_first)
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT);
        Glide.with(context)
                .load(comic_id_storageref.get(position).second)
                .apply(requestOptions)
                .thumbnail(Glide.with(context).load(R.raw.category_load))
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
