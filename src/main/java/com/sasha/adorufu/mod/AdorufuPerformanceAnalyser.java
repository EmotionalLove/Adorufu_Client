/*
 * Copyright (c) Sasha Stevens 2018.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sasha.adorufu.mod;

import java.util.ArrayList;

/**
 * Created by Sasha on 11/08/2018 at 6:36 PM
 **/
public class AdorufuPerformanceAnalyser {

    private ArrayList<Integer> performanceTimesNormal;
    private ArrayList<Integer> performanceTimesRender;
    private int counterNormal = 0;
    private int counterRender = 0;

    public AdorufuPerformanceAnalyser() {
        performanceTimesNormal = new ArrayList<>();
        performanceTimesRender = new ArrayList<>();
    }

    public void recordNewNormalTime(int totalTime) {
        performanceTimesNormal.add(totalTime);
    }

    public void recordNewRenderTime(int totalTime) {
        performanceTimesRender.add(totalTime);
    }

    public int getCurrentMsNormal() {
        return performanceTimesNormal.get(performanceTimesNormal.size() - 1);
    }

    public double getAvgMsNormal() {
        int sum = 0;
        for (int i : performanceTimesNormal) {
            sum += i;
        }
        return (double) sum / performanceTimesNormal.size();
    }

    public int getCurrentMsRender() {
        return performanceTimesRender.get(performanceTimesRender.size() - 1);
    }

    public double getAvgMsRender() {
        int sum = 0;
        for (int i : performanceTimesRender) {
            sum += i;
        }
        return (double) sum / performanceTimesRender.size();
    }

}
