package lt.codeacademy.u8.tarpinisMeteo;

import java.time.LocalDateTime;

public class WeatherFiltered {
    String stationCode;
    double temp;
    double feelsLikeTemp;
    String condition;
    double relativeHumidity;
    double windSpeed;
    double windDirection;
    LocalDateTime dateTime;

    public WeatherFiltered(String stationCode, double temp, double feelsLikeTemp, String condition, double relativeHumidity, double windSpeed, double windDirection, LocalDateTime dateTime) {
        this.stationCode = stationCode;
        this.temp = temp;
        this.feelsLikeTemp = feelsLikeTemp;
        this.condition = condition;
        this.relativeHumidity = relativeHumidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.dateTime = dateTime;
    }


    public String toStringForecast(){
        return String.format("""
                %8s | %5.1f℃ (feels %5.1f℃), humidity %2.0f%s, %16s %4.0f m/s, %s""",
                Utils.timeEnglish(dateTime), temp, feelsLikeTemp,  relativeHumidity, "%", Utils.getWindDirEnglish(windDirection), windSpeed, condition);
    }




    @Override
    public String toString() {
        return String.format("""
                %5.1f℃ (feels %5.1f℃), humidity %2.0f%s, %16s %4.0f m/s, %s""",
                temp, feelsLikeTemp,  relativeHumidity, "%", Utils.getWindDirEnglish(windDirection), windSpeed, condition);
    }



    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getFeelsLikeTemp() {
        return feelsLikeTemp;
    }

    public void setFeelsLikeTemp(double feelsLikeTemp) {
        this.feelsLikeTemp = feelsLikeTemp;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
