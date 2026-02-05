# SEC Rule 606 Report Generator

A Java-based tool that converts SEC Rule 606 XML reports to PDF format, with Python integration via subprocess.

## Project Structure

```
rule606-generator/
├── pom.xml                           # Maven build (PDFBox 3.x, JUnit 5, shade plugin)
├── oh-20191231.xsd                   # XSD schema
├── run_report.py                     # Python wrapper
├── samples/                          # Sample XML files (A1, B1, B3)
└── src/
    ├── main/java/com/rule606/
    │   ├── Main.java                 # CLI entry point
    │   ├── cli/CliArgs.java          # Argument parsing
    │   ├── xml/                      # XmlValidator, XmlParser, XmlUtils
    │   ├── model/                    # POJOs for A1, B1, B3 reports
    │   ├── pdf/                      # PdfStyles, PdfTableRenderer, generators
    │   └── util/                     # NumberFormatter, DateFormatter
    └── test/java/com/rule606/        # Unit + integration tests
```

## Requirements

- Java 25 (OpenJDK)
- Maven 3.9+
- Python 3.x (for the Python wrapper)

## Build

```bash
mvn clean package
```

This creates a fat JAR at `target/rule606-generator-1.0.0.jar`.

## Usage

### Java CLI

```bash
java -jar target/rule606-generator-1.0.0.jar --xsd oh-20191231.xsd --xml input.xml --output report.pdf
```

Options:
- `--xsd <file>` - XSD schema file for validation (optional)
- `--xml <file>` - Input XML file (required)
- `--output <file>` - Output PDF file (required)
- `--help, -h` - Show help message

### Python Wrapper

Command line:
```bash
python run_report.py --xsd oh-20191231.xsd --xml input.xml --output report.pdf
```

As a module:
```python
from run_report import run_report
exit_code = run_report("schema.xsd", "input.xml", "output.pdf")
```

## Report Types

The tool auto-detects and generates three report types based on the XML root element:

| Type | Root Element | Orientation | Description |
|------|--------------|-------------|-------------|
| **A1** | `heldOrderRoutingPublicReport` | Landscape | Quarterly public report with summary tables, venue tables (14 cols), material aspects |
| **B1** | `heldExemptNotHeldOrderRoutingCustomerReport` | Portrait | Customer routing with order/route/transaction tables, multi-file chunking at 500 rows |
| **B3** | `notHeldOrderHandlingCustomerReport` | Landscape | Monthly report with summary, IOI venues, and 22-column detail tables |

## Testing

```bash
mvn test
```

## Examples

Generate an A1 report:
```bash
java -jar target/rule606-generator-1.0.0.jar \
  --xsd oh-20191231.xsd \
  --xml samples/606a1_held_order_public_report.xml \
  --output a1_report.pdf
```

Generate B1 reports (outputs multiple files for each section):
```bash
java -jar target/rule606-generator-1.0.0.jar \
  --xsd oh-20191231.xsd \
  --xml samples/606b1_held_exempt_not_held_customer_routing.xml \
  --output b1_report.pdf
# Outputs: b1_report_section_1.pdf, b1_report_section_2.pdf, b1_report_section_3.pdf
```

Generate a B3 report:
```bash
java -jar target/rule606-generator-1.0.0.jar \
  --xsd oh-20191231.xsd \
  --xml samples/606b3_not_held_customer_handling.xml \
  --output b3_report.pdf
```

## Exit Codes

- `0` - Success
- `1` - Invalid arguments or file not found
- `2` - XML validation failed
- `3` - Processing error

## License

Based on the SEC's Rule 606 Report Generator. Data and content created by government employees within the scope of their employment are not subject to domestic copyright protection (17 U.S.C. 105).
