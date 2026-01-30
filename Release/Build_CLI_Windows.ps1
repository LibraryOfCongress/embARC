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
# Build
Push-Location -Path "${release_directory}\.."
    (Get-Content build.gradle) -replace 'com\.portalmedia\.embarc\.gui\.Launcher', 'com.portalmedia.embarc.cli.Main' | Set-Content build.gradle
    & ./gradlew.bat --no-daemon build
    
    # Generate excutable
    $distDir = "build/distributions/windows"
    New-Item -ItemType Directory -Path $distDir -Force

    jpackage --win-console `
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
Pop-Location
