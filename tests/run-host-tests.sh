#!/bin/sh
# SPDX-License-Identifier: Apache-2.0
set -eu
cd "$(dirname "$0")/.."
output=$(mktemp -d)
trap 'rm -rf "$output"' EXIT HUP INT TERM
"${JAVA_HOME:+$JAVA_HOME/bin/}javac" -Xlint:all -Werror -d "$output" \
  src/com/android/carrierconfig/CarrierAssetIndex.java tests/host/CarrierAssetIndexTest.java
"${JAVA_HOME:+$JAVA_HOME/bin/}java" -cp "$output" com.android.carrierconfig/CarrierAssetIndexTest
