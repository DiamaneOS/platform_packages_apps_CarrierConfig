// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 The DiamaneOS Project
package com.android.carrierconfig;

import java.io.IOException;
import java.util.Arrays;

public final class CarrierAssetIndexTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        String parent = "carrier_config_carrierid_10_Parent.xml";
        String specific = "carrier_config_carrierid_10000_Specific.xml";
        String renamed = "carrier_config_carrierid_10_New-name.xml";
        String mccmnc = "carrier_config_mccmnc_001001.xml";
        String noSim = "carrier_config_no_sim.xml";
        CarrierAssetIndex index = new CarrierAssetIndex(
                new String[] {parent, specific, mccmnc, noSim, "satellite"},
                new String[] {renamed, mccmnc, "vendor.xml"});
        check(!Arrays.asList(index.names()).contains(parent), "renamed carrier overrides by ID");
        check(index.resolve(renamed).equals("device-carrier-config/" + renamed), "device carrier");
        check(index.resolve(specific).equals(specific), "specific MVNO stays available");
        check(index.resolve(mccmnc).equals("device-carrier-config/" + mccmnc), "MCC/MNC override");
        check(index.resolve(noSim).equals(noSim), "no-SIM fallback");
        check(index.resolve("missing.xml").equals("missing.xml"), "unknown network fallback");
        check(index.names().length == 4, "only indexed carrier configuration is visible");
        CarrierAssetIndex reversed = new CarrierAssetIndex(
                new String[] {noSim, mccmnc, specific, parent},
                new String[] {mccmnc, renamed});
        check(Arrays.equals(index.names(), reversed.names()), "order is independent of input order");
        check(new CarrierAssetIndex(null, null).names().length == 0, "absent optional assets");
        check(new CarrierAssetIndex(new String[] {parent}, null).resolve(parent).equals(parent),
                "unconfigured device keeps upstream data");
        check(new CarrierAssetIndex(new String[] {noSim}, new String[] {noSim}).resolve(noSim)
                .equals("device-carrier-config/" + noSim), "device no-SIM data");
        for (String[] bad : new String[][] {{parent, renamed}, {"../" + parent}, {"a\\b"}}) {
            try {
                new CarrierAssetIndex(new String[0], bad);
                throw new AssertionError("ambiguous or nested asset accepted");
            } catch (IOException expected) { }
        }
        System.out.println("Carrier asset selection: PASS");
    }
}
