package com.example.socialcloud.Model;

//PrivateKey class used to store data about the private key that the user gets: modulus, public exponent and private key, but just locally
public class PrivateKey {

    private String modulus;
    private String public_exponent;
    private String private_exponent;

    /**
     * Void constructor
     */
    public PrivateKey(){}

    /**
     * Constructor that accepts all three params to be stored
     * @param m     modulus
     * @param pu_e  public exponent
     * @param pr_e  private exponent
     */
    public PrivateKey(String m, String pu_e, String pr_e){
        this.modulus=m;
        this.public_exponent=pu_e;
        this.private_exponent=pr_e;
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
     * Getter of public exponent
     * @return  public exponent
     */
    public String getPublicExponent() {
        return public_exponent;
    }

    /**
     * Setter of public exponent
     * @param exponent public exponent
     */
    public void setPublicExponent(String exponent) {
        this.public_exponent = exponent;
    }

    /**
     * Getter of private exponent
     * @return  private exponent
     */
    public String getPrivateExponent() {
        return private_exponent;
    }

    /**
     * Setter of private exponent
     * @param exponent  private exponent
     */
    public void setPrivateExponent(String exponent) {
        this.private_exponent = exponent;
    }

}
