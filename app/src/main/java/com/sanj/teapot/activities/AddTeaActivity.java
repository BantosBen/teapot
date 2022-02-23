package com.sanj.teapot.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.sanj.teapot.R;
import com.sanj.teapot.data.TeapotDb;
import com.sanj.teapot.models.Tea;

/**
 * Save a Tea to local data source.
 */
public class AddTeaActivity extends AppCompatActivity {

    private Spinner mTeaType,mCaffeine;
    private TextInputEditText mTeaName;
    private TextInputEditText mDescription;
    private TextInputEditText mOrigin;
    private TextInputEditText mIngredients;
    private SQLiteDatabase teapotDb;
    private TeapotDb dbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tea);

        mTeaType = findViewById(R.id.tea_type);
        mTeaName = findViewById(R.id.tea_name);
        mDescription = findViewById(R.id.description);
        mOrigin = findViewById(R.id.origin);
        mIngredients = findViewById(R.id.ingredients);
        mCaffeine = findViewById(R.id.caffeine_level);
        dbInstance = null;
        teapotDb = dbInstance.getDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveTea();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save all fields.
     */
    private void saveTea() {
        new Thread(insertionThread);
    }

    /**
     *Thread used to add new tea to the database
     * */
    private final Runnable insertionThread=new Runnable() {
        final Handler UIHandler=new Handler();
        @Override
        public void run() {
            String[] teaTypes = getResources().getStringArray(R.array.tea_types);
            String[] teaCaffeineLevels=getResources().getStringArray(R.array.tea_caffeine_level);
            String type = teaTypes[(int) mTeaType.getSelectedItemId()];
            String name = mTeaName.getText().toString().trim();
            String description = mDescription.getText().toString();
            String origin = mOrigin.getText().toString();
            String ingredients = mIngredients.getText().toString();
            String caffeine = teaCaffeineLevels[(int) mCaffeine.getSelectedItemId()];
            if (!(TextUtils.isEmpty(type) || TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(origin) || TextUtils.isEmpty(ingredients) || TextUtils.isEmpty(caffeine))){
                boolean inserted = dbInstance.insert(new Tea(0, description, name,type, origin,ingredients, caffeine, false), teapotDb);
                UIHandler.post(() -> {
                    if (inserted){
                        mTeaName.setText("");
                        mDescription .setText("");
                        mOrigin .setText("");
                        mIngredients .setText("");
                        Toast.makeText(AddTeaActivity.this, "New tea added successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddTeaActivity.this, "Failed to add new tea", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                UIHandler.post(() -> Toast.makeText(AddTeaActivity.this, "All fields required", Toast.LENGTH_SHORT).show());
            }
        }
    };
}
