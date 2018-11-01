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

package com.sasha.adorufu.mod.feature;

import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOptionBehaviour;

import java.util.List;
import java.util.Map;

public interface IAdorufuFeature {

    default void onLoad() {
        //
    }
    AdorufuCategory getCategory();
    List<AdorufuFeatureOption> getOptions();
    AdorufuFeatureOptionBehaviour getOptionBehaviour();
    Map<String, Boolean> getOptionsMap();
    boolean hasOptions();
    String getColouredName();
    String getFeatureName();
    void setSuffix(String s);
    void setSuffix(String[] s);
    void setSuffix(Map<String, Boolean> boolMap);


}
