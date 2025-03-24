# SystemVerilog Importer Plugin

## Overview
The SystemVerilog Importer Plugin is designed to facilitate the importing and exporting of SystemVerilog files within a development environment. This plugin provides functionality to parse SystemVerilog requirement comments, tokenize source code, and manage import/export requests efficiently.

## Features
- **Importing SystemVerilog Files**: The plugin can read and parse SystemVerilog files, extracting relevant information from requirement comments.
- **Exporting Data**: It allows for the exporting of parsed data back into SystemVerilog format or other specified formats.
- **Tokenization**: The plugin includes a lexer that tokenizes SystemVerilog source code for easier processing and analysis.
- **Unit Testing**: Comprehensive unit tests ensure the reliability and correctness of the parsing functionality.

## Installation
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd systemverilog-importer
   ```
3. Build the project using Gradle:
   ```
   ./gradlew build
   ```

## Usage
- To use the importer, integrate it into your application by registering the `SVImporterPlugin` with the appropriate service.
- For exporting data, utilize the `SVExporterPlugin` to manage export requests and format the output as needed.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.