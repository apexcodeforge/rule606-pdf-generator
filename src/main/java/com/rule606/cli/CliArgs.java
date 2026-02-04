package com.rule606.cli;

import java.io.File;

public class CliArgs {
    private File xsdFile;
    private File xmlFile;
    private File outputFile;

    private CliArgs() {}

    public static CliArgs parse(String[] args) {
        CliArgs cli = new CliArgs();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--xsd" -> {
                    if (++i >= args.length) throw new IllegalArgumentException("--xsd requires a value");
                    cli.xsdFile = new File(args[i]);
                }
                case "--xml" -> {
                    if (++i >= args.length) throw new IllegalArgumentException("--xml requires a value");
                    cli.xmlFile = new File(args[i]);
                }
                case "--output" -> {
                    if (++i >= args.length) throw new IllegalArgumentException("--output requires a value");
                    cli.outputFile = new File(args[i]);
                }
                case "--help", "-h" -> {
                    printUsage();
                    System.exit(0);
                }
                default -> throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
        if (cli.xmlFile == null) throw new IllegalArgumentException("--xml is required");
        if (cli.outputFile == null) throw new IllegalArgumentException("--output is required");
        return cli;
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar rule606-generator-1.0.0.jar --xsd <schema.xsd> --xml <input.xml> --output <report.pdf>");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --xsd <file>     XSD schema file for validation (optional)");
        System.out.println("  --xml <file>     Input XML file (required)");
        System.out.println("  --output <file>  Output PDF file (required)");
        System.out.println("  --help, -h       Show this help message");
    }

    public File getXsdFile() { return xsdFile; }
    public File getXmlFile() { return xmlFile; }
    public File getOutputFile() { return outputFile; }
}
