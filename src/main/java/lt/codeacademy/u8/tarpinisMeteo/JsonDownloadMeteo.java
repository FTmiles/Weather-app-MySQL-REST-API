package lt.codeacademy.u8.tarpinisMeteo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.codeacademy.u8.tarpinisMeteo.meteo.RootCityForecast;

import javax.swing.text.html.Option;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class JsonDownloadMeteo {


    public static Optional<RootCityForecast> getDataFromMeteo(String url) {
        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            System.out.println(responseJson);

            return parseJsonToRootCityForecast(responseJson);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public static Optional<RootCityForecast> parseJsonToRootCityForecast(String json) {
        ObjectMapper om = new ObjectMapper();


        try {

            RootCityForecast rootCityForecast = om.readValue(json, RootCityForecast.class);
            return Optional.of(rootCityForecast);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
