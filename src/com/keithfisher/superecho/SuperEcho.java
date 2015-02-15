package com.keithfisher.superecho;

import com.noelportugal.amazonecho.AmazonEchoApi;
import java.io.IOException;


public class SuperEcho {

    private static AmazonEchoApi amazonEchoApi;
    private static Insteon insteonHub;
    private static Parser parser;


    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        amazonEchoApi = new AmazonEchoApi("https://pitangui.amazon.com", "keithfisher@gmail.com", "zaphod");
        amazonEchoApi.httpLogin();
        insteonHub = new Insteon("173.227.35.146", "25105", "password", "admin");
        parser = new Parser();
        while (true) {
            String command = "";
            try {
                command = amazonEchoApi.getLatestHistory();

            } catch (Exception e) {
                System.out.println(e);
            }

            if (command != null) {
                //Parse the command      
                System.out.println("Command=" + command);
                parser.parseCommand(command);
            } else {
                System.out.println("No new command");
            }

            Thread.sleep(5000);
        }

    }

   
}
