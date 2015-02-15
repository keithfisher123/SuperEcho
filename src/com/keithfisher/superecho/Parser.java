/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keithfisher.superecho;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author fisherk
 */
public class Parser {

    public String[] Actions = {"on", "off", "up", "down"};
    public String[] Actors = {"living", "kitchen", "bedroom", "movie", "romance", "party"};
    public String[] Validators = {"light", "lights", "mode", "watch"};

    public void parseCommand(String Command) {
        boolean isAction = false;
        String action = "";
        boolean isActor = false;
        String actor = "";
        boolean hasValidator = false;
        String validator = "";

        //This method will try and parse the command
        //make lower case
        Command = Command.toLowerCase();
        //Loop over our command words and see if it contains actor,action and validator words (in any order)
        for (StringTokenizer stringTokenizer = new StringTokenizer(Command); stringTokenizer.hasMoreTokens();) {
            String token = stringTokenizer.nextToken();
            //System.out.println("processinf token " + token);
            if (Arrays.asList(Actions).contains(token)) {
                isAction = true;
                action = token;
            };
            if (Arrays.asList(Actors).contains(token)) {
                isActor = true;
                actor = token;
            };
            if (Arrays.asList(Validators).contains(token)) {
                hasValidator = true;
                validator = token;
            };
        }
        if (isActor) {
            System.out.println("Command Understood!");
            System.out.print("      Action= " + action);
            System.out.print(" With Actor= " + actor);
            System.out.println(" With validator= " + validator);
        }
    }

}
