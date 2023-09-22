package hankyu.board.spring_board.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void upload(MultipartFile multipartFile, String fileName);
    void delete(String fileName);
}
