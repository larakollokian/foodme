package ca.mcgill.ecse428.foodme;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ca.mcgill.ecse428.foodme.model.*;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ModelTests {

    //Model classes
    private AppUser appUser;
    private Preference preference;
    private Response response;
    private Response response1;
    private Restaurant restaurant;

    //AppUser fields constants
    private final String USERNAME= "johnsmith";
    private final String FIRSTNAME= "John";
    private final String LASTNAME= "Smith";
    private final String EMAIL= "johnsmith@email.com";
    private final String PASSWORD= "Helloworld";
    private final int DEFAULTPREFERENCEID = 1;
    private final Set<Restaurant> likedRestaurants = new HashSet<>();
    private final Set<Restaurant> dislikedRestaurants = new HashSet<>();
    private final Set<Restaurant> visitedRestaurants = new HashSet<>();


    //Restaurant fields constant
    private final String RESTAURANTID="vNB5fXTa2bH07lgqSQXv3g";
    private final String RESTAURANTNAME="Rotisserie Portugalia";
    private final Set<AppUser> appUsers_likes = new HashSet<>();
    private final Set<AppUser> appUsers_dislikes = new HashSet<>();
    private final Set<AppUser> appUsers_visited = new HashSet<>();


    //Preference fields constants
    private final int PID = 1;
    private final String LOCATION = "Montreal";
    private final String CUISINE = "chinese";
    private final String PRICE ="$$$";
    private final String SORTBY = "rating";

    //Response fields constants
    private final Boolean RESPONSE = true;
    private final String MESSAGE = "Hello World";

    /**
     * Initial setup
     * */
    @Before
    public void setUp(){
        appUser = new AppUser();
        appUser.setUsername(USERNAME);
        appUser.setFirstName(FIRSTNAME);
        appUser.setLastName(LASTNAME);
        appUser.setEmail(EMAIL);
        appUser.setPassword(PASSWORD);
        appUser.setDefaultPreferenceID(DEFAULTPREFERENCEID);

        restaurant = new Restaurant();
        restaurant.setRestaurantID(RESTAURANTID);
        restaurant.setRestaurantName(RESTAURANTNAME);

        preference = new Preference();
        preference.setPID(PID);
        preference.setLocation(LOCATION);
        preference.setCuisine(CUISINE);
        preference.setPrice(PRICE);
        preference.setSortBy(SORTBY);
        preference.setUser(appUser);

        response = new Response();
        response.setResponse(RESPONSE);
        response.setMessage(MESSAGE);

        response1 = new Response(RESPONSE,MESSAGE);
    }

    /**
     * UT on AppUser
     * */
    @Test
    public void appUserTests(){

        Assert.assertEquals(USERNAME,appUser.getUsername());
        Assert.assertEquals(FIRSTNAME,appUser.getFirstName());
        Assert.assertEquals(LASTNAME,appUser.getLastName());
        Assert.assertEquals(EMAIL,appUser.getEmail());
        Assert.assertEquals(PASSWORD,appUser.getPassword());
        Assert.assertEquals(DEFAULTPREFERENCEID,appUser.getDefaultPreferenceID());

        appUser.addLikedRestaurants(restaurant);
        appUser.addDislikedRestaurants(restaurant);
        appUser.addVisitedRestaurants(restaurant);

        Assert.assertEquals(true,appUser.removeLikedRestaurants(restaurant));
        Assert.assertEquals(true,appUser.removeDislikedRestaurants(restaurant));
        Assert.assertEquals(true,appUser.removedVisitedRestaurants(restaurant));

        Assert.assertEquals(false,appUser.removeLikedRestaurants(restaurant));
        Assert.assertEquals(false,appUser.removeDislikedRestaurants(restaurant));
        Assert.assertEquals(false,appUser.removedVisitedRestaurants(restaurant));

        appUser.setLikedRestaurants(likedRestaurants);
        appUser.setDislikedRestaurants(dislikedRestaurants);
        appUser.setVisitedRestaurants(visitedRestaurants);

        Assert.assertEquals(likedRestaurants,appUser.getlikedRestaurants());
        Assert.assertEquals(dislikedRestaurants,appUser.getDislikedRestaurants());
        Assert.assertEquals(visitedRestaurants,appUser.getVisitedRestaurants());
    }

    /**
     * UT on Restaurant
     * */
    @Test
    public void restaurantTests(){

        Assert.assertEquals(RESTAURANTID,restaurant.getRestaurantID());
        Assert.assertEquals(RESTAURANTNAME,restaurant.getRestaurantName());

        restaurant.addLikedAppUsers(appUser);
        restaurant.addDislikedAppUsers(appUser);
        restaurant.addVisitedAppUsers(appUser);

        Assert.assertEquals(true,restaurant.removeLikedAppUsers(appUser));
        Assert.assertEquals(true,restaurant.removeDislikedAppUsers(appUser));
        Assert.assertEquals(true,restaurant.removeVisitedAppUsers(appUser));

        Assert.assertEquals(false,restaurant.removeLikedAppUsers(appUser));
        Assert.assertEquals(false,restaurant.removeDislikedAppUsers(appUser));
        Assert.assertEquals(false,restaurant.removeVisitedAppUsers(appUser));

        restaurant.setAppUser_likes(appUsers_likes);
        restaurant.setAppUser_dislikes(appUsers_dislikes);
        restaurant.setAppUser_visited(appUsers_visited);

        Assert.assertEquals(appUsers_likes,restaurant.getAppUser_likes());
        Assert.assertEquals(appUsers_dislikes,restaurant.getAppUser_dislikes());
        Assert.assertEquals(appUsers_visited,restaurant.getAppUser_visited());

    }

    /**
     * UT on Preference
     * */
    @Test
    public void preferenceTests(){

        Assert.assertEquals(PID,preference.getPID());
        Assert.assertEquals(LOCATION,preference.getLocation());
        Assert.assertEquals(CUISINE,preference.getCuisine());
        Assert.assertEquals(PRICE,preference.getPrice());
        Assert.assertEquals(SORTBY,preference.getSortBy());
        Assert.assertEquals(appUser,preference.getUser());

    }

    /**
     * UT on Response
     * */
    @Test
    public void responseTests(){

        Assert.assertEquals(RESPONSE,response.getResponse());
        Assert.assertEquals(MESSAGE,response.getMessage());

        Assert.assertEquals(RESPONSE,response1.getResponse());
        Assert.assertEquals(MESSAGE,response1.getMessage());
    }

}
