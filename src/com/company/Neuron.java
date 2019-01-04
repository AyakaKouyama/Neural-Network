package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Neuron {

    private Connection biasConnection;
    private double output;
    private List<Connection> connections;
    private HashMap<Integer, Connection> connectionHashMap;
    final public int id;
    static int counter = 0;
    private Type type;


    public Neuron(Type type) {
        connections = new ArrayList<>();
        connectionHashMap = new HashMap<>();
        id = counter;
        counter++;
        this.type = type;
    }

    public void calculateOutput() {
        double sum = 0;

        for (var connection : connections) {
            Neuron leftNeuron = connection.getLeftNeuron();
            double weight = connection.getWeight();
            double neuronOutput = leftNeuron.getOutput();

            sum += weight * neuronOutput;
        }

        if(biasConnection != null)
            sum += biasConnection.getWeight();

        output = Functions.sigmoid(sum);

        if(type == type.Output)
            output = sum;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double value){
        output = value;
    }

    public void addConection(Layer layer) {
        for (var neuron : layer.getNeurons()) {
            Connection connection = new Connection(neuron, this);
            connections.add(connection);
            connectionHashMap.put(neuron.id, connection);
        }
    }

    public Connection getConnection(int index){
        return connectionHashMap.get(index);
    }

    public List<Connection> getAllConnections(){
        return connections;
    }

    public void addBiasConnection(Neuron neuron){
        Connection biasConnection = new Connection(neuron, this);
        biasConnection = biasConnection;
        connections.add(biasConnection);
    }

}
