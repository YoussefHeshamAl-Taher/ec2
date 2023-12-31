/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.amazonaws.cloudhsm.examples;

import com.amazonaws.cloudhsm.jce.provider.CloudHsmProvider;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Base64;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Demonstrate how to encrypt and decrypt data using AES keys with ECB mode.
 */
public class ECBEncryptDecryptRunner {

    public static void main(final String[] args) throws Exception {
        try {
            if (Security.getProvider(CloudHsmProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new CloudHsmProvider());
            }
        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }

        System.out.println("Using AES to test encrypt/decrypt in ECB mode");
        String transformation = "AES/ECB/NoPadding";
        Key key = SymmetricKeys.generateAESKey(256, "AESECB Test");
        encryptDecrypt(transformation, key);
    }

    /**
     * Encrypt and decrypt a string using the transformation/key supplied by the caller.
     *
     * @param transformation
     * @param key
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static void encryptDecrypt(String transformation, Key key)
            throws NoSuchAlgorithmException,
            NoSuchProviderException,
            NoSuchPaddingException,
            InvalidKeyException,
            UnsupportedEncodingException,
            IllegalBlockSizeException,
            BadPaddingException {

        byte[] plainText = new byte[32];
        new Random().nextBytes(plainText);
        System.out.println("Base64 plain text = " + Base64.getEncoder().encodeToString(plainText));

        // Encrypt the string and display the base64 cipher text
        Cipher encryptCipher = Cipher.getInstance(transformation, CloudHsmProvider.PROVIDER_NAME);
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = encryptCipher.doFinal(plainText);

        System.out.println("Base64 cipher text = " + Base64.getEncoder().encodeToString(cipherText));

        // Decrypt the cipher text and display the original string
        Cipher decryptCipher = Cipher.getInstance(transformation, CloudHsmProvider.PROVIDER_NAME);
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedText = decryptCipher.doFinal(cipherText);

        System.out.println("Base64 decrypted text = " + Base64.getEncoder().encodeToString(decryptedText));
    }
}
