package mate.academy.bookingapp.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final RestTemplate restTemplate;
    @Value("${TELEGRAM_BOT_TOKEN}")
    private String botToken;
    @Value("${TELEGRAM_CHAT_ID}")
    private String chatId;
    @Value("${TELEGRAM_TOPIC_BOOKINGS}")
    private int bookingsThreadId;
    @Value("${TELEGRAM_TOPIC_PAYMENTS}")
    private int paymentsThreadId;
    @Value("${TELEGRAM_TOPIC_ACCOMMODATIONS}")
    private int accommodationsThreadId;

    @Override
    public void sendMessage(String message, int threadId) {
        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&message_thread_id=%d&text=%s",
                botToken, chatId, threadId, message
        );
        restTemplate.getForObject(url, String.class);
    }

    public void sendBookingMessage(String message) {
        sendMessage(message, bookingsThreadId);
    }

    public void sendPaymentMessage(String message) {
        sendMessage(message, paymentsThreadId);
    }

    public void sendAccommodationMessage(String message) {
        sendMessage(message, accommodationsThreadId);
    }
}
