package lt.codeacademy.u8.tarpinisMeteo;

import lt.codeacademy.u8.tarpinisMeteo.meteo.RootCityForecast;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MySQL {
    String url;
    String user;
    String pass;
    Ui ui;

    public MySQL (String url, String user, String pass, Ui ui){
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.ui = ui;
    }


    public void writeToDbWeatherForecast(List<RootCityForecast> downlDataCityArr){

            String queryPrefix ="""
                    INSERT INTO weatherForecast (stationCode, temp, feelsLIkeTemp,
                    `condition`, relativeHumidity, windSpeed, windDirection, `dateTime`)
                    VALUES
                    """;


            String queryValues = downlDataCityArr.stream().flatMap(city->{
                String code  = city.place.code;
                return city.forecastTimestamps.stream().map(x->
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
