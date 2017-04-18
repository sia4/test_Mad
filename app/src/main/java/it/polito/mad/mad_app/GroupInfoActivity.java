package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;

public class GroupInfoActivity extends AppCompatActivity {
    private GroupData GD;
    private TextView namet,desc;
    private ImageView im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent i = getIntent();
        String name=i.getStringExtra("name");
        GD=MainData.getInstance().getGroup(name);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Information");

        im=(ImageView) findViewById(R.id.im_g);
        im.setImageResource(R.drawable.group_default);
        namet=(TextView) findViewById(R.id.name_g);
        namet.setText(name);
        desc=(TextView) findViewById(R.id.de_g);
        desc.setText(GD.getDescription());

        RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.users);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UsersAdapter uAdapter = new UsersAdapter(GD.getlUsers());
        userRecyclerView.setAdapter(uAdapter);

        RecyclerView CurrenciesRecyclerView = (RecyclerView) findViewById(R.id.currencies);
        CurrenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final CurrenciesAdapter cAdapter = new CurrenciesAdapter(GD.getCurrencies());
        CurrenciesRecyclerView.setAdapter(cAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = getIntent();
        String GroupName = i.getStringExtra("name");
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("name", GroupName);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}