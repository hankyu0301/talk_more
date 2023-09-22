package hankyu.board.spring_board.service.file;

import hankyu.board.spring_board.exception.file.FileUploadFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class LocalFileService implements FileService{

    @Value("${upload.image.location}")
    private String location;

    @PostConstruct
    void postConstruct() {
        File dir = new File(location);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    public void upload(MultipartFile multipartFile, String fileName) {
        try{
            multipartFile.transferTo(new File(location + fileName));
        } catch (IOException e) {
            throw new FileUploadFailureException(e.getCause());
        }
    }

    @Override
    public void delete(String fileName) {
        new File(location + fileName).delete();
    }
}
