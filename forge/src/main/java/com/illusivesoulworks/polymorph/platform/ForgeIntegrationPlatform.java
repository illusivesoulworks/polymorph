/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.platform;

import com.illusivesoulworks.polymorph.common.integration.AbstractCompatibilityModule;
import com.illusivesoulworks.polymorph.common.integration.fastbench.FastBenchModule;
import com.illusivesoulworks.polymorph.common.integration.fastfurnace.FastFurnaceModule;
import com.illusivesoulworks.polymorph.platform.services.IIntegrationPlatform;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeIntegrationPlatform implements IIntegrationPlatform {

  @Override
  public Map<String, Supplier<Supplier<AbstractCompatibilityModule>>> createCompatibilityModules() {
    Map<String, Supplier<Supplier<AbstractCompatibilityModule>>> result = new HashMap<>();
    result.put("fastfurnace", () -> FastFurnaceModule::new);
    result.put("fastbench", () -> FastBenchModule::new);
    return result;
  }
}
