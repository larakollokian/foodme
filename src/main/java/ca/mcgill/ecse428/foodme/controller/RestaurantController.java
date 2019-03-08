package ca.mcgill.ecse428.foodme.controller;

import ca.mcgill.ecse428.foodme.exception.NullObjectException;
import ca.mcgill.ecse428.foodme.model.Response;
import ca.mcgill.ecse428.foodme.model.Restaurant;
import ca.mcgill.ecse428.foodme.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    RestaurantRepository restaurantRepository;

    /**
     * Greeting
     * @return Restaurant connected
     */
    @RequestMapping("/")
    public String greeting() {
        return "Restaurant connected!";
    }

    /**
     * Controller method that adds a restaurant and a user to the likedRestaurant list in the database
     * @param username (of user)
     * @param restaurantID
     * @param restaurantName
     */
    @PostMapping("/{user}/addliked/{id}")
    public ResponseEntity addLiked(@PathVariable("user") String username, @PathVariable("id") String restaurantID, @RequestParam String restaurantName) {
        try {
            restaurantRepository.addLiked(username, restaurantID,restaurantName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "User successfully liked Restaurant"));

    }

    /**
     * Controller method that removes a restaurant and a user from the likedRestaurant list in the database
     * @param username (of user)
     * @param restaurantID
     */
    @PostMapping("/{user}/removeliked/{id}")
    public ResponseEntity removeLiked(@PathVariable("user") String username, @PathVariable("id") String restaurantID) {
       //TO-DO
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "User successfully removed liked Restaurant"));
    }

    /**
     * Controller Method that lists all liked restaurant of a user
     * @param username
     * @return ResponseEntity
     */
    @GetMapping("/{user}/get/all/liked")
    public ResponseEntity allLiked(@PathVariable("user") String username){
        List<String> liked;
        try{
            liked = restaurantRepository.listAllLiked(username);
        }catch(NullObjectException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(liked);
    }

    /**
     * Controller Method that lists all disliked restaurant of a user
     * @param username
     * @return ResponseEntity
     */
    @GetMapping("/{user}/get/all/disliked")
    public ResponseEntity allDisliked(@PathVariable("user") String username){
        List<String> disliked = null;
        return ResponseEntity.status(HttpStatus.OK).body(disliked);
    }

    /**
     * Controller method that adds a restaurant and a user to the dislikedRestaurant list in the database
     * @param username (of user)
     * @param restaurantID
     * @param restaurantName
     * @return ResponseEntity
     */
    @PostMapping("/{user}/adddisliked/{id}")
    public ResponseEntity addDisliked(@PathVariable("user") String username, @PathVariable("id") String restaurantID, @RequestParam String restaurantName) {
        try {
            restaurantRepository.addDisliked(username, restaurantID,restaurantName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "User successfully disliked Restaurant"));
    }

    /**
     * Controller method that removes a restaurant and a user from the dislikedRestaurant list in the database
     * @param username (of user)
     * @param restaurantID
     * @return ResponseEntity
     */
    @PostMapping("/{user}/removedisliked/{id}")
    public ResponseEntity removeDisliked(@PathVariable("user") String username, @PathVariable("id") String restaurantID) {
        //TO-DO
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "User successfully removed disliked Restaurant"));
    }

    /**
     * Controller method to get all restaurants in the database
     * @return ResponseEntity
     */
    @GetMapping("/get/all")
    public ResponseEntity getAllRestaurants() {
        List<Restaurant> restaurants = null;
        try {
            restaurants = restaurantRepository.getAllRestaurants();
        }catch (NullObjectException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(restaurants);
    }

    /**
     * Controller method to get a restaurant's data based on its id
     * @param id
     * @return ResponseEntity
     */
    @GetMapping("/get/{id}")
    public ResponseEntity getRestaurant(@PathVariable("id") String id) {
        List<Restaurant> restaurant = null;
        try {
            restaurant = restaurantRepository.getRestaurantQuery(id);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restaurant.get(0));
    }
}