package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;
import com.example.socialcloud.Model.User;
import com.example.socialcloud.Util.BCryptMS;
import com.example.socialcloud.Util.ImageConverter;

/**
 * Format class to ask for a save a new user
 */
public class UserGenerator extends FormatGenerator {

    private User user;

    /**
     * Constructor for URL and JSON containing the new user that needs to be created
     * @param user User that needs to be created
     */
    public UserGenerator(User user) {
        super();
        url = makeStaticURL(Global.getRegisterUser_path());

        addUserToJSON(user);

        this.user = user;
    }

    /**
     * Method to save the data of the new user in a JSON that needs to be generated with the format:
     * self.user={id:null,birth_day:'',city:'',email:'',firstName:'',lastName:'',photo:null,pw:''};
     * NOTE: Password needs to be encrypted using Blowfish(Password, Salt[10])
     * @param user  User class that contains all user data
     */
    private void addUserToJSON(User user){
        addObjectToJSON("id", null);
        addObjectToJSON("birth_day", user.getBirth_day());
        addObjectToJSON("city", user.getCity());
        addObjectToJSON("email", user.getEmail());
        addObjectToJSON("firstName", user.getFirstname());
        addObjectToJSON("lastName", user.getLastname());
        if(user.getPhoto() == null || user.getPhoto().length==0){
            addObjectToJSON("photo", "");
        } else {
            addObjectToJSON("photo", ImageConverter.bytesToBase64(user.getPhoto()));
        }
        addObjectToJSON("pw", BCryptMS.hashpw(user.getPw(), BCryptMS.gensalt(10)));
    }

    /**
     * Getter of user
     * @return  a User object of the user that is stored
     */
    public User getUser() {
        return user;
    }
}
