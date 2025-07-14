package com.example.graphqlserver.config;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.graphqlserver.repository.UserPrincipalRepository;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

@Configuration
public class GraphQLConfig {

    @Autowired
    UserPrincipalRepository userRepository;

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(dateTimeScalar());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("DateTime scalar that represents time data as an ISO-8601 encoded UTC date string")
                .coercing(new Coercing<OffsetDateTime, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof OffsetDateTime offsetDateTime) {
                            return offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        }
                        throw new CoercingSerializeException(
                                "Expected type OffsetDateTime but was " + dataFetcherResult.getClass().getName());
                    }

                    @Override
                    public OffsetDateTime parseValue(Object input) {
                        try {
                            if (input instanceof String string) {
                                return OffsetDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            }
                            throw new CoercingParseValueException(
                                    "Expected type String but was " + input.getClass().getName());
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid ISO-8601 date time format: " + input);
                        }
                    }

                    @Override
                    public OffsetDateTime parseLiteral(Object input) {
                        if (input instanceof StringValue stringValue) {
                            try {
                                return OffsetDateTime.parse(stringValue.getValue(),
                                        DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Invalid ISO-8601 date time format: " + input);
                            }
                        }
                        throw new CoercingParseLiteralException(
                                "Expected AST type StringValue but was " + input.getClass().getName());
                    }
                })
                .build();
    }
}
