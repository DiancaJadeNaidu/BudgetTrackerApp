DARA Budgeting App - Programming 3C - PROG7313 - Part 2

Welcome to the DARA Budgeting App, an Android-based mobile financial management tool. This program, which was created for the PROG7313 Part 2 module, gives users the ability to track their spending, set financial objectives, and visualize their spending patterns using an easy-to-use interface. In a sleek, contemporary user interface, it provides category creation, budget target settings, picture-supported cost entries, notifications, and summary reports. In contrast to the Part 1 version, the app features a redesigned bottom navigation bar for improved accessibility and ease of use.

GETTING STARTED
PREREQUISITES
Development Environment: Android Studio Ladybug or later

Programming Language: Kotlin

Minimum SDK Version: 24

Target SDK Version: 35

Database: Room Database (RoomDB)

Design Framework: Material Components with View Binding

INSTALLATION AND SETUP
Install Android Studio
Download and install Android Studio

Clone the Repository
Use the following link to clone the GitHub repository:
https://github.com/VCWVL/prog7313-part-2-DiancaJadeNaidu.git

Open the Project
Launch Android Studio, then open the cloned project folder.

Build the Project
Allow Gradle to sync, resolve all dependencies, and compile Room-generated classes.

Run the App
Deploy the app to an emulator or Android device using Android Studio.

FUNCTIONALITY FEATURES
- Category Management
Add Category: Users can create custom categories for organizing expenses.

Manage Categories: A dedicated screen with a RecyclerView to edit, delete, or reset categories. Summary and back navigation included.

- Expense Management
Add Expense: Input name, description, amount, select a category and date range. Optionally, users can upload an image of the receipt.

View Expenses: Filter expenses by name, date, and amount using SeekBars and interactive controls.

- Budget Goals
Set Budget Goals: Users can define monthly minimum and maximum spending goals using EditTexts and SeekBars.

- User Authentication
Register & Login: Screens for secure user registration and login with email and password validation.

Password Requirements: Passwords must be strong, including length and character rules.

- Dashboard
Visual Dashboard: A home screen displays summaries and visual cues for easier navigation and goal monitoring. (not yet functional in Part 2)

- Navigation
Bottom Navigation Bar: Consistent nav bar across screens for easy access to all main features (Home, Add Expense, Categories, Account).

- Profile & Settings
Account Management: Update password, profile picture, or delete the account entirely.

Notifications: Toggle general, promotional, and alert notifications via a settings screen.

Report a Problem: Submit app issues or suggestions via an in-app form or email.


NON-FUNCTIONAL REQUIREMENTS
Material Design: Utilizes Material components for a clean and intuitive UI.

Persistent Local Storage: Uses Room Database for secure offline data storage.

Secure User Handling: Implements password validation and account security features.

Navigation Experience: Integrated bottom nav bar for seamless navigation.

User-Centric Design: Features like image upload, tooltips, and responsive layouts improve usability.


USER GUIDE

Add Category:
Navigate to “Add Category”

Enter category name and tap “Add Category”

Manage Categories:
Tap “Go to Manage Categories”

Tap on a category to update or delete it

Add Expense:
Navigate to “Add Expense”

Enter details (name, description, amount)

Select category, dates, and time period

Upload a receipt photo if needed

Tap “Save Expense”

Set Budget Goals:
Use “Set Budget Goals” to define limits

Adjust using SeekBars or input fields

View Expenses:
Use filters to search by name, amount, or date

View results in a structured list

Summary & Navigation:
Use nav bar to jump between key screens instantly


CHANGELOG
Updates from Part 1 to Part 2:

Added Navigation Bar: A consistent bottom navigation bar was implemented across all screens. This was not included in Part 1 and improves accessibility and flow.

Dashboard Summary: New dashboard feature for summarized insights.

Image Upload: Users can now upload receipts when adding expenses.

Budget Filtering Tools: Added SeekBars and date range filters to improve tracking and control.

Modernized UI: Material Design enhancements and visual consistency.

Notifications Panel: Introduced toggles for enabling/disabling notifications.

New Screens: Profile settings, problem reporting, and account deletion features were added.

Password Rules: Stronger validation and user guidance during registration.


LICENSE
This project is licensed under the MIT License. See the LICENSE file for details.

AUTHORS :Dianca Naidu,Archana Ranjithlall,Riva Jangda, Azande Mnguni

ACKNOWLEDGEMENTS (Code Attributions)
https://youtu.be/7S7646Cymn0?si=rugQTNXzM1ytcQGQ
https://youtu.be/gJPclNjOwP8?si=MWeqtTKeYmgA8KFT
https://youtu.be/Kl3Vb5Lwsak?si=CCESN7bpUzRA00Xv
https://chatgpt.com/
