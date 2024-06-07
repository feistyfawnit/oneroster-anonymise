package com.onerosteranonymise;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
public class OnerosterAnonymiseApplication {
    private static final Faker FAKER = new Faker(Locale.US);
    private static final Map<String, String> ANONYMIZATION_CACHE = new HashMap<>();
    private static final String FAKE_EMAIL_DOMAIN = "@fake.hmhco.com";
    private static final boolean POPULATE_EMPTY_FIELDS = false;
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_GIVEN_NAME = "givenName";
    private static final String FIELD_FAMILY_NAME = "familyName";
    private static final String FIELD_MIDDLE_NAME = "middleName";

    private static final Set<String> FIELDS_TO_ANONYMIZE = Set.of(
            FIELD_USERNAME, FIELD_GIVEN_NAME, FIELD_FAMILY_NAME, FIELD_MIDDLE_NAME, "identifier",
            "email", "sms", "phone", "password"
    );

    public static void main(String[] args) {
        String inputFile = "/Users/terryi/Desktop/users.csv";
        String outputFile = "/Users/terryi/Desktop/anonymised_users.csv";

        anonymiseFile(inputFile, outputFile);
    }

    private static void anonymiseFile(String inputFile, String outputFile) {
        try (var inputFileReader = new FileReader(inputFile);
             var csvParser = new CSVParser(inputFileReader,
                     CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true) // Optional: If your input file already has a header row, set this to true
                     .build());
             var writer = new FileWriter(outputFile);
             var csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(csvParser.getHeaderNames().toArray(new String[0])))) {

            csvParser.forEach(inputRecord -> {
                var anonymisedRecord = csvParser.getHeaderNames().stream()
                        .map(fieldName -> anonymizeField(fieldName, inputRecord.get(fieldName)))
                        .toList();
                try {
                    csvPrinter.printRecord(anonymisedRecord);
                } catch (IOException e) {
                    log.error("Error writing record: {}", e.getMessage());
                }
            });

            log.error("File anonymized successfully!");
        } catch (IOException e) {
            log.error("Error anonymizing file: {}", e.getMessage());
        }
    }

    private static String anonymizeField(String fieldName, String fieldValue) {
        if (!FIELDS_TO_ANONYMIZE.contains(fieldName) || (!POPULATE_EMPTY_FIELDS && fieldValue.trim().isEmpty())) {
            return fieldValue;
        }

        return ANONYMIZATION_CACHE.computeIfAbsent(fieldName + ":" + fieldValue, k -> {
            Map<String, String> personInfo = new HashMap<>();
            return switch (fieldName) {
                case FIELD_USERNAME -> getOrGeneratePersonInfo(personInfo, FIELD_USERNAME, () -> FAKER.name().username());
                case FIELD_GIVEN_NAME -> getOrGeneratePersonInfo(personInfo, FIELD_GIVEN_NAME, () -> FAKER.name().firstName());
                case FIELD_FAMILY_NAME -> getOrGeneratePersonInfo(personInfo, FIELD_FAMILY_NAME, () -> FAKER.name().lastName());
                case FIELD_MIDDLE_NAME -> getOrGeneratePersonInfo(personInfo, FIELD_MIDDLE_NAME, () -> FAKER.name().firstName());
                case "identifier" -> FAKER.idNumber().valid();
                case "email" -> {
                    String givenName = personInfo.get(FIELD_GIVEN_NAME);
                    String familyName = personInfo.get(FIELD_FAMILY_NAME);
                    if (givenName == null || familyName == null) {
                        givenName = getOrGeneratePersonInfo(personInfo, FIELD_GIVEN_NAME, () -> FAKER.name().firstName());
                        familyName = getOrGeneratePersonInfo(personInfo, FIELD_FAMILY_NAME, () -> FAKER.name().lastName());
                    }
                    yield (givenName.toLowerCase() + "." + familyName.toLowerCase() + FAKE_EMAIL_DOMAIN);
                }
                case "sms", "phone" -> FAKER.phoneNumber().phoneNumber();
                case "password" -> FAKER.internet().password();
                default -> fieldValue;
            };
        });
    }

    private static String getOrGeneratePersonInfo(Map<String, String> personInfo, String key, Supplier<String> generator) {
        return personInfo.computeIfAbsent(key, k -> generator.get());
    }
}