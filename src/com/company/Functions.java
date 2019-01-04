package com.company;

public class Functions {

    public static double sigmoid(double value){
        return 1.0 / (1.0 +  (Math.exp(-value)));
        //return (Math.exp(value) - Math.exp(-value))/(Math.exp(value) + Math.exp(-value));
       // if (value >= 0)
        //    return value;
       // else
       //     return value / 20;
    }

    public static double sigmoidDeriviate(double value){
        return sigmoid(value) * (1 - sigmoid(value));
       // return 1 - Math.pow(sigmoid(value), 2);
        //return 0.05 * value;
    }

    public static double linear(double value){
        return value;
    }

    public  static double linearDerivative(double value){
        return 1;
    }

}
