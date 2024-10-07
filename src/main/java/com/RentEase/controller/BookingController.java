package com.RentEase.controller;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Booking;
import com.RentEase.entity.Meridian;
import com.RentEase.entity.Property;
import com.RentEase.exception.ResourceNotFound;
import com.RentEase.exception.UnauthorizedException;
import com.RentEase.repository.BookingRepository;
import com.RentEase.repository.MeridianRepository;
import com.RentEase.repository.PropertyRepository;
import com.RentEase.service.BucketService;
import com.RentEase.service.EmailService;
import com.RentEase.service.PDFService;
import com.RentEase.service.SmsService;
import jakarta.mail.MessagingException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final MeridianRepository meridianRepository;
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final PDFService pdfService;
    private final BucketService bucketService;
    private final SmsService smsService;
    private final EmailService emailService;

    public BookingController(BookingRepository bookingRepository, PropertyRepository propertyRepository,
                             MeridianRepository meridianRepository, PDFService pdfService, BucketService bucketService, SmsService smsService, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.meridianRepository = meridianRepository;
        this.pdfService = pdfService;
        this.bucketService = bucketService;
        this.smsService = smsService;
        this.emailService = emailService;
    }

    public static MultipartFile convert(String filePath) throws IOException {
        //Load the file from the Specified path
        File file = new File(filePath);

        //Reade the file content into a byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        //Convert the byte array to a Resource (ByteArrayResource)
        Resource resource = (Resource) new ByteArrayResource(fileContent);

        //Create MultiPartFile from a Resource
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }
            @Override
            public String getOriginalFilename() {
                return file.getName();
            }
            @Override
            public String getContentType() {
                return null;
            }
            @Override
            public boolean isEmpty() {
                return fileContent.length == 0;
            }
            @Override
            public long getSize() {
                return fileContent.length;
            }
            @Override
            public byte[] getBytes() throws IOException {
                return fileContent;
            }
            @Override
            public InputStream getInputStream() throws IOException {
                return resource.getInputStream();
            }
            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                Files.write(dest.toPath(), fileContent);
            }
        };
        return multipartFile;
    }

    @PostMapping("/addBooking")
    public ResponseEntity<Booking> createBooking(
            @RequestBody Booking booking,
            @RequestParam long propertyId,
            @AuthenticationPrincipal AppUser user
    ) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(
                () -> new ResourceNotFound("Property not found with id: " + propertyId));

        Integer nightlyPrice = property.getNightlyPrice();
        int totalPrice = nightlyPrice * booking.getTotalNights();
        double totalPriceWithTax = totalPrice + (totalPrice * (0.18));

        Meridian meridian = meridianRepository.findById(booking.getMeridian().getId())
                .orElseThrow(
                        () -> new ResourceNotFound("Meridian not found with id: " + booking.getMeridian().getId()));

        booking.setTotalPrice(totalPrice);
        booking.setFinalPriceWithTax(totalPriceWithTax);
        booking.setAppUser(user);
        booking.setProperty(property);
        booking.setMeridian(meridian);

        Booking savedBooking = bookingRepository.save(booking);

        String filePath = pdfService.generateBookingDetailsPdf(savedBooking);

        try {
            MultipartFile fileMultiPart = BookingController.convert(filePath);
            String fileUploadedUrl = bucketService.uploadFile(fileMultiPart, "rentease23");
            sendMessage(fileUploadedUrl);
            emailService.sendEmailWithAttachment(user.getEmailId(), "Booking Confirmation", "Your Booking is confirmed. Booking Pdf is Attached ", fileUploadedUrl, filePath);
        } catch (IOException e) {
            throw new UnauthorizedException("Error generating PDF for booking with id: " + savedBooking.getId() + e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(savedBooking, HttpStatus.OK);
    }



    public void sendMessage(String url) {
        smsService.sendSMS("+918602915346", "Your Booking is confirmed. Click here : " + url);
    }



}
