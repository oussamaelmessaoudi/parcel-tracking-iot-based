package com.tracksecure.trackingservice.mapper;

import com.tracksecure.trackingservice.dto.LocationRequestDTO;
import com.tracksecure.trackingservice.dto.LocationResponseDTO;
import com.tracksecure.trackingservice.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "timestamp", expression = "java(mapTimestamp(dto.getTimestamp()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "additionalData", ignore = true)
    Location toEntity(LocationRequestDTO dto);

    @Mapping(target = "sessionId", expression = "java(getSessionId(entity))")
    LocationResponseDTO toDto(Location entity);

    default Instant mapTimestamp(Long timestamp) {
        return timestamp != null ? Instant.ofEpochMilli(timestamp) : Instant.now();
    }

    default String getSessionId(Location entity) {
        return entity.getSession() != null ? entity.getSession().getSessionId() : null;
    }
}
