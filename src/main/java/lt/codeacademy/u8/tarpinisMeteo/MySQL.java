package lt.codeacademy.u8.tarpinisMeteo;

import lt.codeacademy.u8.tarpinisMeteo.meteo.forecast.RootCityForecast;
import lt.codeacademy.u8.tarpinisMeteo.meteo.observ.Observation;
import lt.codeacademy.u8.tarpinisMeteo.meteo.observ.RootCityObserv;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MySQL {
    String url;
    String user;
    String pass;
    Ui ui;

    DateTimeFormatter dateFormatMeteo = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public MySQL (String url, String user, String pass, Ui ui){
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.ui = ui;
    }

    public void writeToDbObserv(List<RootCityObserv> list){
        String queryPrefix ="""
                    INSERT INTO weatherNow (stationCode, temp, feelsLIkeTemp,
                    `condition`, relativeHumidity, windSpeed, windDirection, `dateTime`)
                    VALUES
                    """;

        String queryValues = list.stream().map(city->{
            String code = city.station.code;
            Observation l = city.observations.getLast();
            return String.format("('%s', %s, %s, '%s', %s, %s, %s, '%s')",
                    code, l.airTemperature, l.feelsLikeTemperature, l.conditionCode, l.relativeHumidity, l.windSpeed, l.windDirection, l.observationTimeUtc);
        }).collect(Collectors.joining(","));

        updateDb(queryPrefix + queryValues);
    }


    public void writeToDbWeatherForecast(List<RootCityForecast> list){
            String queryPrefix ="""
                    INSERT INTO weatherForecast (stationCode, temp, feelsLIkeTemp,
                    `condition`, relativeHumidity, windSpeed, windDirection, `dateTime`)
                    VALUES
                    """;

            LocalDateTime dtNow = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

            String queryValues = list.stream().flatMap(city->{
                String code  = city.place.code;
                return city.forecastTimestamps.stream()
                        .filter(rec->{
                    LocalDateTime dt = LocalDateTime.parse(rec.forecastTimeUtc, dateFormatMeteo);
                    return dt.isAfter(dtNow) && dt.isBefore(dtNow.plusHours(6)) ||
                            dt.isEqual(dtNow.plusDays(1).withHour(12)) ||
                            dt.isEqual(dtNow.plusDays(2).withHour(12)) ||
                            dt.isEqual(dtNow.plusDays(3).withHour(12)) ||
                            dt.isEqual(dtNow.plusDays(4).withHour(12));
                })
                        .map(x->
                     String.format("('%s', %s, %s, '%s', %s, %s, %s, '%s')",
                            code, x.airTemperature, x.feelsLikeTemperature, x.conditionCode, x.relativeHumidity, x.windSpeed, x.windDirection, x.forecastTimeUtc)
                );
            }).collect(Collectors.joining(","));

        updateDb(queryPrefix + queryValues);
    }


    public int updateDb(String updateStr){
        try (Connection conn = DriverManager.getConnection(url, user, pass)){
            if (conn == null) {
                ui.infoOut("DB connection FAILED!");
                return 0;
            }
            ui.infoOut("DB connection SUCCESS.");
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(updateStr);

        }catch (Exception e) { e.printStackTrace(); }

        return 0;
    }

    public Optional<ResultSet> queryDB (String query){
        try (Connection conn = DriverManager.getConnection(url, user, pass)){
            if (conn == null) {
                ui.infoOut("DB connection FAILED!");
                return Optional.empty();
            }
            ui.infoOut("DB connection SUCCESS.");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

        }catch (Exception e) { e.printStackTrace(); }

        return Optional.empty();
    }


}
