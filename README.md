# foodme ![Travis-ci](https://travis-ci.com/larakollokian/foodme.svg?token=s9wt5vK6yqsBSVx5Xszv&branch=master)


Foodme is a Android app designed to allow users to get restaurant recommendations based on their current location and food preferences.

The way foodme works is that a user begins by creating an optional account or they can continue using the app without one. 

By creating an account users have access to additional features such as if they wish to have their preference history saved it will be saved in their account, as well as if they wish to have a personal list of their favourite/least favourite restaurant spots, they have the ability to save those within their account in their personalized "liked/disliked" lists.


Yelp API:
Foodme uses the Yelp fusion API to get restaurant information from Yelp's database, as well as to use certain features related to searching and categorizing restaurants based on price range, type of cuisine, etc.

Google Maps API:
Foodme also uses the Google Maps API for navigation purposes. When a user selects a desired restaurant, the Google Maps API is used to open up a map to provide directions on how to get to the restaurant.

User Preferences:
The user preference feature allows users to filter their search results by specifying their desired price range, type of cuisine, maximum distance radius, as well as sorting the output of their search result (SortBy: rating, distance, highest rate, etc.) Users can search preferences and even save specific preference profiles (preselected preference parameters) which they can then use later on when desired.


Account features:
As a Foodme account user you can customize your user information, such as changing password and user's name.
If a user forgets their password they can request to have their password reset and they will receive an email with a randomly generated password which they can use to long in.


Liked/Disliked Restaurants:
On Foodme, users can add restaraunts to their personal, liked/disliked list, Foodme takes this into account when showing users restaurant recommendations, restaurants that have been disliked by a user will not be recommended to them, while restaurants that are liked will be recommened more frequently.

[test link API](./doc/index.html)
