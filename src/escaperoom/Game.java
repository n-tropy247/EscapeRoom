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
 * @version 10/6/2018
 * @since 3/15/17
 */
final class Game {

    //GAME OBJECT INTEGERS
    /**
     * The values that make up different room components.
     */
    private static final int EMPTY = 1, DOOR = 2, PLAYER = 3, DIGIT_ONE = 4,
            DIGIT_TWO = 5, DIGIT_THREE = 6, DIGIT_FOUR = 7, OBJECT = 8,
            WALL = 9;

    /**
     * Chance bound to hit a bookshelf and die if passing over object tile.
     */
    private static final int BOOKSHELF_COLLIDE = 99;

    /**
     * Length of a door code.
     */
    private static final int CODE_LEN = 3;

    /**
     * Limits to ensure code is within range of 1000 - 9999.
     */
    private static final int CODE_MAX_BOUND = 8999, CODE_MIN_BOUND = 1000;

    /**
     * Used in modulus operation to retrieve a desired code digit.
     */
    private static final int CODE_RADIX = 10;

    /**
     * Chance bound to hit a desk if passing over object tile.
     */
    private static final int DESK_COLLIDE = 50;

    /**
     * MaroomWidthimum number of obstacles.
     */
    private static final int DESK_NUM_MAX = 10;

    /**
     * Places of digits to retrieve values.
     */
    private static final int DIG1 = 1, DIG2 = 2, DIG3 = 3, DIG4 = 4;

    /**
     * Chance bound to get a trophy on a random search.
     */
    private static final int TROPHY_GET = 100;
    //END OBJECT INTEGERS

    //PLACEMENT BOUNDARY INTEGERS
    /**
     * Bounds for code placement to ensure the code is within the room.
     */
    private static final int CODE_PLACEMENT_BOUND = 3;

    /**
     * Boundary for random generation of an obstacle.
     */
    private static final int DESK_GEN_BOUND = 85;

    /**
     * Boundary for random generation of a door.
     */
    private static final int DOOR_GEN_BOUND = 90;

    /**
     * Upper bound of random number to determine object placement.
     */
    private static final int PLACEMENT_BOUND = 100;
    //END BOUNDARY VARIABLES

    /**
     * Number of completed rooms necessary to win.
     */
    private static final int MAX_TURNS = 5;

    /**
     * Upper bound of random search. All searches for items based on value in
     * range of 1 - 100.
     */
    private static final int SEARCH_BOUND = 100;

    /**
     * Bounds of starting room.
     */
    private static final int START_WIDTH = 8, START_HEIGHT = 5;

    /**
     * Keyboard scanner.
     */
    private static final Scanner KB_READER = new Scanner(System.in);

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
     * Easter egg trophies in game, tracks if player has picked one up.
     */
    private static boolean trophy = false;

    /**
     * Code for door of room.
     */
    private static int code;

    /**
     * Player's roomWidth,roomHeight position in the array.
     */
    private static int currentIntX, currentIntY;

    /**
     * Number of desks in a room.
     */
    private static int deskCounter = 0;

    /**
     * Keeps tracks of players last position after a move.
     */
    private static int tempIntX, tempIntY, tempRoom;

    /**
     * Tracks how many trophies player has.
     */
    private static int trophyCount = 0;

    /**
     * Tracks number of turns player has taken.
     */
    private static int turnCount;

    /**
     * User input.
     */
    private static String inpt;

    /**
     * Utility class.
     */
    private Game() {

    }

    /**
     * Main method.
     *
     * @param args
     *          command-line arguments; unused here
     */
    public static void main(final String[] args) {
        //starting pos
        currentIntX = 1;
        currentIntY = 1;

        game(START_WIDTH, START_HEIGHT);
    }

    /**
     * Shows debug values in console.
     *
     * @param room
     *          the array of values for the room
     * @param roomWidth
     *          width of room
     * @param roomHeight
     *          height of room
     */
    private static void debug(final int[][] room, final int roomWidth,
            final int roomHeight) {
        //current roomWidth,roomHeight values
        System.out.println("X: " + currentIntX);
        System.out.println("Y: " + currentIntY);

        System.out.println(tempRoom); //prints tile's inderoomWidth

        System.out.println("Running: " + runToggle);

        System.out.println("Code: " + code);

        //temp map for debugging
        System.out.println("1 - Empty Space"
                + "\n2 - Door"
                + "\n9 - Wall"
                + "\n3 - Player"); //key

        for (int j = 0; j < roomHeight; j++) { //vertical iteration
            for (int k = 0; k < roomWidth; k++) { //horizontal iteration
                if (k == (roomWidth - 1)) {
                    //prints room value for last in line
                    System.out.print(room[j][k]);
                    System.out.println(""); //new line
                } else {
                    System.out.print(room[j][k]); //prints room value
                }
            }
        }
    }

    /**
     * Handles user entering a door.
     * @param roomWidth
     *          width of room
     * @param roomHeight
     *          height of room
     */
    private static void door(final int roomWidth,
            final int roomHeight) {
        System.out.print("There's a door there. Go through it?\n");
        inpt = KB_READER.next();
        if ((inpt.equalsIgnoreCase("Y")
                || inpt.equalsIgnoreCase("yes"))) {
            System.out.print("\nEnter the code: ");
            int inptCode = KB_READER.nextInt();
            if (inptCode == code) {
                System.out.print("\nEnter the code: ");
                int newLimX = (int) (Math.random() * (roomWidth + 2))
                        + roomWidth;
                int newLimY = (int) (Math.random() * (roomHeight + 2))
                        + roomHeight;
                westDoor = true;
                game(newLimY, newLimX);
                inpt = "exit";
            } else {
                System.out.println("Lol, no");
            }
        }
    }

    /**
     * Handles user inputs.
     *
     * @param roomHeight
     *          vertical bound of room
     * @param roomWidth
     *          horizontal bound of room
     */
    private static void game(final int roomWidth, final int roomHeight) {
        //message for new room
        if (turnCount != 0) {
            System.out.println("You exit the room successfully and continue "
                    + "your journey through the building. \nEventually you "
                    + "enter a new room, only to be locked in again! Didn't "
                    + "you learn the first time?");
        }
        turnCount++;

        trophy = false; //start with no trophy

        //storage for value of room
        tempRoom = 1;

        inpt = "";

        int[][] room = generateRoom(roomWidth, roomHeight);

        genCode(room);

        //sets starting position and tile values
        if (northDoor) {
            room[roomHeight - 1][tempIntX] = DOOR;
            room[roomHeight - 2][tempIntX] = PLAYER;
            currentIntX = tempIntX;
            currentIntY = (roomHeight - 2);
            northDoor = false;
        } else if (southDoor) {
            room[0][tempIntX] = DOOR;
            room[1][tempIntX] = PLAYER;
            currentIntX = tempIntX;
            currentIntY = 1;
            southDoor = false;
        } else if (westDoor) {
            room[tempIntY][roomWidth - 1] = DOOR;
            room[tempIntY][roomWidth - 2] = PLAYER;
            currentIntX = roomWidth - 2;
            currentIntY = tempIntY;
            westDoor = false;
        } else if (eastDoor) {
            room[tempIntY][0] = DOOR;
            room[tempIntY][1] = PLAYER;
            currentIntX = 1;
            currentIntY = tempIntY;
            eastDoor = false;
        }
        if (turnCount == MAX_TURNS && !(inpt.equalsIgnoreCase("exit"))) {
            //end message
            System.out.println("You shield your eyes as they adjust to the "
                    + "bright light...You have escaped.");
            System.out.println("You collected " + trophyCount + " trophies.");
            inpt = "exit";
        }

        System.out.println("You are in a room. You can't really see.");

        start = false; //boolean for start of game

        do {
            //prints start of game
            System.out.println("What direction do you want to move?: ");

            //reads user input
            inpt = KB_READER.next();

            //temp values for position
            tempIntX = currentIntX;
            tempIntY = currentIntY;

            move(room, roomWidth, roomHeight);
        } while (!(inpt.equalsIgnoreCase("exit")));
    }

    /**
     * Generates a code and places it in the room.
     *
     * @param room
     *          the array of values for a room
     */
    private static void genCode(final int[][] room) {
        //code for new room
        code = (int) (Math.random() * CODE_MAX_BOUND) + CODE_MIN_BOUND;

        int roomHeight = room.length;
        int roomWidth = room[0].length;

        int randXCode;
        int randYCode;

        for (int j = 0; j <= CODE_LEN; j++) {
            randXCode = (int) (Math.random() * (roomWidth
                    - CODE_PLACEMENT_BOUND)) + 2;
            randYCode = (int) (Math.random() * (roomHeight
                    - CODE_PLACEMENT_BOUND)) + 2;
            if (room[randYCode][randXCode] == 1) {
                room[randYCode][randXCode] = DIGIT_ONE + j;
            } else {
                j--;
            }
        }
    }

    /**
     * Generates a new room.
     *
     * @param roomWidth
     *          width bound of room
     * @param roomHeight
     *          height bound of room
     * @return generated room
     */
    private static int[][] generateRoom(final int roomWidth,
            final int roomHeight) {
        int[][] room = new int[roomHeight][roomWidth];
        for (int j = 0; j < roomHeight; j++) { //vertical iteration
            for (int k = 0; k < roomWidth; k++) { //horizontal iteration
                int rand = (int) (Math.random() * PLACEMENT_BOUND) + 1;
                if ((((j == 0 && k > 0 && k < (roomWidth - 1))
                        || (j == (roomHeight - 1) && k > 0
                        && k < (roomWidth - 1)) || (k == 0 && j > 0
                        && j < (roomHeight - 1)) || (k == (roomWidth - 1))
                        && j > 0 && j < (roomHeight - 1)))
                        && rand > DOOR_GEN_BOUND) {
                    room[j][k] = DOOR;
                } else if (!(j == 0 && k == 1) && room[j][k] != DOOR
                        && (j == 0 || k == 0 || j == (roomHeight - 1)
                        || k == (roomWidth - 1))) { //walls
                    room[j][k] = WALL;
                } else if (!(j == 0 && k == 1) && room[j][k] != 2
                        && room[j][k] != WALL && room[j + 1][k] != 2
                        && room[j - 1][k] != DOOR && room[j][k + 1] != DOOR
                        && room[j][k - 1] != DOOR && rand > DESK_GEN_BOUND
                        && room[j + 1][k] != OBJECT && room[j - 1][k] != OBJECT
                        && room[j][k + 1] != OBJECT && room[j][k - 1] != OBJECT
                        && deskCounter < DESK_NUM_MAX) {
                    room[j][k] = OBJECT;
                    deskCounter++;
                } else { //empty space
                    room[j][k] = EMPTY;
                }
                if (start) { //starting values for game
                    room[1][1] = PLAYER;
                    currentIntX = 1;
                    currentIntY = 1;
                    tempIntX = 0;
                    tempIntY = 0;
                }
            } //end room creation
        }
        return room;
    }

    /**
     * Get a digit of the code.
     *
     * @param dig
     *          desired digit from right to left
     * @return requested digit
     */
    private static int getCodeDig(final int dig) {
        int finDig = 0;
        for (int j = 0; j < dig; j++) {
            finDig = code % CODE_RADIX;
            code /= CODE_RADIX;
        }
        return finDig;
    }

    /**
     * Move player around room.
     *
     * @param room
     *          the array of values for the room
     * @param roomWidth
     *          width of room
     * @param roomHeight
     *          height of room
     */
    private static void move(final int[][] room, final int roomWidth,
            final int roomHeight) {
        int rand = 0;
        //handles direction
        //upper left is 0,0
        if (inpt.equalsIgnoreCase("n")
                || inpt.equalsIgnoreCase("north")
                || inpt.equalsIgnoreCase("up")) {
            if (!(currentIntY - 1 <= 0)) { //handles roomHeight movement
                if (runToggle && (currentIntY - 1 <= 0)) {
                    currentIntY--;
                } else if (runToggle && !(currentIntY - 1 <= 0)) {
                    currentIntY -= 2;
                } else {
                    currentIntY--;
                }
                //door handler
            } else if (!(currentIntY - 1 < 0)
                    && room[currentIntY - 1][currentIntX] == DOOR) {
                door(roomWidth, roomHeight);
            } else if (room[currentIntY][currentIntX] == OBJECT) {
                objectCollision(rand);
            } else { //moved into wall
                System.out.print("There's a wall there.\n");
            }
        } else if (inpt.equalsIgnoreCase("s")
                || inpt.equalsIgnoreCase("south")
                || inpt.equalsIgnoreCase("down")) {
            if (!(currentIntY + 1 >= (roomHeight - 1))) {
                if (runToggle && (currentIntY + 1 <= 0)) {
                    currentIntY++;
                } else if (runToggle && !(currentIntY + 1 <= 0)) {
                    currentIntY += 2;
                } else {
                    currentIntY++;
                }
            } else if (!(currentIntY + 1 > (roomHeight - 1))) {
                if (room[currentIntY + 1][currentIntX] == DOOR) {
                    door(roomWidth, roomHeight);
                } else {
                    System.out.print("There's a wall there.\n");
                }
            } else {
                System.out.print("There's a wall there.\n");
            }
            if (room[currentIntY][currentIntX] == OBJECT) {
                objectCollision(rand);
            }

        } else if (inpt.equalsIgnoreCase("w")
                || inpt.equalsIgnoreCase("west")
                || inpt.equalsIgnoreCase("left")) {
            if (!(currentIntX - 1 <= 0)) { //handles movement in X
                if (runToggle && (currentIntX - 1 <= 0)) {
                    currentIntX--;
                } else if (runToggle && !(currentIntX - 1 <= 0)) {
                    currentIntX -= 2;
                } else {
                    currentIntX--;
                }
            } else if (!(currentIntX - 1 < 0)
                    && room[currentIntY][currentIntX - 1] == DOOR) {
                door(roomWidth, roomHeight);
            } else {
                System.out.print("There's a wall there.\n");
            }
            if (room[currentIntY][currentIntX] == OBJECT) {
                objectCollision(rand);
            }

        } else if (inpt.equalsIgnoreCase("e")
                || inpt.equalsIgnoreCase("east")
                || inpt.equalsIgnoreCase("right")) {
            if ((currentIntX + 1) < (roomWidth - 1)) {
                if (runToggle && (currentIntX + 1 <= 0)) {
                    currentIntX++;
                } else if (runToggle && !(currentIntX + 1 <= 0)) {
                    currentIntX += 2;
                } else {
                    currentIntX++;
                }
            } else if ((currentIntX + 1 >= (roomWidth - 2))
                    && room[currentIntY][currentIntX + 1] == DOOR) {
                door(roomWidth, roomHeight);
            } else if (currentIntX + 1 > (roomWidth - 2)) {
                System.out.print("There's a wall there.\n");
            }
            if (room[currentIntY][currentIntX] == OBJECT) {
                objectCollision(rand);
            }
            //allows player to check current and adjacent tiles
        } else if (inpt.equalsIgnoreCase("search")
                || inpt.equalsIgnoreCase("look")) {
            search(room);
        } else if (inpt.equalsIgnoreCase("Rules")
                || inpt.equalsIgnoreCase("Instructions")) {
            showRules();
        } else if (inpt.equalsIgnoreCase("suicide")) { //alt escape
            System.out.println("You bash your head into the ground until"
                    + "you pass out and die.");
            for (int j = 0; j < turnCount; j++) {
                inpt = "exit";
            }
        } else if (inpt.equalsIgnoreCase("run")) {
            if (runToggle) {
                runToggle = false;
                System.out.println("You stop running");
            } else {
                runToggle = true;
                System.out.println("You start to run");
            }
        } else if (inpt.equalsIgnoreCase("walk")) {
            runToggle = false;
            System.out.println("You begin to walk");
        } else if (inpt.equalsIgnoreCase("help")
                || inpt.equalsIgnoreCase("let me out")) {
            System.out.println("Your cries echo against the cold, "
                    + "unforgiving walls. There is no one to hear them.");
        } else if (inpt.equalsIgnoreCase("debug")) { //debug values
            debug(room, roomWidth, roomHeight);
            //handles unrecognized command
        } else if (!(inpt.equalsIgnoreCase("exit"))) {
            System.out.println("What was I doing again?"
                    + "I can't remember...");
        }
        if (tempRoom == EMPTY && !(inpt.equalsIgnoreCase("exit"))) {
            System.out.println("Still in the room.");
        }
        //saves room's temp value and allows player to advance
        //without affecting that value
        if (!(inpt.equalsIgnoreCase("exit"))) {
            //resets room to value saved when tile moved onto
            room[tempIntY][tempIntX] = tempRoom;
            //sets temp value to new move
            tempRoom = room[currentIntY][currentIntX];

            room[currentIntY][currentIntX] = PLAYER;
        }
    }

    /**
     * Handles collisions with obstacles.
     *
     * @param rand
     *          random number generated for collision
     */
    private static void objectCollision(final int rand) {
        if (rand <= DESK_COLLIDE) {
            System.out.println("You tripped over a table in the "
                    + "dark.");
        } else if (rand > BOOKSHELF_COLLIDE) {
            System.out.println("You run into a bookshelf and "
                    + "something falls off and hits you in the "
                    + "head.");
            inpt = "exit";
        } else {
            System.out.println("You run into a bookshelf.");
            currentIntY++;
        }
    }

    /**
     * Search current tile.
     *
     * @param room
     *          the array of values for a room
     */
    private static void search(final int[][] room) {
        int randomSearch = (int) (Math.random() * SEARCH_BOUND) + 1;
        if (tempRoom != 1) { //checks if tile isn't default
            //checks if tile has code
            if (tempRoom == DIGIT_ONE || tempRoom == DIGIT_TWO
                    || tempRoom == DIGIT_THREE
                    || tempRoom == DIGIT_FOUR) {
                if (randomSearch >= 2) {
                    if (tempRoom == DIGIT_ONE) {
                        System.out.println("You see a number scrawled "
                                + "out on a note!\n" + getCodeDig(DIG1)
                                + "\nOn the back it says, "
                                + "\"Millennials, amirite?\"");
                    }
                    if (tempRoom == DIGIT_TWO) {
                        System.out.println("You see a number scrawled "
                                + "out on a note!\n" + getCodeDig(DIG2)
                                + "\nOn the back it says, \"All about "
                                + "those Benjamins\"");
                    }
                    if (tempRoom == DIGIT_THREE) {
                        System.out.println("You see a number scrawled "
                                + "out on a note!\n" + getCodeDig(DIG3)
                                + "\nOn the back it says, \"7 ate 9. "
                                + "Who's next?\"");
                    }
                    if (tempRoom == DIGIT_FOUR) {
                        System.out.println("You see a number scrawled "
                                + "out on a note!\n" + getCodeDig(DIG4)
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
            } else if ((room[currentIntY + 1][currentIntX] != EMPTY
                    && room[currentIntY + 1][currentIntX] != WALL)
                    || (room[currentIntY - 1][currentIntX] != EMPTY
                    && room[currentIntY - 1][currentIntX] != WALL)
                    || (room[currentIntY][currentIntX + 1] != EMPTY
                    && room[currentIntY][currentIntX + 1] != WALL)
                    || (room[currentIntY][currentIntX - 1] != EMPTY
                    && room[currentIntY][currentIntX - 1] != WALL)) {
                System.out.println("There's something nearby..."
                        + "\nCan't quite make it out.");
            } else if ((room[currentIntY + 1][currentIntX] != EMPTY
                    && (room[currentIntY + 1][currentIntX] == WALL
                    || room[currentIntY + 1][currentIntX] == OBJECT))
                    || (room[currentIntY - 1][currentIntX] != EMPTY
                    && (room[currentIntY - 1][currentIntX] == WALL
                    || room[currentIntY - 1][currentIntX] == OBJECT))
                    || (room[currentIntY][currentIntX + 1] != EMPTY
                    && (room[currentIntY][currentIntX + 1] == WALL
                    || room[currentIntY][currentIntX + 1] == OBJECT))
                    || (room[currentIntY][currentIntX - 1] != EMPTY
                    && (room[currentIntY][currentIntX - 1] == WALL
                    || room[currentIntY][currentIntX - 1] == OBJECT))) {
                int randomItemSearch = (int) (Math.random()
                        * SEARCH_BOUND) + 1;
                if (randomItemSearch < TROPHY_GET) {
                    System.out.println("Nothing nearby...");
                } else if (randomItemSearch == TROPHY_GET && trophy) {
                    System.out.println("You got a trophy. Wow.");
                    trophy = true;
                    trophyCount++;
                }
            } else {
                System.out.println("Nothing nearby...");
            }
        } else {
            System.out.println("Nothing nearby...");
        }
    }

    /**
     * Prints rules to screen.
     */
    private static void showRules() {
        System.out.println("To move one of the four directions type up,"
                + "down, left, or right. \nTo search the tile you are "
                + "on type search or look. \nYour objective is to "
                + "escape the series of rooms by gathering clues and "
                + "tools. \nThere may be some secrets hidden within "
                + "each room, finding them will increase your score.");
    }
}
