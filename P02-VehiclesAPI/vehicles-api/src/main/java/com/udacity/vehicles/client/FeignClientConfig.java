package com.udacity.vehicles.client;

import feign.optionals.OptionalDecoder;
import feign.jackson.JacksonDecoder;
import feign.codec.Decoder;
import feign.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Type;
import java.io.IOException;
import java.util.*;

final class DecoderWith404Support implements Decoder {

    private final Decoder delegate;
    private final int NOT_FOUND = 404;

    public DecoderWith404Support(Decoder delegate) {
        Objects.requireNonNull(delegate, "Decoder must not be null. ");
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        System.out.println("DecoderWith404Support " + response.status());
        if (response.status() == NOT_FOUND) {
            return null;
        }
        return delegate.decode(response, type);
    }

}

public class FeignClientConfig {

    @Bean
    public Decoder decoder() {
        return new DecoderWith404Support(new JacksonDecoder());
    }

}
