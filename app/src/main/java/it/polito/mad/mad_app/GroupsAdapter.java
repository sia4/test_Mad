package it.polito.mad.mad_app;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.GroupData;


public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    private List<GroupsFragment.GroupModel> GData;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView impact_pos;
        public TextView impact_neg;
        public ImageView im;

        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.name_tv);
            impact_pos= (TextView) view.findViewById(R.id.impact_pos_ex);
            impact_neg= (TextView) view.findViewById(R.id.impact_neg_ex);
            im=(ImageView) view.findViewById(R.id.im);
        }
    }


    public GroupsAdapter(Context c, List<GroupsFragment.GroupModel> expensiveData) {
        context = c;
        this.GData = expensiveData;
    }

    @Override
    public GroupsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);

        return new GroupsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupsAdapter.MyViewHolder holder, int position) {
        final GroupsFragment.GroupModel g = GData.get(position);
        holder.name.setText(g.getGroupName());

        String p = g.getGroupUrl();
        System.out.println("Sono qui, p = "+p);

        Glide.with(context).load(p).into(holder.im);

        /*if (p == null) {
            holder.im.setImageResource(R.drawable.group_default);
        } else {
            holder.im.setImageBitmap(BitmapFactory.decodeFile(p));
        }*/

        //holder.im.setImageResource(R.drawable.casa);
        /*holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(v.getContext(), GroupActivity.class);
                String groupName = g.getName();
                intent.putExtra("name",groupName);
                v.getContext().startActivity(intent);
            }
        });*/
        holder.impact_pos.setText("You owe:"+String.format("%.2f", 100.383838));//TODO insert the correct value g.getNegExpenses()
        holder.impact_neg.setText("They owe you:"+String.format("%.2f",33.3333));//TODO insert the correct value g.getPosExpenses()
        holder.impact_neg.setTextColor(Color.parseColor("#27B011"));
        holder.impact_pos.setTextColor(Color.parseColor("#D51111"));
    }

    @Override
    public int getItemCount() {
        return GData.size();
    }
}