package com.aah.selectingfood.model;

import com.aah.selectingfood.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manuel on 04.05.2017.
 */

public class Child {
    private int id;
    private AgeGroup ageGroup;
    private int feedbackFinalGeneralTitleStringResourceId;
    private int feedbackFinalGeneralStringResourceId;

    public Child(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;

        if (ageGroup.equals(AgeGroup.YOUNG)) {
            feedbackFinalGeneralTitleStringResourceId = R.string.feedback_general_young_title;
            feedbackFinalGeneralStringResourceId = R.string.feedback_general_young;
        }
        if (ageGroup.equals(AgeGroup.MIDDLE)) {
            feedbackFinalGeneralTitleStringResourceId = R.string.feedback_general_middle_title;
            feedbackFinalGeneralStringResourceId = R.string.feedback_general_middle;
        }
        if (ageGroup.equals(AgeGroup.OLD)) {
            feedbackFinalGeneralTitleStringResourceId = R.string.feedback_general_old_title;
            feedbackFinalGeneralStringResourceId = R.string.feedback_general_old;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }


    /*
     * Gives instant Feedback based on a specific food.
     *
     * @param   food    a specific food the child should give FeedbackActivity about
     * @return          the resulting FeedbackInstant
     */
    public FeedbackInstant giveFeedbackInstantFood(Food food) {
        return new FeedbackInstant((food.getInstantFeedback()));
    }

    /*
     * Gives final Feedback based on all selected food.
     *
     * @param   List<food>    all food selected in FoodSelectionActivity
     * @return   List<FeedbackCard> allFinalFoodFeedback Cards to be show in FeedbackActivity
     */
    public List<FeedbackCard> giveFeedbackFinalFood(List<Food> foods) {

        List<FeedbackCard> finalFeedbackCardsFood = new ArrayList<>();
        boolean vitaminACardAdded = false;
        boolean notSuitableCardAdded = false;

        boolean containsFruit = false;
        boolean containsVegetable = false;
        boolean containsProtein = false;

        for (Food food : foods) {

            if (food.isNotSuitable() && !notSuitableCardAdded) {
                notSuitableCardAdded = true;
                finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_not_suitable_present_title, R.string.feedback_not_suitable_present_message, "candy_junk_present.png", false));
            }

            if (food.isConsideredVitARich() && !vitaminACardAdded) {
                vitaminACardAdded = true;
                finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_vitamin_A_present_title, R.string.feedback_vitamin_A_present_message, "vitA_rich_present.png", false));
            }

            if (food.getFoodGroup().equals("Fruit")) {
                containsFruit = true;
            }

            if (food.getFoodGroup().equals("Vegetable")) {
                containsVegetable = true;
            }

            if (food.isConsideredProteinRich()) {
                containsProtein = true;
            }
        }

        if (!containsFruit && !containsVegetable) {
            finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_food_lack_of_fruit_vegetable_title, R.string.feedback_food_lack_of_fruit_vegetable_message, "lack_fruits_veg.png", false));
        }

        if (!containsProtein) {
            finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_food_lack_of_iron_title, R.string.feedback_food_lack_of_iron_message, "lack_protein.png", false));
        }

        if (!containsFruit) {
            finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_lack_of_fruit_title, R.string.feedback_lack_of_fruit_message, "healthysnacks.jpg", false));
        }

        return finalFeedbackCardsFood;
    }


    /*
     * Gives a summary Feedback based on number of different foodgroups of selected food.
     *
     * @param   foods    a set of foods the child should give Feedback about
     * @return          the resulting FeedbackCard
     */
    public List<FeedbackCard> giveFeedbackFinalFoodSummary(List<Food> foods) {
        List<FeedbackCard> finalFeedbackCardsFood = new ArrayList<>();
        if (foods == null) {
            finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_no_food_selected_title, R.string.feedback_no_food_selected_message, "no_selection.png", false));
        }

        List<String> selectedFoodGroups = new ArrayList<>();
        for (Food food : foods) {
            if (!selectedFoodGroups.contains(food.getFoodGroup())) {
                selectedFoodGroups.add(food.getFoodGroup());
            }
        }

        if (selectedFoodGroups.size() >= 4) {
            finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_well_balanced_title, R.string.feedback_well_balanced_message, "meal_balanced.png", true));
        } else {
            if (selectedFoodGroups.size() > 0) {
                finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_not_well_balanced_title, R.string.feedback_not_well_balanced_message, "meal_not_balanced.png", true));
                finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_not_well_balanced_title2, R.string.feedback_not_well_balanced_message2, "foodgroups.jpg", false));
            } else {
                finalFeedbackCardsFood.add(new FeedbackCard(R.string.feedback_no_food_selected_title, R.string.feedback_no_food_selected_message, "no_selection.png", false));
            }
        }
        return finalFeedbackCardsFood;
    }


    /*
     * Gives a general Feedback about the child and its needs.
     *
     * @return          the resulting FeedbackCard
     */
    public FeedbackCard giveFeedbackFinalGeneral() {
        return new FeedbackCard(feedbackFinalGeneralTitleStringResourceId, feedbackFinalGeneralStringResourceId, "general_take_away.png", false);
    }

}
