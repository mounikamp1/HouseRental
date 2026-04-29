package com.airbnb.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class BookingRequestTest {

    private static final LocalDate CHECK_IN  = LocalDate.now().plusDays(2);
    private static final LocalDate CHECK_OUT = LocalDate.now().plusDays(6);

    @Test
    void constructor_validDates_createsRecord() {
        BookingRequest req = new BookingRequest(
                "user-1", "home-1", CHECK_IN, CHECK_OUT,
                2, 100.0, "Alice", "alice@example.com", null, null);

        assertThat(req.userId()).isEqualTo("user-1");
        assertThat(req.homeId()).isEqualTo("home-1");
        assertThat(req.checkIn()).isEqualTo(CHECK_IN);
        assertThat(req.checkOut()).isEqualTo(CHECK_OUT);
        assertThat(req.guests()).isEqualTo(2);
        assertThat(req.pricePerNight()).isEqualTo(100.0);
    }

    @Test
    void constructor_checkOutNotAfterCheckIn_throwsIllegalArgumentException() {
        LocalDate sameDate = CHECK_IN;

        assertThatThrownBy(() -> new BookingRequest(
                "user-1", "home-1", sameDate, sameDate,
                2, 100.0, "Alice", "alice@example.com", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-out date must be after check-in date");
    }

    @Test
    void constructor_checkOutBeforeCheckIn_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new BookingRequest(
                "user-1", "home-1", CHECK_OUT, CHECK_IN,
                2, 100.0, "Alice", "alice@example.com", null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_nullDates_doesNotThrow() {
        assertThatNoException().isThrownBy(() ->
            new BookingRequest("u", "h", null, null, 1, 50.0, "Bo", "bo@x.com", null, null)
        );
    }
}