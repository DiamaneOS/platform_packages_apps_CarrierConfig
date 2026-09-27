/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026 The DiamaneOS Project
 */
package com.android.carrierconfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Indexes packaged device data without changing Android's carrier matching rules. */
final class CarrierAssetIndex {
    static final String DEVICE_DIRECTORY = "device-carrier-config";
    private static final Pattern CARRIER_ID =
            Pattern.compile("carrier_config_carrierid_([0-9]+)_.+\\.xml");
    private static final Pattern MCCMNC =
            Pattern.compile("carrier_config_mccmnc_[0-9]{5,6}\\.xml");

    private final Map<String, String> mPaths;

    CarrierAssetIndex(String[] platform, String[] device) throws IOException {
        Map<String, String> selected = index(platform);
        // Match by carrier ID, not its human-readable filename suffix: the
        // same carrier can have different display names in two source releases.
        Map<String, String> overrides = index(device);
        selected.putAll(overrides);
        mPaths = new TreeMap<>();
        for (Map.Entry<String, String> entry : selected.entrySet()) {
            String name = entry.getValue();
            mPaths.put(name, overrides.containsKey(entry.getKey())
                    ? DEVICE_DIRECTORY + "/" + name : name);
        }
    }

    String[] names() {
        return mPaths.keySet().toArray(new String[0]);
    }

    String resolve(String name) {
        // Preserve the upstream missing-asset handling for unknown networks.
        return mPaths.getOrDefault(name, name);
    }

    private static Map<String, String> index(String[] names) throws IOException {
        Map<String, String> result = new HashMap<>();
        if (names == null) return result;
        for (String name : names) {
            if (name.contains("/") || name.contains("\\")) {
                throw new IOException("Carrier asset must be a basename");
            }
            Matcher match = CARRIER_ID.matcher(name);
            String key;
            if (match.matches()) {
                key = "id:" + match.group(1);
            } else if (MCCMNC.matcher(name).matches()
                    || name.equals("carrier_config_no_sim.xml")) {
                key = name;
            } else {
                // Other packaged data (for example satellite assets) is not
                // carrier configuration and remains under upstream ownership.
                continue;
            }
            if (result.put(key, name) != null) {
                throw new IOException("Duplicate carrier asset identity");
            }
        }
        return result;
    }
}
