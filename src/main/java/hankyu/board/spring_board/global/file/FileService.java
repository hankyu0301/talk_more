package hankyu.board.spring_board.global.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void upload(MultipartFile multipartFile, String fileName);
    void delete(String fileName);
}
