package com.example.socialcloud.Util;

import android.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

//Utility class made to implement the AES cryptography algorithm.
public class AesUtil {

    //Default sizes
    public final static int DEFAULT_KEYSIZE = 128;
    public final static int DEFAULT_ITERATIONCOUNT = 1000;
    private final static int DEFAULT_RANDOMBYTES = 16;

    //Others sizes (final)
    private final int keySize;
    private final int iterationCount;
    private Cipher cipher;

    /**
     * Constructor for variable sizes
     * @param keySize           Size of the key that needs to be generated (2^n)
     * @param iterationCount    Number of iteration needed to be done
     */
    public AesUtil(int keySize, int iterationCount) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw fail(e);
        }
    }

    /**
     * Constructor with DEFAULT_KEYSIZE & DEFAULT_ITERATIONCOUNT
     */
    public AesUtil() {
        this.keySize = DEFAULT_KEYSIZE;
        this.iterationCount = DEFAULT_ITERATIONCOUNT;
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw fail(e);
        }
    }

    /**
     * Method that encrypts an image using salt and iv, in combination with a passphrase
     * @param salt          salt used in the AES algorithm
     * @param iv            IV used in the AES algorithm
     * @param passphrase    passphrase input by the user
     * @param plaintext     text needed to be converted
     * @return              Returns an encrypted string
     */
    public String encrypt(String salt, String iv, String passphrase, String plaintext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes("UTF-8"));
            return base64(encrypted);
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    /**
     * Method that decrypts an image using salt and iv, in combination with a passphrase
     * @param salt          salt used in the AES algorithm
     * @param iv            IV used in the AES algorithm
     * @param passphrase    passphrase input by the user
     * @param ciphertext    text that needs to be decrypted
     * @return              Returns a decrypted string
     */
    public String decrypt(String salt, String iv, String passphrase, String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, base64(ciphertext));
            return new String(decrypted, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    /**
     * Method that applies the AES algorithm on the text, both ways
     * @param encryptMode   Mod of operation for the Cipher
     * @param key           Key generated by the combination of salt and passphrase
     * @param iv            Initialization Vector
     * @param bytes         Array of bytes created from the original text
     * @return              Array of bytes resulted from applying the AES algorith on bytes
     */
    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            cipher.init(encryptMode, key, new IvParameterSpec(hexStringToByteArray(iv)));
            return cipher.doFinal(bytes);
        }
        catch (InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            throw fail(e);
        }
    }

    /**
     * Generation of the Key for the AES PBKDF2 with Hmac SHA1
     * @param salt          salt used in the AES algorithm
     * @param passphrase    passphrase input by the user
     * @return              Common key used for both encryption and decryption
     */
    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hexStringToByteArray(salt), iterationCount, keySize);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw fail(e);
        }
    }

    /**
     * Generation of a random word (secure)
     * @param length    Length of the random word needed to be generated
     * @return          A string made of random Bytes generated in a secure way
     */
    public static String random(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * Generation of a random word (secure), with a predetermined length
     * @return  A string made of random Bytes generated in a secure way
     */
    public static String random() {
        byte[] salt = new byte[DEFAULT_RANDOMBYTES];
        new SecureRandom().nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * Conversion to a Base64 string from bytes
     * @param bytes Bytes that needs to be converted
     * @return      A string in base 64
     */
    private static String base64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Conversion to a Base64 string from a string
     * @param str   String that needs to be converted
     * @return      A string in base 64
     */
    private static byte[] base64(String str) {
        return Base64.decode(str, Base64.DEFAULT);
        //return Base64.decodeBase64(str);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Exception for wrong conversions (Hex and Base64)
     * @param e The exception launched
     * @return  A new object of IllegalStateException
     */
    private IllegalStateException fail(Exception e) {
        return new IllegalStateException(e);
    }
}