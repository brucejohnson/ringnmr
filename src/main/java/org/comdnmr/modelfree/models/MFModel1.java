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
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.comdnmr.modelfree.models;

import org.comdnmr.modelfree.RelaxFit;

/**
 *
 * @author brucejohnson
 */
public class MFModel1 extends MFModel {

    double s2;

    public MFModel1(RelaxFit.DiffusionType diffType, double[][] D, double[][] VT, double[] v) {
        super(diffType, D, VT, v);
    }

    @Override
    double calc(double omega2, int i) {
        return s2 * (Df[i] * a[i]);
    }

    @Override
    public double[] calc(double[] omegas, double[] pars) {
        this.s2 = pars[0];
        return calc(omegas);
    }

    public double[] calc(double[] omegas, double s2) {
        this.s2 = s2;
        return calc(omegas);
    }

}