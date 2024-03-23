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

[Evaluation of your app across the following attributes]
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
    * This app covers a unique idea of creating a tracker for both meals with nutrition facts as well as money spent. Out of our three ideas, we felt this idea was the most complex and well defined with its features of adding meals, creating visuals, and a unique API.

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

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Dashboard
* Meals List
* Add Meal
* Profile
* Settings

**Flow Navigation** (Screen to Screen) 

- Welcome Screen
  - Clicking on 'Login' leads to Login Screen
  - Clicking on 'Sign Up' leads to Sign Up Screen
- Sign Up Screen
  - Successful sign up leads to Dashboard Screen
- Login Screen
  - Successful login leads to Dashboard Screen
- Add Meal Screen
  - Logging a new meal leads to the Edit Ingredients Screen
- Edit Ingredients Screen
  - Clicking 'Done' leads to the Meals List Screen
- Meal List Screen
  - Clicking on a meal in the list will lead to the Meal View Detail Screen
  - Pressing and holding on a meal in the list will lead to the Edit Meal Screen
- Edit Meal Screen
  - Confirming any changes will lead to the Edit Ingredients Screen
- Profile Screen
  - Pressing 'Update Profile' will lead to the Edit Profile Screen
- Edit Profile Screen
  - Pressing 'Update my Info' will lead to the Profile Screen

## Wireframes

<!-- [Add picture of your hand sketched wireframes in this section] <img src="YOUR_WIREFRAME_IMAGE_URL" width=600> -->
![20240321_162851](https://github.com/Meal-Prep-Tracker/CS388-Meal-Prep-Tracker/assets/71194959/f345e081-fb29-486b-a67b-fc211db5a114)


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
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

## Issue cards

- [Add screenshot of your Project Board with the issues that you've been working on for this unit's milestone] <img src="YOUR_WIREFRAME_IMAGE_URL" width=600>
- [Add screenshot of your Project Board with the issues that you're working on in the **NEXT sprint**. It should include issues for next unit with assigned owners.] <img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

## Issues worked on this sprint

- List the issues you completed this sprint
- [Add giphy that shows current build progress for Milestone 2. Note: We will be looking for progression of work between Milestone 2 and 3. Make sure your giphys are not duplicated and clearly show the change from Sprint 1 to 2.]

<br>

# Milestone 3 - Build Sprint 2 (Unit 9)

## GitHub Project board

[Add screenshot of your Project Board with the updated status of issues for Milestone 3. Note that these should include the updated issues you worked on for this sprint and not be a duplicate of Milestone 2 Project board.] <img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

## Completed user stories

- List the completed user stories from this unit
- List any pending user stories / any user stories you decided to cut
from the original requirements

[Add video/gif of your current application that shows build progress]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

## App Demo Video

- Embed the YouTube/Vimeo link of your Completed Demo Day prep video
