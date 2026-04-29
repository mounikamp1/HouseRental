package com.airbnb.booking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Booking Entity - Represents a property reservation
 * 
 * Indexed for performance:
 * - homeId: For fast lookup of property bookings
 * - userId: For fast lookup of user bookings
 * - Compound index on (homeId + checkIn + checkOut) for conflict detection
 */
@Document(collection = "bookings")
@CompoundIndex(name = "home_dates_idx", def = "{'homeId': 1, 'checkIn': 1, 'checkOut': 1}")
public class Booking {
    
    @Id
    private String id;
    
    @Field("userId")
    @Indexed
    private String userId;
    
    @Field("homeId")
    @Indexed
    private String homeId;
    
    @Field("checkIn")
    private LocalDate checkIn;
    
    @Field("checkOut")
    private LocalDate checkOut;
    
    @Field("guests")
    private Integer guests;
    
    @Field("totalPrice")
    private Double totalPrice;
    
    @Field("pricePerNight")
    private Double pricePerNight;
    
    @Field("numberOfNights")
    private Long numberOfNights;
    
    @Field("status")
    private BookingStatus status;
    
    @Field("guestName")
    private String guestName;
    
    @Field("guestEmail")
    private String guestEmail;
    
    @Field("guestPhone")
    private String guestPhone;
    
    @Field("specialRequests")
    private String specialRequests;
    
    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Booking() {
        this.status = BookingStatus.PENDING;
    }
    
    // Calculate number of nights
    public void calculateNumberOfNights() {
        if (checkIn != null && checkOut != null) {
            this.numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        }
    }
    
    // Calculate total price
    public void calculateTotalPrice() {
        if (pricePerNight != null && numberOfNights != null) {
            this.totalPrice = pricePerNight * numberOfNights;
        }
    }
    
    // Business logic: Check if booking is active
    public boolean isActive() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.PENDING;
    }
    
    // Business logic: Check if booking can be cancelled
    public boolean canBeCancelled() {
        if (status == BookingStatus.CANCELLED || status == BookingStatus.COMPLETED) {
            return false;
        }
        // Can't cancel if check-in is in the past
        return checkIn.isAfter(LocalDate.now());
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getHomeId() { return homeId; }
    public void setHomeId(String homeId) { this.homeId = homeId; }
    
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { 
        this.checkIn = checkIn;
        calculateNumberOfNights();
        calculateTotalPrice();
    }
    
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { 
        this.checkOut = checkOut;
        calculateNumberOfNights();
        calculateTotalPrice();
    }
    
    public Integer getGuests() { return guests; }
    public void setGuests(Integer guests) { this.guests = guests; }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    
    public Double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(Double pricePerNight) { 
        this.pricePerNight = pricePerNight;
        calculateTotalPrice();
    }
    
    public Long getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(Long numberOfNights) { this.numberOfNights = numberOfNights; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", homeId='" + homeId + '\'' +
                ", userId='" + userId + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", guests=" + guests +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                '}';
    }
}