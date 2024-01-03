package lt.codeacademy.u8.tarpinisMeteo.meteo;

import java.util.List;

public class RootCityForecast {
    public Place place;
    public String forecastType;
    public String forecastCreationTimeUtc;
    public List<ForecastTimestamp> forecastTimestamps;

}
