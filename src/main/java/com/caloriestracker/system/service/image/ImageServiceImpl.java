package com.caloriestracker.system.service.image;

import com.caloriestracker.system.dto.response.ai.ImageResponse;
import com.caloriestracker.system.entity.Image;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<ImageResponse> getGallery(Long userId, Pageable pageable) {

        return imageRepo
                .findByUserIdOrderByUploadedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public void toggleFavorite(Long userId, Long imageId) {

        Image image = imageRepo
                .findByIdAndUserId(imageId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Image not found")
                );

        image.setFavorite(!image.isFavorite());
    }

    @Override
    public void delete(Long userId, Long imageId) {

        Image image = imageRepo
                .findByIdAndUserId(imageId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Image not found")
                );

        try {
            Path path = Paths.get(image.getPath());

            if (Files.exists(path)) {
                Files.delete(path);
            }

        } catch (Exception ignored) {
        }

        imageRepo.delete(image);
    }

    private ImageResponse mapToResponse(Image image) {

        ImageResponse response = new ImageResponse();

        response.setId(image.getId());
        response.setPath(image.getPath());
        response.setUploadedAt(image.getUploadedAt());
        response.setStatus(image.getStatus());
        response.setFavorite(image.isFavorite());

        if (image.getMealItem() != null && image.getMealItem().getId() != null) {
            response.setMealItemId(image.getMealItem().getId());
        }

        return response;
    }
}