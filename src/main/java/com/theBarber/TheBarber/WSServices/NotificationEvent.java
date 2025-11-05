package com.theBarber.TheBarber.WSServices;
import java.time.LocalDateTime;

public record NotificationEvent(
        String clientName,
        String clientPhone,
        LocalDateTime appointmentTime,
        NotificationType type,
        String message

) {
    public enum NotificationType {
        CREATED,
        CONFIRMED
    }
}
