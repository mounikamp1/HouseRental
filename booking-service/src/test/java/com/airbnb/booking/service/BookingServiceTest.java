package com.airbnb.booking.service;

import com.airbnb.booking.dto.BookingRequest;
import com.airbnb.booking.exception.BookingConflictException;
import com.airbnb.booking.exception.BookingNotFoundException;
import com.airbnb.booking.exception.InvalidBookingException;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingStatus;
import com.airbnb.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private LocalDate checkIn;
    private LocalDate checkOut;

    @BeforeEach
    void setUp() {
        checkIn  = LocalDate.now().plusDays(2);
        checkOut = LocalDate.now().plusDays(7);
    }

    @Test
    void createBooking_happyPath_returnsSavedBooking() {
        BookingRequest request = buildRequest(checkIn, checkOut);
        given(bookingRepository.findConflictingBookings(any(), any(), any()))
                .willReturn(Collections.emptyList());
        Booking savedBooking = buildBooking("id-1", request);
        given(bookingRepository.save(any(Booking.class))).willReturn(savedBooking);

        Booking result = bookingService.createBooking(request);

        assertThat(result.getId()).isEqualTo("id-1");
        assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
        then(bookingRepository).should().save(any(Booking.class));
    }

    @Test
    void createBooking_conflictExists_throwsBookingConflictException() {
        BookingRequest request = buildRequest(checkIn, checkOut);
        given(bookingRepository.findConflictingBookings(any(), any(), any()))
                .willReturn(List.of(new Booking()));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void createBooking_checkInInPast_throwsInvalidBookingException() {
        LocalDate pastDate   = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(3);
        BookingRequest request = buildRequest(pastDate, futureDate);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(InvalidBookingException.class)
                .hasMessageContaining("past");
    }

    @Test
    void createBooking_durationExceeds365Nights_throwsInvalidBookingException() {
        LocalDate longCheckOut = checkIn.plusDays(400);
        BookingRequest request = buildRequest(checkIn, longCheckOut);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(InvalidBookingException.class)
                .hasMessageContaining("365");
    }

    @Test
    void checkAvailability_noConflicts_returnsTrue() {
        given(bookingRepository.findConflictingBookings(any(), any(), any()))
                .willReturn(Collections.emptyList());

        boolean available = bookingService.checkAvailability("home-1", checkIn, checkOut);

        assertThat(available).isTrue();
    }

    @Test
    void checkAvailability_conflictsExist_returnsFalse() {
        given(bookingRepository.findConflictingBookings(any(), any(), any()))
                .willReturn(List.of(new Booking()));

        boolean available = bookingService.checkAvailability("home-1", checkIn, checkOut);

        assertThat(available).isFalse();
    }

    @Test
    void getBookingById_found_returnsBooking() {
        Booking booking = new Booking();
        booking.setId("id-42");
        given(bookingRepository.findById("id-42")).willReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById("id-42");

        assertThat(result.getId()).isEqualTo("id-42");
    }

    @Test
    void getBookingById_notFound_throwsBookingNotFoundException() {
        given(bookingRepository.findById("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById("missing"))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void getBookingsByUserId_returnsListFromRepository() {
        given(bookingRepository.findByUserId("user-1")).willReturn(List.of(new Booking()));

        List<Booking> result = bookingService.getBookingsByUserId("user-1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllBookings_returnsAllFromRepository() {
        given(bookingRepository.findAll()).willReturn(List.of(new Booking(), new Booking()));

        List<Booking> result = bookingService.getAllBookings();

        assertThat(result).hasSize(2);
    }

    @Test
    void updateBookingStatus_updatesAndReturns() {
        Booking existing = new Booking();
        existing.setId("id-1");
        existing.setStatus(BookingStatus.PENDING);
        given(bookingRepository.findById("id-1")).willReturn(Optional.of(existing));
        given(bookingRepository.save(existing)).willReturn(existing);

        Booking result = bookingService.updateBookingStatus("id-1", BookingStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void confirmBooking_setsStatusConfirmed() {
        Booking existing = new Booking();
        existing.setId("id-1");
        existing.setStatus(BookingStatus.PENDING);
        given(bookingRepository.findById("id-1")).willReturn(Optional.of(existing));
        given(bookingRepository.save(existing)).willReturn(existing);

        Booking result = bookingService.confirmBooking("id-1");

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void cancelBooking_validPendingBooking_setsStatusCancelled() {
        Booking existing = new Booking();
        existing.setId("id-1");
        existing.setStatus(BookingStatus.PENDING);
        existing.setCheckIn(checkIn);
        given(bookingRepository.findById("id-1")).willReturn(Optional.of(existing));
        given(bookingRepository.save(existing)).willReturn(existing);

        Booking result = bookingService.cancelBooking("id-1");

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_alreadyCancelled_throwsInvalidBookingException() {
        Booking existing = new Booking();
        existing.setId("id-1");
        existing.setStatus(BookingStatus.CANCELLED);
        existing.setCheckIn(checkIn);
        given(bookingRepository.findById("id-1")).willReturn(Optional.of(existing));

        assertThatThrownBy(() -> bookingService.cancelBooking("id-1"))
                .isInstanceOf(InvalidBookingException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    void cancelBooking_checkInInPast_throwsInvalidBookingException() {
        Booking existing = new Booking();
        existing.setId("id-1");
        existing.setStatus(BookingStatus.CONFIRMED);
        existing.setCheckIn(LocalDate.now().minusDays(1));
        given(bookingRepository.findById("id-1")).willReturn(Optional.of(existing));

        assertThatThrownBy(() -> bookingService.cancelBooking("id-1"))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void deleteBooking_exists_callsDeleteById() {
        given(bookingRepository.existsById("id-1")).willReturn(true);

        bookingService.deleteBooking("id-1");

        then(bookingRepository).should().deleteById("id-1");
    }

    @Test
    void deleteBooking_notFound_throwsBookingNotFoundException() {
        given(bookingRepository.existsById("missing")).willReturn(false);

        assertThatThrownBy(() -> bookingService.deleteBooking("missing"))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void getTodayCheckIns_delegatesToRepository() {
        given(bookingRepository.findCheckingInToday(any())).willReturn(List.of(new Booking()));

        List<Booking> result = bookingService.getTodayCheckIns();

        assertThat(result).hasSize(1);
    }

    @Test
    void getTodayCheckOuts_delegatesToRepository() {
        given(bookingRepository.findCheckingOutToday(any())).willReturn(List.of(new Booking()));

        List<Booking> result = bookingService.getTodayCheckOuts();

        assertThat(result).hasSize(1);
    }

    private static BookingRequest buildRequest(LocalDate checkIn, LocalDate checkOut) {
        return new BookingRequest(
                "user-1", "home-1", checkIn, checkOut,
                2, 150.0, "John Doe", "john@example.com",
                "555-1234", "No special requests");
    }

    private static Booking buildBooking(String id, BookingRequest request) {
        Booking b = new Booking();
        b.setId(id);
        b.setUserId(request.userId());
        b.setHomeId(request.homeId());
        b.setCheckIn(request.checkIn());
        b.setCheckOut(request.checkOut());
        b.setGuests(request.guests());
        b.setPricePerNight(request.pricePerNight());
        b.setGuestName(request.guestName());
        b.setGuestEmail(request.guestEmail());
        b.setGuestPhone(request.guestPhone());
        b.setSpecialRequests(request.specialRequests());
        b.setStatus(BookingStatus.PENDING);
        b.calculateNumberOfNights();
        b.calculateTotalPrice();
        return b;
    }
}