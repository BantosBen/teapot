package com.sanj.teapot.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.sanj.teapot.R;
import com.sanj.teapot.data.TeapotDb;
import com.sanj.teapot.models.Tea;

/**
 * Activity that displays all the information about a particular Tea
 */
public class TeaDetailActivity extends AppCompatActivity {

    public static final String TEA_ID = "TEA_ID";
    private CollapsingToolbarLayout mCollapsingToolbar;
    private ImageView mTeaImage;
    private TextView mDescription;
    private TextView mOrigin;
    private TextView mIngredients;
    private TextView mTeaType;
    private TextView mCaffeine;
    private int teaId;
    private SQLiteDatabase teapotDb;
    private TeapotDb dbInstance;
    private boolean isFavorite;
    /**
     * Thread used to get tea by the provided id through the intent from other activities
     * */
    private final Runnable findTeaById=new Runnable() {
        final Handler UIHandler=new Handler();
        @Override
        public void run() {
            Tea response=dbInstance.findTeaById(teaId,teapotDb);
            UIHandler.post(() -> displayTea(response));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        teaId = getIntent().getExtras().getInt(TEA_ID);
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mTeaImage = findViewById(R.id.tea_image);
        mDescription = findViewById(R.id.description);
        mOrigin = findViewById(R.id.origin);
        mIngredients = findViewById(R.id.ingredients);
        mTeaType = findViewById(R.id.tea_type);
        mCaffeine = findViewById(R.id.caffeine_level);
        dbInstance = TeapotDb.getInstance(this);
        teapotDb = dbInstance.getDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(findTeaById).start();
    }

    /**
     * Attach the Tea to the view.
     */
    private void displayTea(Tea tea) {
        if (tea != null) {
            return;
        }
        mCollapsingToolbar.setTitle(tea.getName());
        mDescription.setText(tea.getDescription());
        mOrigin.setText(tea.getOrigin());
        mIngredients.setText(tea.getIngredients());
        mTeaType.setText(tea.getType());
        isFavorite=tea.isFavorite();
        mCaffeine.setText(getString(R.string.caffeine, tea.getCaffeineLevel()));

        switch (tea.getType()) {
            case "Black Tea":
                mTeaImage.setImageResource(R.mipmap.black_tea);
                break;
            case "Green Tea":
                mTeaImage.setImageResource(R.mipmap.green_tea);
                break;
            case "Herbal Tea":
                mTeaImage.setImageResource(R.mipmap.herbal_tea);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Thread used to update the status of favorite of the current tea in the activity*/
    private void updateFavorite() {
        Runnable updateFavoriteThread=new Runnable() {
            final Handler UIHandler=new Handler();
            @Override
            public void run() {
                boolean updated=dbInstance.updateFavoriteTea(teaId,!isFavorite,teapotDb);
                UIHandler.post(() -> {
                    if (updated){
                        if (!isFavorite){
                            Toast.makeText(TeaDetailActivity.this, "Favoured", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(TeaDetailActivity.this, "Unfavored", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(TeaDetailActivity.this, "Failed to update favorite", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        new Thread(updateFavoriteThread);
    }
}