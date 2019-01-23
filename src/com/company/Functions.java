package com.company;

public class Functions {

    public static double sigmoid(double value){
        return 1.0 / (1.0 +  (Math.exp(-value)));
    }

    public static double sigmoidDeriviate(double value){
        return sigmoid(value) * (1 - sigmoid(value));
    }

    public static double linear(double value){
        return value;
    }

    public  static double linearDerivative(double value){
        return 1;
    }

}
