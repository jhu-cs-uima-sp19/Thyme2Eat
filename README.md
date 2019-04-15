# Thyme2Eat
Team K

# Implemented features for 1st Sprint:
- Integration with Spoonacular API and Firebase
- Viewing the current meal plan from the home page.
- Ability to change the time and date for a planned meal or to delete the meal, by long pressing on a recipe.
- Viewing the ingredients and the instructions for a planned meal by tapping on it.
- Planning meals for days chosen in a calendar view, when the user hits "Add Meal" in the home page. Multiple meals per day can be chosen by clicking on a day multiple times.
- Showing shopping list items needed for the planned meals, under the shopping list tab in the nav drawer.
- Deleting shopping list items using the checkbox or the X button.
- Seeing calendar view when you hit "Add Meals" button.
- Click and unclick calendar dates to add and remove meal planning for that day.
-CHANGE: Instead of graying out past dates, an error message pops up when you try to click on a past date to add a meal.
- Scroll through months on the calendar.
- Confirm button on the calendar confirms days you want to plan and calls the API. It takes you to the main activity with potential meals for the days you wanted to plan.
- X on calendar screen takes you back to the Main Activity without saving any changes.
- Different databases for different users.

# Not Complete in the First Sprint:
- We have not added data persistence for meals that have already been planned for certain days. This means that dates that you clicked on for meal planning before will not have a black dot the next time you open the calendar, but on the meal plan screen, those days will still have meals planned.
- Right now, unclicking a calendar date means that you do not want to plan for that day. However, because there is no data persistence right now, if you open the calendar again, you can add another meal to the same day by clicking on it. In the next sprint, we will add data persistence and come up with a conclusive way to set number of meals per day (either through settings or the calendar).
- The "Add" button for adding items to the shopping list does not function entirely properly because only one dialog box is present to take the name input.

# What we plan to do in the Second Sprint:
- Add data persistence for the calendar. This means figuring out whether number of meals per day will be added through the calendar or in settings.
- Add the dialog boxes for the other inputs for shopping list items. Link the added item to the database. Also we will improve the shopping list to combine like items, and delete items from the shipping list if the recipe is deleted. 
- Add communication between calendar in main activity if a user changes the time or date and the calendar in the calednar activity.
- A more robust search process with more preferences given by the user.
- A more automated way of planning meal times (based on time to create meal, user preferences, and posible calendar integration)
- Adding an activity for favorited meals that the user can add to their current meal plan
- Some sort of recipe search engine so that users have even more freedom to add recipes to their meal plan.
- Overall quality of life changes to the UI

# How to use Thyme2Eat
Welcome, and thank you for trying out Thyme2Eat. 

In order to get started, make your way to the SETTINGS, easily located using the navigation drawer on the top left corner of the screen. There, you can set the following preferences (cuisine, diet, allergies, foods to include, foods to exclude, as well as the number of dependents). Make sure you remember to press CONFIRM. These preferences will be used to determine the best recipies for you. If you are not sure about what preferences you want, you can skip this step. 

Next, make your way back to the MEAL PLAN using the navigation drawer again. When you are here, press the ADD MEALS button. This will direct you to a calendar view where you can choose which dates you want to plan meals for. (You can only chose future dates). After you have chosen your dates, press CONFIRM. This will bring you back to the MEAL PLAN screen. From here, wait until your new meal plan loads. 

With your new meal plan, you have several options. You can change the dates, change the time, or delete each of the recipes that you have in your plan. Otherwise, you can click on each recipe to bring you to VIEW RECIPE. Here you have the option to either view the ingredients or the recipe.

When you add a recipe to your meal plan, all of the ingredients and their amounts are automatically added to the shopping list. To access the shopping list, simpily go to the SHOPPING LIST in the navigation drawer. There, you can check off items that you have purchased, delete items that you no longer want, or add other items that you wish to purchase.
