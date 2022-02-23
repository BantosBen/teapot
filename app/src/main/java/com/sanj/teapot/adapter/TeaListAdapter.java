package com.sanj.teapot.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanj.teapot.R;
import com.sanj.teapot.activities.MainActivity;
import com.sanj.teapot.activities.TeaDetailActivity;
import com.sanj.teapot.data.TeapotDb;
import com.sanj.teapot.models.Tea;

import java.util.List;

public class TeaListAdapter extends RecyclerView.Adapter<TeaListAdapter.ViewHolder> {
    private final List<Tea> teas;
    private final Context mContext;
    private final SQLiteDatabase teapotDb;
    private final TeapotDb dbInstance;
    private final TeaListAdapter adapterContext;

    public TeaListAdapter(List<Tea> teas, Context mContext) {
        this.teas = teas;
        this.mContext = mContext;
        dbInstance = TeapotDb.getInstance(mContext);
        teapotDb = dbInstance.getDatabase();
        adapterContext=this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tea tea=teas.get(position);

        if (tea.isFavorite()){
            holder.imgFavorite.setImageResource(R.drawable.ic_favorite_dark);
        }

        holder.teaName.setOnClickListener(v -> {
            Intent intent=new Intent(mContext, TeaDetailActivity.class);
            intent.putExtra("TEA_ID",tea.getId());
            mContext.startActivity(intent);
        });
        holder.btnDelete.setOnClickListener(v -> {
            Runnable teaDeleteThread=new Runnable() {
                final Handler UIHandler=new Handler();
                @Override
                public void run() {
                    boolean isDeleted=dbInstance.deleteTea(-1,teapotDb);
                    UIHandler.post(() -> {
                        if (isDeleted){
                            teas.remove(position);
                            adapterContext.notifyItemRemoved(position);
                            Toast.makeText(mContext, "Tea deleted.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, "Failed to delete tea", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };
            new Thread(teaDeleteThread).start();
        });

    }

    @Override
    public int getItemCount() {
        return teas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView teaName,teaOrigin;
        ImageButton btnDelete;
        ImageView imgFavorite;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teaName=itemView.findViewById(R.id.tea_name);
            teaOrigin=itemView.findViewById(R.id.tea_origin);
            imgFavorite=itemView.findViewById(R.id.favorite);
        }
    }
}
