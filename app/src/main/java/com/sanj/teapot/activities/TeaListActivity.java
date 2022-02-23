package com.sanj.teapot.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanj.teapot.R;
import com.sanj.teapot.adapter.TeaListAdapter;
import com.sanj.teapot.data.TeapotDb;
import com.sanj.teapot.models.Tea;

import java.util.List;

public class TeaListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SQLiteDatabase teapotDb;
    private TeapotDb dbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tea_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        mRecyclerView=findViewById(R.id.recycler_view);
        FloatingActionButton mFab = findViewById(R.id.fab);
        dbInstance = TeapotDb.getInstance(this);
        teapotDb = dbInstance.getDatabase();

        mFab.setOnClickListener(view -> {
            Intent intent = new Intent();
            startActivity(intent);
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        populateRecyclerView();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show();
            populateRecyclerView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**Thread used to load data to the recycler view*/
    private void populateRecyclerView() {
        final Handler UIHandler=new Handler();
        Runnable getAllTeas= () -> {
            List<Tea> response = dbInstance.getAllTeas(teapotDb);
            UIHandler.post(() -> {
                LinearLayoutManager layoutManager=new LinearLayoutManager(TeaListActivity.this,LinearLayoutManager.VERTICAL,false);
                mRecyclerView.setLayoutManager(layoutManager);
                TeaListAdapter teaListAdapter=new TeaListAdapter(null,TeaListActivity.this);
                mRecyclerView.setAdapter(teaListAdapter);
            });
        };
        new Thread(getAllTeas).start();
    }
}
