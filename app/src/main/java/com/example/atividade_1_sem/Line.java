package com.example.atividade_1_sem;

public class Line {
    public String origin;
    public String target;
    public String start;
    public String end;
    public String interval;
    public Coord originCoords;
    public Coord targetCoords;

    public static class Coord {
        public double lat;
        public double lon;
    }
}
