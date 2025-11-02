package org.yeah;

import org.yeah.utils.GraphGenerator;

public class GenerateData {
    public static void main(String[] args) {
        System.out.println("Generating test datasets...");
        GraphGenerator.generateAllDatasets();
        System.out.println("All datasets generated successfully!");
    }
}