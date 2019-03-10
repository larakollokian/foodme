    package ca.mcgill.ecse428.foodme.repository;


    import javax.persistence.EntityManager;
    import javax.persistence.PersistenceContext;
    import javax.persistence.Query;

    import ca.mcgill.ecse428.foodme.exception.InvalidInputException;
    import ca.mcgill.ecse428.foodme.exception.NullObjectException;
    import ca.mcgill.ecse428.foodme.model.*;

    import ca.mcgill.ecse428.foodme.security.Password;
    import ca.mcgill.ecse428.foodme.exception.AuthenticationException;
    import org.springframework.stereotype.Repository;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.*;

    @Repository
    public class AppUserRepository {

        @PersistenceContext
        private EntityManager entityManager;

        /**
         * Method to create an new account
         * @param username The user's chosen username
         * @param firstName The user's first name
         * @param lastName The user's last name
         * @param email The user's email address
         * @param password The user's password
         * @return  appUser
         * @throws Exception (InvalidInputException, IllegalStateException)
         */
        @Transactional
        public AppUser createAccount (String username, String firstName, String lastName, String email, String password) throws Exception {
            String passwordHash="";

            if (!email.contains("@") && !email.contains(".")) {
                throw new InvalidInputException("This is not a valid email address!");
            }
            if (password.length() <= 6) {
                throw new InvalidInputException("Your password must be longer than 6 characters!");
            }
            if(username.length() == 0 || firstName.length() == 0 || lastName.length() == 0 ){
                throw new InvalidInputException("Username, firstName and lastName must be at least 1 character");
            }
            if(entityManager.find(AppUser.class,username)!=null){
                throw new InvalidInputException("User already exists");
            }
            try {
                passwordHash = Password.getSaltedHash(password);
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e.getMessage());
            }
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setDefaultPreferenceID(0);
            u.setPassword(passwordHash);
            entityManager.persist(u);

            return u;

        }

        /**
         * Method that allows users to update their account's password
         * @param username
         * @param oldPassword
         * @param newPassword
         * @return AppUser
         * @throws Exception (AuthenticationException, IllegalStateException, NullObjectException,InvalidInputException)
         */
        @Transactional
        public AppUser changePassword(String username,String oldPassword, String newPassword) throws Exception {

            AppUser u = getAppUser(username);
            try{
                Password.check(oldPassword,u.getPassword());
            }
            catch(AuthenticationException e){
                throw new AuthenticationException("Invalid old password");
            }
            catch(IllegalStateException e){
                throw new IllegalStateException(e.getMessage());
            }
            if(newPassword.length()<=6){
                throw new InvalidInputException("Your password should be longer than 6 characters");
            }
            u.setPassword(Password.getSaltedHash(newPassword));
            entityManager.merge(u);
            return u;
        }

        /**
         * Method that allows to delete a user's account given its username
         * @param username
         * @throws  NullObjectException
         */
        @Transactional
        public AppUser deleteUser(String username) throws NullObjectException {
            AppUser u = getAppUser(username);
            entityManager.remove(u);
            return u;
        }

        /**
         * Method that allows get a user given its username
         * @param username
         * @return AppUser
         * @throws  NullObjectException
         */
        @Transactional
        public AppUser getAppUser(String username) throws NullObjectException {
            if(entityManager.find(AppUser.class, username) == null) {
                throw new NullObjectException("User does not exist");
            }
            else {
                AppUser appUser = entityManager.find(AppUser.class, username);
                return appUser;
            }
        }
        /**
         * Method that allows get a user given its username using query
         * @param username
         * @return AppUser
         * @throws  NullObjectException
         */
        @Transactional
        public List<AppUser> getAppUserQuery(String username) throws NullObjectException {
            Query q = entityManager.createNativeQuery("SELECT * FROM app_users WHERE username=:username");
            q.setParameter("username", username);
            @SuppressWarnings("unchecked")
            List<AppUser> users = q.getResultList();
            if(users.isEmpty()){
                throw new NullObjectException("No users exist");
            }
            return users;
        }

        /**
         * Method that gets all users in database using native SQL query statement
         * @return list of AppUsers
         */
        @Transactional
        public List<AppUser> getAllUsers() throws NullObjectException{
            Query q = entityManager.createNativeQuery("SELECT * FROM app_users");
            @SuppressWarnings("unchecked")
            List<AppUser> users = q.getResultList();
            if(users.isEmpty()){
                throw new NullObjectException("No users exist");
            }
            return users;
        }

        /**
         *Method that gets the number of users in the datase
         * @return number of users
         */
        @Transactional
        public int getNumberUsers(){
            int number = 0;
             try{
                 number = getAllUsers().size();
             }catch(NullObjectException e){
                 return 0;
             }
             return number;
        }

        /**
         * Method that sets a default preference to a user
         * @param pID
         * @param username
         * @throws NullObjectException
         */
        @Transactional
        public int setDefaultPreference(int pID, String username) throws NullObjectException{

            AppUser user = getAppUser(username);

            Query q = entityManager.createNativeQuery("SELECT * FROM preferences WHERE app_user_username =:username AND pid =:pID");
            q.setParameter("username", username);
            q.setParameter("pID", pID);
            List<Preference> preferences = q.getResultList();
            if(preferences.size() == 1) {
                user.setDefaultPreferenceID(pID);
                entityManager.merge(user);
                return pID;
            }
            else{
                //then pid is not a preference of the appUser
                throw new NullObjectException("The preference is not related to the user / does not exist");
            }
        }

        /**
         * Method that gets the default preference of a user
         * @param username
         * @return Preference
         * @throws NullObjectException
         */
        @Transactional
        public List<Preference> getDefaultPreference(String username) throws NullObjectException {

            AppUser user = getAppUser(username);

            int pID = user.getDefaultPreferenceID();

            Query q = entityManager.createNativeQuery("SELECT * FROM preferences WHERE app_user_username =:username AND pid =:pID");
            q.setParameter("username", username);
            q.setParameter("pID", pID);
            @SuppressWarnings("unchecked")
            List<Preference> preferences = q.getResultList();
            if (preferences.size() == 1) {
                return preferences;
            }
            else{
                throw new NullObjectException("User does not have a default preference");
            }
        }

    }

