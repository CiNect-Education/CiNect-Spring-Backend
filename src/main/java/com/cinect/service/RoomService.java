package com.cinect.service;

import com.cinect.dto.request.BulkUpdateSeatsRequest;
import com.cinect.dto.request.CreateRoomRequest;
import com.cinect.dto.request.ImportSeatsRequest;
import com.cinect.dto.request.UpdateRoomRequest;
import com.cinect.dto.response.RoomResponse;
import com.cinect.dto.response.SeatResponse;
import com.cinect.entity.Cinema;
import com.cinect.entity.Room;
import com.cinect.entity.Seat;
import com.cinect.entity.enums.SeatStatus;
import com.cinect.entity.enums.SeatType;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.CinemaRepository;
import com.cinect.repository.RoomRepository;
import com.cinect.repository.SeatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final CinemaRepository cinemaRepository;

    public List<RoomResponse> findAllRooms() {
        var rooms = roomRepository.findAll();
        return rooms.stream().map(r -> toResponseWithSeats(r.getId())).collect(Collectors.toList());
    }

    public List<RoomResponse> findByCinema(UUID cinemaId) {
        var rooms = roomRepository.findByCinemaIdAndIsActiveTrue(cinemaId);
        return rooms.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RoomResponse findById(UUID roomId) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return toResponseWithSeats(room.getId());
    }

    @Transactional
    public RoomResponse update(UUID roomId, UpdateRoomRequest req) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (req.getName() != null) room.setName(req.getName());
        if (req.getFormat() != null) room.setFormat(req.getFormat());
        if (req.getRows() != null) room.setRows(req.getRows());
        if (req.getColumns() != null) room.setColumns(req.getColumns());
        if (req.getIsActive() != null) room.setIsActive(req.getIsActive());
        room = roomRepository.save(room);
        return toResponseWithSeats(room.getId());
    }

    @Transactional
    public void delete(UUID roomId) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        room.setIsActive(false);
        roomRepository.save(room);
    }

    public List<SeatResponse> getSeats(UUID roomId) {
        roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return seatRepository.findByRoomId(roomId).stream().map(this::toSeatResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<SeatResponse> bulkUpdateSeats(UUID roomId, BulkUpdateSeatsRequest req) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (req.getSeats() == null || req.getSeats().isEmpty()) return getSeats(roomId);
        for (var item : req.getSeats()) {
            if (item.getId() == null) continue;
            var seat = seatRepository.findById(item.getId()).orElse(null);
            if (seat == null || !seat.getRoom().getId().equals(roomId)) continue;
            if (item.getRowLabel() != null) seat.setRowLabel(item.getRowLabel());
            if (item.getNumber() != null) seat.setNumber(item.getNumber());
            if (item.getType() != null) seat.setType(item.getType());
            if (item.getStatus() != null) seat.setStatus(item.getStatus());
            if (item.getIsAisle() != null) seat.setIsAisle(item.getIsAisle());
            if (item.getPrice() != null) seat.setPrice(item.getPrice());
            seatRepository.save(seat);
        }
        return getSeats(roomId);
    }

    @Transactional
    public List<SeatResponse> importSeats(UUID roomId, ImportSeatsRequest req) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        seatRepository.deleteAll(seatRepository.findByRoomId(roomId));
        if (req.getSeats() == null || req.getSeats().isEmpty()) {
            room.setTotalSeats(0);
            roomRepository.save(room);
            return List.of();
        }
        List<Seat> seats = new ArrayList<>();
        for (var item : req.getSeats()) {
            String row = item.getRow() != null ? item.getRow() : "A";
            int num = item.getNumber() != null ? item.getNumber() : 1;
            SeatType type = SeatType.STANDARD;
            if (item.getType() != null) {
                try { type = SeatType.valueOf(item.getType()); } catch (Exception ignored) { }
            }
            boolean isAisle = Boolean.TRUE.equals(item.getIsAisle());
            seats.add(Seat.builder()
                    .room(room)
                    .rowLabel(row)
                    .number(num)
                    .type(type)
                    .status(SeatStatus.AVAILABLE)
                    .isAisle(isAisle)
                    .build());
        }
        seatRepository.saveAll(seats);
        room.setTotalSeats(seats.size());
        roomRepository.save(room);
        return getSeats(roomId);
    }

    private RoomResponse toResponseWithSeats(UUID roomId) {
        var room = roomRepository.findById(roomId).orElseThrow();
        var seats = seatRepository.findByRoomId(roomId).stream().map(this::toSeatResponse).collect(Collectors.toList());
        return RoomResponse.builder()
                .id(room.getId())
                .cinemaId(room.getCinema().getId())
                .name(room.getName())
                .format(room.getFormat())
                .totalSeats(room.getTotalSeats())
                .rows(room.getRows())
                .columns(room.getColumns())
                .isActive(room.getIsActive())
                .seats(seats)
                .createdAt(room.getCreatedAt())
                .build();
    }

    @Transactional
    public RoomResponse create(UUID cinemaId, CreateRoomRequest req) {
        var cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        var room = Room.builder()
                .cinema(cinema)
                .name(req.getName())
                .format(req.getFormat())
                .rows(req.getRows())
                .columns(req.getColumns())
                .totalSeats(req.getRows() * req.getColumns())
                .isActive(true)
                .build();
        room = roomRepository.save(room);

        List<Seat> seats = new ArrayList<>();
        if (req.getSeats() != null && req.getSeats().isArray()) {
            int num = 0;
            for (JsonNode node : req.getSeats()) {
                String rowLabel = node.has("row") ? node.get("row").asText() : String.valueOf((char) ('A' + num / req.getColumns()));
                int number = node.has("number") ? node.get("number").asInt() : (num % req.getColumns()) + 1;
                String typeStr = node.has("type") ? node.get("type").asText() : "STANDARD";
                SeatType type;
                try {
                    type = SeatType.valueOf(typeStr);
                } catch (Exception e) {
                    type = SeatType.STANDARD;
                }
                boolean isAisle = node.has("isAisle") && node.get("isAisle").asBoolean();
                var seat = Seat.builder()
                        .room(room)
                        .rowLabel(rowLabel)
                        .number(number)
                        .type(type)
                        .status(SeatStatus.AVAILABLE)
                        .isAisle(isAisle)
                        .build();
                seats.add(seat);
                num++;
            }
        } else {
            for (int r = 0; r < req.getRows(); r++) {
                String rowLabel = String.valueOf((char) ('A' + r));
                for (int c = 0; c < req.getColumns(); c++) {
                    var seat = Seat.builder()
                            .room(room)
                            .rowLabel(rowLabel)
                            .number(c + 1)
                            .type(SeatType.STANDARD)
                            .status(SeatStatus.AVAILABLE)
                            .isAisle(false)
                            .build();
                    seats.add(seat);
                }
            }
        }
        seatRepository.saveAll(seats);
        room.setTotalSeats(seats.size());
        room = roomRepository.save(room);
        return toResponse(room);
    }

    private RoomResponse toResponse(Room r) {
        var seats = r.getSeats() != null
                ? r.getSeats().stream().map(this::toSeatResponse).collect(Collectors.toList())
                : List.<SeatResponse>of();
        return RoomResponse.builder()
                .id(r.getId())
                .cinemaId(r.getCinema().getId())
                .name(r.getName())
                .format(r.getFormat())
                .totalSeats(r.getTotalSeats())
                .rows(r.getRows())
                .columns(r.getColumns())
                .isActive(r.getIsActive())
                .seats(seats)
                .createdAt(r.getCreatedAt())
                .build();
    }

    private SeatResponse toSeatResponse(Seat s) {
        return SeatResponse.builder()
                .id(s.getId())
                .roomId(s.getRoom().getId())
                .rowLabel(s.getRowLabel())
                .number(s.getNumber())
                .type(s.getType())
                .status(s.getStatus())
                .isAisle(s.getIsAisle())
                .price(s.getPrice())
                .build();
    }
}
