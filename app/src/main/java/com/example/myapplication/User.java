package com.example.myapplication;

import java.io.Serializable;

class User implements Serializable {
    private static final long serialVersionUID = 1L;
    int days;
    int age, weight, height, daily_calories, exer, gml ,goal_weight;
    String gender;
    String name;

    public User( int age , int heihgt , int weight , int gml , int goal_weight , int exer ,int days   , String gender) {
        this.age = age;
        this.height = heihgt;
        this.weight = weight;
        this.gml= gml;
        this.daily_calories = goal_weight;
        this.exer = exer;
        this.days =days;
        this.gender = gender;

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setExerciseLevel(int exer) {
        this.exer = exer;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setGoal_weight(int goal_weight) {
        this.goal_weight = goal_weight;
    }

    public double calculateCalories() {
        double bmr;
        if (gender.equals("male")) {
            bmr = 66.47 + (13.75 * weight) + (5.003 * height) - (6.755 * age);
        } else {
            bmr = 655.1 + (9.563 * weight) + (1.850 * height) - (4.676 * age);
        }

        double activityLevelMultiplier;
        switch (exer) {
            case 1:
                activityLevelMultiplier = 1.2;
                break;
            case 2:
                activityLevelMultiplier = 1.375;
                break;
            case 3:
                activityLevelMultiplier = 1.55;
                break;
            case 4:
                activityLevelMultiplier = 1.725;
                break;

            default:
                throw new IllegalArgumentException("Invalid activity level choice.");
        }

        double maintenanceCalories = bmr * activityLevelMultiplier;
        double loseCalories = ((weight - goal_weight) * 1100)/ days;
        double gainCalories = ((goal_weight-weight)*1100) / days;

        if(gml == 1){
            return   daily_calories = (int) (maintenanceCalories - loseCalories);
        }else if(gml == 2){
             return daily_calories = (int) maintenanceCalories;
        }else {
              return daily_calories = (int) (maintenanceCalories + gainCalories); // Aim for a 1000 calorie deficit per day for extreme fat loss

        }



    }
}
