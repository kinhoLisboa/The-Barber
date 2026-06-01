package com.theBarber.TheBarber_notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dead_letter_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeadLetterMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID eventId;

    private UUID appointmentId;

    private String clientName;
    private String clientPhone;
    private LocalDateTime appointmentTime;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String errorDescription;

    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDENTE,
        REPROCESSADO
    }
}
