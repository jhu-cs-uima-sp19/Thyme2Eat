# Thyme2Eat
User Interfaces and Mobile Applications 

Spring 2019

Team K

# How to use Thyme2Eat
Welcome, and thank you for trying out Thyme2Eat. **Internet connection is a must for using the application.**

Thyme2Eat is a mobile application that focuses on creating meal plan for users based on their preferences.

College students often struggle to find the right time to cook meals that they would actually enjoy creating and eating.
The goal of this app is to simplify the process of purchasing and making food while adhering to the needs of the users.


In order to get started, make your way to the SETTINGS, easily located using the navigation drawer on the top left corner of the screen. There, you can set the following preferences (cuisine, diet, allergies, foods to include, foods to exclude, as well as the number of dependents). Make sure you remember to press CONFIRM. These preferences will be used to determine the best recipies for you. If you are not sure about what preferences you want, you can skip this step. 

Next, make your way back to the MEAL PLAN using the navigation drawer again. When you are here, press the ADD MEALS button. This will direct you to a calendar view where you can choose which dates you want to plan meals for. (You can only chose future dates). After you have chosen your dates, press CONFIRM. This will bring you back to the MEAL PLAN screen. From here, wait until your new meal plan loads. 

With your new meal plan, you have several options. You can long click on the meals to change the dates, change the time, or delete each of the recipes that you have in your plan. Otherwise, you can click on each recipe to bring you to VIEW RECIPE. Here you have the option to either view the ingredients or the recipe. You can also find ALTERNATIVE RECIPES which are suggested if you click on the fork and knife button beside each meal. If you like one of these meals better than your suggested meal, you can choose to SWAP it.

If you view a meal that you love and want to save it for future use, you can press the star button to add it to your FAVORITES. In the favorites section, you can choose to add this favorited meal into your future meal schedule.

When you add a recipe to your meal plan, all of the ingredients and their amounts are automatically added to the shopping list. To access the shopping list, simpily go to the SHOPPING LIST in the navigation drawer. There, you can check off items that you have purchased, delete items that you no longer want, or add other items that you wish to purchase.

# Implemented features for First Sprint:
- Integration with Spoonacular API and Firebase
- Viewing the current meal plan from the home page.
- Ability to change the time and date for a planned meal or to delete the meal, by long pressing on a recipe.
- Viewing the ingredients and the instructions for a planned meal by tapping on it.
- Planning meals for days chosen in a calendar view, when the user hits "Add Meal" in the home page. Multiple meals per day can be chosen by clicking on a day multiple times.
- Showing shopping list items needed for the planned meals, under the shopping list tab in the nav drawer.
- Deleting shopping list items using the checkbox or the X button.
- Seeing calendar view when you hit "Add Meals" button.
- Click and unclick calendar dates to add and remove meal planning for that day.
- CHANGE: Instead of graying out past dates, an error message pops up when you try to click on a past date to add a meal.
- Scroll through months on the calendar.
- Confirm button on the calendar confirms days you want to plan and calls the API. It takes you to the main activity with potential meals for the days you wanted to plan.
- X on calendar screen takes you back to the Main Activity without saving any changes.
- Different databases for different users.

# Not complete in the First Sprint:
- We have not added data persistence for meals that have already been planned for certain days. This means that dates that you clicked on for meal planning before will not have a black dot the next time you open the calendar, but on the meal plan screen, those days will still have meals planned.
- Right now, unclicking a calendar date means that you do not want to plan for that day. However, because there is no data persistence right now, if you open the calendar again, you can add another meal to the same day by clicking on it. In the next sprint, we will add data persistence and come up with a conclusive way to set number of meals per day (either through settings or the calendar).
- The "Add" button for adding items to the shopping list does not function entirely properly because only one dialog box is present to take the name input.

# What we plan to do in the Second Sprint:
- Add data persistence for the calendar. This means figuring out whether number of meals per day will be added through the calendar or in settings.
- Add the dialog boxes for the other inputs for shopping list items. Link the added item to the database. Also we will improve the shopping list to combine like items, and delete items from the shipping list if the recipe is deleted. 
- Add communication between calendar in main activity if a user changes the time or date and the calendar in the calendar activity.
- A more robust search process with more preferences given by the user.
- A more automated way of planning meal times (based on time to create meal, user preferences, and posible calendar integration)
- Adding an activity for favorited meals that the user can add to their current meal plan
- Some sort of recipe search engine so that users have even more freedom to add recipes to their meal plan.
- Overall quality of life changes to the UI

# Completed in the Second Sprint:
- We implemented the Favorites feature, which users can use for recipes that they enjoy and would like quick access to in case the would like to cook it again.
- The addition and deletion of shopping list items based on the addition and deletion of the meal plan items have been optimized. Ingredients of the same name will have their amounts added together. If the units are different, the app tries its best to convert the unit. If there is no conversion possible, they will be listed as different items.
- Adding a shopping list item now has a functional dialog box with error handling built in.
- The checkboxes within the shopping list function properly and are used to mass delete items from the list for easy use.
- Conglomerating like ingredients based on quantities has been optimized.
- Data persistence of the calendar, where the user will be shown if they already have a meal planned for a date. They will also be shown an error message if a date they chose is in the past, or if they have a meal planned already.
- Alternative meals are suggested to the user by clicking the fork and knife button beside a meal; this is based on the type of recipe and the ingredients within it. Users can only check off one alternative recipe and can only swap to one alternative recipe.
- We changed buttons, colors, layouts, and overall UI to make it resemble an Android application more.

