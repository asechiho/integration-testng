package testng.listener;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

public class FeignDriver {

    private FeignDriver() {}

    public static <T> T getInstance(String baseUrl, Class<T> clazz) {
        return Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .target(clazz, baseUrl);
    }

}
