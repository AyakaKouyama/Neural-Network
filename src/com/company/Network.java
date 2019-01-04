package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Network {

    Layer inputLayer;
    Layer outputLayer;
    List<Layer> hiddenLayer;
    Neuron bias;

    double[][] input;
    double[][] output;
    double epsilon = 0.00000000001;
    double learningRate = 0.3;
    double momentum = 0.7;
    double minError = 0.001;

    int hiddenLayers;


    public Network(int inputs, int hiddenLayers, int neuronsInHiddenLayer, int outputs, double[][] input, double[][] output) {

        inputLayer = new Layer();
        outputLayer = new Layer();
        hiddenLayer = new ArrayList<>();
        bias = new Neuron(Type.Hidden);

        this.input = input;
        this.output = output;
        this.hiddenLayers = hiddenLayers;

        for (int i = 0; i < inputs; i++) {
            inputLayer.addNeuron(new Neuron(Type.Input));
        }

        for (int i = 0; i < hiddenLayers; i++) {
            hiddenLayer.add(new Layer());
            for (int j = 0; j < neuronsInHiddenLayer; j++) {
                Neuron neuron = new Neuron(Type.Hidden);
                hiddenLayer.get(i).addNeuron(neuron);
                if (i == 0)
                    neuron.addConection(inputLayer);
                else
                    neuron.addConection(hiddenLayer.get(i - 1));

                neuron.addBiasConnection(bias);
            }
        }

        for (int i = 0; i < outputs; i++) {
            Neuron neuron = new Neuron(Type.Output);
            outputLayer.addNeuron(neuron);
            neuron.addConection(hiddenLayer.get(hiddenLayers - 1));
            neuron.addBiasConnection(bias);
        }

        for (int i = 0; i < hiddenLayers; i++) {
            for (var neuron : hiddenLayer.get(i).getNeurons()) {
                List<Connection> connections = neuron.getAllConnections();

                for (Connection connection : connections) {
                    double newWeight = randomNumber();
                    connection.setWeight(newWeight);
                }
            }
        }
        for (Neuron neuron : outputLayer.getNeurons()) {
            List<Connection> connections = neuron.getAllConnections();

            for (Connection conn : connections) {
                double newWeight = randomNumber();
                conn.setWeight(newWeight);
            }
        }
    }

    private double randomNumber() {
        Random random = new Random();
        return random.nextDouble() * 2 - 1;
    }

    public void setInput(double[] input) {
        for (int i = 0; i < inputLayer.getNeurons().size(); i++) {
            inputLayer.getNeurons().get(i).setOutput(input[i]);
        }
    }

    public double[] getOutput() {
        double[] outputs = new double[outputLayer.getNeurons().size()];
        for (int i = 0; i < outputLayer.getNeurons().size(); i++)
            outputs[i] = outputLayer.getNeurons().get(i).getOutput();
        return outputs;
    }

    public void calculateOutputs() {
        for (int i = 0; i < hiddenLayers; i++) {
            for (var neuron : hiddenLayer.get(i).getNeurons())
                neuron.calculateOutput();
        }

        for (var neuron : outputLayer.getNeurons())
            neuron.calculateOutput();
    }

    public void backpropagation(double output[]) {

        //for (int i = 0; i < output.length; i++) {
          //  double d = output[i];
           // if (d < 0 || d > 1) {
              //  if (d < 0)
              //      output[i] = 0 + epsilon;
               // else
              //      output[i] = 1 - epsilon;
           // }
        //}

        int i = 0;
        for (var neuron : outputLayer.getNeurons()) {
            List<Connection> connections = neuron.getAllConnections();

            for (Connection connection : connections) {
                double ak = neuron.getOutput();
                double ai = connection.getLeftNeuron().getOutput();
                double desiredOutput = output[i];

                double partialDerivative = -Functions.sigmoidDeriviate(ak) * ai * (desiredOutput - ak);
                //double partialDerivative = -1 * ai * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = connection.getWeight() + deltaWeight;
                connection.setDelta(deltaWeight);
                connection.setWeight(newWeight + momentum * connection.getPrevDelta());
                connection.setWeight(newWeight);
            }
            i++;
        }

        for (int j = 0; j < hiddenLayers; j++) {
            for (var neuron : hiddenLayer.get(j).getNeurons()) {
                List<Connection> connections = neuron.getAllConnections();
                for (Connection connection : connections) {
                    double aj = neuron.getOutput();
                    double ai = connection.getLeftNeuron().getOutput();
                    double sumKoutputs = 0;
                    int k = 0;
                    for (var outputNeuron : outputLayer.getNeurons()) {
                        double wjk = outputNeuron.getConnection(neuron.id).getWeight();
                        double desiredOutput = output[k];
                        double ak = outputNeuron.getOutput();
                        k++;
                        sumKoutputs = sumKoutputs + (-(desiredOutput - ak) * Functions.sigmoidDeriviate(ak) * wjk);
                    }

                    double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                    double deltaWeight = -learningRate * partialDerivative;
                    double newWeight = connection.getWeight() + deltaWeight;
                    connection.setDelta(deltaWeight);
                    connection.setWeight(newWeight + momentum * connection.getPrevDelta());
                }
            }
        }
    }

    public void learn() {
        int i;
        double error = 1;
        double[] currentOutput = new double[outputLayer.getNeurons().size()];
        for (i = 0; error > minError; i++) {
            error = 0;
            for (int p = 0; p < input.length; p++) {
                setInput(input[p]);
                calculateOutputs();
                currentOutput = getOutput();
                // resultOutputs[p] = output;
                backpropagation(output[p]);

                for (int j =  0 ; j < output[p].length; j++) {
                    double err = Math.pow(currentOutput[j] - output[p][j], 2);
                    error += err;
                }
            }
            System.out.println(error);
        }
        print();
    }

    public void print(){
        double[] currentOutput = new double[outputLayer.getNeurons().size()];

        for(int i = 0; i<input.length; i++){
            setInput(input[i]);
            calculateOutputs();
            currentOutput = getOutput();

            for(int j = 0; j<currentOutput.length; j++){
                System.out.println(inputLayer.getNeurons().get(0).getOutput() + "  "  + "   " + currentOutput[j]);
            }
        }
    }




}
