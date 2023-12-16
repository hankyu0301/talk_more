package hankyu.board.spring_board.domain.post.service;

import hankyu.board.spring_board.domain.post.entity.Image;
import hankyu.board.spring_board.domain.post.entity.Post;
import hankyu.board.spring_board.domain.post.repository.ImageRepository;
import hankyu.board.spring_board.global.exception.post.ImageNotFoundException;
import hankyu.board.spring_board.global.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final FileService fileService;

    @Transactional
    public void create(MultipartFile multipartFile, Post post) {
        Image image = new Image(multipartFile.getOriginalFilename(), post);
        fileService.upload(multipartFile, image.getUniqueName());
        imageRepository.save(image);
    }

    @Transactional
    //  Post의 update()에서 사용
    public void delete(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(ImageNotFoundException::new);
        imageRepository.delete(image);
        fileService.delete(image.getUniqueName());
    }

    //  Post의 delete()에서 사용
    @Transactional
    public void deleteAll(List<Image> images) {
        imageRepository.deleteAll(images);
        images.stream().map(Image::getUniqueName).forEach(fileService::delete);
    }
}