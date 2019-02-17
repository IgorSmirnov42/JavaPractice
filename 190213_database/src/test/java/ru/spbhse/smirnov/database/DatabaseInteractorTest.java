package ru.spbhse.smirnov.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseInteractorTest {

    private static final String databaseName = "test";

    @BeforeEach
    void clearDB() throws SQLException {
        var db = new PhoneDatabase(databaseName);
        db.clear();
    }

    @Test
    void executeExit() throws IOException, SQLException {
        assertTrue(runTest("0.in", "0.out", "0.ideal"));
    }

    @Test
    void executeAddRecord() throws IOException, SQLException {
        assertTrue(runTest("1.in", "1.out", "1.ideal"));
    }

    @Test
    void executeGetPhoneNumbers() throws IOException, SQLException {
        assertTrue(runTest("2.in", "2.out", "2.ideal"));
    }

    @Test
    void executeGetAllOwners() throws IOException, SQLException {
        assertTrue(runTest("3.in", "3.out", "3.ideal"));
    }

    @Test
    void executeDeleteRecord() throws IOException, SQLException {
        assertTrue(runTest("4.in", "4.out", "4.ideal"));
    }

    @Test
    void executeReplaceName() throws IOException, SQLException {
        assertTrue(runTest("5.in", "5.out", "5.ideal"));
    }

    @Test
    void executeReplacePhone() throws IOException, SQLException {
        assertTrue(runTest("6.in", "6.out", "6.ideal"));
    }

    @Test
    void executePrintAll() throws IOException, SQLException {
        assertTrue(runTest("7.in", "7.out", "7.ideal"));
    }

    @Test
    void executeHelp() throws IOException, SQLException {
        assertTrue(runTest("8.in", "8.out", "8.ideal"));
    }

    @Test
    void executeUnknown() throws IOException, SQLException {
        assertTrue(runTest("9.in", "9.out", "9.ideal"));
    }

    @Test
    void executeShouldNotBreakIfEOF() throws IOException, SQLException {
        assertTrue(runTest("10.in", "10.out", "10.ideal"));
    }

    private boolean runTest(String inputFile, String outputFile, String answerFile) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                new File("src/test/resources/" + inputFile))));
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(
                                     new File("src/test/resources/" + outputFile))))) {
            DatabaseInteractor.execute(reader, writer, databaseName);
        }
        return filesAreEqual("src/test/resources/" + outputFile, "src/test/resources/" + answerFile);
    }

    private boolean filesAreEqual(String path1, String path2) throws IOException {
        var firstFile = Files.lines(Paths.get(path1)).collect(Collectors.toList());
        var secondFile = Files.lines(Paths.get(path2)).collect(Collectors.toList());
        return firstFile.equals(secondFile);
    }
}