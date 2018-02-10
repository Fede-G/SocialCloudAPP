package com.example.socialcloud.Model;

//PublicKey class used to store data about the public key that the user and services get: service, modulus and public exponent, but just locally
public class PublicKey {

    private String service;
    private String modulus;
    private String exponent;

    /**
     * Void constructor
     */
    public PublicKey(){}

    /**
     * Constructor that accepts all three params to be stored
     * @param s service
     * @param m modulus
     * @param e public exponent
     */
    public PublicKey(String s,String m,String e){
        this.service=s;
        this.modulus=m;
        this.exponent=e;
    }

    /**
     * Getter of service
     * @return  service
     */
    public String getService() {
        return service;
    }

    /**
     * Setter of service
     * @param service service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Getter of modulus
     * @return  modulus
     */
    public String getModulus() {
        return modulus;
    }

    /**
     * Setter of modulus
     * @param modulus modulus
     */
    public void setModulus(String modulus) {
        this.modulus = modulus;
    }

    /**
     * Getter of exponent
     * @return  exponent
     */
    public String getExponent() {
        return exponent;
    }

    /**
     * Setter of exponent
     * @param exponent
     */
    public void setExponent(String exponent) {
        this.exponent = exponent;
    }
}
