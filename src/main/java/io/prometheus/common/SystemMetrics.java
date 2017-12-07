/*
 *  Copyright 2017, Paul Ambrose All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.prometheus.common;

import io.prometheus.client.hotspot.ClassLoadingExports;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import io.prometheus.client.hotspot.VersionInfoExports;

public class SystemMetrics {
  private static boolean initialized = false;

  private SystemMetrics() {
  }

  public static synchronized void initialize(final boolean enableStandardExports,
                                             final boolean enableMemoryPoolsExports,
                                             final boolean enableGarbageCollectorExports,
                                             final boolean enableThreadExports,
                                             final boolean enableClassLoadingExports,
                                             final boolean enableVersionInfoExports) {
    if (!initialized) {
      if (enableStandardExports)
        new StandardExports().register();
      if (enableMemoryPoolsExports)
        new MemoryPoolsExports().register();
      if (enableGarbageCollectorExports)
        new GarbageCollectorExports().register();
      if (enableThreadExports)
        new ThreadExports().register();
      if (enableClassLoadingExports)
        new ClassLoadingExports().register();
      if (enableVersionInfoExports)
        new VersionInfoExports().register();
      initialized = true;
    }
  }

}