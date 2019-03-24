package ca.mcgill.ecse428.foodme.controller;

import ca.mcgill.ecse428.foodme.exception.NullObjectException;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.model.Response;
import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferences")
public class PreferenceController {

    @Autowired
    PreferenceRepository preferenceRepository;

    /**
     * Greating
     * @return String Preference connected!
     */
    @RequestMapping("/")
    public String greeting() {
        return "Preference connected!";
    }

    /**
     * Controller method that gets all preferences in the database
     * @return ResponseEntity a response in type ResponseEntity
     */
    @GetMapping("/get/all")
    public ResponseEntity getAllPreferences() {
        List<Preference> allPs;
        try {
            allPs = preferenceRepository.getAllPreferences();
        } catch(NullObjectException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(allPs);
    }

    /**
     * Controller method that gets the list of preferences of a user
     * @param username the username
     * @return ResponseEntity a response in type ResponseEntity
     */
    @GetMapping("/{username}")
    public ResponseEntity getPreferencesForUser(@PathVariable("username") String username) {
        List<Preference> prefForUser = null;
        try {
            prefForUser = preferenceRepository.getPreferencesForUser(username);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(prefForUser);
    }

    /**
     * Controller method that adds a preference
     * @param username the username
     * @param location the location
     * @param cuisine the type of cuisine
     * @param price the price range
     * @param sortBy the category to which it is sort by
     * @return Preference the preference set by the user
     */
    @PostMapping("/{user}/add")
    public ResponseEntity addPreference(
            @PathVariable("user") String username, @RequestParam String location, @RequestParam String cuisine,
            @RequestParam String price, @RequestParam String sortBy) {
        try {
           preferenceRepository.createPreference(username, location, cuisine, price, sortBy);
        } catch(NullObjectException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Preference successfully created."));
    }

    /**
     * Controller method that edits a preference
     * @param username the username
     * @param pID the id of the preference
     * @param location the location
     * @param cuisine the type of cuisine
     * @param price the price range
     * @param sortBy the category to which it is sort by
     * @return Preference the preference set by the user
     */
    @PostMapping("/{user}/edit/{pID}")
    public ResponseEntity editPreference(
            @PathVariable("user") String username, @PathVariable("pID") int pID, @RequestParam String location,
            @RequestParam String cuisine, @RequestParam String price, @RequestParam String sortBy){
        try {
            preferenceRepository.editPreference(username, pID, location, cuisine, price, sortBy);
        }catch(NullObjectException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Preference successfully modified."));
    }

    /**
     * Controller method that deletes a preference
     * @param pID the id of the preference
     * @param username the username
     * @return ResponseEntity a response in type ResponseEntity
     */
    @PostMapping("/{user}/delete/{pID}")
    public ResponseEntity deletePreference(
            @PathVariable("user") String username, @PathVariable("pID") int pID){

        try {
            preferenceRepository.deletePreference(username,pID);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Preference successfully deleted."));
    }
}
