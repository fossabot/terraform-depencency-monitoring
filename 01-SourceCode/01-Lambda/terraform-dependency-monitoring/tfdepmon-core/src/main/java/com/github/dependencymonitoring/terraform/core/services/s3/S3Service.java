package com.github.dependencymonitoring.terraform.core.services.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.github.dependencymonitoring.terraform.core.exceptions.S3ConstraintNotMetException;
import lombok.Cleanup;
import lombok.val;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Amazon Simple Storage Service base implementation
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
public class S3Service {

    /**
     * singleton instance variable
     */
    private static S3Service instance;

    /**
     * Amazon S3 client instance variable
     */
    private AmazonS3 s3Client;

    /**
     * Default constructor
     */
    private S3Service() {
        s3Client = AmazonS3Client.builder().build();
    }

    /**
     * Obtains a singleton instance of the service
     *
     * @return - S3Service instance
     */
    public static S3Service getInstance() {
        if (instance == null)
            instance = new S3Service();
        return instance;
    }

    /**
     * Downloads a file from S3 if the file has been modified since a particular date.
     * @param bucketName - S3 bucket name
     * @param keyName - S3 key name
     * @param modifiedSinceDate - Date reference to be used in the validation
     * @return a byte[] instance if the constraints are met or an exception if not
     * @throws IOException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     */
    public byte[] downloadFile(String bucketName, String keyName, Date modifiedSinceDate) throws S3ConstraintNotMetException, IOException {
        val getObjectRequest = new GetObjectRequest(bucketName, keyName);
        getObjectRequest.setModifiedSinceConstraint(modifiedSinceDate);
        val s3ClientObject = s3Client.getObject(getObjectRequest);
        if (s3ClientObject != null)
            return IOUtils.toByteArray(s3ClientObject.getObjectContent());
        else
            throw new S3ConstraintNotMetException();
    }

    /**
     * Downloads a file from S3
     * @param bucketName - S3 bucket name
     * @param keyName - S3 key name
     * @return a byte[] instance containing the file's content
     * @throws IOException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     */
    public byte[] downloadFile(String bucketName, String keyName) throws IOException {
        val getObjectRequest = new GetObjectRequest(bucketName, keyName);
        val s3ClientObject = s3Client.getObject(getObjectRequest);
        return IOUtils.toByteArray(s3ClientObject.getObjectContent());
    }

    /**
     * Uploads a file to S3
     * @param bucketName - S3 bucket name
     * @param keyName - S3 key name
     * @param content - contents of the file
     * @throws IOException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     */
    public void uploadFile(String bucketName, String keyName, byte[] content) throws IOException {
        val file = File.createTempFile("temp_tf", ".json");

        //Write to a file
        @Cleanup FileOutputStream fout = new FileOutputStream(file);
        fout.write(content);
        fout.flush();

        // Upload it to S3
        s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));

        // Delete file
        file.delete();
    }
}
