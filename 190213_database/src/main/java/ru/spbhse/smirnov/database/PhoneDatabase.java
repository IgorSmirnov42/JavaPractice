package ru.spbhse.smirnov.database;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to operate with database that stores pairs of person name and his/her/its telephone number
 * Implemented with sqlite
 * SQL injections prevented
 * Each person may have many phone numbers and each number can belong to many persons
 * Phone number and name are strings
 */
public class PhoneDatabase {
    @NotNull private final String databaseUrl;

    /**
     * Constructs database with given name if it doesn't exist.
     * Database url is constructed by short name
     * @param databaseShortName name for file with your database
     */
    public PhoneDatabase(@NotNull String databaseShortName) throws SQLException {
        databaseUrl = "jdbc:sqlite:" + databaseShortName + ".db";
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS Owners ("
                        + "id   INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL,"
                        + "UNIQUE (name)"
                        + ")");
                statement.execute("CREATE TABLE IF NOT EXISTS Phones ("
                        + "id          INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "phoneNumber TEXT NOT NULL,"
                        + "UNIQUE (phoneNumber)"
                        + ")");
                statement.execute("CREATE TABLE IF NOT EXISTS OwnersPhones ("
                        + "ownerId INTEGER,"
                        + "phoneId INTEGER,"
                        + "FOREIGN KEY(ownerId) REFERENCES Owners(id),"
                        + "FOREIGN KEY(phoneId) REFERENCES Phones(id),"
                        + "UNIQUE(ownerId, phoneId) ON CONFLICT REPLACE"
                        + ")");
            }
        }
    }

    /** Returns list of pairs stored in database */
    @NotNull
    public List<NamePhonePair> getAllNamePhonePairs() throws SQLException {
        List<NamePhonePair> allPairs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT Owners.name, Phones.phoneNumber FROM Phones, Owners, OwnersPhones "
                                + "WHERE Phones.id = OwnersPhones.phoneId "
                                + "AND Owners.id = OwnersPhones.ownerId ")) {
                    while (resultSet.next()) {
                        allPairs.add(new NamePhonePair(resultSet.getString("name"),
                                resultSet.getString("phoneNumber")));
                    }
                }
            }
        }
        return allPairs;
    }

    /** Returns list of persons who have phone number same as given */
    @NotNull
    public List<String> getAllNamesByPhone(@NotNull String phoneNumber) throws SQLException {
        List<String> allNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "SELECT Owners.name FROM Phones, Owners, OwnersPhones "
                    + "WHERE Phones.id = OwnersPhones.phoneId "
                    + "AND Owners.id = OwnersPhones.ownerId "
                    + "AND Phones.phoneNumber = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, phoneNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        allNames.add(resultSet.getString("name"));
                    }
                }
            }
        }
        return allNames;
    }

    /** Returns list of phone numbers that belong to person with given name */
    @NotNull
    public List<String> getAllPhonesByName(@NotNull String name) throws SQLException {
        List<String> allPhones = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "SELECT Phones.phoneNumber FROM Phones, Owners, OwnersPhones "
                    + "WHERE Phones.id = OwnersPhones.phoneId "
                    + "AND Owners.id = OwnersPhones.ownerId "
                    + "AND Owners.name = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        allPhones.add(resultSet.getString("phoneNumber"));
                    }
                }
            }
        }
        return allPhones;
    }

    /** Adds new pair name-phone to database if it wasn't there before */
    public void addRecord(@NotNull String ownerName, @NotNull String phoneNumber) throws SQLException {
        addNameIfNotExists(ownerName);
        addPhoneIfNotExists(phoneNumber);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "INSERT OR IGNORE INTO OwnersPhones(ownerId, phoneId)"
                    + "VALUES ((SELECT id FROM Owners WHERE name = ?),"
                    + " (SELECT id FROM Phones WHERE phoneNumber = ?))";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, ownerName);
                statement.setString(2, phoneNumber);
                statement.executeUpdate();
            }
        }
    }

    /**
     * Deletes pair name-phone from database if it was there before
     * Deletes pair only from OwnersPhones table
     */
    public void deleteRecord(@NotNull String ownerName, @NotNull String phoneNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "DELETE FROM OwnersPhones "
                    + "WHERE ownerId = (SELECT id FROM Owners WHERE name = ?) "
                    + "AND phoneId = (SELECT id FROM Phones WHERE phoneNumber = ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, ownerName);
                statement.setString(2, phoneNumber);
                statement.executeUpdate();
            }
        }
    }

    /**
     * Replaces name in pair name-phone
     * If there was pair newName-phoneNumber doesn't create new one
     * Doesn't delete unnecessary elements of Owners and Phones tables
     */
    @SuppressWarnings("Duplicates")
    public void replaceNameByPair(@NotNull String oldName, @NotNull String phoneNumber, @NotNull String newName) throws SQLException {
        addNameIfNotExists(newName);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "UPDATE OwnersPhones "
                    + "SET ownerId = (SELECT id FROM Owners WHERE name = ?) "
                    + "WHERE ownerId = "
                    + "(SELECT id FROM Owners WHERE name = ?) "
                    + "AND phoneId = "
                    + "(SELECT id FROM Phones WHERE phoneNumber = ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newName);
                statement.setString(2, oldName);
                statement.setString(3, phoneNumber);
                statement.executeUpdate();
            }
        }
    }

    /**
     * Replaces phone in pair name-phone
     * If there was pair name-newPhoneNumber doesn't create new one
     * Doesn't delete unnecessary elements of Owners and Phones tables
     */
    @SuppressWarnings("Duplicates")
    public void replacePhoneByPair(@NotNull String name, @NotNull String oldPhoneNumber, @NotNull String newPhoneNumber) throws SQLException {
        addPhoneIfNotExists(newPhoneNumber);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "UPDATE OwnersPhones "
                    + "SET phoneId = (SELECT id FROM Phones WHERE phoneNumber = ?) "
                    + "WHERE ownerId = "
                    + "(SELECT id FROM Owners WHERE name = ?) "
                    + "AND phoneId = "
                    + "(SELECT id FROM Phones WHERE phoneNumber = ?) ";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newPhoneNumber);
                statement.setString(2, name);
                statement.setString(3, oldPhoneNumber);
                statement.executeUpdate();
            }
        }
    }

    /** Adds new name to Owners table if is wasn't presented there */
    private void addNameIfNotExists(@NotNull String name) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "INSERT OR IGNORE INTO Owners(name) VALUES(?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }
        }
    }

    /** Adds new phone number to Phones table if is wasn't presented there */
    private void addPhoneIfNotExists(@NotNull String phoneNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "INSERT OR IGNORE INTO Phones(phoneNumber) VALUES(?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, phoneNumber);
                statement.executeUpdate();
            }
        }
    }

    /** Clears all tables in database */
    public void clear() throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM Owners");
                statement.execute("DELETE FROM Phones");
                statement.execute("DELETE FROM OwnersPhones");
                statement.execute("VACUUM");
            }
        }
    }
}
