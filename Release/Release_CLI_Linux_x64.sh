##  Copyright (c) MediaArea.net SARL. All Rights Reserved.
 #
 #  Use of this source code is governed by a BSD-style license that can
 #  be found in the License.html file in the root of the source tree.
 ##

#!/bin/bash

set -e

#-----------------------------------------------------------------------
# Setup
release_directory="$(realpath "$(dirname "${BASH_SOURCE[0]}")")"
version="$(<"${release_directory}/../Project/version.txt")"

#-----------------------------------------------------------------------
# Cleanup
rm -fr "${release_directory}/embARC_CLI_${version}_Linux_x64.zip"

pushd "${release_directory}/.."
    ./gradlew clean
popd

#-----------------------------------------------------------------------
# Build
pushd "${release_directory}/.."
    sed -i.bak 's/com.portalmedia.embarc.gui.Launcher/com.portalmedia.embarc.cli.Main/g' build.gradle
    ./gradlew build
popd

#-----------------------------------------------------------------------
# Export artifacts
mv "${release_directory}/../build/distributions/embARC-shadow-${version}.zip" "${release_directory}/embARC_CLI_${version}_Linux_x64.zip"
