package com.caloriestracker.system.controller.ai;

import com.caloriestracker.system.dto.response.ai.AiAnalyzeResponse;
import com.caloriestracker.system.dto.response.ai.ImageResponse;
import com.caloriestracker.system.enums.ImageStatus;
import com.caloriestracker.system.service.ai.AiService;
import com.caloriestracker.system.service.image.ImageService;
import com.caloriestracker.system.util.AuthUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final AiService aiService;
    private final ImageService imageService;
    private final AuthUtils authUtils;

    @PostMapping(
            value = "/analyze/{mealId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AiAnalyzeResponse> analyze(
            @PathVariable Long mealId,
            @RequestPart("file") MultipartFile file
    ) {

        return ResponseEntity.accepted()
                .body(aiService.analyze(mealId, file));
    }

    @GetMapping("/status/{imageId}")
    public ResponseEntity<ImageStatus> getStatus(
            @PathVariable Long imageId
    ) {

        return ResponseEntity.ok(
                aiService.getStatus(imageId)
        );
    }

    @GetMapping("/result/{imageId}")
    public ResponseEntity<AiAnalyzeResponse> getResult(
            @PathVariable Long imageId
    ) {
        return ResponseEntity.ok(aiService.getResult(imageId));
    }

    @GetMapping("/gallery")
    public ResponseEntity<Page<ImageResponse>> getGallery(
            Authentication auth,
            Pageable pageable
    ) {

        Long userId = authUtils.getUserId(auth);

        return ResponseEntity.ok(
                imageService.getGallery(userId, pageable)
        );
    }

    @PatchMapping("/favorite/{imageId}")
    public ResponseEntity<Void> toggleFavorite(
            @PathVariable Long imageId,
            Authentication auth
    ) {

        Long userId = authUtils.getUserId(auth);

        imageService.toggleFavorite(userId, imageId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId,
            Authentication auth
    ) {

        Long userId = authUtils.getUserId(auth);

        imageService.delete(userId, imageId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/retry/{imageId}")
    public ResponseEntity<Void> retry(
            @PathVariable Long imageId
    ) {

        aiService.retry(imageId);

        return ResponseEntity.accepted().build();
    }
}