package task.steady_heat_transfer.algorithm;

import java.util.ArrayList;
import java.util.List;

public class FiniteDifference {
    private final double STEP;
    private final int SIZE;
    private double[] xValues;
    private double[] values;
    private double[] functions;
    private final Data.BoundaryConditions conditions;
    private final boolean isPValueSeek;

    public FiniteDifference(int size, double[] v, Data.BoundaryConditions conditions, boolean isSeekPValue) {
        this.SIZE = size;
        this.STEP = (Data.RIGHT_BORDER - Data.LEFT_BORDER) / (size-1);
        this.xValues = new double[SIZE];
        this.values = new double[SIZE];
        this.functions = v;
        this.conditions = conditions;
        this.isPValueSeek = isSeekPValue;
    }

    public List<double[]> start() {
        List<double[]> resultList = new ArrayList<>(SIZE);
        finiteDifferenceMethod();

        resultList.add(xValues);
        resultList.add(values);

        return resultList;
    }

    private void finiteDifferenceMethod() {
        for (int i = 0; i < SIZE; i++) {
            xValues[i] = Data.LEFT_BORDER + i*STEP;
        }

        double[][] coefficientMatrix = new double[SIZE][SIZE];
        double[] valuesOfEquations = new double[SIZE];

        coefficientMatrix[0][0] = conditions.ALPHA_0 - conditions.ALPHA_1/STEP;
        coefficientMatrix[0][1] = conditions.ALPHA_1/STEP;
        coefficientMatrix[SIZE-1][SIZE - 2] = -conditions.BETA_1/STEP;
        coefficientMatrix[SIZE-1][SIZE-1] = conditions.BETA_0 + conditions.BETA_1/STEP;

        for(int i = 1; i < SIZE-1; i++) {
            coefficientMatrix[i][i-1] = Data.k(xValues[i]) - STEP*Data.kPrime(xValues[i])/2;
            coefficientMatrix[i][i] = -2*Data.k(xValues[i]);
            coefficientMatrix[i][i+1] = Data.k(xValues[i]) + STEP*Data.kPrime(xValues[i])/2;
        }

        valuesOfEquations[0] = conditions.A;
        valuesOfEquations[SIZE-1] = conditions.B;

        for(int i = 1; i < SIZE-1; i++) {
            if(isPValueSeek) {
                valuesOfEquations[i] = Math.pow(STEP,2)* (Data.temperatureDistribution(xValues[i])-functions[i]);
            }
            else {
                valuesOfEquations[i] = -Math.pow(STEP,2)*(Data.f(xValues[i]) + functions[i]);
            }
        }

        tridiagonalMatrixAlgorithm(coefficientMatrix, valuesOfEquations);
    }

    private void tridiagonalMatrixAlgorithm(double[][] matrix, double[] valuesOfEquations) {
        double[] alpha = new double[SIZE];
        double[] beta = new double[SIZE];

        alpha[0] = -matrix[0][1] / matrix[0][0];
        beta[0] = valuesOfEquations[0] / matrix[0][0];

        for (int i = 1; i < SIZE-1; i++) {
            double denominator = matrix[i][i] + matrix[i][i-1] * alpha[i-1];
            if (denominator == 0.0) {
                throw new ArithmeticException("Division by zero detected in tridiagonal matrix.");
            }
            alpha[i] = -matrix[i][i + 1] / denominator;
            beta[i] = (valuesOfEquations[i] - matrix[i][i - 1] * beta[i - 1]) / denominator;
        }

        double denominator = matrix[SIZE-1][SIZE-1] + matrix[SIZE-1][SIZE-2] * alpha[SIZE-2];
        if (denominator == 0.0) {
            throw new ArithmeticException("Division by zero detected in tridiagonal matrix.");
        }
        beta[SIZE-1] = (valuesOfEquations[SIZE-1] - matrix[SIZE-1][SIZE-2] * beta[SIZE-2]) / denominator;
        values[SIZE-1] = beta[SIZE-1];

        for (int i = SIZE - 2; i >= 0; i--) {
            values[i] = alpha[i] * values[i + 1] + beta[i];
        }

    }
}
