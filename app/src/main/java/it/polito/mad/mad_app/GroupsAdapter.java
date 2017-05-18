package it.polito.mad.mad_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.GroupData;


public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    private List<GroupsFragment.GroupModel> gData;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView im;
        public TextView date;
        public TextView operation;
        public CheckBox favourite;

        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.name_tv);
            im=(ImageView) view.findViewById(R.id.im);
            date = (TextView) view.findViewById(R.id.date);
            operation = (TextView) view.findViewById(R.id.operation);
            favourite = (CheckBox) view.findViewById(R.id.favourite);

        }
    }


    public GroupsAdapter(Context c, List<GroupsFragment.GroupModel> expensiveData) {
        context = c;
        this.gData = expensiveData;
    }

    @Override
    public GroupsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);

        return new GroupsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupsAdapter.MyViewHolder holder, int position) {
        final GroupsFragment.GroupModel g = gData.get(position);
        holder.name.setText(g.getGroupName());
        holder.date.setText(g.getDateLastOperationWellFormed());
        holder.operation.setText(g.getLastOperation());
        if(g.getFavourite()!=null && g.getFavourite().equals("yes"))
            holder.favourite.setVisibility(View.VISIBLE);
        else
            holder.favourite.setVisibility(View.INVISIBLE);

        Log.d("Groups Adapter", "dati: "+g.getGroupId()+" "+g.dateLastOperation+" "+g.lastOperation);

        String p = g.getGroupUrl();
        if(p!=null) {

            Log.d("Groups Adapter", "Retrive dell'Immagine");
            Glide.with(context).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.im) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.im.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        else{
            Log.d("Groups Adapter", "Set immagine di default");

            Glide.with(context).load(R.drawable.group_default).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.im) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.im.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return gData.size();
    }
}