package com.arieschwartzman.cortana.model;

import com.arieschwartzman.cortana.model.CaloriesBurnedSummary;

/**
 * Created by ariesch on 16-Jun-16.
 */
public class SummaryInfo {
    private int stepsTaken;
    private CaloriesBurnedSummary caloriesBurnedSummary;

    public int getStepsTaken() {
        return stepsTaken;
    }

    public CaloriesBurnedSummary getCaloriesBurnedSummary() {
        return caloriesBurnedSummary;
    }
}

