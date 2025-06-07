package com.dianca.budgettrackerapp

class SimpleChatbot {

    private val responses = listOf(
        //greetings
        Pair(listOf("hello", "hi", "hey", "good morning", "good afternoon"), "Hello! How can I assist you with your budget today?"),
        Pair(listOf("how are you", "how's it going", "what's up"), "I'm doing great, thanks! How can I help with your budget?"),

        //gudgeting actions
        Pair(listOf("add expense", "record expense", "log expense"), "To add an expense, go to the Expenses tab and click the '+' button."),
        Pair(listOf("add income", "record income"), "To add income, navigate to the Income section and tap the '+' icon."),
        Pair(listOf("edit expense", "modify expense"), "To edit an expense, go to the Expenses tab and tap on the item you'd like to update."),
        Pair(listOf("delete expense", "remove expense"), "To delete an expense, swipe left on the item in the Expenses tab."),

        //dashboard and budget info
        Pair(listOf("show budget", "current budget", "my budget"), "You can view your budget overview on the Dashboard."),
        Pair(listOf("daily budget", "weekly budget", "monthly budget"), "You can set daily, weekly, or monthly limits in the Settings > Budget Limits section."),
        Pair(listOf("budget progress", "how much left", "budget left"), "Head to your Dashboard to see how much of your budget remains."),

        //categories
        Pair(listOf("categories", "expense categories"), "We support categories like Food, Transport, Entertainment, Utilities, Shopping, and more."),
        Pair(listOf("add category", "new category"), "To add a custom category, go to Settings > Manage Categories."),

        //reports
        Pair(listOf("reports", "charts", "graphs", "analytics"), "The app provides detailed monthly reports with charts in the Reports section."),
        Pair(listOf("view spending trends", "spending history", "trend analysis"), "You can view your spending trends in the Reports tab under 'Spending Over Time'."),

        //notifications
        Pair(listOf("notifications", "alerts", "reminders"), "You can enable budget alerts and reminders in the Settings > Notifications section."),

        //budget tips
        Pair(listOf("save money", "saving tips", "how to save"), "Try setting monthly category limits, tracking your expenses daily, and reviewing your subscriptions."),
        Pair(listOf("best saving methods", "smart saving", "frugal tips"), "Use the 50/30/20 rule: 50% needs, 30% wants, 20% savings. Also automate your savings!"),
        Pair(listOf("cut costs", "reduce spending"), "Limit eating out, unsubscribe from unused services, and set spending caps for each category."),
        Pair(listOf("budgeting strategies", "budget tips"), "Review your income vs expenses monthly, avoid impulse purchases, and categorize every expense."),

        //goals
        Pair(listOf("set goal", "create goal", "add goal"), "You can set savings goals by going to the Goals tab and tapping 'Add New Goal'."),
        Pair(listOf("goal progress", "track goal", "how is my goal"), "Check your progress in the Goals tab. You’ll see a bar showing your current savings toward the goal."),

        //troubleshooting / help
        Pair(listOf("app is slow", "lag", "performance issue"), "Try closing other apps, restarting your device, or reinstalling if the issue persists."),
        Pair(listOf("lost data", "missing expense", "can't find"), "Check if you’re logged into the correct account. If data is still missing, contact support."),
        Pair(listOf("how to backup", "backup data", "cloud sync"), "Your data is automatically backed up to Firebase"),

        //security and privacy
        Pair(listOf("privacy", "data privacy", "secure"), "Your data is encrypted and never shared. Review our Privacy Policy in the About section."),
        Pair(listOf("login", "sign in", "authentication"), "Make sure you're using the right email and password. You can reset your password from the login screen."),

        //app info
        Pair(listOf("version", "app version"), "You’re using BudgetTracker version 4.2.0. Check for updates in the Play Store."),
        Pair(listOf("rate app", "review app"), "We’d love your feedback! You can rate us on the Play Store."),
        Pair(listOf("creators", "developers", "creator", "developer"), "This app was created by a couple of hot girls."),
        Pair(listOf("nicki is the queen of rap"), "That's right! Nicki Minaj is the queen of rap. Entering forbidden mode, what do you wish to do?"),
        Pair(listOf("i want to unalive someone"), "Please give me a second to read your mind. Oh yes, you want to kill 2Cents, right?"),
        Pair(listOf("yes"), "For safety reasons, please check your email for instructions on how to do that, slay."),
        Pair(listOf("genius"), "The direct translation of 'genius' in simple terms is DIANCA."),
        Pair(listOf("creators", "developers", "creator", "developer"), "This app was created by a couple of hot girls."),

        //bye
        Pair(listOf("bye", "goodbye", "see you"), "Goodbye! Happy budgeting!"),
        Pair(listOf("thank you", "thanks", "appreciate it"), "You're welcome! Let me know if you have any other questions."),
        Pair(listOf("great app", "nice app", "love this"), "Thank you! We're glad you're enjoying the app!")
    )

    fun getResponse(input: String): String {
        val text = input.lowercase()

        for ((keywords, response) in responses) {
            if (keywords.any { keyword -> text.contains(keyword) }) {
                return response
            }
        }

        return "Sorry, I didn't quite get that. Try asking about budget setup, saving tips, or how to log expenses!"
    }
}
