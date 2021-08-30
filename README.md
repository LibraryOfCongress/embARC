# embARC (Metadata Embedded for Archival Content)

### About
embARC is a free, open source application that enables users to audit and correct embedded metadata of a subset of MXF files, as well as both individual DPX files or an entire DPX sequence, while not impacting the image data. MXF, short for Material Exchange Format, is an object-based file format that wraps video, audio, and other bitstreams ("essences"), optimized for content interchange or archiving by creators and/or distributors, and intended for implementation in devices ranging from cameras and video recorders to computer systems. DPX, short for Digital Picture Exchange, is a pixel-based (raster) file format intended for very high quality moving image content with attributes defined in a binary file header. 

embARC, short for “metadata embedded for archival content,” is in active development by the Federal Agencies Digital Guidelines Initiative (FADGI) to support two major guideline projects: 
Guidelines for Embedded Metadata within DPX File Headers for Digitized Motion Picture Film.
SMPTE RDD 48: MXF Archive and Preservation Format 

#### DPX
Digital Picture eXchange
- Import DPX files to support FADGI’s Guidelines for Embedded Metadata within DPX File Headers for Digitized Motion Picture Film as well as required SMPTE 268 metadata rules.
- Audit and correct internal metadata of both individual files or an entire DPX sequence while not impacting the image data.

#### MXF
Material eXchange Format
- Import and inspect MXF files.
- Audit and correct internal metadata of one or more MXF files at a time.
- Download embedded text based and binary based data.

#### Building and Running from Source Code

##### Project Setup
Import the source code into eclipse or the IDE of your choice. Import (MAJ)[https://github.com/PortalMedia/embARC-maj] and (DROID)[https://github.com/digital-preservation/droid] projects and include alongside embARC. Include `droid-core`, `droid-core-interfaces`, and `maj` as projects in the build path.

##### Testing
There are separate tests for each supported file type (DPX & MXF) included in the src/tests folder. Tests include reading, writing, and file format detection.

#### Running
Set up a run configuration with embARC as the target project and `com.portalmedia.embarc.gui.Main` as the main class. Include Java 1.8 or equivalent in project execution environment.

#### Building
Use gradle tasks to build embARC. See `launch4j/createExe` and `macappbundle/createApp` to build for Windows and MacOS, resepectively.
