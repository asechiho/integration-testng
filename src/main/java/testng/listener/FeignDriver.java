package testng.listener;

import feign.Feign;
import feign.gson.GsonDecoder;

public class FeignDriver {

    private FeignDriver() {}

    public static <T> T getInstance(String baseUrl, Class<T> clazz) {
        return Feign.builder()
                .decoder(new GsonDecoder())
                .target(clazz, baseUrl);
    }

}
