"""
SEC Rule 606 Report Generator - Python wrapper.

Usage as CLI:
    python run_report.py --xsd schema.xsd --xml input.xml --output report.pdf

Usage as module:
    from run_report import run_report
    exit_code = run_report("schema.xsd", "input.xml", "output.pdf")
"""

import subprocess
import sys
import os
import glob


def find_jar():
    """Auto-discover the fat JAR in the target/ directory."""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    target_dir = os.path.join(script_dir, "target")
    pattern = os.path.join(target_dir, "rule606-generator-*.jar")
    jars = glob.glob(pattern)
    # Prefer the shaded jar (without 'original-' prefix)
    jars = [j for j in jars if not os.path.basename(j).startswith("original-")]
    if not jars:
        raise FileNotFoundError(
            f"No JAR found in {target_dir}. Run 'mvn clean package' first."
        )
    return sorted(jars)[-1]  # latest version


def run_report(xsd_path=None, xml_path=None, output_path=None, timeout=120):
    """
    Run the Rule 606 Report Generator JAR.

    Args:
        xsd_path: Path to XSD schema file (optional).
        xml_path: Path to input XML file (required).
        output_path: Path to output PDF file (required).
        timeout: Timeout in seconds (default 120).

    Returns:
        Exit code from the Java process.
    """
    if xml_path is None:
        raise ValueError("xml_path is required")
    if output_path is None:
        raise ValueError("output_path is required")

    jar_path = find_jar()
    cmd = ["java", "-jar", jar_path]
    if xsd_path:
        cmd.extend(["--xsd", xsd_path])
    cmd.extend(["--xml", xml_path, "--output", output_path])

    try:
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            timeout=timeout,
        )
        if result.stdout:
            print(result.stdout, end="")
        if result.stderr:
            print(result.stderr, end="", file=sys.stderr)
        return result.returncode
    except subprocess.TimeoutExpired:
        print(f"Error: Process timed out after {timeout} seconds", file=sys.stderr)
        return 124
    except FileNotFoundError as e:
        print(f"Error: {e}", file=sys.stderr)
        return 127


def main():
    import argparse

    parser = argparse.ArgumentParser(description="SEC Rule 606 Report Generator")
    parser.add_argument("--xsd", help="XSD schema file for validation")
    parser.add_argument("--xml", required=True, help="Input XML file")
    parser.add_argument("--output", required=True, help="Output PDF file")
    parser.add_argument(
        "--timeout", type=int, default=120, help="Timeout in seconds (default: 120)"
    )
    args = parser.parse_args()

    exit_code = run_report(
        xsd_path=args.xsd,
        xml_path=args.xml,
        output_path=args.output,
        timeout=args.timeout,
    )
    sys.exit(exit_code)


if __name__ == "__main__":
    main()
