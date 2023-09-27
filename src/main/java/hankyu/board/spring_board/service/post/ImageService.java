package hankyu.board.spring_board.service.post;

import hankyu.board.spring_board.entity.post.Image;
import hankyu.board.spring_board.exception.post.ImageNotFoundException;
import hankyu.board.spring_board.repository.post.ImageRepository;
import hankyu.board.spring_board.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final FileService fileService;

    @Transactional
    public Image create(MultipartFile multipartFile) {
        Image image = new Image(multipartFile.getOriginalFilename());
        imageRepository.save(image);
        fileService.upload(multipartFile, image.getUniqueName());
        return image;
    }

    @Transactional
    public Image read(Long id) {
        return imageRepository.findById(id).orElseThrow(ImageNotFoundException::new);
    }

    @Transactional
    public void delete(Image image) {
        imageRepository.delete(image);
        fileService.delete(image.getUniqueName());
    }
}
