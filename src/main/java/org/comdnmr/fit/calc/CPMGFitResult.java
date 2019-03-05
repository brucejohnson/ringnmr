package org.comdnmr.fit.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bruce Johnson
 */
public class CPMGFitResult {

    /**
     * @return the nGroupPars
     */
    public int getnGroupPars() {
        return nGroupPars;
    }

    /**
     * @return the nCurves
     */
    public int getNCurves() {
        return curveFits.size();
    }

    /**
     * @return the equationName
     */
    public String getEquationName() {
        return equationName;
    }

    /**
     * @return the fitParNames
     */
    public String[] getFitParNames() {
        return fitParNames;
    }

    /**
     * @return the aicc
     */
    public double getAicc() {
        return aicc;
    }

    /**
     * @return the rms
     */
    public double getRms() {
        return rms;
    }

    /**
     * @return the reduced chi-squared statistic
     */
    public double getRChiSq() {
        return rChiSq;
    }

    public CurveFit getCurveFit(int iCurve) {
        return curveFits.get(iCurve);
    }

    public double[] getPars(int iCurve) {
        return curveFits.get(iCurve).plotEquation.pars;
    }

    public double[] getErrs(int iCurve) {
        return curveFits.get(iCurve).plotEquation.errs;
    }

    public String getResidueNumber(int iCurve) {
        return curveFits.get(iCurve).resNum;
    }

    public boolean exchangeValid() {
        return hasExchange;
    }

    public Map<String, double[]> getSimsMap() {
        return simsMap;
    }
    
    public CurveFit.CurveFitStats getCurveFitStats() {
        return curveFitStats;
    }

    private final String[] fitParNames;
    private final List<CurveFit> curveFits = new ArrayList<>();
    private final double aicc;
    private final double rms;
    private final double rChiSq;
    private final String equationName;
    private final int nGroupPars;
    private final Map<String, double[]> simsMap;
    private final boolean hasExchange;
    private final CurveFit.CurveFitStats curveFitStats;

    public CPMGFitResult(String[] fitParNames, List<CurveFit> curveFits, String equationName, int nGroupPars, double aicc, double rms, double rChiSq, 
            Map<String, double[]> simsMap, boolean hasExchange, CurveFit.CurveFitStats curveStats) {
        this.curveFits.addAll(curveFits);
        this.fitParNames = fitParNames.clone();
        this.equationName = equationName;
        this.nGroupPars = nGroupPars;
        this.aicc = aicc;
        this.rms = rms;
        this.rChiSq = rChiSq;
        this.simsMap = simsMap;
        this.hasExchange = hasExchange;
        this.curveFitStats = curveStats;
    }
}
