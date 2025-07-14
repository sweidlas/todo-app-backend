package com.example.graphqlserver.config;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new DateToOffsetDateTimeConverter(),
                new OffsetDateTimeToDateConverter()));
    }

    @ReadingConverter
    public class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {

        @Override
        public OffsetDateTime convert(Date source) {
            return source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC);
        }
    }

    @WritingConverter
    public class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {

        @Override
        public Date convert(OffsetDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }
}
