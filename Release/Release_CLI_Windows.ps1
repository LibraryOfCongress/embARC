##  Copyright (c) MediaArea.net SARL. All Rights Reserved.
##
##  Use of this source code is governed by a BSD-style license that can
##  be found in the License.html file in the root of the source tree.
##

$ErrorActionPreference = "Stop"

#-----------------------------------------------------------------------
# Setup
$release_directory = $PSScriptRoot
$version = (Get-Content "${release_directory}\..\Project\version.txt" -Raw).Trim()

#-----------------------------------------------------------------------
# Cleanup
$artifact = "${release_directory}\embARC_CLI_${version}_Windows_x64.zip"
if (Test-Path $artifact) {
    Remove-Item $artifact -Force
}

#-----------------------------------------------------------------------
# Package CLI
Push-Location -Path "${release_directory}\..\build\distributions\windows\embARC"
    & 7za.exe a -r -tzip "${release_directory}\embARC_CLI_${version}_Windows_x64.zip" *
Pop-Location
