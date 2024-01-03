package lt.codeacademy.u8.tarpinisMeteo;

import lt.codeacademy.u8.tarpinisMeteo.meteo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BusinessLogic {
    Ui ui;
    List<RootCityForecast> futureForecastCitiesArr = new ArrayList<>();
//    List<RootCityObservations> pastObservationsCitiesArr = new ArrayList<>();
    List<String> cities = new ArrayList<>(List.of("vilnius", "anyksciai"));

    String dbUrl =  "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11673584";
    String dbUser = "sql11673584";
    String dbPass = "4IfiVURzW3";
    MySQL mySql;

    public BusinessLogic(Ui ui){
        this.ui = ui;

        this.mySql = new MySQL(dbUrl, dbUser, dbPass, ui);
    }

    public void start(){
    Boolean running = true;
        while(running){
            ui.infoOut("Weather forecast for cities: " + cities.toString());
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
            case "4" -> syncForecast();
        }

        return true;
    }
    private void getFromDb(){

    }

    private void changeCities() {
    }

    private void weatherNow() {
    }

    private void weatherForecast() {
    }

    public void syncForecast(){

        for (String city : cities) {
            //Download FUTURE forecasts
            String url = String.format("https://api.meteo.lt/v1/places/%s/forecasts/long-term", city);
            Optional<RootCityForecast> opt = JsonDownloadMeteo.getDataFromMeteo(url);
            if (opt.isPresent())
                futureForecastCitiesArr.add(opt.get());


            //Download PAST observations
//            https://api.meteo.lt/v1/stations/vilniaus-ams/observations/latest
        }

        mySql.writeToDbWeatherForecast(futureForecastCitiesArr);
    }

    public void syncWeatherObservations(){

        for (String city : cities) {
            String url = String.format("https://api.meteo.lt/v1/places/%s/forecasts/long-term", city);

            Optional<RootCityForecast> opt = JsonDownloadMeteo.getDataFromMeteo(url);
            if (opt.isPresent())
                futureForecastCitiesArr.add(opt.get());
                RootCityForecast oneTown = opt.get();
            System.out.println("donka donka");

//                opt.get().forecastTimestamps.get()
        }
        System.out.println("GOODD");


    }
}
