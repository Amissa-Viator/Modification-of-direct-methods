package task.steady_heat_transfer.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradientMethod {
    private static final boolean FRAGMENTATION = false;
    private final double STEP;
    private final int SIZE;
    private double alpha;
    private final double EPSILON;
    private double[] gradient, xValues, uValues, pValues, vValues;
    private List<Double> functionalList;
    private final int MAX_OF_ATTEMPTS = 100000;
    private final double MULTIPLIER = 0.5;
    private double initialAlpha;
    private int numberOfIterations;

    public GradientMethod(double epsilon, int size, double alpha, double[] v) {
        this.SIZE = size;
        this.STEP = (Data.RIGHT_BORDER - Data.LEFT_BORDER) / (size-1);
        this.EPSILON = epsilon;
        this.gradient = new double[SIZE];
        this.xValues = new double[SIZE];
        this.uValues = new double[SIZE];
        this.pValues = new double[SIZE];
        this.functionalList = new ArrayList<>();
        this.initialAlpha = alpha;
        this.vValues = Arrays.copyOf(v, v.length);
    }

    public GradientMethod(double epsilon, int size, double alpha, double[] v, double step) {
        this.SIZE = size;
        this.STEP = step;
        this.EPSILON = epsilon;
        this.gradient = new double[SIZE];
        this.xValues = new double[SIZE];
        this.uValues = new double[SIZE];
        this.pValues = new double[SIZE];
        this.functionalList = new ArrayList<>();
        this.initialAlpha = alpha;
        this.vValues = Arrays.copyOf(v, v.length);
    }

    public void start() {
        conditionalGradientMethod();
        //gradientMethod();
    }

    private void calculateGradient(double[] value) {
        for(int i = 0; i < SIZE; i++) {
            gradient[i] = 2*pValues[i];
        }
    }

    private void calculateValues(double[] values) {
        FiniteDifference uSolver = new FiniteDifference(SIZE, values, Data.U_BOUNDARY_CONDITIONS, false);
        List<double[]> uResult = uSolver.start();
        xValues = uResult.get(0);
        uValues = uResult.get(1);

        FiniteDifference pSolver = new FiniteDifference(SIZE, uValues, Data.P_BOUNDARY_CONDITIONS, true);
        List<double[]> pResult = pSolver.start();
        pValues = pResult.get(1);
    }

    private double findOptimalAlpha(double[] currentPoint, double[] directionPoint) {
        double bestAlpha = 0.0;
        double minFunctional = Double.MAX_VALUE;
        double testAlpha;
        double[] testPoint = new double[SIZE];
        double stepAlpha = 0.001;

        for (testAlpha = 0.0; testAlpha <= 1.0; testAlpha += stepAlpha) {
            for (int i = 0; i < SIZE; i++) {
                testPoint[i] = currentPoint[i] + testAlpha * (directionPoint[i] - currentPoint[i]);
            }

            calculateValues(testPoint);
            double fValue = Data.methodSimpson(xValues, uValues, STEP);

            if (fValue < minFunctional) {
                minFunctional = fValue;
                bestAlpha = testAlpha;
            }
        }

        return bestAlpha;
    }

    private void calculateNextPoint(double[] basePoint, double[] directionPoint, double alpha, double[] resultPoint) {
        for (int i = 0; i < SIZE; i++) {
            resultPoint[i] = basePoint[i] + alpha * (directionPoint[i] - basePoint[i]);
        }
    }

    private void conditionalGradientMethod() {
        int count = 0;
        double[] nextPoint = new double[SIZE];
        double[] optSolution = new double[SIZE];
        double deviation;

        do {
            if(count >= MAX_OF_ATTEMPTS) {
                System.err.println("Too many attempts were used");
                break;
            }
            alpha = initialAlpha;

            calculateValues(vValues);
            calculateGradient(vValues);
            double functionalValue = Data.methodSimpson(xValues, uValues, STEP);
            functionalList.add(functionalValue);

            for (int i = 0; i < SIZE; i++) {
                optSolution[i] = (gradient[i] > 0) ? Data.V_MIN : Data.V_MAX;
            }

            if(FRAGMENTATION) {
                calculateNextPoint(vValues, optSolution, alpha, nextPoint);
                calculateValues(nextPoint);
                calculateGradient(nextPoint);
                double nextFunctionValue = Data.methodSimpson(xValues, uValues, STEP);

                while (nextFunctionValue >= functionalValue && alpha > 1e-30) {
                    alpha *= MULTIPLIER;
                    calculateNextPoint(vValues, optSolution, alpha, nextPoint);
                    calculateValues(nextPoint);
                    nextFunctionValue = Data.methodSimpson(xValues, uValues, STEP);
                }
            } else {
                alpha = findOptimalAlpha(vValues, optSolution);
                calculateNextPoint(vValues, optSolution, alpha, nextPoint);
            }

            if (Math.abs(alpha) < 1e-25) {
                System.out.println("Alpha is close to zero");
            }

            deviation = Data.netNormDifference(nextPoint, vValues, STEP);
            System.arraycopy(nextPoint, 0, vValues, 0, SIZE);

            count++;

            if(deviation < EPSILON) {
                break;
            }

        } while(true);

        numberOfIterations = count;
        calculateValues(vValues);
        calculateGradient(vValues);
        functionalList.add(Data.methodSimpson(xValues, uValues, STEP));
        System.out.println("Alpha: " + alpha);
    }

    private void gradientMethod() {
        int count = 0;
        double[] nextPoint = new double[SIZE];
        double deviation;

        do {
            if(count >= MAX_OF_ATTEMPTS) {
                System.err.println("Too many attempts were used");
                break;
            }
            alpha = initialAlpha;

            calculateValues(vValues);
            calculateGradient(vValues);
            double functionalValue = Data.methodSimpson(xValues, uValues, STEP);
            functionalList.add(functionalValue);

            for (int i = 0; i < SIZE; i++) {
                nextPoint[i] = vValues[i] - alpha * gradient[i];
            }

            calculateValues(nextPoint);
            calculateGradient(nextPoint);
            double nextFunctionValue = Data.methodSimpson(xValues, uValues, STEP);

            while (nextFunctionValue >= functionalValue && alpha > 1e-30) {
                alpha *= MULTIPLIER;
                for (int i = 0; i < SIZE; i++) {
                    nextPoint[i] = vValues[i] - alpha * gradient[i];
                }
                calculateValues(nextPoint);
                calculateGradient(nextPoint);
                nextFunctionValue = Data.methodSimpson(xValues, uValues, STEP);
            }

            if (Math.abs(alpha) < 1e-25) {
                System.out.println("Alpha is close to zero");
            }

            deviation = Data.netNormDifference(nextPoint, vValues, STEP);
            System.arraycopy(nextPoint, 0, vValues, 0, SIZE);

            count++;

            if(deviation < EPSILON) {
                break;
            }

        } while(true);

        numberOfIterations = count;
        functionalList.add(Data.methodSimpson(xValues, uValues, STEP));
        System.out.println("Alpha: " + alpha);
    }

    public double[] getGradient() {
        return gradient;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public List<Double> getFunctionalList() {
        return functionalList;
    }

    public double[] getxValues() {
        return xValues;
    }

    public double[] getuValues() {
        return uValues;
    }

    public double[] getvValues() {
        return vValues;
    }

    public double getSTEP() {
        return STEP;
    }
}
