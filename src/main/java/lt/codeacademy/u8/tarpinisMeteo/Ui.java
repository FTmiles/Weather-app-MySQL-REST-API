package lt.codeacademy.u8.tarpinisMeteo;

import java.util.Scanner;

public class Ui {
    Scanner sc = new Scanner(System.in);

    public void infoOut(String msg){
        System.out.println(msg);
    }

    public String printScan(String msg){
        System.out.print(msg);
        return sc.nextLine();
    }

    public String getUserMenuInput(){
        String in = sc.nextLine();
        return in.substring(0,1);
    }

    public void printMenu(){
        System.out.println("""

                    Last update ___-___-___
                    1. Show weather now
                    2. Show weather forecast
                    3. Change cities
                    4. Download latest Meteo
                    Q. Quit life
                    """);
    }
}
