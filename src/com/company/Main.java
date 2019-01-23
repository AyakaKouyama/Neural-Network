package com.company;

import java.util.Random;

public class Main {

    public static double max(double[][] input) {
        double max = input[0][0];
        for (int i = 0; i < input.length; i++) {
            if (input[i][0] > max) {
                max = input[i][0];
            }
        }
        return max;
    }

    public static double min(double[][] input) {
        double min = input[0][0];
        for (int i = 0; i < input.length; i++) {
            if (input[i][0] < min) {
                min = input[i][0];
            }
        }
        return min;
    }

    public static void main(String[] args) {

        double[][] input = new double[80][2];
        double[][] output = new double[80][1];

        Random random = new Random();

        int x = 0;
        for (double i = 0; i < input.length; i++) {
            input[x][0] = random.nextDouble() * 100;
            output[x][0] = 1/Math.sqrt(input[x][0]);
            x++;
        }

        int inputs = Integer.parseInt(args[0]);
        int hiddenLayers = Integer.parseInt(args[1]);
        int neuronsInHiddenLayer = Integer.parseInt(args[2]);
        int outputs = Integer.parseInt(args[3]);

        long start = System.nanoTime();
        Network network = new Network(inputs, hiddenLayers, neuronsInHiddenLayer, outputs, input, output, args[4], args[5]);
        network.learn();
       // System.out.println("MAX: " + max(input) + "   MIN: " + min(input));

        long stop = System.nanoTime() - start;
       // System.out.println(stop * 10e-10);

        network.testInput();
    }
}
