package lt.codeacademy.u8.tarpinisMeteo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.codeacademy.u8.tarpinisMeteo.meteo.forecast.RootCityForecast;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class JsonDownloadMeteo {


    public static String getDataFromMeteo(String url) {
        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            System.out.println(responseJson);

            return responseJson;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public static <T> Optional<T>  parseJsonToRootCityForecast(String json, Class<T> valueType) {
        ObjectMapper om = new ObjectMapper();
        System.out.println("NON STOP POP");
        System.out.println(valueType);
        try {
            T result = om.readValue(json, valueType);
            return Optional.of(result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

//    public static <T> Optional<T> parseJsonToRootCityForecast(String json, Class<T> valueType) {
//        ObjectMapper om = new ObjectMapper();
//        System.out.println("NON STOP POP");
//        System.out.println(valueType);
//        try {
//            T result = om.readValue(json, new TypeReference<T>() {});
//            return Optional.of(result);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return Optional.empty();
//    }


}
