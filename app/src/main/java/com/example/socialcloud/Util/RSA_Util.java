package com.example.socialcloud.Util;

import android.util.Base64;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//Class that implements the RSA Algorithm
public class RSA_Util {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Encryption of a message using the modulus and public exponent of the receiver
     * @param modulus   modulus, base 16
     * @param exponent  public exponent, base 16
     * @param rawData   data that needs to be converted
     * @return          a string encrypted using RSA
     */
    public static String encryptPublic(String modulus, String exponent, String rawData){
        KeyFactory keyFactory;
        String encryptedResult = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");

            //Decode from string hex (exp and mod) to byte[]
            byte[] crypted_modulus = new byte[256];
            BigInteger cryptedmod = new BigInteger(modulus, 16);
            if (cryptedmod.toByteArray().length > 256) {
                for (int i=1; i<257; i++) {
                    crypted_modulus[i-1] = cryptedmod.toByteArray()[i];
                }
            } else {
                crypted_modulus = cryptedmod.toByteArray();
            }

            byte[] crypted_exponent = new byte[256];
            BigInteger cryptedexp = new BigInteger(exponent, 16);
            if (cryptedexp.toByteArray().length > 256) {
                for (int i=1; i<257; i++) {
                    crypted_exponent[i-1] = cryptedexp.toByteArray()[i];
                }
            } else {
                crypted_exponent = cryptedexp.toByteArray();
            }

            //Generator of public keys
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(cryptedmod, cryptedexp);
            RSAPublicKey key = (RSAPublicKey)keyFactory.generatePublic(pubKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] rawUTF8 = rawData.getBytes("UTF-8");

            //encryption
            byte[] encryptedRawData = cipher.doFinal(rawUTF8);
            encryptedResult = bytesToHex(encryptedRawData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encryptedResult;
    }

    public static String decrypt(String modulus, String private_exponent, String rawData){
        KeyFactory keyFactory = null;
        try {
            //Decoder from HexString to byte[]
            byte[] crypted_bytes = new byte[256];
            BigInteger cryptedmsg = new BigInteger(rawData, 16);
            if (cryptedmsg.toByteArray().length > 256) {
                for (int i=1; i<257; i++) {
                    crypted_bytes[i-1] = cryptedmsg.toByteArray()[i];
                }
            } else {
                crypted_bytes = cryptedmsg.toByteArray();
            }

            //generator of private keys
            keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec prvKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus, 16), new BigInteger(private_exponent, 16));
            RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(prvKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] cipherData = cipher.doFinal(crypted_bytes);
            return new String(cipherData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * To encode the bytes to an hex string
     * @param bytes array of bytes that needs to be encoded
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
