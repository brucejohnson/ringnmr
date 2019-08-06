/*
 * CoMD/NMR Software : A Program for Analyzing NMR Dynamics Data
 * Copyright (C) 2018-2019 Bruce A Johnson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.comdnmr.eqnfit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bruce Johnson
 */
public class FitResult {

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

    public FitResult(String[] fitParNames, List<CurveFit> curveFits, String equationName, int nGroupPars, double aicc, double rms, double rChiSq, 
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
