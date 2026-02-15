package com.cinect.scheduler;

import com.cinect.entity.Hold;
import com.cinect.entity.enums.HoldStatus;
import com.cinect.repository.HoldRepository;
import com.cinect.repository.HoldSeatRepository;
import com.cinect.websocket.SeatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class HoldExpirationScheduler {

    private final HoldRepository holdRepository;
    private final HoldSeatRepository holdSeatRepository;
    private final SeatWebSocketHandler seatWebSocketHandler;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void expireOldHolds() {
        var now = Instant.now();
        var toExpire = holdRepository.findExpiredActive(now);
        for (var hold : toExpire) {
            var seatIds = hold.getSeatIds();
            if (seatIds != null && !seatIds.isEmpty()) {
                seatWebSocketHandler.broadcastHoldExpired(hold.getShowtime().getId(), seatIds);
            }
            holdSeatRepository.deleteByHoldId(hold.getId());
        }
        holdRepository.expireOldHolds(now);
    }
}
