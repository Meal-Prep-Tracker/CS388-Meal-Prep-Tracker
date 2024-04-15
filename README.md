# Milestone 1 - Meal Prep Tracker (Unit 7)

## Table of Contents

1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)

## Overview

### Description

 * Users are able to keep track of meals they make each week. This app would allow users to add their meals to the app which includes the ingredients for the meal, how much did all the ingredients cost, and how many meals were made from the given ingredients. The CalorieNinja API would be used to obtain the nutrition facts of each inputted ingredient. Users would then be able to see their weekly spending and nutrition consumption habits.
     * API: [https://calorieninjas.com/api](https://calorieninjas.com/api)
     * Recycler View Details (Scrollable List):
         * Meal Name
         * Price of Meal
         * Total Calories
     * 4 responsive functionalities:
         * Add meals
         * Remove Meals
         * Mark meals as favorite
         * Edit Meals
     * Navigation (Bottom Navigation)
         * Dashboard
         * Meals List
         * Add Meal
         * Profile
         * Settings
     * Data
         * Local database to store user preferences, favorite meals and images
         * Remote database to store past entered meal and user information.

### App Evaluation

- **Category:**
    * Health and Finance.  It is in these categories because we are tracking the meals that the users make as well as how much they have spent for the ingredients of the meal.
- **Mobile:**
    * This app would allow users to easily access the meals they have been consuming as well as looking at past meals they have made in case they want to make it again.
    * This app can include personalized push notifications regarding added meals, weekly food consumption summaries, and more
    * Offline functionality: the app will data on the device for offline access, like preferences and most recently added meals
    * Seamless use of camera: the user will be able to take optionally take a picture of what meal they made when logging it in the app
- **Story:**
    * This app would help users keep better track of their meal prep plans and understand more about their spending habits for meals and how much nutritional intake they are getting.
- **Market:**
    * This app would target an audience that is into meal prepping.
    * It would allow users to easily track their meals.
- **Habit:**
    * This app would be used every time the user wants to enter a new meal, see their weekly statistics, or look at past meals they have made. This app would be able to be used at least weekly or more often.
- **Scope:**
    * This app would allow users to enter their meal prep plans to keep track of expenses and nutritional intake. Users would be able to review past-entered meals to see their ingredients. Additionally, users can favorite meals to quickly find them in the future. This app covers a unique idea of creating a tracker for both meals with nutrition facts as well as money spent. This idea is techneically challenging and well defined with its features of adding meals, creating visuals, and a unique API.

## Product Spec

### 1. User Features (Required and Optional)

**Required Features**

1. The user can **submit** meal prep meals that include ingredients, quantity of ingredients (grams), price of ingredients, and how many meals were made from the ingredients
2. The user can **view** their money spent, calories consumed, nutrition consumed, and number of meals entered on their dashboard.
3. The user can **review** past entered meals by viewing the meal in more detail in the Meal Detail View Activity.
4. The user can **edit** entered meals and their ingredients by pressing and holding on the meal to access the Meal Edit View Activity.
5. The user can **login** to a created account.
6. The user can **register** an account. 

**Optional Features**

1. The user can **add favorite** meals by clicking the star on any specific meal on the Meals List View and this will be shown on the user’s profile.
2. The user can **analyze** what their highest or lowest calorie meal made.
3. The user can **reuse** a previously entered meal and add it as a duplicate meal without manually entering the values a second time. 

### 2. Screen Archetypes
* Dashboard
    * Shows a summary of the user’s meals made per week/month using the following data visualizations
        * Money spent per week/month (line chart)
        * Calories consumed per week/month (line chart)
        * Total number of meals entered (numeric value)
        * Percentage of various macronutrients consumed (pie chart)

* Meals List Screen
    * Shows the list of entered meals that were prepped by the user
    * Includes tile cards that show meal name, price of meal, and total calories
    * Users can also remove meals or mark meals as favorite

* Add Meal Activity
    * A form that allows users to enter new meal prep recipes.
    * Includes:
        * Name of Meal
        * Servings Made
        * Picture of Meal (optional)

* Edit Ingredient Activity
    * A recycler view that shows added ingredients and allows users to edit/remove/add new ingredients
    * Includes:
        * Ingredient Name
        * Quantity of ingredient (in grams)
        * Price of ingredient

* Meal View Detail
    * Shows more details about a specifical meal prep meal that includes:
        * Meal Name
        * Price of meal
        * Servings Made 
        * Picture of meal
        * Total Calories and other total nutrition facts
        * Ingredients (Name and Quantity)

* Profile Page
    * Allows the user to view their profile, favorite meals, and a button that redirects them to the Update Profile Page

* Update Profile Page
    * Allows user to edit their information

* Settings Page
    * Allows the user to adjust their settings: Dark Mode and Notifications
    * Allows user to logout

* Login Page
    * Allows user to log into their account or sign up

* Register Page
    * Allows user to register a new account

* Welcome Page
    * Starting page for a non-logged-in or unregistered user
    * Contains buttons that allow a user to login or register
 
* Edit Meal Page
    * For an already created meal, pressing and holding on it in the meals list will lead to the edit meal page
    * Here, the user can edit the name, number of servings, and picture
    * The user can click on "Edit Ingredients" to move to the Edit Ingredients Page

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Dashboard
* Meals List
* Add Meal
* Profile
* Settings

**Flow Navigation** (Screen to Screen) 

- Welcome Screen
  - => Login (clicking on 'Login' leads to Login Screen)
  - => Sign Up (clicking on 'Sign Up' leads to Sign Up Screen)
- Sign Up Screen
  - => Meals List (successful sign up leads to Meals List Screen)
- Login Screen
  - => Meals List (successful login leads to Meals List Screen)
- Add Meal Screen
  - => Edit Ingredients (logging a new meal leads to the Edit Ingredients Screen)
- Edit Ingredients Screen
  - => Meals List (clicking 'Done' leads to the Meals List Screen)
- Meal List Screen
  - => Meal View Detail (clicking on a meal in the list will lead to the Meal View Detail Screen)
  - => Edit Meal Screen (pressing and holding on a meal in the list will lead to the Edit Meal Screen)
- Edit Meal Screen
  - => Edit Ingredients (confirming any changes will lead to the Edit Ingredients Screen)
- Profile Screen
  - => Edit Profile (pressing 'Update Profile' will lead to the Edit Profile Screen)
- Edit Profile Screen
  - => Profile (pressing 'Update my Info' will lead to the Profile Screen)
- Settings Screen
  - => Welcome (clicking on 'Logout' will lead to the Welcome Screen)
- Dashboard Screen
  - => None
- Meal View Detail Screen
  - => None

## Wireframes

<!-- [Add picture of your hand sketched wireframes in this section] <img src="YOUR_WIREFRAME_IMAGE_URL" width=600> -->
![wireframe](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/1af60a5f-26b2-4a1c-901e-6e35be2aace4)



<br>

<br>

### [BONUS] Digital Wireframes & Mockups
![MealPrepTracker1](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/f6f644f4-4386-43b9-a149-a943d3e1bcca)
![MealPrepTracker2](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/e90dfa14-8bfb-40d2-bb0b-3305d7ca6ef0)

## Digital Wireframes with flows
![MealPrepTracker3](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/4c81bc0d-109d-45a6-804a-51556c38f1b2)

### [BONUS] Interactive Prototype
![MealPrepTrackerPrototype](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/0098d56d-a1a0-451d-839f-33f359baed9a)

<br>

# Milestone 2 - Build Sprint 1 (Unit 8)

## GitHub Project board

[Add screenshot of your Project Board with three milestones visible in
this section]
![Image](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/98120760/a8ea0a5d-473c-4a68-91d4-aeb16814ee71)

## Issue cards
[Add screenshot of your Project Board with the issues that you've been working on for this unit's milestone]
![issues_worked_on_ms2](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/98120760/2cd1afbd-c8c4-4283-bedb-72c768872f8b)
[Add screenshot of your Project Board with the issues that you're working on in the **NEXT sprint**. It should include issues for next unit with assigned owners.]
![issues_to_be_worked_on_ms2](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/98120760/3a198b11-968a-458b-8e3c-319c3c25e80f)

## Issues worked on this sprint

- [x] Make sign up layout
- [x] Make edit ingredient activity layout
- [x] Make login layout
- [x] Make welcome layout
- [x] Make add meal activity layout
- [x] Make meals list layout
- [x] Make meal list recycler view tile layout
- [x] Make dashboard layout
- [x] Make profile page layout
- [x] Make settings layout
- [x] Make bottom navigation bar
- [x] Make update profile layout
- [x] Make edit meal layout
- [x] Make meal view detail layout
- [x] Create empty android project

[Add video/gif of your current application that shows build progress]
<img src="M2_Progress.gif" width=600>
<br>

# Milestone 3 - Build Sprint 2 (Unit 9)

## GitHub Project board

[Add screenshot of your Project Board with the updated status of issues for Milestone 3. Note that these should include the updated issues you worked on for this sprint and not be a duplicate of Milestone 2 Project board.]
![milestone3board](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/7169227d-8839-4ab6-b104-4e0f3f99f256)

## Issue cards
![milestone3](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/9e4e28bb-1a58-4a8b-a2df-2f865c279b25)

## Completed user stories

- [x] Create database schema
- [x] Implement login activity
- [x] Implement Meal View Detail Activity
- [x] Implement Register Activity
- [x] Create data access objects to perform CRUD operations
- [x] Implement welcome activity
- [x] Implement edit meal activity
- [x] Create Firebase DB and connection class that connects to the database
- [x] Implement Update Profile Activity
- [x] Implement settings Fragment
- [x] Implement dashboard Fragment
- [x] Implement profile Fragment
- [x] Set up navbar on activity main
- [x] Implement add meal fragment
- [x] Implement meals list fragment
- [x] Implement edit ingredient activity

[Add video/gif of your current application that shows build progress]

## App Demo Video

- Embed the YouTube/Vimeo link of your Completed Demo Day prep video
