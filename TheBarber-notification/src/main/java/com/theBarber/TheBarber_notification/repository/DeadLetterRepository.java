package com.theBarber.TheBarber_notification.repository;
import com.theBarber.TheBarber_notification.model.DeadLetterMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface DeadLetterRepository extends JpaRepository <DeadLetterMessage, UUID> {

    Optional<DeadLetterMessage> findById(UUID id);
    List<DeadLetterMessage> findByStatus(DeadLetterMessage.Status status);
}
