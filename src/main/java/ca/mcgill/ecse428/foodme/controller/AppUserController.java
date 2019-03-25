package ca.mcgill.ecse428.foodme.controller;

import ca.mcgill.ecse428.foodme.exception.*;
import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.model.Response;
import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
import ca.mcgill.ecse428.foodme.repository.RestaurantRepository;
import ca.mcgill.ecse428.foodme.security.Password;
import ca.mcgill.ecse428.foodme.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    AuthenticationService authentication;

    /**
     * Greeting
     * @return AppUser connected
     */
    @RequestMapping("/")
    public String greeting() {
        return "AppUser connected!";
    }

    /**
     * Controller method that attempts to login
     * @param username
     * @param password
     * @return ResponseEntity
     */
    @GetMapping("/auth/{username}/{password}")
    public ResponseEntity login(@PathVariable("username") String username, @PathVariable("password") String password) {
        // No exception caught means the authentication succeeded
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
    public ResponseEntity logout(@PathVariable("username")String username){
        //No exception caught means the authentication succeeded
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
            sendRegistrationConfirmationEmail(email, firstName, username);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "User account successfully created."));
    }

    /**
     * Helper method that sends a confirmation email after an account is successfully created
     * @param recipient
     * @param firstName
     * @param username
     * @return ResponseEntity
     */
    private void sendRegistrationConfirmationEmail(String recipient, String firstName, String username) throws Exception {
        String host = "smtp.gmail.com";
        String wmail = "foodmeapplication@gmail.com";//change accordingly
        String pw = "FoodMeApp428";//change accordingly
        String to = recipient;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        javax.mail.Session session2 = javax.mail.Session.getDefaultInstance(props, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(wmail,pw);
            }
        });
        //Compose the message
        try {
            MimeMessage message = new MimeMessage(session2);
            message.setFrom(new InternetAddress("FoodMe Application <foodmeapplication@gmail.com>"));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject("Welcome to FoodMe!");
            message.setText("Hi "+firstName+", \n\nThank you for creating a profile under the username "+username+". \n\nYour account has been successfully activated!\n\n" +
                    "The FoodMe team");
            //send the message
            Transport.send(message);
        } catch (MessagingException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Controller method that gets a AppUser
     * @param username
     * @return ResponseEntity
     */
    @GetMapping("/get/{username}")
    public ResponseEntity getAppUser(@PathVariable("username") String username) {
        AppUser user = new AppUser();
        try {
            user = userRepository.getAppUser(username);
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);//user.get(0));

    }

    /**
     * Controller method that deletes user from the database given a username
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
     * @param username
     * @param oldPass
     * @param newPass
     * @return ResponseEntity
     */
    @PostMapping("/changePassword/{username}/{oldPass}/{newPass}")
    public ResponseEntity changePassword(@PathVariable("username") String username,
            @PathVariable("oldPass") String oldPass, @PathVariable("newPass") String newPass) {
        try {
            userRepository.changePassword(username, oldPass, newPass);
        } catch (Exception e) { // NullObjectException , IllegalStateException, NullObjectException
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Password successfully changed"));
    }

    /**
     * Controller method that changes a user's first name
     * @param username
     * @param newFName
     * @return ResponseEntity
     */
    @PostMapping("/changeFirstName/{username}/{newFName}")
    public ResponseEntity changeFirstName(@PathVariable("username") String username, @PathVariable("newFName") String newFName) {
        try {
            userRepository.changeFirstName(username, newFName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "First Name successfully changed"));
    }

    /**
     * Controller method that changes a user's last name
     * @param username
     * @param newLName
     * @return ResponseEntity
     */
    @PostMapping("/changeLastName/{username}/{newLName}")
    public ResponseEntity changeLastName(@PathVariable("username") String username, @PathVariable("newLName") String newLName) {
        try {
            userRepository.changeLastName(username, newLName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "Last Name successfully changed"));
    }

    /**
     * Controller method that generates a random password given a length n
     * 
     * @param length
     * @return ResponseEntity
     * @throws Exception
     */
    @PostMapping("{username}/resetPassword/{n}")
    public ResponseEntity resetPassword(@PathVariable("username") String username, @PathVariable("n") int length) {
        String uUsername = username;
        String randPassword;
        try {
            AppUser u = userRepository.getAppUser(username);
            randPassword = Password.generateRandomPassword(length);
            userRepository.resetPassword(uUsername, randPassword);
            sendResetPasswordConfirmationEmail(u.getEmail(), u.getFirstName(), uUsername, randPassword);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true,"Password successfully reset"));
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
        return ResponseEntity.status(HttpStatus.OK).body(preference);
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

    /**
     * Controller method to get all users in the database
     * @return ResponseEntity
     */
    @GetMapping("/get/all")
    public ResponseEntity getAllUsers() {
        List<AppUser> users = null;
        try {
            users = userRepository.getAllUsers();
        } catch (NullObjectException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /**
     * Helper method that sends a confirmation email after a password is successfully changed
     * @param recipient
     * @param firstName
     * @param username
     * @param newPassword
     * @return ResponseEntity
     */
    private void sendResetPasswordConfirmationEmail(String recipient, String firstName, String username, String newPassword) {
        String host = "smtp.gmail.com";  
        String wmail = "foodmeapplication@gmail.com";//change accordingly  
        String pw = "FoodMeApp428";//change accordingly
        String to = recipient;
        String generatedPW = newPassword;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        javax.mail.Session session2 = javax.mail.Session.getDefaultInstance(props, new javax.mail.Authenticator() {  
       
             protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(wmail,pw);
              }
         });
         //Compose the message
         try {
             MimeMessage message = new MimeMessage(session2);
             message.setFrom(new InternetAddress("FoodMe Application <foodmeapplication@gmail.com>"));
             message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
             message.setSubject("Your New Foodme Account Password");
             message.setText("Hi "+firstName+", \n\nLooks like you forgot the password associated with the username "+username+". \n\nWe have generated a new password for you\n\n\nYour new password is: " + generatedPW +
             "\n\nThe FoodMe team");
             //send the message
             Transport.send(message);
         } catch (MessagingException e) {e.printStackTrace();} 
    }

}
