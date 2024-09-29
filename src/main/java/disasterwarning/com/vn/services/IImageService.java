package disasterwarning.com.vn.services;

import disasterwarning.com.vn.models.dtos.DisasterInfoDTO;
import disasterwarning.com.vn.models.dtos.ImageDTO;
import disasterwarning.com.vn.models.entities.Image;

import java.util.List;
import java.util.Optional;

public interface IImageService {
    public ImageDTO createImage(ImageDTO image) {
        if (image.getImageUrl() == null || image.getImageUrl().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        if (image.getPublicId() == null || image.getPublicId().isEmpty()) {
            throw new IllegalArgumentException("Public ID cannot be null or empty");
        }
        log.info("Create image with url: {}" , image.getImageUrl());
        Image imageEntity = mapper.convertToEntity(image, Image.class);
        log.info("Mapper imageEntity : {}" , imageEntity);

        imageEntity = imageRepository.save(imageEntity);
        log.info("Image saved successfully : {}", imageEntity);
        return new ImageDTO(
                imageEntity.getImageId(),
                imageEntity.getImageUrl(),
                imageEntity.getPublicId(),
                mapper.convertToDto(imageEntity.getDisasterInfo(), DisasterInfoDTO.class)
        );
    }

    @Override
    public List<ImageDTO> getAllImages() {
        return List.of();
    }

    @Override
    public Optional<ImageDTO> getImageById(int id) {
        return Optional.empty();
    }

    @Override
    public ImageDTO updateImage(int id, ImageDTO imageDetails) {
        return null;
    }

    @Override
    public boolean deleteImage(int id) {
        return false;
    }
}
}
