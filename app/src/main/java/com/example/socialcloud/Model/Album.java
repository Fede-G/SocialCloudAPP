package com.example.socialcloud.Model;

/**
 * Class that represent an Album object but not the image itself
 */
public class Album {
    private Integer id_album;
    private String metaTag;
    private User user;
    private String fileName;

    /**
     *
     */
    public Album(){ user = new User(); }

    /**
     * Getter for the File name
     * @return  the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter for the file name
     * @param fileName the name of the file that will be set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter for the ID of the album
     * @return the ID of the album
     */
    public Integer getId() {
        return id_album;
    }

    /**
     * Setter for the ID of the album
     * @param id the ID that will be set
     */
    public void setId(Integer id) {
        this.id_album = id;
    }

    /**
     * Getter for the meta tag
     * @return the meta tag
     */
    public String getMetaTag() {
        return metaTag;
    }

    /**
     * Setter for the meta tag
     * @param tag the meta tag that will be set
     */
    public void setMetaTag(String tag) {
        this.metaTag = tag;
    }

    /**
     * Getter of the user of the album
     * @return User object of the album
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter of the user for the album
     * @param user the User that will be set
     */
    public void setUser(User user) {
        this.user = user;
    }

}
