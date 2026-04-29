package com.airbnb.booking.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class BookingTest {

    @Test
    void calculateNumberOfNights_validDates_computesCorrectly() {
        Booking booking = new Booking();
        booking.setCheckIn(LocalDate.of(2027, 6, 1));
        booking.setCheckOut(LocalDate.of(2027, 6, 6));
        assertThat(booking.getNumberOfNights()).isEqualTo(5L);
    }

    @Test
    void calculateNumberOfNights_singleNight_returnsOne() {
        Booking booking = new Booking();
        booking.setCheckIn(LocalDate.of(2027, 6, 1));
        booking.setCheckOut(LocalDate.of(2027, 6, 2));
        assertThat(booking.getNumberOfNights()).isEqualTo(1L);
    }

    @Test
    void calculateNumberOfNights_nullDates_doesNotThrow() {
        Booking booking = new Booking();
        booking.calculateNumberOfNights();
        assertThat(booking.getNumberOfNights()).isNull();
    }

    @Test
    void calculateTotalPrice_validInputs_computesCorrectly() {
        Booking booking = new Booking();
        booking.setCheckIn(LocalDate.of(2027, 6, 1));
        booking.setCheckOut(LocalDate.of(2027, 6, 6));
        booking.setPricePerNight(100.0);
        assertThat(booking.getTotalPrice()).isEqualTo(500.0);
    }

    @Test
    void calculateTotalPrice_nullPricePerNight_doesNotThrow() {
        Booking booking = new Booking();
        booking.setCheckIn(LocalDate.of(2027, 6, 1));
        booking.setCheckOut(LocalDate.of(2027, 6, 3));
        assertThat(booking.getTotalPrice()).isNull();
    }

    @Test
    void isActive_pendingStatus_returnsTrue() {
        Booking booking = new Booking();
        assertThat(booking.isActive()).isTrue();
    }

    @Test
    void isActive_confirmedStatus_returnsTrue() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMED);
        assertThat(booking.isActive()).isTrue();
    }

    @Test
    void isActive_cancelledStatus_returnsFalse() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELLED);
        assertThat(booking.isActive()).isFalse();
    }

    @Test
    void isActive_completedStatus_returnsFalse() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.COMPLETED);
        assertThat(booking.isActive()).isFalse();
    }

    @Test
    void canBeCancelled_pendingWithFutureCheckIn_returnsTrue() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);
        booking.setCheckIn(LocalDate.now().plusDays(3));
        assertThat(booking.canBeCancelled()).isTrue();
    }

    @Test
    void canBeCancelled_confirmedWithFutureCheckIn_returnsTrue() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCheckIn(LocalDate.now().plusDays(1));
        assertThat(booking.canBeCancelled()).isTrue();
    }

    @Test
    void canBeCancelled_alreadyCancelled_returnsFalse() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCheckIn(LocalDate.now().plusDays(5));
        assertThat(booking.canBeCancelled()).isFalse();
    }

    @Test
    void canBeCancelled_completedStatus_returnsFalse() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCheckIn(LocalDate.now().plusDays(5));
        assertThat(booking.canBeCancelled()).isFalse();
    }

    @Test
    void canBeCancelled_checkInInPast_returnsFalse() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCheckIn(LocalDate.now().minusDays(1));
        assertThat(booking.canBeCancelled()).isFalse();
    }

    @Test
    void equals_sameId_returnsTrue() {
        Booking b1 = new Booking(); b1.setId("id-1");
        Booking b2 = new Booking(); b2.setId("id-1");
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        Booking b1 = new Booking(); b1.setId("id-1");
        Booking b2 = new Booking(); b2.setId("id-2");
        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void toString_containsEssentialFields() {
        Booking booking = new Booking();
        booking.setId("id-1");
        booking.setHomeId("home-1");
        booking.setUserId("user-1");
        assertThat(booking.toString()).contains("id-1", "home-1", "user-1");
    }
}