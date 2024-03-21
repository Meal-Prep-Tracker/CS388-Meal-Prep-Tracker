
Dustin La, Joshua Quizon, Rahul Shah

CS388-002

Professor Jayarajah

25 March 2024

Group 2

Idea Evaluation

**Initial Brainstorming**



1. **Video Game Explorer - A discovery app to find video games.**
2. Invest Portfolio Manager - An app that manages your stocks, bonds, ETF, mutual funds, and other investment securities. 
3. **Meal Prep Tracker - A tracker app that allows users to track their meal prep plans with their nutrition facts and expenses. **
4. Expense Tracker - An app to track your expenses on food, transportation, entertainment, and other areas of your life. 
5. Sleep Tracker - An app tracks how many hours of sleep you are getting per week, allowing the user to input their sleep time every day. 
6. **Trivia App - A trivia app that challenges users to a Trivia game and can compete against other users.**
7. Food Place Search - Finds local food restaurants to eat at and users mark restaurants as favorite and write notes about them.
8. Water Consumption App - An app that tracks how much water you drank on a week by week basis. 
9. Pomodoro Assistant - An application that helps manage the use of the Pomodoro technique for study and work sessions; study habits would be tracked in the app.
10. Art Inspiration App - An application that presents various works of art (both real and AI generated) that ideally inspires artists to come up with new ideas.

**Project Idea #1 - Videogame Explorer**



* Description:
    * Users would be able to search video games and see detailed information about each game including title, developer, genre, description, links to buy the game, and reviews.
    * Users would also be able to add games to their wishlist and played games list. If a game was played by the user, they can create a review of the game.
        * API: [https://www.giantbomb.com/api/documentation/](https://www.giantbomb.com/api/documentation/)
        * Recycler View Details (Scrollable List):
            * Title
            * Developer
            * Genre
            * Description
            * Links to places where you can buy it
            * Reviews
        * 3 responsive functionalities:
            * Add to wishlist (star an item in the recycler view or in the detail page)
            * Add to “played game” list
            * If in “played game” list,  user can submit a review
        * Navigation
            * Trending
            * recommended (based on user profile params)
            * Search
                * by Name
                * by Genre
                * by Studio
        * Data
            * Local Database to store previous search queries
            * Remote database to store wishlist items, reviews, and “played game” list
* Mobile: How uniquely mobile is the product experience?
    * This app could incorporate push notifications as well as increased user interactions to search for games. 
* Story: How compelling is the story around this app?
    * This app would be able to help users find games to play as well as keep track of previously played games.
    * Users could also rate games after they play it.
* Market: How large or unique is the market for this app?
    * This app would target an audience that plays video games frequently. 
    * Users that frequently look for games to play or have played many games would find the most use from this app.
* Habit: How habit-forming or addictive is this app? :
    * This app would be used if a user wants to find a new game to play or if a user wants to review a game.
* Scope: How well-formed is the scope for this app?
    * This app covered the basic idea for creating a scrollable list, having persistent data, and allowing users to add items. But we found this idea to be underwhelming, since it is no more than just a glorified normal website.  It is also too similar to other videogame browsing platforms, like Epic Games and Steam.

**Project Idea #2 - Meal Prep Tracker**



* Category: Health and Finance.  It is in these categories because we are tracking the meals that the users make as well as how much they have spent for the ingredients of the meal.
* Description:
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
* Mobile: How uniquely mobile is the product experience?
    * This app would allow users to easily access the meals they have been consuming as well as looking at past meals they have made in case they want to make it again.
    * This app can include personalized push notifications regarding added meals, weekly food consumption summaries, and more
    * Offline functionality: the app will data on the device for offline access, like preferences and most recently added meals
    * Seamless use of camera: the user will be able to take optionally take a picture of what meal they made when logging it in the app
* Story: How compelling is the story around this app?
    * This app would help users keep better track of their meal prep plans and understand more about their spending habits for meals and how much nutritional intake they are getting.  
* Market: How large or unique is the market for this app?
    * This app would target an audience that is into meal prepping.
    * It would allow users to easily track their meals.
* Habit: How habit-forming or addictive is this app?
    * This app would be used every time the user wants to enter a new meal, see their weekly statistics, or look at past meals they have made. This app would be able to be used at least weekly or more often. 
* Scope: How well-formed is the scope for this app?
    * This app covers a unique idea of creating a tracker for both meals with nutrition facts as well as money spent. Out of our three ideas, we felt this idea was the most complex and well defined with its features of adding meals, creating visuals, and a unique API.

**Project Idea #3 - Trivia App**



* Description:
    * Users are able to compete in a Trivia game with any amount of questions and any category they choose. They will be presented questions in a multiple choice style quiz and receive their final results in the end. The questions and answers will be obtained from an API. Once the user finishes, their score would be uploaded to a leaderboard for that category. Users would also be able to submit their own trivia questions for other users. 
        * API: [https://opentdb.com/api_config.php](https://opentdb.com/api_config.php)
        * Recycler View Details (Scrollable List):
            * Question
            * Answer
            * Correct/Incorrect Status
            * Answered on Date
        * 3 responsive functionalities:
            * Mark questions for review
            * Delete questions 
            * Create custom questions
        * Navigation (Bottom Navigation)
            * Play
            * Profile
            * Create Question
            * Leaderboard
        * Data
            * Local database to store most recently answered questions
            * Remote database to store uploaded questions and older answered questions
* Mobile: How uniquely mobile is the product experience?
    * This app would allow users to compete in Trivia quizzes with easy interactable features.
    * It can include push notifications. 
* Story: How compelling is the story around this app?
    * This app would allow users to play trivia games to advance their knowledge. Users can compete against their friends to answer as many questions as they can correctly.
* Market: How large or unique is the market for this app?
    * This app would target an audience that likes trivia and learning.
    * Users that frequently like challenges and learning would like this app. 
* Habit: How habit-forming or addictive is this app? :
    * This app could be used any time the user wants to do a new challenge. It can be a daily action of doing a daily trivia quiz. 
* Scope: How well-formed is the scope for this app?
    * This app covers the idea of trivia games and allows users to to play, create questions, and view past answered questions and leaderboard. This idea lacks the mobile specific features as well as being as unique as our chosen idea. 

**Final app idea chosen: Meal Prep Tracker**
