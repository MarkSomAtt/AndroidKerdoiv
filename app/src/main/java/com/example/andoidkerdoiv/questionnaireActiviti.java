package com.example.andoidkerdoiv;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class questionnaireActiviti extends AppCompatActivity {
    private static final String LOG_TAG=questionnaireActiviti.class.getName();

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private ArrayList<Questions> itemList;
    private QuestionsAdapter mAdapter;
    private boolean viewRow=true;
    private FrameLayout redCircle;
    private TextView countTextView;
    private int ki_toltott_count;

    private int gritNumber=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_activiti);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            Log.d(LOG_TAG,"Létező felhasználó");
        }else{
            Log.d(LOG_TAG,"Nemlétező felhasználó");
            finish();
        }

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gritNumber));
        itemList=new ArrayList<>();
        mAdapter=new QuestionsAdapter(this,itemList);
        recyclerView.setAdapter(mAdapter);

        initializeData();

    }

    private void initializeData(){
        String[] questionList=getResources().getStringArray(R.array.Qestions_list);
        String[] answer1List=getResources().getStringArray(R.array.answers1_list);
        String[] answer2List=getResources().getStringArray(R.array.answers2_list);
        String[] answer3List=getResources().getStringArray(R.array.answers3_list);
        String[] answer4List=getResources().getStringArray(R.array.answers4_list);
        String[] answer5List=getResources().getStringArray(R.array.answers5_list);
        TypedArray itemsRating=getResources().obtainTypedArray(R.array.shopping_item_rates);
        itemList.clear();

        for (int i=0;i<questionList.length;i++){
            itemList.add(new Questions(questionList[i],itemsRating.getFloat(i,0),answer1List[i],answer2List[i],answer3List[i],answer4List[i],answer5List[i]));
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.question_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Log_out_Button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.setting_button:
                Log.d(LOG_TAG, "Setting clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.kitoltott:
                Log.d(LOG_TAG, "Cart clicked!");
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_baseline_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int ic_row, int i) {
        viewRow=!viewRow;
        item.setIcon(ic_row);
        GridLayoutManager layoutManager=(GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanCount(i);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.kitoltott);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        ki_toltott_count = (ki_toltott_count+ 1);
        if (0 < ki_toltott_count) {
            countTextView.setText(String.valueOf(ki_toltott_count));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((ki_toltott_count > 0) ? VISIBLE : GONE);

    }



}