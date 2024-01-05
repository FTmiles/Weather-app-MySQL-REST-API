package lt.codeacademy.u8.tarpinisMeteo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public void writeDbWeather(List<WeatherFiltered> list, String table) {
        String prepStmt = String.format("""
                INSERT INTO %s (stationCode, temp, feelsLIkeTemp,
                `condition`, relativeHumidity, windSpeed, windDirection, `dateTime`)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);""", table);

        String delQuery = "TRUNCATE TABLE weatherForecast;";

        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(prepStmt)) {
            conn.setAutoCommit(false);

            for (WeatherFiltered forecast : list) {
                preparedStatement.setString(1, forecast.getStationCode());
                preparedStatement.setDouble(2, forecast.getTemp());
                preparedStatement.setDouble(3, forecast.getFeelsLikeTemp());
                preparedStatement.setString(4, forecast.getCondition());
                preparedStatement.setDouble(5, forecast.getRelativeHumidity());
                preparedStatement.setDouble(6, forecast.getWindSpeed());
                preparedStatement.setDouble(7, forecast.getWindDirection());
                preparedStatement.setString(8, forecast.getDateTime().toString());

                preparedStatement.addBatch();
            }

            //stetement - delete
            Statement stmt = conn.createStatement();
            stmt.execute(delQuery);

            //prepared stetement - write
            preparedStatement.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeDbCities(List<String> cities){
        String prepStmt = "INSERT INTO cities (activeCity) VALUES (?);";
        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(prepStmt)) {
            for (String city : cities) {
                preparedStatement.setString(1, city);

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void wipeWriteDbCities(List<String> cities) {
        String query1 = "DELETE FROM cities WHERE activeCity >= 'a';";
        String query2 = String.format("INSERT INTO cities  VALUES %s;",
                cities.stream().map(x->"('"+x+"')").collect(Collectors.joining(",")));

        try (Connection conn = getConnection(); ){
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();
             stmt.execute(query1);

            Statement stmt2 = conn.createStatement();
            stmt2.execute(query2);

            conn.commit();


        }catch (Exception e) {e.printStackTrace();}

    }

    public List<String> readDbCities(){
        String query = "SELECT * FROM cities;";
        List<String> returnList = new ArrayList<>();

        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String city = rs.getString("ActiveCity");
                returnList.add(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnList;
    }

    public List<WeatherFiltered> readDbWeather(String table) {
        String query = String.format("SELECT * FROM %s;", table);
        List<WeatherFiltered> returnList = new ArrayList<>();

        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()){
                String stationCode = rs.getString("stationCode");
                double temp = rs.getInt("temp");
                double feelsLikeTemp = rs.getInt("feelsLikeTemp");
                String condition = rs.getString("condition");
                double relativeHumidity = rs.getInt("relativeHumidity");
                double windSpeed = rs.getInt("windSpeed");
                double windDirection = rs.getInt("windDirection");
                String dateTimeStr = rs.getString("dateTime");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, BusinessLogic.dateFormatMeteo);
                returnList.add(new WeatherFiltered(stationCode, temp, feelsLikeTemp, condition, relativeHumidity, windSpeed, windDirection, dateTime));
            }
        } catch (Exception e) {e.printStackTrace();}
        return returnList;
    }



    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, pass);
        if (conn != null) ui.infoOut("DB connection SUCCESS.");
        return conn;
    }

}

