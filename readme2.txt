----CS102 PROJECT GROUP NUMBER----
g1A

----PROJECT TITLE---
BookUrBook

----DESCRIPTION----
BookUrBook is an Android app whose users can sell and buy second-hand school materials. Those users can only register by @.edu.tr mails, which enhances
the safety of the app. In this app, users can communicate with other users via chat.

----SOFTWARES AND LIBRARIES USED----
-Android Studio SDK 4.1.1
-Google Services 4.3.4
-Google Firebase
  |-> Firebase Firestore 22.0.0
  |-> Firebase Storage 19.2.0
  |-> Firebase Authentication 6.4.0
  |-> Firebase Cloud Messaging 21.0.0
-Squareup
  |-> Picasso 2.7
  |-> Retrofit2 2.3.0
-JavaMailAPI by Musfick(GitHub)
-SendNotificationPack by VaibhavMojidra(GitHub)
-CircleImageView 3.1.0 by hdodenhof(GitHub)
-GitHub and GitKraken


----HOW TO SETUP AND RUN THE APP-----
-Download Android Studio 4.1.1
-Open the project named "BookUrBook" with Android Studio. If you get an error regarding your SDK directory,
please click on Files, then click Invalidate Caches/Restart. This should fix the problem.
-Create an emulator in AVD manager(Recommended Pixel 3a with API level 30)
-Please check your Internet connection before starting app.
-You can click the green Run button to start to use app.

----CURRENT STATUS AND WHAT WORKS?----
Group/project Selection: Completed.
Requirements: Completed.
UI Design: Completed.
Detailed Design: Completed.
Implementation: Completed.

Regarding Detailed Design stage, we revised our report regarding the last version of our model classes.

Regarding Implementation,
-We have successfully implemented Login/Register/Verification system in the app.
-We have successfully implemented Post Edit/Create system. Users can add image to their posts. We made the posts presented in Post List
-Searching, sorting and filtering in Post List works properly.
-Wishlist system, adding a Post to wishlist and getting notifications when the price is changed, is works great.
-We implemented a chat system where users can communicate properly. Blocking, notifications, reporting and filtering chats works great.
-Current user does not see the messages or posts of blocked users. Blocked users see a warning when they send message to the user who blocked them.
-In Blocked Users screen, users can see the users they have blocked. They can unblock the blocked users if they want.
-Reporting the users on Post or Chat works succesfully. Admins get email about the reports and they can use Admin Panel to ban users. They can delete
unwanted posts on Post screen.
-In My Posts screen, users can see their post and they can mark them as solved. This make the post invisible in Post List.
-In My Posts screen, users see if they are admin or not. They can edit their posts or add a new post, too.
-In Settings, users can change their profile image, reset their passwords, see the users they have blocked, send us feedback and logout.
-We have succesfully implemented a password reset system in settings or login screen. It sends reset link to users' emails.
-Users can send feedback to us in app. The feedbacks are being sent to our project email.

**Some Reminders:
-To receieve notifications properly, after starting the emulator, close the app and open it from emulator again like a normal phone.
-Sometimes notifications can be received late. This is about the API service. Also, notifications are also being send to only the last phone user have logged in with.

----GROUP MEMBERS and CONTRIBUTIONS----

---Ferhat Korkmaz---
-> Mostly, database management by using FireBase and Connecting MVC Models with the gotten information from Database for activities (with Melih).
-> Login-Register-Logout systems on which only the users with @.edu.tr mails can register with a verification code that are sent to their email.
-> Setting and displaying the user avatar.
-> Designing main menu, login , verification , register, and welcome screens (with Melih).
-> Implementing the JavaMailAPI into the project in order to send a mail that contains the verification code.
-> Making wishlist system in database and connecting it to the app properly.
-> Connecting the admin panel with database.
-> Developing a proper report system for the posts and chats (with Kerem)
-> Block system (With Melih)
-> Eliminating banned and blocked (by the current user) users' posts from the postlist and wishlist screens (with Miray)
-> While registering, it checks more. (Alphanumerical email, password, and username)
-> Feedback system by using JavaMailAPI.
-> Resetting password for the users.
-> Fixing bugs with my group mates.

----Melih Fazıl Keskin----
-> Planning of database structure design with Ferhat. We created our Database in Firebase.
-> After Register, adding user information to database (with Ferhat)
-> After Login or Register, retrieving user information from database and generating User.(with Ferhat)
-> Planning of the chat structure in database
-> Creating Chat and Message model classes
-> Designing the application logo that appears on the initial screens.
-> Implementation of MyChatsActiviy, ChatsActivity and MessageAdapter.
-> Design of Chat Activity
-> Block system (with Ferhat)
-> Notification system in chat and wishlist (with Kaan and Kerem)
-> Device token update in database for notifications
-> New message icon implementation in MyChatsActivity
-> Working on bug fixing, mostly in chat

--- Miray Ayerdem---
-> Adding some methods to model class related to Wishlist 
-> Creating some methods and designing it again Wishlist screen according to feedback
-> Creating Wishlist screen layout in Android Studio using xml, its adapter and its activity class using Java
-> Creating My Posts screen layout in Android Studio using xml, its adapter and its activity class using Java
-> Creating My Blocklist screen layout and its pop-ups in Android Studio using xml, its adapter and its activity class using Java
-> Designing Settings screen layout in Android Studio using xml
-> Also Creating Admin Panel screen layout in Android Studio using xml, its adapter and its activity class using Java
-> Creating My Blocklist screen layout and its pop-ups in Android Studio using xml, its adapter and its activity class using Java
-> Adding alert dialog to some of views like My Posts screen, My Blocklist Screen and Wishlist Screen to interact with users 
-> Meeting Ferhat in order to connect between the objects, which are used in My Posts, Blocklist, and Wishlist activities, and database
-> Some changes in My Posts, Post Details, Main Menu and Settings Screens to try fixing layouts to support for some of the screen size

--- Kaan Tek---
-> Mostly working on user interface and adapting the Java classes to Android environment.
-> Designed Post List screen in Android Studio and built an adapter for the recycler view.
-> Designed Filter screen so that the user can filter de Post List results.
-> Designing My Chats screen in Android Studio and building an adapter for the recycler view.
-> Designing Chat screen in Android Studio and building an adapter for the recycler view.
-> Creating pop up screen for filtering the post list.
-> Worked on notification with Melih and Kerem.
-> Fixed bugs with recycler view so that the view do not get pushed up when opening the keyboard
-> GUI changes with post list for a better user experience
-> Addition of crown icon for free posts and changing the visibility when the price is changed.
-> Bug fixing with friends.

---Kerem Şahin---
->Creation of Model Classes and implementation of their methods.
->Post Details Screen layout in Android Studio using xml, design and the creation of activity classes.
->Create Post Screen layout in Android Studio using xml, design and the creation of activity class.
->Edit Post layout in Android Studio using xml, design and the creation of activity classes.
->Creation of the report post pop up.
->Additions that provide the user-interface interactions (as pop up screens) for the previously mentioned screens. 
->Creation of the notification system when there is change on the wishlist and when there is a message (with Melih and Kaan).
->Creation of the send feedback dialog.
->Revisions on the report system with Ferhat.
->Creation of different toolbars for different screens and the connection of the toolbar buttons with the activity classes.
->Providing linkage between different screens using back arrow, home button and providing the necessary intents.
->Bug fixes for connection of model classes.
