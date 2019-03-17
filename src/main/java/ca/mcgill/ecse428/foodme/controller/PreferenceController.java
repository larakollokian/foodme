package ca.mcgill.ecse428.foodme.controller;

import ca.mcgill.ecse428.foodme.exception.NullObjectException;
import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.model.Response;
import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
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
     * Greeting
     * @return Preference connected
     */
    @RequestMapping("/")
    public String greeting() {
        return "Preference connected!";
    }

    /**
     * Controller method that gets all preferences in the database
     * @return ResponseEntity
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
     * @param username
     * @return ResponseEntity
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
     * @param username
     * @param location
     * @param cuisine
     * @param price
     * @param sortBy
     * @return ResponseEntity
     */
    @PostMapping("/{user}/add")
    public ResponseEntity addPreference(
            @PathVariable("user") String username, @RequestParam String location, @RequestParam String cuisine,
            @RequestParam String price, @RequestParam String sortBy) {
        try {
            Preference preference = preferenceRepository.createPreference(username, location, cuisine, price, sortBy);
        } catch(NullObjectException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Preference successfully created."));
    }

    /**
     * Controller method that edits a preference
     * @param username
     * @param pID
     * @param location
     * @param cuisine
     * @param price
     * @param sortBy
     * @return ResponseEntity
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
     * @param pID
     * @param username
     * @return ResponseEntity
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
