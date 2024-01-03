package lt.codeacademy.u8.tarpinisMeteo;

import lt.codeacademy.u8.tarpinisMeteo.meteo.forecast.RootCityForecast;
import lt.codeacademy.u8.tarpinisMeteo.meteo.observ.RootCityObserv;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BusinessLogic {
    Ui ui;
    List<RootCityForecast> futureForecastCitiesArr = new ArrayList<>();
    List<RootCityObserv> pastObservationsCitiesArr = new ArrayList<>();
    List<String[]> cities = new ArrayList<>(List.of(new String[] {"vilnius", "vilniaus-ams"}, new String[] {"kaunas", "kauno-ams"}));

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
            case "4" -> syncWithMeteo();
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

    public void syncWithMeteo(){

        //<editor-fold desc="Old Method without generics">
        //        for (String[] city : cities) {
//            //Download FUTURE forecasts
//            String url = String.format("https://api.meteo.lt/v1/places/%s/forecasts/long-term", city[0]);
//            String jsonString = JsonDownloadMeteo.getDataFromMeteo(url);
//            Optional<RootCityForecast> opt = JsonDownloadMeteo.parseJsonToRootCityForecast(jsonString, RootCityForecast.class);
//            if (opt.isPresent())
//                futureForecastCitiesArr.add(opt.get());
//
//
//            //Download PAST observations
//            String url2 = String.format("https://api.meteo.lt/v1/stations/%s/observations/latest", city[1]);
//            Optional<RootCityObserv> opt2 = JsonDownloadMeteo.parseJsonToRootCityForecast(url2, RootCityObserv.class);
//            if (opt2.isPresent())
//                pastObservationsCitiesArr.add(opt2.get());
//        }
        //</editor-fold>

        for (String[] city : cities) {
            getApisFillArrays(forecastUrlPattern, city[0], futureForecastCitiesArr, RootCityForecast.class );
            getApisFillArrays(observUrlPattern, city[1], pastObservationsCitiesArr, RootCityObserv.class );

        }

        mySql.writeToDbWeatherForecast(futureForecastCitiesArr);
        mySql.writeToDbObserv(pastObservationsCitiesArr);
    }


    public <T> void getApisFillArrays (String urlPattern, String city, List<T> list, Class<T> valueType){
        String url = String.format(urlPattern, city);
        String json = JsonDownloadMeteo.getDataFromMeteo(url);
        Optional<T> opt = JsonDownloadMeteo.parseJsonToRootCityForecast(json, valueType);
        if (opt.isPresent())
            list.add(opt.get());
    }




//    public void syncWeatherObservations(){
//
//        for (String city : cities) {
//            String url = String.format("https://api.meteo.lt/v1/places/%s/forecasts/long-term", city);
//
//            Optional<RootCityForecast> opt = JsonDownloadMeteo.getDataFromMeteo(url);
//            if (opt.isPresent())
//                futureForecastCitiesArr.add(opt.get());
//                RootCityForecast oneTown = opt.get();
//            System.out.println("donka donka");
//
////                opt.get().forecastTimestamps.get()
//        }
//        System.out.println("GOODD");
//
//
//    }
}
