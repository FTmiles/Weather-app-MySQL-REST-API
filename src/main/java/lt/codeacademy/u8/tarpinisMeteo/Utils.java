package lt.codeacademy.u8.tarpinisMeteo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Utils {
    private Utils(){} //hidden constructor

    public static String firstLetterCaps(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

    //getWindDirEnglish metodas buvo WeatherFiltered klasej, bet po to perkeliau cia, ar ji geriau cia ar ten?
    public static String getWindDirEnglish(double w) {
        String windDir = "";

        if (w > 337.5 || w <= 22.5) windDir = "North wind";
        if (w > 25.5 || w <= 67.5) windDir = "North-east wind";
        if (w > 67.5 || w <= 112.5) windDir = "East wind";
        if (w > 112.5 || w <= 157.5) windDir = "South-east wind";
        if (w > 157.5 || w <= 202.5) windDir = "South wind";
        if (w > 202.5 || w <= 247.5) windDir = "South-west wind";
        if (w > 247.5 || w <= 292.5) windDir = "West wind";
        if (w > 292.5 || w <= 337.5) windDir = "North-west wind";
        return windDir;
    }

    public static String timeEnglish(LocalDateTime dateTime){
        LocalDateTime dtNow = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        if ((dateTime.getHour() == 12) && (dateTime.getDayOfMonth() == LocalDate.now().plusDays(1).getDayOfMonth())) return "Tomorrow";
        if (dateTime.isBefore(LocalDateTime.now().plusHours(7))) return dateTime.getHour() + ":00";
        return Utils.firstLetterCaps(dateTime.getDayOfWeek().toString());
    }

    public static String cityStationToFrom(String input){

        if (BusinessLogic.allStations.contains(input))
            return BusinessLogic.allCities.stream().filter(city->city.substring(0,3).equalsIgnoreCase(input.substring(0,3))).findFirst().orElse("city error");

        if (BusinessLogic.allCities.contains(input))
            return BusinessLogic.allStations.stream().filter(sta->sta.substring(0,3).equalsIgnoreCase(input.substring(0,3))).findFirst().orElse("station error");

        return "city not available";
    }

}
