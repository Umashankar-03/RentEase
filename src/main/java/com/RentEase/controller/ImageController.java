package com.RentEase.controller;

import com.RentEase.entity.Image;
import com.RentEase.entity.Property;
import com.RentEase.repository.ImageRepository;
import com.RentEase.repository.PropertyRepository;
import com.RentEase.service.BucketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private ImageRepository imageRepository;

    private PropertyRepository propertyRepository;

    private BucketService bucketService;

    public ImageController(ImageRepository imageRepository, PropertyRepository propertyRepository, BucketService bucketService) {
        this.imageRepository = imageRepository;
        this.propertyRepository = propertyRepository;
        this.bucketService = bucketService;
    }

    @PostMapping("/addImage")
    public ResponseEntity<Image> addImages(
            @RequestParam long propertyId,
            @RequestParam String bucketName,
            MultipartFile file
    ) {
        String imageUrl = bucketService.uploadFile(file, bucketName);
        Optional<Property> byId = propertyRepository.findById(propertyId);
        Property property = byId.get();
        Image image = new Image();
        image.setImageUrl(imageUrl);
        image.setProperty(property);
        Image savedImage = imageRepository.save(image);
        return new ResponseEntity<>(savedImage, HttpStatus.OK);

    }

    @GetMapping("/propertyImages")
    public ResponseEntity<List<Image>> fetchPropertyImage(
            @RequestParam long propertyId
    ) {
        List<Image> images = imageRepository.findByPropertyId(propertyId);
        return new ResponseEntity<>(images, HttpStatus.OK);

    }

    @DeleteMapping("/deleteImages")
    public ResponseEntity<String> deleteImageByProperty(
            @RequestParam long propertyId
    ) {
        List<Image> images = imageRepository.findByPropertyId(propertyId);
        if (images.isEmpty()) {
            return new ResponseEntity<>("No image is found for the specified property Id", HttpStatus.OK);
        }
        for (Image image : images) {
            String imageUrl = image.getImageUrl();
            bucketService.deleteFileByUrl(imageUrl);

            imageRepository.delete(image);
        }
        return new ResponseEntity<>("Image delete Successfully ", HttpStatus.OK);
    }


    @PutMapping("/updateImage")
    public ResponseEntity<Image> updatePropertyImage(
            @RequestParam long propertyId,
            @RequestParam String bucketName,
            @RequestParam int imageNumber,
            @RequestParam MultipartFile file
    ) {
        List<Image> images = imageRepository.findByPropertyId(propertyId);

        if (images.isEmpty()){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Image imageToUpdate = images.get(imageNumber);// this line helps to find the number of the image which one you update from property
        String existingImageUrl = imageToUpdate.getImageUrl();

        bucketService.deleteFileByUrl(existingImageUrl);

        String newImageUrl = bucketService.uploadFile(file, bucketName);
        imageToUpdate.setImageUrl(newImageUrl);
        Image updatedImage = imageRepository.save(imageToUpdate);

        return new ResponseEntity<>(updatedImage , HttpStatus.OK);


    }


}