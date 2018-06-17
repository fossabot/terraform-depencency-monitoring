package com.github.dependencymonitoring.terraform.core.services.kms;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;
import lombok.val;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * AWS Key Management Service base implementation
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class KMSService {
    /**
     * singleton instance variable
     */
    private static KMSService instance;

    /**
     * Default constructor
     */
    private KMSService() {
    }

    /**
     * Obtains a singleton instance of the service
     *
     * @return - KMSService instance
     */
    public static KMSService getInstance() {
        if (instance == null)
            instance = new KMSService();
        return instance;
    }

    /**
     * Decrypts a base64-encoded encrypted text
     *
     * @param key - base64-encoded encrypted string
     * @return a plain-text string containing the decrypted value
     */
    public String decryptKey(String key) {
        System.out.printf("Decrypting key %s\n", key);
        byte[] encryptedKey = Base64.decode(key);

        val client = AWSKMSClientBuilder.standard()
                .withRegion(Regions.fromName(System.getenv("AWS_DEFAULT_REGION")))
                .build();

        val request = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(encryptedKey));

        val plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8"));
    }
}
