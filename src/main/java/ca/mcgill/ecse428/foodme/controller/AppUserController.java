package ca.mcgill.ecse428.foodme.controller;

import ca.mcgill.ecse428.foodme.exception.*;
import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.model.Response;
import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
import ca.mcgill.ecse428.foodme.security.Password;
import ca.mcgill.ecse428.foodme.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    AuthenticationService authentication;

    /**
     * Greeting
     * 
     * @return AppUser connected
     */
    @RequestMapping("/")
    public String greeting() {
        return "AppUser connected!";
    }

    /**
     * Controller method that attempts to login
     * 
     * @param username
     * @param password
     * @return ResponseEntity
     */
    @GetMapping("/auth/{username}/{password}")
    public ResponseEntity login(@PathVariable("username") String username, @PathVariable("password") String password)
            throws Exception {
        // No exception thrown means the authentication succeeded
        try {
            authentication.login(username, password);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Login successful"));
    }

    /** Controller method that attempts to logout
     * @param username
     * @return ResponseEntity
     */
    @GetMapping("/logout/{username}")
    public ResponseEntity logout(@PathVariable("username")String username) throws Exception {
        //No exception thrown means the authentication succeeded
        try {
            authentication.logout(username);
        }
        catch(AuthenticationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Logout successful"));
    }

    /**
     * Method that creates a new account for a user. Username must be unique.
     * 
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     * @return ResponseEntity
     */
    @PostMapping("/create/{username}/{firstName}/{lastName}/{email}/{password}")
    public ResponseEntity createAccount(@PathVariable("username") String username,
            @PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
            @PathVariable("email") String email, @PathVariable("password") String password) {

        try {
            AppUser user = userRepository.createAccount(username, firstName, lastName, email, password);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "User account successfully created."));
    }

    /**
     * Controller method that gets a AppUser
     * 
     * @param username
     * @return ResponseEntity
     */
    @GetMapping("/get/{username}")
    public ResponseEntity getAppUser(@PathVariable("username") String username) {
        List<AppUser> user;
        try {
            user = userRepository.getAppUserQuery(username);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(user.get(0));

    }

    /**
     * Controller method that gets all users in the database
     * 
     * @return ResponseEntity
     */
    @GetMapping("/get/all")
    public ResponseEntity getAllUsers() {
        List<AppUser> allUsers;
        try {
            allUsers = userRepository.getAllUsers();
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    /**
     * Controller method that deletes user from the database given a username
     * 
     * @param username
     * @return ResponseEntity
     */
    @GetMapping("/delete/{username}")
    public ResponseEntity deleteUser(@PathVariable("username") String username) {
        try {
            userRepository.deleteUser(username);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "User account successfully deleted."));

    }

    /**
     * Controller method that changes a user's password
     * 
     * @param username
     * @param oldPass
     * @param newPass
     * @return ResponseEntity
     */
    @PostMapping("/changePassword/{username}/{oldPass}/{newPass}")
    public ResponseEntity changePassword(@PathVariable("username") String username,
            @PathVariable("oldPass") String oldPass, @PathVariable("newPass") String newPass) {
        AppUser u = new AppUser();
        try {
            userRepository.changePassword(username, oldPass, newPass);
        } catch (Exception e) { // NullObjectException , IllegalStateException, NullObjectException
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Password successfully changed"));
    }

    @PostMapping("/changeFirstName/{username}/{oldFName}/{newFName}")
    public ResponseEntity changeFirstName(@PathVariable("username") String username,
            @PathVariable("oldFName") String oldFName, @PathVariable("newFName") String newFName) {
        AppUser u = new AppUser();
        try {
            userRepository.changeFirstName(username, oldFName, newFName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "First Name successfully changed"));

    }

    /**
    * 
    */
    @PostMapping("/changeFirstName/{username}/{oldLName}/{newLName}")
    public ResponseEntity changeLastName(@PathVariable("username") String username,
            @PathVariable("oldLName") String oldLName, @PathVariable("newLName") String newLName) {
        AppUser u = new AppUser();
        try {
            userRepository.changeLastName(username, oldLName, newLName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "First Name successfully changed"));

    }

    /**
     * Controller method that generates a random password given a length n
     * @param length
     * @return ResponseEntity
     */
    @GetMapping("/password/random/{n}")
    public ResponseEntity getRandomPassword(@PathVariable("n") int length) {
        if(length <= 6){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, "Password should be longer than 6 characters"));
        }
        String randPassword = Password.generateRandomPassword(length);
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true,randPassword));
    }

    /**
     * Controller method that gets a default preference
     * @param username
     * @return ResponseEntity
     */
    @GetMapping("/{username}/getdefault")
    public ResponseEntity getDefaultPreferences(@PathVariable("username") String username) {
        List<Preference> preference;
        try {
            preference = userRepository.getDefaultPreference(username);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(preference.get(0));
    }

    /**
     * Controller method that sets a default preference
     * @param pID
     * @param username
     * @return ResponseEntity
     */
    @PostMapping("/{username}/setdefault/{pid}")
    public ResponseEntity setDefaultPreferences(@PathVariable("username") String username, @PathVariable("pid") int pID) {
        try {
            userRepository.setDefaultPreference(pID,username);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Preference successfully set to default"));
    }


}
