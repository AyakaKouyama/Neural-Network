package com.company;

import java.util.ArrayList;
import java.util.List;

public class Layer{
    private List<Neuron> neurons;

    public Layer(){
        neurons = new ArrayList<>();
    }

    public void addNeuron(Neuron value){
        neurons.add(value);
    }

    public List<Neuron> getNeurons(){
        return neurons;
    }
}