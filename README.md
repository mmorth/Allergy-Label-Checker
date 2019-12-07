# Allergy-Label-Checker
Allergy Label Scanner is an Android app that I created. I originally thought of this idea when I was at the grocery store while reading food ingredient labels to verify that they did not contain any of my food allergens. This was a very time-consuming process as it involved reading each food label carefully to ensure it did not contain any of my allergens. Then, I had the idea to create an app that would help me double check that none of my food allergens were missed while reading the food labels.

I was able to implement this project in my Android Embedded Systems class’ final project (CprE 388). I started the app development process by learning how I could convert text captured by my phone’s camera into text that could be sent to my program in real time by researching online. For this, I used the Text API for Android found here. First, the user inputs their allergens into the app. The user then points their camera toward the food’s ingredient label, and the API passes the text captured by the phone’s camera to the code. Next, I had to create the logic for parsing through the text I received from the API to make sure it did not contain any of the allergens. If an allergen is detected, the app will overlay the text for that ingredient in red and the phone will vibrate. If the ingredient is safe, the app will overlay green text for that ingredient.

Creating this app was a great experience for me because it is something that I will use before I eat anything with a food label. It also allowed me to learn the importance of seeking the opinion of the users when developing a solution to a problem as they know best what they want. I look forward to what other apps or programs I can develop that may help my life and the lives of others.

![alt text](https://github.com/mmorth/Allergy-Label-Checker/blob/master/Ingredients_Scanner.png)
![alt text](https://github.com/mmorth/Allergy-Label-Checker/blob/master/Allergy_Input.png)

## Getting Started
These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites
* [Java 8+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Installation Steps
* [Android Studio](https://developer.android.com/studio/) - Installation Steps
* Android Device

### Project Setup
Using Git Bash or the Linux Terminal, navigate to the folder where you want the project located

```
git clone https://github.com/mmorth/Allergy-Label-Checker.git
```

Finally, open the project in Android Studio.

## Authors
Matthew Orth
