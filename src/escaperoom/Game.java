/*
 * Copyright (C) 2018 Ryan Castelli
 * Copyright (C) 2018 Charles Dorsey-Ward
 * Copyright (C) 2018 Timothy Dovci
 * Copyright (C) 2018 Samantha Cole
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package escaperoom;

import java.util.Scanner;

/**
 * Randomly generates successive rooms. User must move through rooms using
 * intuitive console commands.
 *
 * @author NTropy
 * @author Charles Dorsey-Ward
 * @author Timothy Dovci
 * @author Samantha Cole
 * @version 3/15/17
 */
final class Game {

    /**
     * Tracks continuity, e.g. if player left room on east side they begin on
     * west side of next room.
     */
    private static boolean eastDoor, northDoor, southDoor, westDoor;

    /**
     * Tracks if player is sprinting. If true, player moves two spaces at a
     * time. If false, player moves one space at a time.
     */
    private static boolean runToggle = false;

    /**
     * Tracks if player is in the first room. Used to determine how a room
     * should be initialized.
     */
    private static boolean start = true;

    /**
     * Player's x,y position in the array.
     */
    private static int currentIntX, currentIntY;

    /**
     * Number of desks in a room.
     */
    private static int deskCounter = 0;

    /**
     * Keeps tracks of players last position after a move.
     */
    private static int tempIntX, tempIntY;

    /**
     * Tracks number of turns player has taken.
     */
    private static int turnCount;

    /**
     * Easter egg trophies in game, tracks if player has picked one up.
     */
    private static boolean trophy = false;

    /**
     * Tracks how many trophies player has.
     */
    private static int trophyCount = 0;

    /**
     * Utility class.
     */
    private Game() {

    }

    /**
     * Main method.
     * @param args
     *          command-line arguments; unused here
     */
    public static void main(final String[] args) {
        //starting dimensions
        int startY = 5;
        int startX = 8;

        //starting pos
        currentIntX = 1;
        currentIntY = 1;

        game(startY, startX); //creates room
    }

    /**
     * Creates a new game and handles user inputs.
     *
     * @param y
     *          vertical bound of room
     * @param x
     *          horizontal bound of room
     */
    private static void game(final int y, final int x) {
        //message for new room
        if (turnCount != 0) {
            System.out.println("You exit the room successfully and continue "
                    + "your journey through the building. \nEventually you "
                    + "enter a new room, only to be locked in again! Didn't "
                    + "you learn the first time?");
        }

        turnCount++;

        trophy = false;

        //storage for value of room
        int tempRoom = 1;

        //resets random number
        int rand = 0;
        int randXCode;
        int randYCode;

        //keyboard scanner
        Scanner kbReader = new Scanner(System.in);

        //var for user's choices
        String userInput = "";
        int userInputCode;

        //code for new room
        int code = (int) (Math.random() * 8999) + 1000;

        //gets code digits for placement
        int digit0 = code % 10;
        code /= 10;
        int digit1 = code % 10;
        code /= 10;
        int digit2 = code % 10;
        code /= 10;
        int digit3 = code;

        //creates int array for new room
        int[][] room = generateRoom(x, y);
        for (int j = 0; j <= 3; j++) {
            randXCode = (int) (Math.random() * (x - 3)) + 2;
            randYCode = (int) (Math.random() * (y - 3)) + 2;
            if (room[randYCode][randXCode] == 1) {
                room[randYCode][randXCode] = 4 + j;
            } else {
                j--;
            }
        }

        if (northDoor) {
            //sets starting pos
            room[y - 1][tempIntX] = 2;
            room[y - 2][tempIntX] = 3;

            //sets ints for current tile
            currentIntX = tempIntX;
            currentIntY = (y - 2);

            //boolean for door
            northDoor = false;
        }
        if (southDoor) {
            room[0][tempIntX] = 2;
            room[1][tempIntX] = 3;
            currentIntX = tempIntX;
            currentIntY = 1;
            southDoor = false;
        }
        if (westDoor) {
            room[tempIntY][x - 1] = 2;
            room[tempIntY][x - 2] = 3;
            currentIntX = x - 2;
            currentIntY = tempIntY;
            westDoor = false;
        }
        if (eastDoor) {
            room[tempIntY][0] = 2;
            room[tempIntY][1] = 3;
            currentIntX = 1;
            currentIntY = tempIntY;
            eastDoor = false;
        }
        //end of game after 5 rooms
        if (turnCount == 5 && !(userInput.equalsIgnoreCase("exit")) &&
                !(userInput.equalsIgnoreCase("exity"))) {
            //end message
            System.out.println("You shield your eyes as they adjust to the "
                    + "bright light...You have escaped.");
            System.out.println("You collected " + trophyCount + " trophies.");
            userInput = "exit";
        }

        System.out.println("You are in a room. You can't really see.");

        do {
            //prints start of game
            System.out.println("What direction do you want to move?: ");

            //reads user input
            userInput = kbReader.next();

            //temp values for position
            tempIntX = currentIntX;
            tempIntY = currentIntY;

            start = false; //boolean for start of game

            //handles direction
            //upper left is 0,0
            if (userInput.equalsIgnoreCase("n")
                    || userInput.equalsIgnoreCase("north")
                    || userInput.equalsIgnoreCase("up")) {
                if (!(currentIntY - 1 <= 0)) { //handles y movement
                    if (runToggle && (currentIntY - 1 <= 0)) {
                        currentIntY--;
                    } else if (runToggle && !(currentIntY - 1 <= 0)) {
                        currentIntY -= 2;
                    } else {
                        currentIntY--;
                    }
                    //door handler
                } else if (!(currentIntY - 1 < 0)
                        && room[currentIntY - 1][currentIntX] == 2) {
                    System.out.print("There's a door there. Go through it?\n");
                    //door that was generated on room creation.
                    userInput = kbReader.next();
                    if ((userInput.equalsIgnoreCase("Y")
                            || userInput.equalsIgnoreCase("yes"))) {
                        System.out.print("\nEnter the code: ");
                        userInputCode = kbReader.nextInt();
                        if (userInputCode == code) {
                            //new limits for room
                            int newLimX = (int) (Math.random() * (x + 2)) + x;
                            int newLimY = (int) (Math.random() * (y + 2)) + y;

                            //door wall
                            northDoor = true;

                            //recursively generates new room
                            game(newLimY, newLimX);

                            //removes code
                            //recursive exit
                            userInput = "exit";
                        } else {
                            System.out.println("Lol, no");
                        }
                    } else { //moved into wall
                        System.out.print("There's a wall there.\n");
                    }
                } else if (room[currentIntY][currentIntX] == 8) {
                    if (rand <= 50) {
                        System.out.println("You tripped over a table in the "
                                + "dark.");
                    } else if (rand > 95) {
                        System.out.println("You run into a bookshelf and "
                                + "something falls off and hits you in the "
                                + "head.");
                        userInput = "exit";
                    } else {
                        System.out.println("You run into a bookshelf.");
                        currentIntY++;
                    }
                } else { //moved into wall
                    System.out.print("There's a wall there.\n");
                }
            } else if (userInput.equalsIgnoreCase("s")
                    || userInput.equalsIgnoreCase("south")
                    || userInput.equalsIgnoreCase("down")) {
                if (!(currentIntY + 1 >= (y - 1))) {
                    if (runToggle && (currentIntY + 1 <= 0)) {
                        currentIntY++;
                    } else if (runToggle && !(currentIntY + 1 <= 0)) {
                        currentIntY += 2;
                    } else {
                        currentIntY++;
                    }
                } else if (!(currentIntY + 1 > (y - 1))) {
                    if (room[currentIntY + 1][currentIntX] == 2) {
                        System.out.print("There's a door there. Go through "
                                + "it?\n");
                        userInput = kbReader.next();
                        if ((userInput.equalsIgnoreCase("Y")
                                || userInput.equalsIgnoreCase("yes"))) {
                            System.out.print("\nEnter the code: ");
                            userInputCode = kbReader.nextInt();
                            if (userInputCode == code) {
                                int newLimX = (int) (Math.random() * (x + 2))
                                        + x;
                                int newLimY = (int) (Math.random() * (y + 2))
                                        + y;
                                southDoor = true;
                                game(newLimY, newLimX);
                                userInput = "exit";
                            } else {
                                System.out.println("Lol, no");
                            }
                        }
                    } else {
                        System.out.print("There's a wall there.\n");
                    }
                } else {
                    System.out.print("There's a wall there.\n");
                }
                if (room[currentIntY][currentIntX] == 8) {
                    if (rand <= 50) {
                        System.out.println("You tripped over a table in the "
                                + "dark.");
                    } else if (rand > 99) {
                        System.out.println("You run into a bookshelf and "
                                + "something falls off and hits you in the "
                                + "head.");
                        for (int j = 0; j < turnCount; j++) {
                            userInput = "exit";
                        }
                    } else {
                        System.out.println("You run into a bookshelf. Idiot.");
                        currentIntY--;
                    }
                }

            } else if (userInput.equalsIgnoreCase("w")
                    || userInput.equalsIgnoreCase("west")
                    || userInput.equalsIgnoreCase("left")) {
                if (!(currentIntX - 1 <= 0)) { //handles movement in X
                    if (runToggle && (currentIntX - 1 <= 0)) {
                        currentIntX--;
                    } else if (runToggle && !(currentIntX - 1 <= 0)) {
                        currentIntX -= 2;
                    } else {
                        currentIntX--;
                    }
                } else if (!(currentIntX - 1 < 0)
                        && room[currentIntY][currentIntX - 1] == 2) {
                    System.out.print("There's a door there. Go through it?\n");
                    userInput = kbReader.next();
                    if ((userInput.equalsIgnoreCase("Y")
                            || userInput.equalsIgnoreCase("yes"))) {
                        System.out.print("\nEnter the code: ");
                        userInputCode = kbReader.nextInt();
                        if (userInputCode == code) {
                            System.out.print("\nEnter the code: ");
                            int newLimX = (int) (Math.random() * (x + 2)) + x;
                            int newLimY = (int) (Math.random() * (y + 2)) + y;
                            westDoor = true;
                            game(newLimY, newLimX);
                            userInput = "exit";
                        } else {
                            System.out.println("Lol, no");
                        }
                    }
                } else {
                    System.out.print("There's a wall there.\n");
                }
                if (room[currentIntY][currentIntX] == 8) {
                    if (rand <= 50) {
                        System.out.println("You tripped over a table in the "
                                + "dark.");
                    } else if (rand > 95) {
                        System.out.println("You run into a bookshelf and "
                                + "something falls off and hits you in the "
                                + "head.");
                        for (int j = 0; j < turnCount; j++) {
                            userInput = "exit";
                        }
                    } else {
                        System.out.println("You run into a bookshelf. Idiot.");
                        currentIntX++;
                    }
                }

            } else if (userInput.equalsIgnoreCase("e")
                    || userInput.equalsIgnoreCase("east")
                    || userInput.equalsIgnoreCase("right")) {
                if ((currentIntX + 1) < (x - 1)) {
                    if (runToggle && (currentIntX + 1 <= 0)) {
                        currentIntX++;
                    } else if (runToggle && !(currentIntX + 1 <= 0)) {
                        currentIntX += 2;
                    } else {
                        currentIntX++;
                    }
                } else if ((currentIntX + 1 >= (x - 2))
                        && room[currentIntY][currentIntX + 1] == 2) {
                    System.out.print("There's a door there. Go through it?\n");
                    userInput = kbReader.next();
                    if ((userInput.equalsIgnoreCase("Y")
                            || userInput.equalsIgnoreCase("yes"))) {
                        System.out.print("\nEnter the code: ");
                        userInputCode = kbReader.nextInt();
                        if (userInputCode == code) {
                            System.out.print("\nEnter the code: ");
                            int newLimX = (int) (Math.random() * (x + 2))
                                    + x;
                            int newLimY = (int) (Math.random() * (y + 2))
                                    + y;
                            eastDoor = true;
                            game(newLimY, newLimX);
                            userInput = "exit";
                        } else {
                            System.out.println("Lol, no");
                        }
                    }
                } else if (currentIntX + 1 > (x - 2)) {
                    System.out.print("There's a wall there.\n");
                }
                if (room[currentIntY][currentIntX] == 8) {
                    if (rand <= 50) {
                        System.out.println("You tripped over a table in the "
                                + "dark.");
                    } else if (rand > 95) {
                        System.out.println("You run into a bookshelf and "
                                + "something falls off and hits you in the "
                                + "head.");
                        for (int j = 0; j < turnCount; j++) {
                            userInput = "exit";
                        }
                    } else {
                        System.out.println("You run into a bookshelf. Idiot.");
                        currentIntX--;
                    }
                }
                //allows player to check current and adjacent tiles
            } else if (userInput.equalsIgnoreCase("search")
                    || userInput.equalsIgnoreCase("look")) {
                int randomSearch = (int) (Math.random() * 100) + 1;
                if (tempRoom != 1) { //checks if tile isn't default
                    //checks if tile has code
                    if (tempRoom == 4 || tempRoom == 5 || tempRoom == 6
                            || tempRoom == 7) {
                        if (randomSearch >= 2) {
                            if (tempRoom == 4) {
                                System.out.println("You see a number scrawled "
                                        + "out on a note!\n" + digit0
                                        + "\nOn the back it says, "
                                        + "\"Millennials, amirite?\"");
                            }
                            if (tempRoom == 5) {
                                System.out.println("You see a number scrawled "
                                        + "out on a note!\n" + digit1
                                        + "\nOn the back it says, \"All about "
                                                + "those Benjamins\"");
                            }
                            if (tempRoom == 6) {
                                System.out.println("You see a number scrawled "
                                        + "out on a note!\n" + digit2
                                        + "\nOn the back it says, \"7 ate 9. "
                                        + "Who's next?\"");
                            }
                            if (tempRoom == 7) {
                                System.out.println("You see a number scrawled "
                                        + "out on a note!\n" + digit3 
                                        + "\nOn the back it says, \"You\"");
                            }
                            tempRoom = 1;
                        } else if (randomSearch < 1) {
                            System.out.print("You don't find anything. Maybe "
                                    + "there's something elsewhere...\n");
                            System.out.print("Despite this, you get the strange"
                                    + " feeling you're not getting out "
                                    + "of here...");
                            tempRoom = 1;
                        } else {
                            System.out.println("Nothing nearby...");
                        }
                    } else if ((room[currentIntY + 1][currentIntX] != 1
                            && room[currentIntY + 1][currentIntX] != 9)
                            || (room[currentIntY - 1][currentIntX] != 1
                            && room[currentIntY - 1][currentIntX] != 9)
                            || (room[currentIntY][currentIntX + 1] != 1
                            && room[currentIntY][currentIntX + 1] != 9)
                            || (room[currentIntY][currentIntX - 1] != 1
                            && room[currentIntY][currentIntX - 1] != 9)) {
                        System.out.println("There's something nearby..."
                                + "\nCan't quite make it out.");
                    } else if ((room[currentIntY + 1][currentIntX] != 1
                            && (room[currentIntY + 1][currentIntX] == 9
                            || room[currentIntY + 1][currentIntX] == 8))
                            || (room[currentIntY - 1][currentIntX] != 1
                            && (room[currentIntY - 1][currentIntX] == 9
                            || room[currentIntY - 1][currentIntX] == 8))
                            || (room[currentIntY][currentIntX + 1] != 1
                            && (room[currentIntY][currentIntX + 1] == 9
                            || room[currentIntY][currentIntX + 1] == 8))
                            || (room[currentIntY][currentIntX - 1] != 1
                            && (room[currentIntY][currentIntX - 1] == 9
                            || room[currentIntY][currentIntX - 1] == 8))) {
                        int randomItemSearch = (int) (Math.random() * 100) + 1;
                        if (randomItemSearch <= 95) {
                            System.out.println("Nothing nearby...");
                        }
                        if (randomItemSearch == 100 && trophy) {
                            System.out.println("You got a trophy. Wow.");
                            trophy = true;
                            trophyCount++;
                        } else if (randomItemSearch > 95
                                && randomItemSearch <= 99) {
                            System.out.println("In your greed, you failed to"
                                    + " notice the lack of structural integrity"
                                    + " of the container you were searching in,"
                                    + " and it has slammed shut, trapping you "
                                    + "inside.");
                            for (int j = 0; j < turnCount; j++) {
                                userInput = "exit";
                            }
                        }
                    } else {
                        System.out.println("Nothing nearby...");
                    }
                } else {
                    System.out.println("Nothing nearby...");
                }
            } else if (userInput.equalsIgnoreCase("Rules")
                    || userInput.equalsIgnoreCase("Instructions")) {
                System.out.println("To move one of the four directions type up,"
                        + "down, left, or right. \nTo search the tile you are "
                        + "on type search or look. \nYour objective is to "
                        + "escape the series of rooms by gathering clues and "
                        + "tools.");
                System.out.println("There may be some secrets hidden within "
                        + "each room, finding them will increase your score.");
            } else if (userInput.equalsIgnoreCase("suicide")) { //alt escape
                System.out.println("You bash your head into the ground until"
                        + "you pass out and die.");
                for (int j = 0; j < turnCount; j++) {
                    userInput = "exit";
                }
            } else if (userInput.equalsIgnoreCase("run")) {
                if (runToggle) {
                    runToggle = false;
                    System.out.println("You stop running");
                } else {
                    runToggle = true;
                    System.out.println("You start to run");
                }
            } else if (userInput.equalsIgnoreCase("walk")) {
                runToggle = false;
                System.out.println("You begin to walk");
            } else if (userInput.equalsIgnoreCase("help")
                    || userInput.equalsIgnoreCase("let me out")) {
                System.out.println("Your cries echo against the cold, "
                        + "unforgiving walls. There is no one to hear them.");
            } else if (userInput.equalsIgnoreCase("debug")) { //debug values
                //current x,y values
                System.out.println("X: " + currentIntX);
                System.out.println("Y: " + currentIntY);

                System.out.println(tempRoom); //prints tile's index

                System.out.println("Running: " + runToggle);

                System.out.println("Code: " + code);

                System.out.println("Digits: " + digit0 + " " + digit1 + " "
                        + digit2 + " " + digit3);
                //temp map for debugging
            } else if (userInput.equalsIgnoreCase("Map")) {
                System.out.println("1 - Empty Space"
                        + "\n2 - Door"
                        + "\n9 - Wall"
                        + "\n3 - Player"); //key

                for (int j = 0; j < y; j++) { //vertical iteration
                    for (int k = 0; k < x; k++) { //horizontal iteration
                        if (k == (x - 1)) {
                            //prints room value for last in line
                            System.out.print(room[j][k]);
                            System.out.println(""); //new line
                        } else {
                            System.out.print(room[j][k]); //prints room value
                        }
                    }
                }
                //handles unrecognized command
            } else if (!(userInput.equalsIgnoreCase("exit"))
                    && !(userInput.equalsIgnoreCase("exity"))) {
                System.out.println("What was I doing again? I can't remember...");
            }
            if (tempRoom == 1 && !(userInput.equalsIgnoreCase("exit"))
                    && !(userInput.equalsIgnoreCase("exity"))) {
                System.out.println("Still in the room.");
            }
            //saves room's temp value and allows player to advance
            //without affecting that value
            if (!(userInput.equalsIgnoreCase("exit"))
                    && !(userInput.equalsIgnoreCase("exity"))) {
                //resets room to value saved when tile moved onto
                room[tempIntY][tempIntX] = tempRoom;
                //sets temp value to new move
                tempRoom = room[currentIntY][currentIntX];

                room[currentIntY][currentIntX] = 3; //sets player location
            }
        } while (!(userInput.equalsIgnoreCase("exit"))
                && !(userInput.equalsIgnoreCase("exity"))); //exit commands
    }

    /**
     * Generates a new room.
     *
     * @param x
     *          width bound of room
     * @param y
     *          height bound of room
     * @return generated room
     */
    private static int[][] generateRoom(final int x, final int y) {
        int[][] room = new int[y][x];
        for (int j = 0; j < y; j++) { //vertical iteration
            for (int k = 0; k < x; k++) { //horizontal iteration
                int rand = (int) (Math.random() * 100) + 1;
                if ((((j == 0 && k > 0 && k < (x - 1)) || (j == (y - 1)
                        && k > 0 && k < (x - 1)) || (k == 0 && j > 0
                        && j < (y - 1)) || (k == (x - 1)) && j > 0
                        && j < (y - 1))) && rand > 90) { //doors
                    room[j][k] = 2;
                } else if (!(j == 0 && k == 1) && room[j][k] != 2
                        && (j == 0 || k == 0 || j == (y - 1)
                        || k == (x - 1))) { //walls
                    room[j][k] = 9;
                } else if (!(j == 0 && k == 1) && room[j][k] != 2
                        && room[j][k] != 9 && room[j + 1][k] != 2
                        && room[j - 1][k] != 2 && room[j][k + 1] != 2
                        && room[j][k - 1] != 2 && rand > 85
                        && room[j + 1][k] != 8 && room[j - 1][k] != 8
                        && room[j][k + 1] != 8 && room[j][k - 1] != 8
                        && deskCounter < 10) {
                    room[j][k] = 8;
                    deskCounter++;
                } else { //empty space
                    room[j][k] = 1;
                }
                if (start) { //starting values for game
                    room[1][1] = 3;
                    currentIntX = 1;
                    currentIntY = 1;
                    tempIntX = 0;
                    tempIntY = 0;
                }
            } //end room creation
        }
        return room;
    }
}
