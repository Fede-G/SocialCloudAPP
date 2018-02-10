package com.example.socialcloud.Model;

import java.io.Serializable;

//User class that rappresents my user but not his encryption data. It needs to be Serializable because we need to pass it from one to another activity
//Serializable means that can be converted to a sequence of bytes and reconverted again to get the same result
public class User implements Serializable{

    private Integer id_user;
    private String firstname;
    private String lastname;
    private String email;
    private String birth_day;
    private String city;
    private String pw;
    private byte[] photo = null;

    /**
     * Void constructor, just to initialize all fields
     */
    public User(){
        this.firstname = "";
        this.lastname = "";
        this.email = "";
        this.birth_day = "";
        this.city = "";
        this.pw = "";
        photo = new byte[0];
    }

    /**
     * Constructor for all params
     * @param firstname Name
     * @param lastname  Surname
     * @param email     Email
     * @param birth_day Birth date
     * @param city      City
     * @param pw        Password
     * @param photo     Photo
     */
    public User(String firstname, String lastname, String email, String birth_day, String city, String pw, byte[] photo){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.birth_day = birth_day;
        this.city = city;
        this.pw = pw;
        this.photo = photo;
    }

    /**
     * Constructor for params (but not image)
     * @param firstname Name
     * @param lastname  Surname
     * @param email     Email
     * @param birth_day Birth date
     * @param city      City
     * @param pw        Password
     */
    public User(String firstname, String lastname, String email, String birth_day, String city, String pw){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.birth_day = birth_day;
        this.city = city;
        this.pw = pw;
        if(this.photo==null){
            photo = new byte[0];
        }
    }

    /**
     * Getter of ID
     * @return ID
     */
    public Integer getId_user() {
        return id_user;
    }

    /**
     * Setter of ID
     * @param id_user ID of the User
     */
    public void setId_user(Integer id_user) {
        this.id_user = id_user;
    }

    /**
     * Getter of Name
     * @return Name
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Setter of Name
     * @param firstname Name
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Getter of Surname
     * @return Surname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Setter of Surname
     * @param lastname Surname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Getter of Email
     * @return Email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter of Email
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter of Birth day
     * @return Birth day (format: dd/MM/yy)
     */
    public String getBirth_day() {
        return birth_day;
    }

    /**
     * Setter of Birth day
     * @param birth_day Birth day (format: dd/MM/yy)
     */
    public void setBirth_day(String birth_day) {
        this.birth_day = birth_day;
    }

    /**
     * Getter of City
     * @return City
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter of City
     * @param city City
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter of Password (plaintext)
     * @return Password
     */
    public String getPw() {
        return pw;
    }

    /**
     * Setter of Password (plaintext)
     * @param pw password
     */
    public void setPw(String pw) {
        this.pw = pw;
    }

    /**
     * Getter of photo (bytes[])
     * @return Photo
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Setter of photo
     * @param photo Photo
     */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
