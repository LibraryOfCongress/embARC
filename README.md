# embARC (Metadata Embedded for Archival Content)

embARC is a free, open source application that enables users to audit and correct embedded metadata of a subset of MXF files, as well as both individual DPX files or an entire DPX sequence, while not impacting the image data. MXF, short for Material Exchange Format, is an object-based file format that wraps video, audio, and other bitstreams ("essences"), optimized for content interchange or archiving by creators and/or distributors, and intended for implementation in devices ranging from cameras and video recorders to computer systems. DPX, short for Digital Picture Exchange, is a pixel-based (raster) file format intended for very high quality moving image content with attributes defined in a binary file header.

embARC, short for “metadata embedded for archival content,” is in active development by the Federal Agencies Digital Guidelines Initiative (FADGI) to support two major guideline projects:

- [Guidelines for Embedded Metadata within DPX File Headers for Digitized Motion Picture Film.](http://www.digitizationguidelines.gov/guidelines/digitize-DPXembedding.html)
- [SMPTE RDD 48: MXF Archive and Preservation Format](http://www.digitizationguidelines.gov/guidelines/MXF_app_spec.html)
  - Including [RDD 48 Amendment 1: Mapping FFV1 to MXF](https://www.digitizationguidelines.gov/guidelines/rdd48-amd1-2022.pdf)

## Supported File Formats

### DPX (Digital Picture eXchange) support in embARC:

- Import DPX files to support FADGI’s Guidelines for Embedded Metadata within DPX File Headers for Digitized Motion Picture Film as well as required SMPTE 268 metadata rules.
- Audit and correct internal metadata of both individual files or an entire DPX sequence while not impacting the image data.

### MXF (Material eXchange Format) support in embARC:

- Import and inspect MXF files.
- Audit and correct internal metadata of one or more MXF files at a time.
- Download embedded text based and binary based data.

## Project Setup

Import the source code into eclipse or the IDE of your choice. Import [MAJ](https://github.com/PortalMedia/embARC-maj) and [DROID](https://github.com/digital-preservation/droid) projects and include alongside embARC. Include `droid-core`, `droid-core-interfaces`, and `maj` as projects in the build path.

### Testing

There are separate tests for each supported file type (DPX & MXF) included in the src/tests folder. Tests include reading, writing, and file format detection.

### Running

Set up a run configuration with embARC as the target project and `com.portalmedia.embarc.gui.Main` as the main class. Include Java 1.8 or equivalent in project execution environment.

### Building GUI for Distribution

1. Update GUI `version` in build.gradle
2. Write a summary of changes in Changelog.md
3. Run gradle clean
4. To create embARC for MacOS, run gradle task macAppBundle/createDMG. When complete find embARC-x.x.x.dmg in the build/distributions folder.
5. To create embARC for Windows, run gradle task launch4j/createExe. When complete find embARC-x.x.x.exe in the build/launch4j folder.
6. To package embARC for Windows as an installer: use [Inno Setup](https://jrsoftware.org/isdl.php#stable) on a Windows machine. Place a copy of the embARC_exe_installer.iss file included at the root of this repo alongside your recently created embARC-x.x.x.exe. Open the .iss file in Inno Setup and edit the "MyAppVersion" property and any other properties therein as needed. Run the script. When complete, find embARC-x.x.x.exe packaged as an installer in the Output folder.

## embARC CLI

embARC CLI is a free, open source application that enables users to audit embedded metadata of DPX and MXF files from the command line.

embARC, short for “metadata embedded for archival content,” is in active development by the Federal Agencies Digital Guidelines Initiative (FADGI) to support two major guideline projects:

Guidelines for Embedded Metadata within DPX File Headers for Digitized Motion Picture Film.
SMPTE RDD 48: MXF Archive and Preservation Format

### DPX Usage

`java -jar [path/to/embARC-CLI.jar] [input] [output] [options]`

- [input]: path to target DPX file or DPX sequence folder
- [output]:
  - `-csv <filepath/newfile.csv>` CSV formatted output
  - `-json <filepath/newfile.json>` JSON formatted output
- [options]
  - `-print` Print file metadata to console
  - `-conformanceInputJSON` Input validation json file
  - `-conformanceOutputCSV` Output validation csv file
  - `-applyChangesFromJSON` Apply metadata changes from JSON file

### MXF Usage

`java -jar [path/to/embARC-CLI.jar] [input] [options]`

- [input]: path to target MXF file
- [options]:
  - `-print` Print file metadata to console
  - `-downloadTDStream` Write text data stream to local directory
  - `-downloadBDStream` Write binary data stream to local directory
  - `-streamOutputPath` Specify data stream output directory

### Building CLI for Distrubtion

1. Update CLI `version` in build.gradle
2. Write a summary of changes in Changelog-CLI.md
3. Switch to CLI settings (`version` and `mainClassName`) in build.gradle
4. Run gradle clean
5. Run gradle task shadow/shadowJar. When complete find embARC-CLI-x.x.x.jar in the build/libs folder.
