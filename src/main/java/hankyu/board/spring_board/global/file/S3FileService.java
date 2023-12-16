package hankyu.board.spring_board.global.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import hankyu.board.spring_board.global.exception.file.FileUploadFailureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class S3FileService implements FileService {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public void upload(MultipartFile multipartFile, String fileName) {

        try(InputStream is = multipartFile.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            String key = "images/" + fileName;

            amazonS3Client.putObject(new PutObjectRequest(bucket, key, is, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FileUploadFailureException(e.getCause());
        }

    }

    @Override
    public void delete(String fileName) {
        String key = "images/" + fileName;
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
    }
}
