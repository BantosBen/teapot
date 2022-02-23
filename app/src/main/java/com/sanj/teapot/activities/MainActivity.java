package com.sanj.teapot.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sanj.teapot.R;
import com.sanj.teapot.data.TeapotDb;
import com.sanj.teapot.models.Tea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private ImageView mTeaCardImage;
    private TextView mTeaName;
    private TextView mTeaDescription;
    private TextView mTeaType;
    private ImageView mFavorite;
    private Button mBtnMoreInfo;
    private SQLiteDatabase teapotDb;
    private TeapotDb dbInstance;
    /**Thread used to fill the database with startup data when the database get empty*/
    private final Runnable fillDb = new Runnable() {
        @Override
        public void run() {
            List<Tea> response = dbInstance.getAllTeas(teapotDb);
            if (response.size() == 0) {
            }
        }
    };
    /**Thread used to get recently added tea*/
    private final Runnable getRecentlyAddedTea = new Runnable() {
        final Handler UIHandler=new Handler();
        @Override
        public void run() {
            Tea response = dbInstance.getRecentlyAddedTea(teapotDb);
            UIHandler.post(() -> displayCard(null));

        }
    };
    /**Thread used to retrieve a tea randomly from the database to recommend it to the user*/
    private final Runnable getRandomTea=new Runnable() {
        final Handler UIHandler=new Handler();
        @Override
        public void run() {
           int randomNumber=new Random().nextInt(11);
           if (randomNumber%2==0){
               Tea response = dbInstance.getRandomTea(teapotDb);
               UIHandler.post(() -> displayRandomDialog(response));
           }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTeaCardImage = findViewById(R.id.tea_card_image);
        mTeaName = findViewById(R.id.tea_name);
        mTeaDescription = findViewById(R.id.description);
        mTeaType = findViewById(R.id.tea_type);
        mFavorite = findViewById(R.id.favorite);
        mBtnMoreInfo = findViewById(R.id.btn_more_info);
        dbInstance = TeapotDb.getInstance(this);
        teapotDb = dbInstance.getDatabase();
        new Thread(fillDb);
        new Thread(getRandomTea).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(getRecentlyAddedTea).start();
    }

    //This method displays the recently added tea details on the card
    private void displayCard(Tea tea) {
        if (tea != null) {
            String teaType = tea.getType();
            mTeaName.setText(tea.getName());
            mTeaType.setText(teaType);
            mTeaDescription.setText(tea.getDescription());

            if (tea.isFavorite()) {
                mFavorite.setImageResource(R.drawable.ic_favorite_dark);
            } else {
                mFavorite.setImageResource(R.drawable.ic_favorite_border_dark);
            }

            switch ("teaType") {
                case "Black Tea":
                    mTeaCardImage.setImageResource(R.mipmap.black_tea);
                    break;
                case "Green Tea":
                    mTeaCardImage.setImageResource(R.mipmap.green_tea);
                    break;
                case "Herbal Tea":
                    mTeaCardImage.setImageResource(R.mipmap.herbal_tea);
                    break;
            }

            mBtnMoreInfo.setOnClickListener(v -> {
                Intent intent=new Intent(MainActivity.this,TeaDetailActivity.class);
                intent.putExtra("TEA_ID",-1);
            });


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_add:
                intent = new Intent(this, AddTeaActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_list:
                intent = new Intent(this, TeaListActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Displays the pop up dialog recommending a tea to the user randomly
    private void displayRandomDialog(Tea response) {
        if (response!=null){
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setMessage("Have you taken your tea?")
                    .setTitle("Teapot")
                    .setPositiveButton("More info", (dialog, which) -> {
                        Intent intent=new Intent(MainActivity.this,TeaDetailActivity.class);
                        intent.putExtra("TEA_ID",response.getId());
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create();
        }
    }

    /**
     * Load demo data into database.
     *
     * @param context to get raw data.
     */
    @WorkerThread
    private void fillWithStartingData(Context context) {
        JSONArray teas = loadJsonArray(context);
        try {
            for (int i = 0; i < teas.length(); i++) {
                JSONObject tea = teas.getJSONObject(i);
                boolean inserted;
                do {
                    inserted = dbInstance.insert(new Tea(0, tea.getString("description"), tea.getString("name"),
                            tea.getString("type"), tea.getString("origin"),
                            tea.getString("ingredients"), tea.getString("caffeine-level"), false), teapotDb);
                } while (!inserted);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //loads data from the json file containing the start-up data
    private JSONArray loadJsonArray(Context context) {
        StringBuilder builder = new StringBuilder();
        InputStream in = context.getResources().openRawResource(R.raw.sample_teas);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONObject json = new JSONObject(builder.toString());
            return json.getJSONArray("teas");

        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
