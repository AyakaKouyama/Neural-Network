package com.company;

public class Connection {

    private double weight;
    private double delta;
    private Neuron leftNeuron;
    private Neuron rightNeuron;

    public Connection(Neuron from, Neuron to){
        this.leftNeuron = from;
        this.rightNeuron = to;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double value){
        weight = value;
    }

    public void setDelta(double value){
        delta = value;
    }

    public double getDelta(){
        return delta;
    }

    public Neuron getLeftNeuron(){
        return leftNeuron;
    }

    public Neuron getRightNeuron(){
        return rightNeuron;
    }

}
