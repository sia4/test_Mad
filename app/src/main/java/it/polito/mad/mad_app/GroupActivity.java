package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;

public class GroupActivity extends AppCompatActivity {
    private String name;
    private ListView lv;
    private List<ExpenseData> data = new ArrayList<>();
    private List<GroupData> GData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Intent intent = getIntent();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final String gname = intent.getStringExtra("name");
        name=gname;

        System.out.println("Hello: " + gname);

        List<GroupData> d = MainData.getInstance().getGroupList();

        GroupData datigruppo = null;
        int i, n;
        n = d.size();

        for (i = 0; i < n; i++) {

            datigruppo = d.get(i);

            if (datigruppo.getName().equals(gname)) {
                System.out.println("Hello? ");
                break;
            }

        }

        final Bundle b = new Bundle();
        b.putString("GroupName", gname);

        System.out.println("CICCIOBOMBA" + datigruppo.getName());

        Float pos = datigruppo.getPosExpenses();
        Float neg = datigruppo.getNegExpenses();

        String subtitle = "";
        subtitle += "They Owe You: " + pos.toString()+ " - You Owe: " + neg.toString();


        getSupportActionBar().setTitle(gname);
        getSupportActionBar().setSubtitle(subtitle);
        try{
            Field field = Toolbar.class.getDeclaredField( "mSubtitleTextView" );
            field.setAccessible( true );
            TextView subtitleTextView = (TextView)field.get( toolbar );
            subtitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            subtitleTextView.setFocusable(true);
            subtitleTextView.setFocusableInTouchMode(true);
            subtitleTextView.requestFocus();
            subtitleTextView.setSingleLine(true);
            subtitleTextView.setSelected(true);
            subtitleTextView.setMarqueeRepeatLimit(1);
            //subtitleTextView.setMarqueeRepeatLimit(-1); //continua all'infinito
        }catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }

        Fragment hfrag = new HistoryFragment();
        hfrag.setArguments(b);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        transaction.replace(R.id.group_framelayout, hfrag);
        transaction.commit();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsGroup);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment frag;
                switch (tab.getPosition()) {
                    case 0:
                        frag = new HistoryFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        frag = new BudgetFragment();
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        frag = new HistoryFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                }

                frag.setArguments(b);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.group_framelayout, frag);
                transaction.commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), InsertExActivity.class
                );
                intent.putExtra("GroupName", gname);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final String gname = data.getStringExtra("name");
            System.out.println("Name = " + gname);
            Intent refresh = new Intent(this, GroupActivity.class);
            refresh.putExtra("name", gname);
            startActivity(refresh);
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.options:
                Intent intent = new Intent(getApplicationContext(), GroupInfoActivity.class);
                intent.putExtra("name", name);
                startActivityForResult(intent, 1);
                return true;

            case R.id.addcurrency:
                Intent i2 = new Intent(getApplicationContext(), InsertCurrencyActivity.class);
                i2.putExtra("name", name);
                startActivity(i2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*startActivity(new Intent(
            getApplicationContext(),GroupOptionActivity.class
                ));*/
}
/*Intent intent = new Intent().setClass(view.getContext(), ExpenseInfoActivity.class);
                intent.putExtra("name", expense.getName());
                intent.putExtra("category", expense.getCategory());
                intent.putExtra("currency", expense.getCurrency());
                intent.putExtra("algorithm", expense.getAlgorithm());
                intent.putExtra("description", expense.getDescription());
                //String.format("%.2f", expense.getMyvalue())
                intent.putExtra("myvalue",String.format("%.2f", expense.getMyvalue()) );
                intent.putExtra("value", String.format("%.2f", expense.getValue()));
                intent.putExtra("creator", MainData.getInstance().getMyName());//TODO change with getCreator
                intent.putExtra("date", expense.getDate());
                view.getContext().startActivity(intent);*/