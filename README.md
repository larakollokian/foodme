# foodme ![Travis-ci](https://travis-ci.com/larakollokian/foodme.svg?token=s9wt5vK6yqsBSVx5Xszv&branch=master)

## What is it?

Foodme is a Android app designed to give users restaurant recommendations based on their current location and food preferences. We use 2 different API's to make our app's magic work: 

#### Yelp API:

Foodme uses the [Yelp fusion API](https://www.yelp.ca/developers/documentation/v3/get_started) to get restaurant information from Yelp's database, as well as to use certain features related to searching and categorizing restaurants based on price range, type of cuisine, etc.

#### Google Maps API

Foodme also uses the [Google Maps API](https://developers.google.com/maps/documentation/) for navigation purposes. When a user selects a desired restaurant, the Google Maps API is used to open up a map to provide directions on how to get to the restaurant.

## Features

Users begins by creating an optional account or they can continue using the app without one.

Users that choose not to create an account have access to the basic features, which are getting restaurant recommendations based on their current location and food preferences, or getting a random recommendation.

By creating an account, users have access to additional features. Users can choose to save their restaurant preference history. They can also choose to have a personal list of their favourite/least favourite restaurants. These correspond to their personalized "liked/disliked" lists that are saved in their accounts.

As a Foodme account user, you can customize your user information, such as changing your name, your email or your password. If a user forgets their password, they can request to have their password reset and they will receive an email with a randomly generated password which they can use to long in.

#### What is a preference?

The user preference feature allows users to filter their search results by specifying their desired price range, type of cuisine, maximum distance radius, as well as sorting the output of their search result (Sort by: rating, distance, highest rate, etc.) Users can search preferences and even save specific preference profiles (preselected preference parameters) which they can then use later on when desired.

#### What are the liked/disliked lists?

On Foodme, users can add restaraunts to their personal liked/disliked list, Foodme takes this into account when showing users restaurant recommendations, restaurants that have been disliked by a user will not be recommended to them, while restaurants that are liked will be recommened more frequently.


By: The Foodme Team
