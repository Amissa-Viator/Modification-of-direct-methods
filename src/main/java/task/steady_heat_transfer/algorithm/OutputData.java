package task.steady_heat_transfer.algorithm;

public class OutputData {
    private double xValue;
    private double uValue;
    private double vValue;

    public OutputData(double xValue, double uValue, double vValue) {
        this.xValue = xValue;
        this.uValue = uValue;
        this.vValue = vValue;
    }

    public double getxValue() {
        return xValue;
    }

    public double getuValue() {
        return uValue;
    }

    public double getvValue() {
        return vValue;
    }
}
