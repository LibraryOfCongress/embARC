##  Copyright (c) MediaArea.net SARL. All Rights Reserved.
##
##  Use of this source code is governed by a BSD-style license that can
##  be found in the License.html file in the root of the source tree.
##

$ErrorActionPreference = "Stop"

#-----------------------------------------------------------------------
# Setup
$release_directory = Split-Path -Parent $MyInvocation.MyCommand.Path
$version = (Get-Content (Join-Path $release_directory "..\Project\version.txt") -Raw).Trim()

#-----------------------------------------------------------------------
# Cleanup
$artifact = Join-Path $release_directory "embARC_CLI_${version}_Windows_x64.zip"
if (Test-Path $artifact) {
    Remove-Item $artifact -Force
}

Push-Location (Split-Path $release_directory -Parent)
    & ./gradlew.bat --no-daemon clean
Pop-Location

#-----------------------------------------------------------------------
# Build
Push-Location (Split-Path $release_directory -Parent)
    (Get-Content build.gradle) -replace 'com\.portalmedia\.embarc\.gui\.Launcher', 'com.portalmedia.embarc.cli.Main' | Set-Content build.gradle
    & ./gradlew.bat --no-daemon build
Pop-Location

#-----------------------------------------------------------------------
# Package .exe
Push-Location (Split-Path $release_directory -Parent)
    $distDir = "build/distributions/windows"
    New-Item -ItemType Directory -Path $distDir -Force

    & jpackage --win-console `
               --type app-image `
               --name embARC `
               --input build/libs `
               --main-jar "embARC-${version}.jar" `
               --main-class com.portalmedia.embarc.cli.Main `
               --dest $distDir `
               --vendor "Library of Congress" `
               --description "embARC - metadata embedded for archival content" `
               --icon "icons/embARC.ico"

    # Remove ReadOnly flag from the generated executable
    $executable = Get-Item 'build\distributions\windows\embARC\embARC.exe'
    $executable.Attributes -= 'ReadOnly'

    & signtool sign `
             /fd sha256 `
             /tr http://timestamp.acs.microsoft.com `
             /td sha256 `
             /d embARC `
             /du http://mediaarea.net `
             build\distributions\windows\embARC\embARC.exe
Pop-Location

#-----------------------------------------------------------------------
# Package cli
Push-Location (Join-Path (Split-Path $release_directory -Parent) 'build\distributions\windows\embARC')
    & 7za.exe a -r -tzip ..\..\..\..\Release\embARC_CLI_${version}_Windows_x64.zip *
Pop-Location
