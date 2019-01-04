package com.company;

public class Main {

    public static void main(String[] args) {

        //double input[][] = { { 1, 0}, { 1.5, 0}, { 1.7, 0}};
        //double output[][] = { { 1 }, { 1.22 }, { 1.3}};

       double[][] input = new double[100][2];
       double[][] output = new double[100][1];

       int x = 0;
       for(double i = 0; i<50; i+=0.5){
          input[x][0] = i;
          output[x][0] = Math.sqrt(i);
          x++;
       }

        Network network = new Network(1, 1, 10, 1, input, output);
        network.learn();
    }
}
