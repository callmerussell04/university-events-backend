package com.university.university_events.events.api;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.itextpdf.text.DocumentException;
import com.university.university_events.core.api.PageDto;
import com.university.university_events.core.api.PageDtoMapper;
import com.university.university_events.core.configuration.Constants;
import com.university.university_events.core.utils.Formatter;
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.service.EventService;
import com.university.university_events.events.stats.EventStatisticsDto;
import com.university.university_events.events.stats.PdfGenerator;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/event")
public class EventController {
    private final EventService eventService;
    private final ModelMapper modelMapper;
    BiMap<String, String> statusMap = HashBiMap.create();

    public EventController(EventService eventService, ModelMapper modelMapper) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        statusMap.put("PLANNED", "Запланировано");
        statusMap.put("ACTIVE", "В процессе");
        statusMap.put("COMPLETED", "Завершено");
        statusMap.put("CANCELED", "Отменено");
    }

    private EventDto toDto(EventEntity entity) {
        final EventDto dto = modelMapper.map(entity, EventDto.class);
        dto.setStatus(statusMap.get(dto.getStatus()));
        dto.setStartDateTime(Formatter.formatWithTime(entity.getStartDateTime()));
        dto.setEndDateTime(Formatter.formatWithTime(entity.getEndDateTime()));
        return dto;
    }

    private EventEntity toEntity(EventDto dto) throws ParseException {
        dto.setStatus(statusMap.inverse().get(dto.getStatus()));
        final EventEntity entity = modelMapper.map(dto, EventEntity.class);
        entity.setStartDateTime(Formatter.parseWithTime(dto.getStartDateTime()));
        entity.setEndDateTime(Formatter.parseWithTime(dto.getEndDateTime()));
        return entity;
    }
    
    @GetMapping
    public PageDto<EventDto> getAll (
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "locationId", required = false) Long locationId,
            @RequestParam(name = "startDate", required = false) String startDateString,
            @RequestParam(name = "endDate", required = false) String endDateString,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", defaultValue = "0") int page) throws ParseException {
        Date startDate = startDateString != null ? Formatter.parseWithTime(startDateString) : null;
        Date endDate = endDateString != null ? Formatter.parseWithTime(endDateString) : null;
        return PageDtoMapper.toDto(eventService.getAll(statusMap.inverse().get(status), locationId, startDate, endDate, name, page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
    }

    
    @GetMapping("/no-args")
    public List<EventDto> getAll() {
        return eventService.getAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public EventDto get(@PathVariable(name = "id") Long id) {
        return toDto(eventService.get(id));
    }

    @PostMapping
    public EventDto create(@RequestBody @Valid EventDto dto) throws ParseException {
        return toDto(eventService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public EventDto update(@PathVariable(name = "id") Long id, @RequestBody EventDto dto) throws ParseException {
        return toDto(eventService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public EventDto delete(@PathVariable(name = "id") Long id) {
        return toDto(eventService.delete(id));
    }

    @GetMapping("/statistics/export-pdf")
    public ResponseEntity<byte[]> exportEventStatisticsToPdf(
            @RequestParam(name = "startDate") String startDateString,
            @RequestParam(name = "endDate") String endDateString) throws ParseException, DocumentException {

        Date startDate = Formatter.parse(startDateString);
        Date endDate = Formatter.parse(endDateString);

        List<Object[]> rawStatistics = eventService.getEventStatistics(startDate, endDate);
        List<EventStatisticsDto> statistics = rawStatistics.stream().map(row -> {
            EventEntity event = (EventEntity) row[0];
            Long invitedCount = (Long) row[1]; // Получаем как Long
            Long attendedCount = (Long) row[2]; // Получаем как Long

            EventStatisticsDto dto = new EventStatisticsDto();
            dto.setId(event.getId());
            dto.setName(event.getName());
            dto.setStatus(statusMap.get(event.getStatus().name()));
            dto.setStartDateTime(Formatter.formatWithTime(event.getStartDateTime()));
            dto.setEndDateTime(Formatter.formatWithTime(event.getEndDateTime()));
            dto.setOrganizer(event.getOrganizer());
            dto.setLocationName(event.getLocation() != null ? event.getLocation().getName() : "N/A");
            dto.setInvitedCount(invitedCount != null ? invitedCount.intValue() : 0); // Преобразуем к int
            dto.setAttendedCount(attendedCount != null ? attendedCount.intValue() : 0); // Преобразуем к int
            dto.setNotAttendedCount(dto.getInvitedCount() - dto.getAttendedCount()); // Рассчитываем
            return dto;
        }).collect(Collectors.toList());

        byte[] pdfBytes = PdfGenerator.generateEventStatisticsPdf(statistics, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "event_statistics_" + startDateString + "_" + endDateString + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
