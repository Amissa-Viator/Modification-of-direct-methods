package task.steady_heat_transfer.algorithm;

public class Data {
    public static final double LEFT_BORDER = 0;
    public static final double RIGHT_BORDER = 1;
    public static final double V_MIN = 0.0;
    public static final double V_MAX = 1.0;
    public static final BoundaryConditions U_BOUNDARY_CONDITIONS = new BoundaryConditions(1, 0, 1, 0, 0, 0.6);
    public static final BoundaryConditions P_BOUNDARY_CONDITIONS = new BoundaryConditions(1, 0, 1, 0, 0, 0);
    private int nMax, nMin;
    protected double epsilon, alpha;

    public Data(double epsilon, int nMax, double alpha, int nMin) {
        this.nMax = nMax;
        this.nMin = nMin;
        this.epsilon = epsilon;
        this.alpha = alpha;
    }

    public Data(Data other) {
        this.epsilon = other.epsilon;
        this.nMax = other.nMax;
        this.alpha = other.alpha;
        this.nMin = other.nMin;
    }

    public static double f(double x) {
        return 0.5*x*(1-x) + 0.1;
    }

    public static double k(double x) {
        return 1.0 + 0.1*x*x;
    }

    public static double kPrime(double x) {
        return 0.2*x;
    }

    public static double temperatureDistribution(double x) {
        return 0.5*x*(2-x) + 0.1*x;
    }

    private static double integralFunction(double x, double u) {
        return Math.pow((u - temperatureDistribution(x)), 2);
    }

    public static double methodSimpson(double[] X, double[] U, double step) {
        double integral = 0.0;
        double coefficient = step/3.0;
        double sum = integralFunction(X[0],U[0]) + integralFunction(X[X.length-1], U[U.length-1]);
        for(int i = 1; i < X.length-1; i++) {
            double x = X[i];
            double u = U[i];
            if(i % 2 == 0) {
                sum += 2*integralFunction(x, u);
            } else{
                sum += 4*integralFunction(x, u);
            }
        }
        integral = coefficient*sum;
        return integral;
    }

    public static double netNormDifference(double[] point1, double[] point2, double step) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }

        return Math.sqrt(step * sum);
    }

    public static double netNorm(double[] values, double step) {
        double sum = 0.0;

        for(int i = 0; i < values.length; i++) {
            sum += Math.pow(values[i], 2);
        }

        return Math.sqrt(step*sum);
    }

    public int getnMax() {
        return nMax;
    }

    public int getnMin() {
        return nMin;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getAlpha() {
        return alpha;
    }

    public static class BoundaryConditions {
        public final double ALPHA_0;
        public final double ALPHA_1;
        public final double BETA_0;
        public final double BETA_1;
        public final double A;
        public final double B;

        public BoundaryConditions(double alpha0, double alpha1, double beta0, double beta1, double a, double b) {
            this.ALPHA_0 = alpha0;
            this.ALPHA_1 = alpha1;
            this.BETA_0 = beta0;
            this.BETA_1 = beta1;
            this.A = a;
            this.B = b;
        }
    }
}
