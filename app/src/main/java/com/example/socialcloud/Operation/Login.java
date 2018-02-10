package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;
import com.example.socialcloud.Model.User;

public class Login extends FormatGenerator{
    public Login(User user) {
        super();
        url = makeStaticURL(Global.getLogin_path());

        addEmailPwToJSON(user);
    }

    public Login() {
        super();
        url = makeStaticURL(Global.getLogin_path());
    }

    private void addEmailPwToJSON(User user){
        addObjectToJSON("id", null);
        addObjectToJSON("birth_day", "");
        addObjectToJSON("city", "");
        addObjectToJSON("email", user.getEmail());
        addObjectToJSON("firstName", "");
        addObjectToJSON("lastName", "");
        addObjectToJSON("photo", "");
        addObjectToJSON("pw", user.getPw());
    }
}
