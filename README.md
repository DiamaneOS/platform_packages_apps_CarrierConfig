# Carrier configuration

This fork keeps Android's carrier configuration service and its carrier-ID,
specific-carrier-ID and MCC/MNC matching rules.

- Optional device data can replace the matching packaged asset; a changed
  carrier name does not create a second identity. Carriers without device data
  keep the upstream asset.
- Adds no runtime configuration download, permission or stock APK code. With
  no device module selected, upstream behavior is unchanged.

## Selecting device data

- Set the `diamaneos_carrierconfig` Soong configuration value `asset_module` to
  a filegroup name. Its paths must keep the `device-carrier-config/` prefix.
- That directory holds `carrier_config_*.xml` files and may hold `vendor.xml`
  and `vendor_no_sim.xml`.
- Device vendor defaults keep their original filtered order. Android's vendor
  XML resources apply afterwards, so the normal resource-overlay mechanism
  still works for explicit device overrides.

## FP6 data

- The DiamaneOS vendor generator authenticates the stock carrier APK and
  extracts its XML with `aapt2` into the filegroup `fp6_stock_carrier_assets`.
- Carrier assets keep their bytes; binary XML resources are decoded to text.
- Numeric-only legacy filenames are left out: the inspected stock service does
  not load them, and renaming them would activate obsolete, sometimes
  conflicting data.
- Stock satellite data is not imported by this path.
- The extractor repairs one hash-pinned, malformed empty no-SIM XML document,
  and silently repairs nothing else.
- Provenance records the APK, extraction tool, generated file hashes and that
  repair with its source and output identities.
- Review stock data on each update; a stock override can intentionally differ
  from newer AOSP defaults.

## Tests

- `sh tests/run-host-tests.sh` with a JDK (or `JAVA_HOME`) runs the asset-index
  tests.
- Run the existing Android carrier tests after integration. Host tests do not
  exercise Android XML parsing, framework binding or a carrier network.
