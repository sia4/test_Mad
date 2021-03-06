package it.polito.mad.mad_app;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.BalanceData;


public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.MyViewHolder> {

    private List<ActivityData> activityData;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, text, value;
        public ImageView image;
        public Button viewMore;
        public MyViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date_act);
            text = (TextView) view.findViewById(R.id.text_act);
            value = (TextView) view.findViewById(R.id.text_value);
            viewMore = (Button) view.findViewById(R.id.AcceptActivity);
            image = (ImageView) view.findViewById(R.id.activity_im);
        }
    }


    public ActivitiesAdapter(List<ActivityData> activityData) {
        this.activityData = activityData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ActivityData activity = activityData.get(position);
        int imgId = activity.getImgId();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.ITALIAN);
        Date resultdate = new Date(Long.valueOf(activity.getDate()));
        PrettyTime prettyTime = new PrettyTime(Locale.US);
        String ago = prettyTime.format(resultdate);
        holder.date.setText(ago);
        holder.text.setText(activity.getText());
        holder.image.setImageResource(imgId);

        if(activity.getType().equals("deletegroup")||activity.getType().equals("leavegroup")){
            holder.viewMore.setVisibility(View.VISIBLE);
        }
        else{
            holder.viewMore.setVisibility(View.GONE);
        }
        if(activity.getType().equals("expense")){
            String v = activity.getItemValue();
            if(v!=null && v!=""){
                holder.value.setText(v);
            }
        }
        if(activity.getRead()==false){
            holder.itemView.setBackgroundColor(Color.parseColor("#E0F2F1"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    @Override
    public int getItemCount() {
        return activityData.size();
    }
}