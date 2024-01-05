package lt.codeacademy.u8.tarpinisMeteo;

import lt.codeacademy.u8.tarpinisMeteo.meteo.forecast.RootCityForecast;
import lt.codeacademy.u8.tarpinisMeteo.meteo.observ.Observation;
import lt.codeacademy.u8.tarpinisMeteo.meteo.observ.RootCityObserv;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BusinessLogic {
    Ui ui;
    List<WeatherFiltered> forecastFltrArr = new ArrayList<>();
    List<WeatherFiltered> weatherNowByCityArr = new ArrayList<>();
    List<String[]> cities = new ArrayList<>();
    List<String> cityNames = new ArrayList<>();

    final static DateTimeFormatter dateFormatMeteo = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    final static List<String> allStations = List.of("birzu-ams", "dotnuvos-ams", "duksto-ams", "kauno-ams", "klaipedos-ams", "kybartu-ams", "laukuvos-ams", "lazdiju-ams", "nidos-ams", "panevezio-ams", "raseiniu-ams", "siauliu-ams", "silutes-ams", "telsiu-ams", "ukmerges-ams", "utenos-ams", "varenos-ams", "vilniaus-ams");
    final static List<String> allCities = List.of("birzai", "dotnuva", "dukstas", "kaunas", "klaipeda", "kybartai", "laukuva", "lazdijai", "neringa-nida", "panevezys", "raseiniai", "siauliai", "silute", "telsiai", "ukmerge", "utena", "varena", "vilnius");


    String dbUrl =  "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11673584";
    String dbUser = "sql11673584";
    String dbPass = "4IfiVURzW3";
    MySQL mySql;

    String forecastUrlPattern = "https://api.meteo.lt/v1/places/%s/forecasts/long-term";
    String observUrlPattern = "https://api.meteo.lt/v1/stations/%s/observations/latest";

    public BusinessLogic(Ui ui){
        this.ui = ui;

        this.mySql = new MySQL(dbUrl, dbUser, dbPass, ui);

    }

    public void start(){
        //load from DB
        forecastFltrArr = mySql.readDbWeather("weatherForecast");
        weatherNowByCityArr = mySql.readDbWeather("weatherNow");
        cityNames = mySql.readDbCities();
        //-----


    Boolean running = true;
        while(running){
            ui.infoOut(".......................................\n" +
                    "Weather forecast for cities: " + String.join(", ", cityNames.stream().map(Utils::firstLetterCaps).toList()));
            ui.printMenu();
            String menuSelection = ui.getUserMenuInput();
            running =  handleMenuSelection(menuSelection);


        }
        ui.sc.close();
        System.out.println("The app is closing\nHave a good day!");
    }






    public Boolean handleMenuSelection(String menuSelection){
        if (menuSelection.equalsIgnoreCase("q")) return false;

        switch(menuSelection){
            case "1" -> weatherNow();
            case "2" -> weatherForecast();
            case "3" -> changeCities();
            case "4" -> syncWithMeteo();
        }

        return true;
    }

    private void changeCities() {

        ui.infoOut("Available cities: " + String.join(", ", allCities));
        ui.infoOut("Currently selected cities: " + String.join(", ", cityNames.stream().map(Utils::firstLetterCaps).toList()));

        ui.infoOut("1. Add new city\n2. Remove currently city");
        String menuSelection = ui.getUserMenuInput();

        switch (menuSelection) {
            case "1" -> addCity();
            case "2" -> removeCity();
        }

//        mySql.writeDbCities(cityNames);
        mySql.wipeWriteDbCities(cityNames);
    }

    public void addCity(){
        String input = ui.printScan("Enter one or more cities separated by commas\nAdd cities: ");
        String inputNoSpace = input.replaceAll("\\s", "");
        String[] cities = inputNoSpace.split(",");

        for (String city : cities) {
            if (BusinessLogic.allCities.contains(city.toLowerCase()))
                cityNames.add(city.toLowerCase());
            else ui.infoOut(city + " - not added, not available");
        }

        mySql.wipeWriteDbCities(cityNames);
    }

    public void removeCity(){
        String input = ui.printScan("Enter one or more cities separated by commas\nDel cities: ");
        String inputNoSpace = input.replaceAll("\\s", "");
        String[] cities = inputNoSpace.split(",");

        for (String city : cities) {
            if (!cityNames.remove(city.toLowerCase()))
                ui.infoOut(city + " - not removed, was never selected");
        }
        mySql.wipeWriteDbCities(cityNames);
    }



    private void weatherNow() {
        Map<String, List<WeatherFiltered>> grouped = weatherNowByCityArr.stream().collect(Collectors.groupingBy(x->x.stationCode));
        grouped.forEach((key,val)-> {
            String city = Utils.cityStationToFrom(key);
            ui.infoOut(Utils.firstLetterCaps(city) + " - weather NOW " + "-".repeat(20) +
                    "last measured at " + val.get(0).dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));

            for (WeatherFiltered x : val) {
                ui.infoOut(x.toString());
            }
        });
    }

    private void weatherForecast() {
        Map<String, List<WeatherFiltered>> grouped = forecastFltrArr.stream().collect(Collectors.groupingBy(x->x.stationCode));
        grouped.forEach((key,val)-> {
            ui.infoOut(Utils.firstLetterCaps(key) + "-".repeat(20));

            for (WeatherFiltered x : val) {
                ui.infoOut(x.toStringForecast());
            }
        });
    }

    public void syncWithMeteo(){
        weatherNowByCityArr.clear();
        forecastFltrArr.clear();
        //forecast
        for (String city : cityNames) {
            Optional<RootCityForecast> opt1 = getApiData(forecastUrlPattern, city,  RootCityForecast.class );
            filterSaveForecasts(opt1);

        //now
            Optional<RootCityObserv> opt2 = getApiData(observUrlPattern, Utils.cityStationToFrom(city), RootCityObserv.class );
            filterSaveObserv(opt2);
        }

        mySql.writeDbWeather(forecastFltrArr, "weatherForecast");
        mySql.writeDbWeather(weatherNowByCityArr, "weatherNow");

    }



    public <T> Optional<T> getApiData(String urlPattern, String city, Class<T> valueType){
        String url = String.format(urlPattern, city);
        String json = JsonDownloadMeteo.getDataFromMeteo(url);
        return JsonDownloadMeteo.parseJsonToRootCityForecast(json, valueType);
    }

    public void filterSaveObserv(Optional<RootCityObserv> opt){
        if (opt.isEmpty()) return;
        RootCityObserv city = opt.get();

        String code  = city.station.code;
        Observation last = city.observations.getLast();
        LocalDateTime dt = LocalDateTime.parse(last.observationTimeUtc,dateFormatMeteo);
        weatherNowByCityArr.add(new WeatherFiltered(code, last.airTemperature, last.feelsLikeTemperature, last.conditionCode, last.relativeHumidity, last.windSpeed, last.windDirection, dt));

}


    public void filterSaveForecasts(Optional<RootCityForecast> opt){
        if (opt.isEmpty()) return;
        RootCityForecast city = opt.get();

        LocalDateTime dtNow = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        String code = city.place.code;

        city.forecastTimestamps.stream()
                .filter(rec->{
            LocalDateTime dt = LocalDateTime.parse(rec.forecastTimeUtc, dateFormatMeteo);
            return dt.isAfter(dtNow) && dt.isBefore(dtNow.plusHours(6)) ||
                    dt.isEqual(dtNow.plusDays(1).withHour(12)) ||
                    dt.isEqual(dtNow.plusDays(2).withHour(12)) ||
                    dt.isEqual(dtNow.plusDays(3).withHour(12)) ||
                    dt.isEqual(dtNow.plusDays(4).withHour(12));
        })
                .forEach(rec->{
                    LocalDateTime dt = LocalDateTime.parse(rec.forecastTimeUtc,dateFormatMeteo);
                    forecastFltrArr.add(new WeatherFiltered(code, rec.airTemperature, rec.feelsLikeTemperature, rec.conditionCode, rec.relativeHumidity, rec.windSpeed, rec.windDirection, dt));
                });
    }



}
