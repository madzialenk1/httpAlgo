

package com.company;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;

public class Map {
    private char[][] maze;
    private int[][] possibilitiesCounters;
    private int[][] howManyTimesVisitedCell;
    private boolean[][] isItDead;
    private int actualX;
    private int actualY;
    private int x;
    private int y;
    private int Xsize;
    private int Ysize;
    private boolean theEnd;
    private HttpClient client;
    private HttpRequest possibilitiesRequest;

    public Map() throws IOException, InterruptedException {

        client = HttpClient.newHttpClient();
        possibilitiesRequest = HttpRequest.newBuilder().uri(URI.create("http://tesla.iem.pw.edu.pl:4444/711be1fa/4/possibilities")).build();
        this.theEnd = false;
        setStartPosition();
        initMaze();
        this.x = actualX * 2 - 1;
        this.y = actualY * 2 - 1;
        possibilitiesCounters = new int[Xsize][Ysize];
        howManyTimesVisitedCell = new int[Xsize][Ysize];
        setHowManyTimesVisitedCell();
        setIsItDead();


    }

    public void fillMaze() throws IOException, InterruptedException {
        System.out.println(theEnd);

        while (this.theEnd == false) {
            go(x, y);
            counterPossibilitiesForOneCell();
            move();
            printMaze();
        }
    }

    private void counterPossibilitiesForOneCell() {

        ArrayList<String> possibilities = checkDirection();
        possibilitiesCounters[x][y] = possibilities.size();
    }

    private void setHowManyTimesVisitedCell() {
        for (int i = 0; i < Xsize; i++) {
            for (int j = 0; j < Ysize; j++) {
                howManyTimesVisitedCell[i][j] = 0;
            }
        }
    }


    private void go(int x, int y) {
        maze[x][y] = '0';
        markPossibilitiesInMaze(checkDirection(), x, y);

    }


    private void setIsItDead() {
        isItDead = new boolean[Xsize][Ysize];
        for (int i = 0; i < Xsize; i++) {
            for (int j = 0; j < Ysize; j++) {
                isItDead[i][j] = false;
            }
        }
    }


    private void initMaze() {
        setSize();
        maze = new char[Xsize][Ysize];

        for (int i = 0; i < Xsize; i++) {
            for (int j = 0; j < Ysize; j++)
                maze[i][j] = '+';
        }
    }

    private void dead() {
        if (howManyTimesVisitedCell[x][y] >= possibilitiesCounters[x][y]) {
            isItDead[x][y] = true;
        }

    }

    private void moveRight() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://tesla.iem.pw.edu.pl:4444/711be1fa/4/move/right"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = null;
        try {

            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.body()); // Moved successfully


    }

    private ArrayList<String> directionToMove() {
        ArrayList<String> list = checkDirection();
        ArrayList<String> direction = new ArrayList<>();
        String[] tab = new String[4];
        int howManyTimesVisited = 5;
        int counter = 0;

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);

            switch (s) {
                case "left":
                    if (isItDead[x][y - 2] == false) {
                        System.out.println("mamam");
                        if (howManyTimesVisitedCell[x][y - 2] == howManyTimesVisited) {
                            if (i != 0) {
                                counter++;
                                tab[counter] = "left";
                            } else {
                                tab[i] = "left";
                            }
                        }
                        if (howManyTimesVisitedCell[x][y - 2] < howManyTimesVisited) {
                            howManyTimesVisited = howManyTimesVisitedCell[x][y - 2];
                            counter = 0;
                            tab[counter] = "left";


                        }
                    }
                    break;

                case "right":
                    if (isItDead[x][y + 2] == false) {
                        System.out.println("mamam");
                        if (howManyTimesVisitedCell[x][y + 2] == howManyTimesVisited) {
                            if (i != 0) {
                                counter++;
                                tab[counter] = "right";
                            } else {
                                tab[i] = "right";
                            }
                        }

                        if (howManyTimesVisitedCell[x][y + 2] < howManyTimesVisited) {
                            howManyTimesVisited = howManyTimesVisitedCell[x][y + 2];
                            counter = 0;
                            tab[counter] = "right";


                        }
                    }
                    break;

                case "up":
                    if (isItDead[x - 2][y] == false) {
                        if (howManyTimesVisitedCell[x - 2][y] == howManyTimesVisited) {
                            if (i != 0) {
                                counter++;
                                tab[counter] = "up";
                            } else {
                                tab[i] = "up";
                            }
                        }
                        if (howManyTimesVisitedCell[x - 2][y] < howManyTimesVisited) {
                            howManyTimesVisited = howManyTimesVisitedCell[x - 2][y];
                            counter = 0;
                            tab[counter] = "up";


                        }
                    }
                    break;


                case "down":
                    if (isItDead[x + 2][y] == false) {
                        if (howManyTimesVisitedCell[x + 2][y] == howManyTimesVisited) {
                            if (i != 0) {
                                counter++;
                                tab[counter] = "down";
                            } else {
                                tab[i] = "down";
                            }
                        }

                        if (howManyTimesVisitedCell[x + 2][y] < howManyTimesVisited) {
                            howManyTimesVisited = howManyTimesVisitedCell[x + 2][y];
                            counter = 0;
                            tab[counter] = "down";


                        }
                    }
                    break;

            }
        }


        for(String s : tab){
            if(s!=null)
                direction.add(s);

        }


        if (direction.size() == 0) {
            theEnd = true;

            // dzięki temu wiemy kiedy koniec
        }

        return direction;
    }


    private void move() throws IOException, InterruptedException {
        ArrayList<String> list = directionToMove();
        Random rand = new Random();

        if (theEnd == false) {
            String wylosowana = list.get(rand.nextInt(list.size()));
            switch (wylosowana) {
                case "left":
                    if (isItDead[x][y - 2] == false) {
                        howManyTimesVisitedCell[x][y]++;
                        dead();
                        moveLeft();
                        y = y - 2;

                    }
                    break;
                case "right":

                    if (isItDead[x][y + 2] == false) {
                        howManyTimesVisitedCell[x][y]++;
                        dead();
                        moveRight();
                        y = y + 2;
                    }
                    break;
                case "up":
                    if (isItDead[x - 2][y] == false) {
                        howManyTimesVisitedCell[x][y]++;
                        dead();
                        moveUp();
                        x = x - 2;
                    }
                    break;
                case "down":
                    if (isItDead[x + 2][y] == false) {
                        howManyTimesVisitedCell[x][y]++;
                        dead();
                        moveDown();
                        x = x + 2;
                    }
                    break;
            }
        }
    }


    private void moveLeft() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://tesla.iem.pw.edu.pl:4444/711be1fa/4/move/left"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = null;
        try {

            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();

        }
        System.out.println(response.body()); // Moved successfully


    }

    private void moveUp() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://tesla.iem.pw.edu.pl:4444/711be1fa/4/move/up"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = null;
        try {

            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.body()); // Moved successfully


    }

    private void moveDown() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://tesla.iem.pw.edu.pl:4444/711be1fa/4/move/down"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = null;
        try {

            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.body()); // Moved successfully


    }


    private void printMaze() {
        for (int i = 0; i < Xsize; i++) {
            for (int j = 0; j < Ysize; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }


    private void setSize() {
        HttpRequest sizeRequest = HttpRequest.newBuilder().uri(URI.create("http://tesla.iem.pw.edu.pl:4444/711be1fa/4/size")).build();
        HttpResponse<String> size = null;
        try {
            size = client.send(sizeRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        Ysize = Integer.parseInt(size.body().split("x")[0]) * 2 + 1;
        Xsize = Integer.parseInt(size.body().split("x")[1]) * 2 + 1;

        System.out.println("Rozmiary tablicy to x=" + Xsize + "a y=" + Ysize);
    }

    private void setStartPosition() {
        HttpRequest startPositionRequest = HttpRequest.newBuilder().uri(URI.create("http://tesla.iem.pw.edu.pl:4444/711be1fa/4/startposition")).build();
        HttpResponse<String> startPosition = null;
        try {
            startPosition = client.send(startPositionRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        String[] position = startPosition.body().split(",");
        this.actualY = Integer.parseInt(position[0]);
        this.actualX = Integer.parseInt(position[1]);

        System.out.println("Pozycja startowa: x = " + actualX + " y = " + actualY);
    }


    private ArrayList<String> checkDirection() {
        HttpResponse<String> response = null;
        try {
            response = client.send(this.possibilitiesRequest,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.body());
        String[] responses = response.body().split(",");
        ArrayList<String> possibilities = new ArrayList<>();


        for (String direction : responses) {
            if (direction.split("\"")[3].charAt(0) == '0')
                possibilities.add(direction.split("\"")[1]);
        }


        return possibilities;
    }

    private void markPossibilitiesInMaze(ArrayList<String> possibilities, int x, int y) {

        for (String p :
                possibilities) {
            switch (p) {
                case "down":
                    maze[x + 2][y] = '0';
                    maze[x + 1][y] = '0';
                    break;
                case "left":
                    maze[x][y - 2] = '0';
                    maze[x][y - 1] = '0';
                    break;
                case "right":
                    maze[x][y + 2] = '0';
                    maze[x][y + 1] = '0';
                    break;
                case "up":
                    maze[x - 2][y] = '0';
                    maze[x - 1][y] = '0';
                    break;
            }
        }
    }


}



