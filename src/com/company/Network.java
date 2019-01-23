package com.company;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Network {

    private Layer inputLayer;
    private Layer outputLayer;
    private List<Layer> hiddenLayer;

    private String testFile;
    private String learningFile;

    private double[][] input;
    private double[][] output;
    private double[][] testInput;
    private double[][] testOutput;

    private double learningRate = 0.1;
    private double minError = 0.01;

    private int hiddenLayers;


    public Network(int inputs, int hiddenLayers, int neuronsInHiddenLayer, int outputs, double[][] input, double[][] output, String testFile, String learningFile) {

        inputLayer = new Layer();
        outputLayer = new Layer();
        hiddenLayer = new ArrayList<>();
        Neuron bias = new Neuron(Type.Hidden);

        this.testFile = testFile;
        this.learningFile = learningFile;
        this.hiddenLayers = hiddenLayers;

        this.input = new double[(int) (input.length * 0.75)][2];
        this.output = new double[(int) (input.length * 0.75)][1];
        this.testInput = new double[(int) (input.length * 0.25)][2];
        this.testOutput = new double[(int) (input.length * 0.25)][1];

        int x = 0;
        for (int i = 0; i < input.length; i++) {
            if (i < (int) (input.length * 0.75)) {
                this.input[i][0] = input[i][0];
                this.output[i][0] = output[i][0];
            } else {
                this.testInput[x][0] = input[i][0];
                this.testOutput[x][0] = output[i][0];
                x++;
            }
        }

        for (int i = 0; i < inputs; i++) {
            inputLayer.addNeuron(new Neuron(Type.Input));
        }

        for (int i = 0; i < hiddenLayers; i++) {
            hiddenLayer.add(new Layer());
            for (int j = 0; j < neuronsInHiddenLayer; j++) {
                Neuron neuron = new Neuron(Type.Hidden);
                hiddenLayer.get(i).addNeuron(neuron);
                if (i == 0)
                    neuron.addConnection(inputLayer);
                else
                    neuron.addConnection(hiddenLayer.get(i - 1));

                neuron.addBiasConnection(bias);
            }
        }

        for (int i = 0; i < outputs; i++) {
            Neuron neuron = new Neuron(Type.Output);
            outputLayer.addNeuron(neuron);
            neuron.addConnection(hiddenLayer.get(hiddenLayers - 1));
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

    private void setInput(double[] input) {
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

    private void calculateOutputs() {
        for (int i = 0; i < hiddenLayers; i++) {
            for (var neuron : hiddenLayer.get(i).getNeurons())
                neuron.calculateOutput();
        }

        for (var neuron : outputLayer.getNeurons())
            neuron.calculateOutput();
    }

    private void backpropagation(double output[]) {

        int i = 0;
        for (var neuron : outputLayer.getNeurons()) {
            List<Connection> connections = neuron.getAllConnections();

            for (Connection connection : connections) {
                double ak = neuron.getOutput();
                double ai = connection.getLeftNeuron().getOutput();
                double desiredOutput = output[i];

                double partialDerivative = -Functions.sigmoidDeriviate(ak) * ai * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = connection.getWeight() + deltaWeight;
                connection.setDelta(Functions.sigmoidDeriviate(ak) * -(desiredOutput - ak));
                connection.setWeight(newWeight);
            }
            i++;
        }
        for (int j = hiddenLayers - 1; j >= 0; j--) {
            for (var neuron : hiddenLayer.get(j).getNeurons()) {
                List<Connection> connections = neuron.getAllConnections();
                for (Connection connection : connections) {
                    double aj = neuron.getOutput();
                    double ai = connection.getLeftNeuron().getOutput();
                    double sum = 0;

                    if (j == hiddenLayers - 1) {
                        for (var outputNeuron : outputLayer.getNeurons()) {
                            double wjk = outputNeuron.getConnection(neuron.id).getWeight();
                            sum += outputNeuron.getConnection(neuron.id).getDelta() * wjk;
                        }
                    } else {
                        for (var outputNeuron : hiddenLayer.get(j + 1).getNeurons()) {
                            double wjk = outputNeuron.getConnection(neuron.id).getWeight();
                            sum += outputNeuron.getConnection(neuron.id).getDelta() * wjk;
                        }
                    }

                    double partialDerivative = aj * (1 - aj) * ai * sum;
                    double deltaWeight = -learningRate * partialDerivative;
                    double newWeight = connection.getWeight() + deltaWeight;
                    connection.setDelta(aj * (1 - aj) * sum);
                    connection.setWeight(newWeight);
                }
            }
        }
    }

    public void learn() {
        PrintWriter testFile = null;
        PrintWriter learningFile = null;
        try {
            testFile = new PrintWriter(this.testFile, "UTF-8");
            learningFile = new PrintWriter(this.learningFile, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int i;
        double error = 1;
        double learningSetError = 0;
        double[] currentOutput;

        for (i = 0; error > minError; i++) {
            error = 0;
            learningSetError = 0;

            for (int p = 0; p < input.length; p++) {
                setInput(input[p]);
                calculateOutputs();
                currentOutput = getOutput();
                backpropagation(output[p]);

                for (int j = 0; j < output[p].length; j++) {
                    double err = Math.pow(currentOutput[j] - output[p][j], 2);
                    learningSetError += err;
                }

                for (int k = 0; k < testInput.length; k++) {
                    for (int j = 0; j < output[k].length; j++) {
                        setInput(testInput[k]);
                        calculateOutputs();
                        currentOutput = getOutput();
                        double err = Math.pow(currentOutput[j] - testOutput[k][j], 2);
                        error += err;
                    }
                }

            }

            learningSetError /= input.length;
            error /= testInput.length * input.length;

            System.out.println(error);

           // testFile.println(error);
           // learningFile.println(learningSetError);
        }

        System.out.println("Epoch: " + i);
       // System.out.println(learningSetError + "   " + error);
        pointsXY();

        testFile.close();
        learningFile.close();
    }


    public void pointsXY() {
        double[] currentOutput;

        for (double i = 0; i < 100; i += 0.1) {
            input[0][0] = i;
            setInput(input[0]);
            calculateOutputs();
            currentOutput = getOutput();
            System.out.println(i + "    " + currentOutput[0]);
        }

    }

    public void testInput() {
        Scanner scanner = new Scanner(System.in);
        double[][] inputArr = new double[1][2];
        double[] currentOutput;
        String input;
        double numericInput;

        do {
            input = scanner.nextLine();
            numericInput = Double.parseDouble(input);
            inputArr[0][0] = numericInput;

            setInput(inputArr[0]);
            calculateOutputs();
            currentOutput = getOutput();

            System.out.println("Your input:   " + input + "   Sqrt(your input):   " + 1/Math.sqrt(numericInput) + "  Calculated output:  " + currentOutput[0] + " Error in %:   " + ((Math.abs(Math.sqrt(numericInput) - currentOutput[0])) / Math.sqrt(numericInput)) * 100);

        } while (Integer.parseInt(input) != -1);
    }


}
