package com.airbnb.booking.dto;

import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class BookingResponseTest {

    @Test
    void fromEntity_mapsAllFieldsCorrectly() {
        Booking booking = new Booking();
        booking.setId("id-1");
        booking.setUserId("user-1");
        booking.setHomeId("home-1");
        LocalDate checkIn  = LocalDate.of(2027, 7, 1);
        LocalDate checkOut = LocalDate.of(2027, 7, 5);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuests(3);
        booking.setPricePerNight(200.0);
        booking.setGuestName("Bob");
        booking.setGuestEmail("bob@example.com");
        booking.setGuestPhone("555-9999");
        booking.setSpecialRequests("Late check-in");
        booking.setStatus(BookingStatus.CONFIRMED);

        BookingResponse response = BookingResponse.fromEntity(booking);

        assertThat(response.id()).isEqualTo("id-1");
        assertThat(response.userId()).isEqualTo("user-1");
        assertThat(response.homeId()).isEqualTo("home-1");
        assertThat(response.checkIn()).isEqualTo(checkIn);
        assertThat(response.checkOut()).isEqualTo(checkOut);
        assertThat(response.guests()).isEqualTo(3);
        assertThat(response.pricePerNight()).isEqualTo(200.0);
        assertThat(response.totalPrice()).isEqualTo(800.0);
        assertThat(response.numberOfNights()).isEqualTo(4L);
        assertThat(response.guestName()).isEqualTo("Bob");
        assertThat(response.guestEmail()).isEqualTo("bob@example.com");
        assertThat(response.guestPhone()).isEqualTo("555-9999");
        assertThat(response.specialRequests()).isEqualTo("Late check-in");
        assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED);
    }
}