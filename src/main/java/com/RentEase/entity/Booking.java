    package com.RentEase.entity;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDate;

    @Getter
    @Setter
    @Entity
    @Table(name = "booking")
    public class Booking {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Long id;

        @NotNull
        @Column(name = "guest_name", nullable = false)
        private String guestName;

        @Column(name = "total_nights", nullable = false)
        private Integer totalNights;

        @Column(name = "total_price", nullable = false)
        private Integer totalPrice;

        @Column(name = "booking_date", nullable = false)
        private LocalDate bookingDate;

        @NotNull
        @Column(name = "check_in_time", nullable = false)
        private Integer checkInTime;

        @FutureOrPresent
        @Column(name = "check_in_date", nullable = false)
        private LocalDate checkInDate;

        @FutureOrPresent
        @Column(name = "check_out_date", nullable = false)
        private LocalDate checkOutDate;

        @Column(name = "final_price_with_tax", nullable = false)
        private Double finalPriceWithTax;


        @Pattern(regexp = "^[9876]\\d{9}$", message = "Mobile number must start with 9, 8, 7, or 6 and must be 10 digits long")
//        @Size(min = 10, max = 10, message = "Mobile number must be 10 digits")
        @Column(name = "mobile_number", nullable = false)
        private String mobileNumber;


        @Column(name = "booking_pdf_url")
        private String bookingPdfUrl;

        @ManyToOne
        @JoinColumn(name = "property_id")
        private Property property;

        @ManyToOne
        @JoinColumn(name = "app_user_id")
        private AppUser appUser;

        @ManyToOne
        @JoinColumn(name = "meridian_id")
        private Meridian meridian;



    }