package org.example;

import java.util.Scanner;
import java.util.Random;

class Complex {
    double real;double imag;
    public Complex(double real, double imag) {this.real = real;this.imag = imag;
    }
    public Complex add(Complex other) {
        return new Complex(this.real + other.real, this.imag + other.imag);
    }
    public Complex multiply(Complex other) {
        return new Complex(this.real * other.real - this.imag * other.imag,
                this.real * other.imag + this.imag * other.real);
    }
    public double magnitude() {
        return Math.sqrt(real * real + imag * imag);
    }

    @Override
    public String toString() {
        return real + " + " + imag + "i";
    }
}
class Qubit {
    private Complex amplitude0;private Complex amplitude1;
    public Qubit() {
        this.amplitude0 = new Complex(1, 0);  // |0⟩ durumu
        this.amplitude1 = new Complex(0, 0);  // |1⟩ durumu
    }
    public void applyGate(Gate gate) {
        Complex newAmplitude0 = gate.apply(this.amplitude0, 0);
        Complex newAmplitude1 = gate.apply(this.amplitude1, 1);
        this.amplitude0 = newAmplitude0;
        this.amplitude1 = newAmplitude1;
    }
    public void setInitialState(int value) {
        if (value == 0) {
            this.amplitude0 = new Complex(1, 0);  // |0⟩ durumu
            this.amplitude1 = new Complex(0, 0);  // |1⟩ durumu
        } else {
            this.amplitude0 = new Complex(0, 0);  // |0⟩ durumu
            this.amplitude1 = new Complex(1, 0);  // |1⟩ durumu
        }
    }
    public void reset() {
        this.amplitude0 = new Complex(1, 0);
        this.amplitude1 = new Complex(0, 0);
    }
    public int measure() {
        double probability0 = amplitude0.magnitude() * amplitude0.magnitude();
        Random rand = new Random();
        return rand.nextDouble() < probability0 ? 0 : 1;
    }

    @Override
    public String toString() {
        return "|0⟩: " + amplitude0 + ", |1⟩: " + amplitude1;
    }
}
interface Gate {
    Complex apply(Complex amplitude, int qubitIndex);
}
class HadamardGate implements Gate {
    @Override
    public Complex apply(Complex amplitude, int qubitIndex) {
        double factor = 1 / Math.sqrt(2);
        if (qubitIndex == 0) {
            return amplitude.multiply(new Complex(factor, 0)).add(amplitude.multiply(new Complex(factor, 0)));
        } else {
            return amplitude.multiply(new Complex(factor, 0)).add(amplitude.multiply(new Complex(-factor, 0)));
        }
    }
}

class ControlledNotGate implements Gate {
    private final Qubit controlQubit;
    public ControlledNotGate(Qubit controlQubit) {
        this.controlQubit = controlQubit;
    }
    @Override
    public Complex apply(Complex amplitude, int qubitIndex) {
        return controlQubit.measure() == 1 ? new Complex(-amplitude.real, -amplitude.imag) : amplitude;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Lütfen iki basamaklı bir sayı girin: ");
        int inputValue = scanner.nextInt();

        String binaryString = Integer.toBinaryString(inputValue);

        int numQubits = binaryString.length();
        Qubit[] qubits = new Qubit[numQubits];

        for (int i = 0; i < numQubits; i++) {
            qubits[i] = new Qubit();
            int bitValue = Character.getNumericValue(binaryString.charAt(i));
            qubits[i].setInitialState(bitValue);
        }

        System.out.println("Başlangıç Durumları:");
        for (int i = 0; i < numQubits; i++) {
            System.out.println("Qubit " + (i + 1) + ": " + qubits[i]);
        }

        Gate hadamardGate = new HadamardGate();
        for (int i = 0; i < numQubits; i++) {
            qubits[i].applyGate(hadamardGate);
        }

        System.out.println("\nHadamard Kapısı Sonrası:");
        for (int i = 0; i < numQubits; i++) {
            System.out.println("Qubit " + (i + 1) + ": " + qubits[i]);
        }

        int[] results = new int[numQubits];
        System.out.println("\nÖlçüm Sonuçları:");
        for (int i = 0; i < numQubits; i++) {
            results[i] = qubits[i].measure();
            System.out.println("Qubit " + (i + 1) + ": " + results[i]);
        }

        StringBuilder resultBinary = new StringBuilder();
        for (int result : results) {
            resultBinary.append(result);
        }

        int resultDecimal = Integer.parseInt(resultBinary.toString(), 2);
        System.out.println("\nSonuç (Binary): " + resultBinary.toString());
        System.out.println("Sonuç (Decimal): " + resultDecimal);
    }
}
