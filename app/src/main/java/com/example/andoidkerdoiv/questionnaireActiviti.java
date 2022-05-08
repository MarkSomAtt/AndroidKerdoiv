package com.example.andoidkerdoiv;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

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
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private Integer itemLimit = 5;
    private AlarmManager mAlarmManager;
    private JobScheduler mJobScheduler;

    private NotificationHelper notificationHelper;

    private int gritNumber=1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

        mFirestore=FirebaseFirestore.getInstance();
        mItems=mFirestore.collection("items");
        notificationHelper =new NotificationHelper(this);
        mAlarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);



        queryData();

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver,filter);

        //setAlarmManager();
        setJobScheduler();
    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();

            if (intentAction == null)
                return;

            switch (intentAction) {
                case Intent.ACTION_POWER_CONNECTED:
                    itemLimit = 10;
                    queryData();
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    itemLimit = 5;
                    queryData();
                    break;
            }
        }
    };

    private void queryData(){
        itemList.clear();

        mItems.orderBy("kitoltesCount", Query.Direction.DESCENDING).limit(itemLimit).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                    Questions item=document.toObject(Questions.class);
                    item.setId(document.getId());
                    itemList.add(item);
                }

                if (itemList.size()==0){
                    initializeData();
                    queryData();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }




    public void deleteItem(Questions item){
        DocumentReference ref = mItems.document(item._getId());
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + item._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be deleted.", Toast.LENGTH_LONG).show();
                });

        queryData();
        notificationHelper.cancel();

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

        for (int i=0;i<questionList.length;i++) {
            mItems.add(new Questions(questionList[i], itemsRating.getFloat(i, 0), answer1List[i], answer2List[i], answer3List[i], answer4List[i], answer5List[i],0));

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
            case R.id.add:
                startActivity(new Intent(this,Add.class));
                overridePendingTransition(R.anim.pop_up_show,R.anim.pop_up_exit);
            case R.id.kitoltott:
                Log.d(LOG_TAG, "Cart clicked!");
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_baseline_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_row, 2);
                }

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

    public void updateAlertIcon(Questions item) {
        ki_toltott_count = (ki_toltott_count+ 1);
        if (0 < ki_toltott_count) {
            countTextView.setText(String.valueOf(ki_toltott_count));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((ki_toltott_count > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("kitoltesCount", item.getKitoltesCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();
                });

        //notificationHelper.send(item.getQuestion());
        queryData();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }

    private void setAlarmManager() {
        long repeatInterval = 60000; // AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);


        mAlarmManager.cancel(pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJobScheduler() {
        // SeekBar, Switch, RadioButton
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        Boolean isDeviceCharging = true;
        int hardDeadline = 5000; // 5 * 1000 ms = 5 sec.

        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceName)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(isDeviceCharging)
                .setOverrideDeadline(hardDeadline);

        JobInfo jobInfo = builder.build();
        mJobScheduler.schedule(jobInfo);

        // mJobScheduler.cancel(0);
        // mJobScheduler.cancelAll();

    }


    public void queryDataRated(View view) {
        itemList.clear();


        mItems.whereGreaterThan("rated", 3.9).limit(itemLimit).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                    Questions item=document.toObject(Questions.class);
                    item.setId(document.getId());
                    itemList.add(item);
                }

                if (itemList.size()==0){
                    queryData();
                }
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    public void queryData2(View view) {
        itemList.clear();

        mItems.orderBy("kitoltesCount", Query.Direction.DESCENDING).limit(itemLimit).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                    Questions item=document.toObject(Questions.class);
                    item.setId(document.getId());
                    itemList.add(item);
                }

                if (itemList.size()==0){
                    queryData();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}