package com.skypan.covid_19;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    TextView mTVTitle,mTVDate,mTVYear,mTVMonth,mTVDay,mTVHistory;
    EditText mETYear,mETMonth,mETDay;
    Button mBtnSearch;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListenerRegistration historyListener;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item_home:
                intent = new Intent(this,MainActivity.class);
                break;
            case R.id.item_information:
                intent = new Intent(this,InformationActivity.class);
                break;
            case R.id.item_history:
                intent = new Intent(this,HistoryActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTVTitle = findViewById(R.id.tv_title);
        mTVDate = findViewById(R.id.tv_date);
        mTVYear = findViewById(R.id.tv_year);
        mTVMonth = findViewById(R.id.tv_month);
        mTVDay = findViewById(R.id.tv_day);
        mTVHistory = findViewById(R.id.load_history);
        mBtnSearch = findViewById(R.id.btn_search);
        mETYear = findViewById(R.id.et_year);
        mETMonth = findViewById(R.id.et_month);
        mETDay = findViewById(R.id.et_day);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable Year = mETYear.getText();
                Editable Month = mETMonth.getText();
                Editable Day = mETDay.getText();
                new SimpleDateFormat("YYYY-MMMdd");
                String MddateTime = Year+"-"+Month+"月"+Day;
                if(mETYear != null && mETMonth != null && mETDay != null){
                    loadHistory(MddateTime);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat MdsimpleDateFormat = new SimpleDateFormat("YYYY-MMMdd");
        String MddateTime = MdsimpleDateFormat.format(calendar.getTime());

        historyListener = db.collection(MddateTime).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    return;
                }
                StringBuilder data = new StringBuilder();

                assert queryDocumentSnapshots != null;
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    History history = documentSnapshot.toObject(History.class);

                    String date = history.getDate();
                    String time = history.getTime();
                    String location = history.getLocation();
                    String address = history.getAddress();

                    data.append("Date : ").append(date).append("\nTime : ").append(time).append("\nLocation : ").append(location).append("\nAddress : ").append(address).append("\n\n");
                }

                mTVHistory.setText(data.toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        historyListener.remove();
    }

    private void loadHistory(String MddateTime) {
        db.collection(MddateTime).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                StringBuilder data = new StringBuilder();

                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    History history = documentSnapshot.toObject(History.class);

                    String date = history.getDate();
                    String time = history.getTime();
                    String location = history.getLocation();
                    String address = history.getAddress();

                    data.append("Date : ").append(date).append("\nTime : ").append(time).append("\nLocation : ").append(location).append("\nAddress : ").append(address).append("\n\n");
                }

                mTVHistory.setText(data.toString());
            }
        });
    }
}