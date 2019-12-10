package testng.listener;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import testng.listener.config.IntegrationConfig;

public class FeignDriver {

    private FeignDriver() {}

    public static <T> T getInstance(String baseUrl, Class<T> clazz) {
        return Feign.builder()
                .requestInterceptor(new BasicAuthRequestInterceptor(IntegrationConfig.getInstance().getTestTrackingSystemLogin(),
                        IntegrationConfig.getInstance().getTestTrackingSystemPassword()))
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .target(clazz, baseUrl);
    }

}
