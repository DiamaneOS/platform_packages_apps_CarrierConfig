# Carrier configuration

This fork retains Android's carrier configuration service and its carrier-ID,
specific-carrier-ID and MCC/MNC matching rules. Optional device data can replace
the corresponding packaged asset; a changed carrier name does not create a
second identity. Carriers without device data keep the upstream asset.

Set the `diamaneos_carrierconfig` Soong configuration value `asset_module` to a
filegroup name. Its paths must retain the `device-carrier-config/` prefix. That
directory contains `carrier_config_*.xml` files and may contain
`vendor.xml` and `vendor_no_sim.xml`. Device vendor defaults are applied last,
in their original filtered order, just as the upstream vendor resource is.
No runtime configuration download, additional permission or stock APK code is
introduced. With no device module selected, upstream behavior is unchanged.

For FP6, the DiamaneOS vendor generator authenticates the stock carrier APK and
extracts its XML using `aapt2`. The generated filegroup is
`fp6_stock_carrier_assets`. Carrier assets retain their bytes, and binary XML resources are decoded to text.
Numeric-only legacy filenames are excluded: the inspected stock service does not
load them. Renaming them would activate obsolete, sometimes conflicting data.
Provenance records the APK, extraction tool and generated file hashes. Stock
satellite data is not imported by this path. Review stock data on each update;
a stock override can intentionally differ from newer AOSP defaults.

The FP6 extractor repairs one hash-pinned, malformed empty no-SIM XML document.
The repair and both source/output identities are recorded in the generated
provenance. It does not silently repair other malformed configuration.

Run `sh tests/run-host-tests.sh` with a JDK (or `JAVA_HOME`) for asset-index tests.
Run the existing Android carrier tests after integration; host tests do not
exercise Android XML parsing, framework binding or a carrier network.
