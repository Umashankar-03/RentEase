package com.RentEase.controller;

import com.RentEase.service.BucketService;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping("s3bucket")
public class BucketController {

    private final BucketService bucketService;
    private final AmazonS3 amazonS3;

    public BucketController(BucketService bucketService, AmazonS3 amazonS3) {
        this.bucketService = bucketService;
        this.amazonS3 = amazonS3;
    }

    @PostMapping(path = "/upload/file/{bucketName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file, @PathVariable String bucketName) {
        String fileUrl = bucketService.uploadFile(file, bucketName);
        return new ResponseEntity<>(fileUrl, HttpStatus.OK);
    }

    @GetMapping(path = "/file/{bucketName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> listOfFiles(@PathVariable String bucketName) {
        List<String> files = bucketService.listOfFiles(bucketName);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }


    @GetMapping("/file/{bucketName}/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String bucketName, @PathVariable String fileName) {
        byte[] fileData = bucketService.downloadFile(bucketName, fileName);
        ByteArrayResource resource = new ByteArrayResource(fileData);
        return ResponseEntity
                .ok()
                .contentLength(fileData.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }


    @DeleteMapping(path = "/file/{bucketName}/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String bucketName, @PathVariable String fileName) {
        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            bucketService.deleteFile(bucketName, decodedFileName);
            return new ResponseEntity<>("file deleted Successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }


    @PutMapping(path = "/file/{bucketName}/{fileName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateFile(@RequestParam MultipartFile file, @PathVariable String bucketName, @PathVariable String fileName) {
        String updateFileUrl = bucketService.updateFile(file, bucketName, fileName);
        return new ResponseEntity<>(updateFileUrl, HttpStatus.OK);
    }

    @DeleteMapping(path = "/file/{bucketName}")
    public ResponseEntity<String> deleteBucket(@PathVariable String bucketName) {
        try {
            bucketService.deleteBucket(bucketName);
            return new ResponseEntity<>("Bucket deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete bucket: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping(path = "/ ")
    public ResponseEntity<String>deleteFileByUrl(@RequestParam String fileUrl){
        try {
            bucketService.deleteFileByUrl(fileUrl);
            return new ResponseEntity<>("File deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}

