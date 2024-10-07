package com.RentEase.service;
//Here business logic is written  to upload the file in the S3 bucket
// Data type of image or any file :- MultipartFile


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BucketService {

    private AmazonS3 amazonS3;

    public BucketService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        if (file.isEmpty()) {
            throw new IllegalStateException("cannot upload empty file");
        } else {
            try {
                File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
                file.transferTo(convFile);
                try {
                    amazonS3.putObject(bucketName, convFile.getName(), convFile);
                    return amazonS3.getUrl(bucketName, file.getOriginalFilename()).toString();
                } catch (AmazonS3Exception s3Exception) {
                    return "Unable to upload file :" + s3Exception.getMessage();
                }
            } catch (Exception e) {
                throw new IllegalStateException("failed to upload file ", e);
            }
        }
    }


    public List<String> listOfFiles(String bucketName) {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        ArrayList<String> fileNames = new ArrayList<>();
        for (S3ObjectSummary os : objects) {
            fileNames.add(os.getKey());
        }
        return fileNames;
    }


    public byte[] downloadFile(String bucketName, String fileName) {
        String byFileName = findFileByFileName(bucketName, fileName);
        if (byFileName == null) {
            throw new IllegalStateException("File not found with partial name :" + byFileName);

        }
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String findFileByFileName(String bucketName, String fileName) {
        List<String> files = listOfFiles(bucketName);
        for (String fileNames : files) {
            if (fileNames.contains(fileName)) {
                return fileName;
            }

        }
        return null;
    }

    public void deleteFile(String bucketName, String decodedFileName) {
        String byFileName = findFileByFileName(bucketName, decodedFileName);
        if (byFileName == null) {
            throw new IllegalStateException("file not found with partial name :" + decodedFileName);
        }
        try {
            amazonS3.deleteObject(bucketName, byFileName);
        } catch (AmazonS3Exception e) {
            throw new IllegalStateException("failed t delete file", e);
        }
    }

    public String updateFile(MultipartFile file, String bucketName, String fileName) {
        deleteFile(bucketName, fileName);

        String uploadFile = uploadFile(file, bucketName);
        return uploadFile;
    }


    public void deleteBucket(String bucketName) {
        VersionListing versionListing = amazonS3.listVersions(new ListVersionsRequest().withBucketName(bucketName));
        while (true) {
            for (S3VersionSummary summary : versionListing.getVersionSummaries()) {
                amazonS3.deleteVersion(bucketName, summary.getKey(), summary.getVersionId());
            }
            if (versionListing.isTruncated()) {
                amazonS3.listNextBatchOfVersions(versionListing);
            } else {
                break;
            }
        }
        amazonS3.deleteBucket(bucketName);
    }
//// https://rentease23.s3.ap-south-1.amazonaws.com/shree%20Ganesh.jpg
    public void deleteFileByUrl(String fileUrl){
        try{
            String bucketName  = fileUrl.split("/")[2].split("\\.")[0];
            String fileName = fileUrl.split("/",4)[3];
            fileName = URLDecoder.decode(fileName , StandardCharsets.UTF_8);

            amazonS3.deleteObject(new DeleteObjectRequest(bucketName , fileName));

        } catch (AmazonS3Exception e) {
            throw new IllegalStateException("Failed to delete file from S3 bucket: " + e.getMessage(), e);
        }
    }




}