package com.university.university_events.core.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MapperConfiguration {
    @Bean
    ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}
