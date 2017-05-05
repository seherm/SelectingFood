package com.aah.selectingfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v4.app.Fragment;


import android.support.annotation.Nullable;
import android.graphics.Color;
import android.widget.Toast;

import com.aah.selectingfood.model.DataManagement;
import com.aah.selectingfood.model.FeedbackCard;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class FeedbackActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add feedback slides
        // TODO: Optimize this for 1-3 children
        // TODO: Handle duplicate feedback
        FeedbackCard finalFoodSummaryFeedback = DataManagement.getInstance(this).getUser().getChildren().get(0).giveFeedbackFinalFoodSummary(null);
        addSlide(SampleSlide.newInstance(R.layout.fragment_feedback, finalFoodSummaryFeedback.getText(), finalFoodSummaryFeedback.getTextColor(), finalFoodSummaryFeedback.getBackgroundColor(), R.drawable.ic_menu_slideshow));

        FeedbackCard generalAgeFeedback = DataManagement.getInstance(this).getUser().getChildren().get(0).giveFeedbackFinalGeneral();
        addSlide(SampleSlide.newInstance(R.layout.fragment_feedback, generalAgeFeedback.getText(), generalAgeFeedback.getTextColor(), generalAgeFeedback.getBackgroundColor(), R.drawable.ic_menu_slideshow));


        // Note here that we DO NOT use setContentView();


        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*
        addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);
        */

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#b5aba2"));
        setSeparatorColor(Color.parseColor("#000000"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}