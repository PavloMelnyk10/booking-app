package mate.academy.bookingapp.exception;

public class AccommodationFullyBookedException extends RuntimeException {
    public AccommodationFullyBookedException(String message) {
        super(message);
    }
}
