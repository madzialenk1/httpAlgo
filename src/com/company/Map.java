package com.company;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
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
    private String httpRequest;
    private HttpRequest possibilitiesRequest;

    public Map() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        httpRequest = "http://tesla.iem.pw.edu.pl:4444/a732410b/4/";
        possibilitiesRequest = HttpRequest.newBuilder().uri(URI.create(httpRequest + "possibilities")).build();
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

        while (!this.theEnd) {
            markPossibilitiesInMaze(checkPossibilities(), x, y);
            possibilitiesCounters[x][y] = checkPossibilities().size();
            move();
            //printMaze();
        }
    }

    private void setHowManyTimesVisitedCell() {
        for (int i = 0; i < Xsize; i++) {
            for (int j = 0; j < Ysize; j++) {
                howManyTimesVisitedCell[i][j] = 0;
            }
        }
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

    private ArrayList<String> directionToMove() {
        ArrayList<String> list = checkPossibilities();
        ArrayList<String> direction = new ArrayList<>();
        String[] tab = new String[4];
        int howManyTimesVisited = 5;
        int counter = 0;

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);

            switch (s) {
                case "left":
                    if (!isItDead[x][y - 2]) {
                        if (howManyTimesVisitedCell[x][y - 2] == howManyTimesVisited) {
                            if (i != 0) {
                                tab[++counter] = "left";
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
                    if (!isItDead[x][y + 2]) {
                        if (howManyTimesVisitedCell[x][y + 2] == howManyTimesVisited) {
                            if (i != 0) {
                                tab[++counter] = "right";
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
                    if (!isItDead[x - 2][y]) {
                        if (howManyTimesVisitedCell[x - 2][y] == howManyTimesVisited) {
                            if (i != 0) {
                                tab[++counter] = "up";
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
                    if (!isItDead[x + 2][y]) {
                        if (howManyTimesVisitedCell[x + 2][y] == howManyTimesVisited) {
                            if (i != 0) {
                                tab[++counter] = "down";
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
            theEnd = true;  // dzięki temu wiemy kiedy koniec
        }

        return direction;
    }

    private void move() throws IOException, InterruptedException {
        ArrayList<String> list = directionToMove();
        Random rand = new Random();

        if (!theEnd) {
            String direction = list.get(rand.nextInt(list.size()));
            howManyTimesVisitedCell[x][y]++;
            move(direction);
            dead();
            switch (direction) {
                case "left":
                    if (!isItDead[x][y - 2]) {
                        y = y - 2;
                    }
                    break;
                case "right":
                    if (!isItDead[x][y + 2]) {
                        y = y + 2;
                    }
                    break;
                case "up":
                    if (!isItDead[x - 2][y]) {
                        x = x - 2;
                    }
                    break;
                case "down":
                    if (!isItDead[x + 2][y]) {
                        x = x + 2;
                    }
                    break;
            }
        }
    }

    private void move(String direction) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpRequest + "move/" + direction))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        //System.out.println(response.body()); // Moved successfully
    }

    private void setSize() {
        HttpRequest sizeRequest = HttpRequest.newBuilder().uri(URI.create(httpRequest + "size")).build();
        HttpResponse<String> size = null;
        try {
            size = client.send(sizeRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        Ysize = Integer.parseInt(size.body().split("x")[0]) * 2 + 1;
        Xsize = Integer.parseInt(size.body().split("x")[1]) * 2 + 1;

        System.out.println("Rozmiary tablicy to x = " + Xsize + " y = " + Ysize);
    }

    private void setStartPosition() {
        HttpRequest startPositionRequest = HttpRequest.newBuilder().uri(URI.create(httpRequest + "startposition")).build();
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

    private ArrayList<String> checkPossibilities() {
        HttpResponse<String> response = null;
        try {
            response = client.send(this.possibilitiesRequest,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        //System.out.println(response.body());
        String[] responses = response.body().split(",");
        ArrayList<String> possibilities = new ArrayList<>();

        for (String direction : responses) {
            if (direction.split("\"")[3].charAt(0) == '0')
                possibilities.add(direction.split("\"")[1]);
        }

        return possibilities;
    }

    private void markPossibilitiesInMaze(ArrayList<String> possibilities, int x, int y) {
        maze[x][y] = '0';

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

    private void printMaze() {
        System.out.println(mazeToString());
    }

    private String mazeToString(){
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < Xsize; i++){
            for (int j =0; j < Ysize; j++){
                sb.append(maze[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    void saveToFile(String fileName) throws IOException {
        FileWriter fileOut = null;
        try{
            fileOut = new FileWriter(fileName);

            fileOut.write(mazeToString());

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (fileOut != null)
                fileOut.close();
        }
    }

    void sendOnServer(String fileName) throws FileNotFoundException {
        try{
            saveToFile(fileName);
        }catch (IOException e){
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(httpRequest + "upload")).POST(HttpRequest.BodyPublishers.ofFile(Paths.get(fileName))).build();

        HttpResponse response = null;
        try {
            response = client.send(request,HttpResponse.BodyHandlers.ofString());
        }catch (InterruptedException | IOException e){
            e.printStackTrace();
        }
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}