# Standalone running

It is possible to run the program as a standalone application for batched template programming.

## Prepare

- Make sure you have Leiningen and Java installed.
- First, you need to compile the application with the `lein compile` command.
- The standalone jar file will be found in the `target` directory.
- The `stencil-...-standalone.jar` file can be used to running the application in standalone mode.

## Usage

Syntax: `java -jar stencil-*-standalone.jar [-Tpdf] TEMPLATE_FILE DATA_FILE_1 ... DATA_FILE_N`

The following command line parameters are supported:

- `-Tformat`: specifies the output file format. For example: `-Tpdf`, `-Tdocx`, `-Todt`, etc. Defaults to the template file format.

Run the program as the following:

```
java -jar  stencil-*-standalone.jar -Tpdf TEMPLATE_FILE.docx DATA_FILE_1.json DATA_FILE_2.json ... DATA_FILE_N.json
```

This reads the template from `TEMPLATE_FILE.docx` file and template data from the JSON files to output generated documents as PDF files.
