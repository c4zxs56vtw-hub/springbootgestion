package com.stockpilot.common.idempotency.service;

import com.stockpilot.common.exception.IdempotencyKeyReusedException;
import com.stockpilot.common.idempotency.model.IdempotencyRecord;
import com.stockpilot.common.idempotency.repository.IdempotencyRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;

    public IdempotencyService(
            IdempotencyRepository idempotencyRepository
    ) {
        this.idempotencyRepository = idempotencyRepository;
    }

    public Long findExisting(
            UUID key,
            String operation,
            String requestHash
    ) {

        return idempotencyRepository
                .findByIdempotencyKey(key)
                .map(record -> {

                    if (!record.getOperation().equals(operation)
                            || !record.getRequestHash().equals(requestHash)) {

                        throw new IdempotencyKeyReusedException(
                                "Cette Idempotency-Key a déjà été utilisée pour une autre requête"
                        );
                    }

                    return record.getMovementId();
                })
                .orElse(null);
    }

    public void remember(
            UUID key,
            String operation,
            String requestHash,
            Long movementId
    ) {

        IdempotencyRecord record =
                new IdempotencyRecord(
                        null,
                        key,
                        operation,
                        requestHash,
                        movementId,
                        Instant.now()
                );

        idempotencyRepository.save(record);
    }

    public String hash(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] result =
                    digest.digest(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(result);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "Impossible de calculer le hash",
                    exception
            );
        }
    }
}