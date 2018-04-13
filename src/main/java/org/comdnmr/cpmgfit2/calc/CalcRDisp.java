package org.comdnmr.cpmgfit2.calc;

import java.util.ArrayList;
import java.util.stream.IntStream;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;

public class CalcRDisp extends FitModel {

    static RandomGenerator random = new SynchronizedRandomGenerator(new Well19937c());
    int[] r2Mask = {0, 1, 3};
    double[] rexErrors = new double[nID];

    public CalcRDisp() {
        this.equation = CPMGEquation.CPMGFAST;
    }

    public void setEquation(String eqName) {
        equation = CPMGEquation.valueOf(eqName.toUpperCase());
    }

    public CalcRDisp(double[] x, double[] y, double[] err, double[] fieldValues, double[] fields) throws IllegalArgumentException {
        this(x, y, err, fieldValues, fields, new int[x.length]);
    }

    public CalcRDisp(double[] x, double[] y, double[] err, double[] fieldValues, double[] fields, int[] idNums) throws IllegalArgumentException {
        this.xValues = x.clone();
        this.yValues = y.clone();
        this.errValues = err.clone();
        this.fieldValues = fieldValues.clone();
        this.fields = fields.clone();
        this.idNums = idNums.clone();
        this.idNums = new int[xValues.length];
        this.equation = CPMGEquation.CPMGFAST;
        this.equation.setFieldRef(fields);
        if (setNID()) {
            throw new IllegalArgumentException("Invalid idNums, some values not used");
        }
    }

    public static int getNPars(int[][] map) {
        int maxIndex = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                maxIndex = Math.max(map[i][j], maxIndex);
            }
        }
        return maxIndex + 1;
    }

    public int[] getMask() {
        return r2Mask;
    }

    @Override
    public double value(double[] par) {
        double sumAbs = 0.0;
        double sumSq = 0.0;
        for (int i = 0; i < xValues.length; i++) {
            final double value;
            value = equation.calculate(par, map[idNums[i]], xValues[i], idNums[i], fieldValues[i]);
            //System.out.println( "xxxxxxxxxxx " + value + " " + yValues[i] + " " + equation.name());
            double delta = (value - yValues[i]);
            //System.out.print(xValues[i] + " " + yValues[i] + " " + value + " " + (delta*delta) + " ");
            //double delta = (value - yValues[i]) / errValues[i];
            sumAbs += FastMath.abs(delta);
            sumSq += delta * delta;
        }
        if (reportFitness) {
            double rms = Math.sqrt(sumSq / xValues.length);
            for (double p : par) {
                System.out.printf("%7.3f ", p);
            }
            System.out.printf("%7.3f %7.3f %7.3f\n", sumSq, sumAbs, rms);
        }

        if (absMode) {
            return sumAbs;
        } else {
            return sumSq;
        }
    }

    public ArrayList<Double> simY(double[] par, double[] xValues, double field) {
        ArrayList<Double> result = new ArrayList<>();
        equation.setFieldRef(field);
        for (int i = 0; i < xValues.length; i++) {
            double yCalc = equation.calculate(par, map[idNums[i]], xValues[i], idNums[i], field);
            result.add(yCalc);
        }
        return result;
    }

    public static double rDisp(double[] par, double x) {
        double a = par[0];
        double b = par[1];
        double c = par[2];
        // fixme check for x=0;
        return Math.exp(-(a - a * Math.sin(b / x) / (b / x) + c));
    }

    public double[] getRex(double[] pars) {
        double[] result = new double[nID];
        for (int i = 0; i < map.length; i++) {
            result[i] = equation.getRex(pars, map[i]);
        }
        return result;
    }

    public double getRex(double[] pars, int id) {
        return equation.getRex(pars, map[id]);
    }

    public double[] getRexError() {
        return rexErrors.clone();
    }

    public double getKex(double[] pars) {
        return equation.getKex(pars);
    }

    public double[] simBounds(double[] start, double[] lowerBounds, double[] upperBounds, double[] inputSigma) {
        reportFitness = false;
        int nPar = start.length;
        double[][] parValues = new double[nPar][nSim];
        double[] yPred = getPredicted(start);
        double[] yValuesOrig = yValues.clone();
        double[][] rexValues = new double[nID][nSim];
        rexErrors = new double[nID];
        for (int i = 0; i < nSim; i++) {
            for (int k = 0; k < yValues.length; k++) {
                yValues[k] = yPred[k] + errValues[k] * random.nextGaussian();
            }
            PointValuePair result = refine(start, lowerBounds, upperBounds, inputSigma);
            double[] rPoint = result.getPoint();
            for (int j = 0; j < nPar; j++) {
                parValues[j][i] = rPoint[j];
            }
            if (equation == CPMGEquation.CPMGSLOW) {
                for (int j = 0; j < map.length; j++) {
                    rexValues[j][i] = equation.getRex(result.getPoint(), map[j]);
                }
            }
        }
        double[] parSDev = new double[nPar];
        for (int i = 0; i < nPar; i++) {
            DescriptiveStatistics dStat = new DescriptiveStatistics(parValues[i]);
            double p5 = dStat.getPercentile(5.0);
            double p95 = dStat.getPercentile(95.0);
            parSDev[i] = dStat.getStandardDeviation();
        }
        if (equation == CPMGEquation.CPMGSLOW) {
            for (int j = 0; j < nID; j++) {
                DescriptiveStatistics dStat = new DescriptiveStatistics(rexValues[j]);
                rexErrors[j] = dStat.getStandardDeviation();
            }
        }
        yValues = yValuesOrig;
        return parSDev;
    }

    public double[] simBoundsStream(double[] start, double[] lowerBounds, double[] upperBounds, double[] inputSigma) {
        reportFitness = false;
        int nPar = start.length;
        double[][] parValues = new double[nPar][nSim];
        double[][] rexValues = new double[nID][nSim];
        rexErrors = new double[nID];
        double[] yPred = getPredicted(start);
        IntStream.range(0, nSim).parallel().forEach(i -> {
//        IntStream.range(0, nSim).forEach(i -> {
            CalcRDisp rDisp = new CalcRDisp(xValues, yPred, errValues, fieldValues, fields, idNums);
            rDisp.setEquation(equation.getName());
            rDisp.setAbsMode(absMode);
            double[] newY = new double[yValues.length];
            for (int k = 0; k < yValues.length; k++) {
                newY[k] = yPred[k] + errValues[k] * random.nextGaussian();
            }
            rDisp.setXY(xValues, newY);
            rDisp.setIds(idNums);
            rDisp.setMap(map);

            PointValuePair result = rDisp.refine(start, lowerBounds, upperBounds, inputSigma);
            double[] rPoint = result.getPoint();
            for (int j = 0; j < nPar; j++) {
                parValues[j][i] = rPoint[j];
            }
            if (equation == CPMGEquation.CPMGSLOW) {
                for (int j = 0; j < map.length; j++) {
                    rexValues[j][i] = equation.getRex(result.getPoint(), map[j]);
                }
            }
        });

        double[] parSDev = new double[nPar];
        for (int i = 0; i < nPar; i++) {
            DescriptiveStatistics dStat = new DescriptiveStatistics(parValues[i]);
            parSDev[i] = dStat.getStandardDeviation();
        }
        if (equation == CPMGEquation.CPMGSLOW) {
            for (int j = 0; j < nID; j++) {
                DescriptiveStatistics dStat = new DescriptiveStatistics(rexValues[j]);
                rexErrors[j] = dStat.getStandardDeviation();
            }
        }
        return parSDev;
    }

    public double[] simBoundsBootstrapStream(double[] start, double[] lowerBounds, double[] upperBounds, double[] inputSigma) {
        reportFitness = false;
        int nPar = start.length;
        double[][] parValues = new double[nPar][nSim];
        double[][] rexValues = new double[nID][nSim];
        rexErrors = new double[nID];
        IntStream.range(0, nSim).parallel().forEach(i -> {
            CalcRDisp rDisp = new CalcRDisp(xValues, yValues, errValues, fieldValues, fields, idNums);
            rDisp.setEquation(equation.getName());
            rDisp.setAbsMode(absMode);
            double[] newX = new double[yValues.length];
            double[] newY = new double[yValues.length];
            double[] newErr = new double[yValues.length];
            double[] newFieldValues = new double[yValues.length];
            int[] newID = new int[yValues.length];
            int iTry = 0;
            do {
                for (int k = 0; k < yValues.length; k++) {
                    int rI = random.nextInt(yValues.length);
                    newX[k] = xValues[rI];
                    newY[k] = yValues[rI];
                    newErr[k] = errValues[rI];
                    newFieldValues[k] = fieldValues[rI];
                    newID[k] = idNums[rI];
                }
                iTry++;
            } while (!checkID(idNums, newID, 2) && (iTry < 10));
            // fixme  idNum should be set in above loop
            rDisp.setXY(newX, newY);
            rDisp.setErr(newErr);
            rDisp.setFieldValues(newFieldValues);
            rDisp.setIds(newID);
            rDisp.setMap(map);

            PointValuePair result = rDisp.refine(start, lowerBounds, upperBounds, inputSigma);
            double[] rPoint = result.getPoint();
            for (int j = 0; j < nPar; j++) {
                parValues[j][i] = rPoint[j];
            }
            if (equation == CPMGEquation.CPMGSLOW) {
                for (int j = 0; j < map.length; j++) {
                    rexValues[j][i] = equation.getRex(result.getPoint(), map[j]);
                }
            }
        });

        double[] parSDev = new double[nPar];
        for (int i = 0; i < nPar; i++) {
            DescriptiveStatistics dStat = new DescriptiveStatistics(parValues[i]);
            parSDev[i] = dStat.getStandardDeviation();
        }
        if (equation == CPMGEquation.CPMGSLOW) {
            for (int j = 0; j < nID; j++) {
                DescriptiveStatistics dStat = new DescriptiveStatistics(rexValues[j]);
                rexErrors[j] = dStat.getStandardDeviation();
            }
        }
        return parSDev;
    }

}
