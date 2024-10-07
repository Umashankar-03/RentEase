package com.RentEase.controller;

import com.RentEase.entity.AppUser;
import com.RentEase.entity.Booking;
import com.RentEase.entity.Meridian;
import com.RentEase.entity.Property;
import com.RentEase.exception.ResourceNotFound;
import com.RentEase.exception.UnauthorizedException;
import com.RentEase.repository.AppUserRepository;
import com.RentEase.repository.BookingRepository;
import com.RentEase.repository.MeridianRepository;
import com.RentEase.repository.PropertyRepository;
import com.RentEase.service.BucketService;
import com.RentEase.service.EmailService;
import com.RentEase.service.PDFService;
import com.RentEase.service.SmsService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/CreateBooking")
public class CreateBookingController {

   private BookingRepository bookingRepository;
   private AppUserRepository appUserRepository;
   private MeridianRepository meridianRepository;
   private PropertyRepository propertyRepository;
   private PDFService pdfService;
   private EmailService emailService;
   private BucketService bucketService;
   private SmsService smsService;

    public CreateBookingController(BookingRepository bookingRepository, AppUserRepository appUserRepository, MeridianRepository meridianRepository, PropertyRepository propertyRepository, PDFService pdfService, EmailService emailService, BucketService bucketService, SmsService smsService) {
        this.bookingRepository = bookingRepository;
        this.appUserRepository = appUserRepository;
        this.meridianRepository = meridianRepository;
        this.propertyRepository = propertyRepository;
        this.pdfService = pdfService;
        this.emailService = emailService;
        this.bucketService = bucketService;
        this.smsService = smsService;
    }

    @PostMapping
    public ResponseEntity<Booking>createBooking(
           @Valid @RequestBody Booking booking,
            @AuthenticationPrincipal AppUser user,
            @RequestParam long propertyId
    ){

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow( () -> new ResourceNotFound("Property not found with Id: " + propertyId));

        LocalDate today = LocalDate.now();
        LocalDate checkInDate = booking.getCheckInDate();
        LocalDate checkOutDate = booking.getCheckOutDate();
        
        if (booking.getCheckInDate().isBefore(today)){
            throw new UnauthorizedException("Cannot book for past date");
        }
        
        if (checkOutDate.isBefore(checkInDate)) {
            throw new UnauthorizedException("Checkout date must be after check-in date");
        }

//checking for any existing booking overlap with new booking
        Optional<Booking> existingBooking = bookingRepository.findByPropertyAndCheckInDateBeforeAndCheckOutDateAfter(
                property, checkOutDate, checkInDate);

        if(existingBooking.isPresent()){
            throw new UnauthorizedException("Property is already booked for the selected dates.");
        }

        // Check for duplicate booking
        boolean existByUser = bookingRepository.existsByAppUserAndPropertyAndCheckInDate(user, property, checkInDate);
        if(existByUser){
            throw new UnauthorizedException("Duplicate booking is not allowed !!");
        }

        Integer totalNights =(int) checkInDate.until(checkOutDate, ChronoUnit.DAYS);

        Integer nightlyPrice = property.getNightlyPrice();
        int totalPrice = nightlyPrice * totalNights;
        double totalPriceWithTax = totalPrice + (totalPrice * 0.18);

        Meridian meridian = meridianRepository.findById(booking.getMeridian().getId())
                .orElseThrow(() -> new ResourceNotFound("Meridian not found with id" + booking.getMeridian().getId()));

        booking.setTotalNights(totalNights);
        booking.setBookingDate(today);
        booking.setTotalPrice(totalPrice);
        booking.setFinalPriceWithTax(totalPriceWithTax);
        booking.setAppUser(user);
        booking.setProperty(property);
        booking.setMeridian(meridian);

        Booking savedBooking = bookingRepository.save(booking);
// Generate booking PDF and upload to S3 bucket and sending email , sms
        String filePath = pdfService.generateBookingDetailsPdf(savedBooking);
        try {
            MultipartFile fileMultipart = CreateBookingController.convert(filePath);
            String uploadFileURL = bucketService.uploadFile(fileMultipart, "rentease23");
            emailService.sendEmailWithAttachment(user.getEmailId(), "Booking Confirmation" , "Yout Booking is confirmrd. Booking Pdf is Attached", uploadFileURL , filePath);
            sendMessage(uploadFileURL);
// Set PDF URL in booking entity and save
            savedBooking.setBookingPdfUrl(uploadFileURL);
            bookingRepository.save(savedBooking);

        } catch (IOException e) {
            throw new UnauthorizedException("Error generating PDF for booking with id: " + savedBooking.getId() + e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(savedBooking , HttpStatus.OK);
    }



    public void sendMessage(String url){
        smsService.sendSMS("+918602915346", "Your Booking is confirmed . Click here : " +url);
    }

    //Conversion of multipart file

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


}
