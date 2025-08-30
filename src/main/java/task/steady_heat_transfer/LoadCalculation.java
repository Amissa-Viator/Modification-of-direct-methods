package task.steady_heat_transfer;

import task.steady_heat_transfer.algorithm.Data;
import task.steady_heat_transfer.algorithm.GradientMethod;
import task.steady_heat_transfer.algorithm.OutputData;
import task.steady_heat_transfer.excel_worker.ExcelWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadCalculation {
    private static double[] gradientValues;
    private static List<Double> functionalValues = new ArrayList<>();
    private static List<double[]> modifiedGradient = new ArrayList<>();
    private static List<List<Double>> modifiedFunctional = new ArrayList<>();

    public static void startMethod(Data data) {
        List<Long> time = new ArrayList<>();
        System.out.println("Start");

        System.out.println("Ordinary gradient method: ");
        long startTime = System.currentTimeMillis();
        List<OutputData> gradientMethodResult = ordinaryGradientMethod(new Data(data));
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        time.add(executionTime);

        System.out.println("\nModified gradient method: ");
        startTime = System.currentTimeMillis();
        List<List<OutputData>> modifiedGradientResult = modifiedGradientMethod(new Data(data));
        endTime = System.currentTimeMillis();
        executionTime = endTime - startTime;
        time.add(executionTime);

        System.out.println("\nTime consumptions: ");
        System.out.println(STR."Gradient method: \{time.getFirst()}ms");
        System.out.println(STR."Modified gradient method: \{time.getLast()}ms");

        saveMeasurements(gradientMethodResult, modifiedGradientResult);
    }

    private static List<OutputData> ordinaryGradientMethod(Data data) {
        int size = data.getnMax() + 1;
        double[] vValues = new double[size];
        Arrays.fill(vValues, 1.0);

        GradientMethod gradientMethod = new GradientMethod(data.getEpsilon(), size, data.getAlpha(), vValues);
        gradientMethod.start();
        List<OutputData> result = collectAllResults(size, gradientMethod.getxValues(), gradientMethod.getuValues(), gradientMethod.getvValues());
        gradientValues = Arrays.copyOf(gradientMethod.getGradient(), gradientMethod.getGradient().length);
        functionalValues = gradientMethod.getFunctionalList();
        System.out.println(STR."Gradient method iterations: \{gradientMethod.getNumberOfIterations()}");
        System.out.println(STR."||J'(v)|| = \{Data.netNorm(gradientValues, gradientMethod.getSTEP())}");
        System.out.println(STR."||v(x)|| = \{Data.netNorm(gradientMethod.getvValues(), gradientMethod.getSTEP())}");

        return result;
    }

    private static List<List<OutputData>> modifiedGradientMethod(Data data) {
        List<List<OutputData>> result = new ArrayList<>();
        double coefficient = 10.0;
        int n = data.getnMin();
        double eps = data.getEpsilon();
        double[] vValues = null;
        int k = 0, count = 0;

        while(n <= data.getnMax()) {
            System.out.println(STR." N = \{n}");
            double step = (Data.RIGHT_BORDER - Data.LEFT_BORDER) / n;
            double epsilon = coefficient * Math.pow(step, 2);
            int size = n + 1;

            if(epsilon < eps || n == data.getnMax()) {
                epsilon = eps;
            }

            System.out.println(STR."Eps: \{epsilon}");
            System.out.println(STR."Step: \{step}");

            if(result.isEmpty()) {
                vValues = new double[size];
                Arrays.fill(vValues, 1.0);
            } else {
                vValues = interpolateValues(vValues, size);
            }

            GradientMethod gradientMethod = new GradientMethod(epsilon, size, data.getAlpha(), vValues, step);
            gradientMethod.start();
            System.out.println(STR."Iterations: \{gradientMethod.getNumberOfIterations()}");
            k += gradientMethod.getNumberOfIterations();

            List<OutputData> values = collectAllResults(size, gradientMethod.getxValues(), gradientMethod.getuValues(), gradientMethod.getvValues());
            result.add(values);

            double[] gradient = gradientMethod.getGradient();
            System.out.println(STR."||J'(v)|| = \{Data.netNorm(gradient, step)}");
            modifiedGradient.add(gradient);

            List<Double> functional = gradientMethod.getFunctionalList();
            modifiedFunctional.add(functional);

            vValues = Arrays.copyOf(gradientMethod.getvValues(), gradientMethod.getvValues().length);
            System.out.println(STR."||v(x)|| = \{Data.netNorm(vValues, step)}");

            n *= 2;
            count ++;
        }

        System.out.println(STR."\nAll iterations: \{k}");
        System.out.println(STR."Count: \{count}");

        return result;
    }

    private static double[] interpolateValues(double[] data, int size) {
        double[] newV = new double[size];

        newV[0] = data[0];
        newV[size - 1] = data[data.length - 1];

        for (int i = 1; i < size - 1; i++) {
            int oldIndex = i / 2;
            if (i % 2 == 0) {
                newV[i] = data[oldIndex];
            } else {
                newV[i] = (data[oldIndex] + data[Math.min(oldIndex + 1, data.length - 1)]) / 2;
            }
        }

        return newV;
    }

    private static void saveMeasurements(List<OutputData> gradientMethodResult, List<List<OutputData>> modifiedResults) {
         ExcelWriter.exportToExcelValues(gradientMethodResult, "Gradient Method ");
         ExcelWriter.exportToExcelFunctional(functionalValues, "J(v) for GM");
         ExcelWriter.exportToExcelGradient(gradientValues, "J'(v)");

         for(int i=0; i < modifiedGradient.size(); i++) {
             ExcelWriter.exportToExcelValues(modifiedResults.get(i), STR."Modified GM \{i}");
             ExcelWriter.exportToExcelFunctional(modifiedFunctional.get(i), STR."J(v) for modified GM \{i}");
             ExcelWriter.exportToExcelGradient(modifiedGradient.get(i), STR."modified J'(v) \{i}");
         }
    }

    private static List<OutputData> collectAllResults(int size, double[] xValues, double[] uValues, double[] vValues) {
        List<OutputData> result = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            OutputData data = new OutputData(xValues[i], uValues[i], vValues[i]);
            result.add(data);
        }
        double step = (Data.RIGHT_BORDER - Data.LEFT_BORDER) / (size-1);

        System.out.println(STR."Functional Gradient Method: \{Data.methodSimpson(xValues, uValues, step)}");

        return result;
    }
}
