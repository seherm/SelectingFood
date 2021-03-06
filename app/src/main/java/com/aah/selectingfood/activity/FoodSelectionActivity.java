package com.aah.selectingfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aah.selectingfood.R;
import com.aah.selectingfood.adapter.FoodToSelectArrayAdapter;
import com.aah.selectingfood.adapter.SelectedFoodRecyclerViewAdapter;
import com.aah.selectingfood.helper.DataManagement;
import com.aah.selectingfood.model.Child;
import com.aah.selectingfood.model.FeedbackInstant;
import com.aah.selectingfood.model.Food;

import java.util.List;

/**
 * In this Activity the user selects the food after selecting the food group in the FoodGroupSelectionActivity before
 **/
public class FoodSelectionActivity extends BaseActivity {

    private DataManagement dataManagement;
    private FoodToSelectArrayAdapter foodToSelectArrayAdapter;
    private SelectedFoodRecyclerViewAdapter selectedFoodRecyclerViewAdapter;
    private SearchView searchView;
    private MenuItem item;
    private String selectedFoodGroup;
    private MediaPlayer m = new MediaPlayer();
    private TextView instantFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.title_activity_food_selection));

        SharedPreferences sharedPref = getSharedPreferences("user_selection", MODE_PRIVATE);
        selectedFoodGroup = sharedPref.getString("SELECTED_FOOD_GROUP", null);
        final int selectedFoodGroupResourceId = sharedPref.getInt("SELECTED_FOOD_GROUP_ID", 0);
        final int selectedFoodGroupColor = sharedPref.getInt("SELECTED_FOOD_GROUP_COLOR", 0);
        dataManagement = DataManagement.getInstance(this);
        dataManagement.generateFoodList(selectedFoodGroup);
        setTitle(getString(selectedFoodGroupResourceId));

        instantFeedback = (TextView) findViewById(R.id.instantFeedback);
        instantFeedback.setMovementMethod(new ScrollingMovementMethod());

        //Configure Food to Select View
        foodToSelectArrayAdapter = new FoodToSelectArrayAdapter(this, R.layout.food_to_select_item_layout, dataManagement.getFoodToSelect());
        final GridView gridViewFoodToSelect = (GridView) findViewById(R.id.foodToSelect);
        gridViewFoodToSelect.setAdapter(foodToSelectArrayAdapter);
        gridViewFoodToSelect.setBackgroundResource(selectedFoodGroupColor);
        gridViewFoodToSelect.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //collapse searchView
                if (searchView.isShown()) {
                    item.collapseActionView();
                    searchView.setQuery("", false);
                }

                Food selectedFood = foodToSelectArrayAdapter.getItemAtPosition(position);

                if (v.getId() == R.id.soundButton) {
                    playSound(selectedFood.getSound(), "foodSound");
                } else {
                    dataManagement.addSelectedFood(selectedFood);
                    dataManagement.storeLastUsedFoodToPrefs();
                    foodToSelectArrayAdapter.notifyDataSetChanged();
                    selectedFoodRecyclerViewAdapter.notifyDataSetChanged();
                    checkForImmediateFeedback(selectedFood);
                }
            }
        });

        //Configure Selected Food View
        dataManagement = DataManagement.getInstance(this);
        RecyclerView recyclerViewSelectedFood = (RecyclerView) findViewById(R.id.selectedFoodRecyclerView);
        selectedFoodRecyclerViewAdapter = new

                SelectedFoodRecyclerViewAdapter(dataManagement.getSelectedFood(), getApplication(), new SelectedFoodRecyclerViewAdapter.OnItemClickListener()

        {
            @Override
            public void onItemClick(Food item) {
                dataManagement.removeSelectedFood(item, selectedFoodGroup);
                selectedFoodRecyclerViewAdapter.notifyDataSetChanged();
                foodToSelectArrayAdapter.notifyDataSetChanged();
            }
        });
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(FoodSelectionActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSelectedFood.setLayoutManager(horizontalLayoutManager);
        recyclerViewSelectedFood.setAdapter(selectedFoodRecyclerViewAdapter);

        //Configure Image of the Baby
        ImageView imageViewChild = (ImageView) findViewById(R.id.childImageView);
        Bitmap childImage = dataManagement.getUser().getChildPhotoBitmap();
        imageViewChild.setImageBitmap(childImage);

    }

    public void checkForImmediateFeedback(Food food) {
        List<Child> children = dataManagement.getUser().getChildren();
        for (Child child : children) {
            FeedbackInstant feedbackInstant = child.giveFeedbackInstantFood(food);
            instantFeedback.setText(feedbackInstant.getText());
        }
        if (food.isNotSuitable()) {
            playSound("bad_sound.mp3", "feedbackSound");
        } else if(food.isConsideredProteinRich() || food.isConsideredVitARich()) {
            playSound("good_sound.mp3", "feedbackSound");
        }
    }


    public void playSound(String fileName, String folderName) {
        try {
            m.release();
            m = new MediaPlayer();

            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }
            AssetFileDescriptor descriptor = getAssets().openFd(folderName + "/" + fileName);
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.setLooping(false);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //used to display search bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        if (selectedFoodGroup.equals("Last Used")) {
            inflater.inflate(R.menu.options_menu_clear_list, menu);
        }

        item = menu.findItem(R.id.search);
        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                foodToSelectArrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.clear) {
            dataManagement.getFoodToSelect().clear();
            dataManagement.getLastUsedFood().clear();
            foodToSelectArrayAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToFeedbackPage(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }
}

