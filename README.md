# Oneroster Anonymiser

This application anonymises personal data in a oneRoster formatted CSV file. It helps protect sensitive student information while preserving the overall structure of the data.

## Features

- Anonymises fields like username, name, email, and phone numbers.
- Uses Faker library to generate realistic anonymised data.
- Maintains the original CSV format with anonymised values.

## Requirements

- Java Runtime Environment (JRE) 11 or above
- [Apache Commons CSV library](https://commons.apache.org/csv)

## Instructions

1. **Download**: Download the latest executable JAR file for this application (if provided) or compile the source code yourself.
2. **Place Files**: Place your oneRoster formatted CSV file (e.g., `users.csv`) in the same directory as the application (JAR file or compiled class).
3. **Run the Application**: Execute the application:
   - From the command line:
     ```bash
     java -jar /path/to/your/oneroster-anonymiser.jar
     ```
   - From an IDE, run the main method in `OnerosterAnonymiseApplication.java` located at:
     ```bash
     /Users/terryi/Desktop/oneroster-anonymise/src/main/java/com/onerosteranonymise/OnerosterAnonymiseApplication.java
     ```
4. **Specify Input/Output**: If prompted, provide the paths to the input (`users.csv`) and desired output file name for the anonymised data (e.g., `anonymised_users.csv`).

## Output

The application will create a new CSV file with the anonymised data, typically named `anonymised_users.csv` by default (you can specify a different name during execution).

## Customization

- **Source Code**: If you have the source code, you can modify the `FIELDS_TO_ANONymise` set in the code to specify which fields you want to anonymise.
- **Advanced Users**: For advanced customization, refer to the source code for options like handling empty fields or generating specific types of anonymised data.

## Note

This application is designed for demonstration purposes. Consider security best practices and relevant regulations when handling sensitive data in real-world scenarios.
