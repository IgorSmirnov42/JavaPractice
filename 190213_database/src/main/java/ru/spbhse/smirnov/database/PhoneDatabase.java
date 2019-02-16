package ru.spbhse.smirnov.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhoneDatabase {
    private final String databaseUrl;

    public PhoneDatabase(String databaseShortName) throws SQLException {
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

    public List<String> getAllNamesByPhone(String phoneNumber) throws SQLException {
        List<String> allNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT Owners.name FROM Phones, Owners, OwnersPhones "
                                + "WHERE Phones.id = OwnersPhones.phoneId "
                                + "AND Owners.id = OwnersPhones.ownerId "
                                + "AND Phones.phoneNumber = '" + phoneNumber + "'")) {
                    while (resultSet.next()) {
                        allNames.add(resultSet.getString("name"));
                    }
                }
            }
        }
        return allNames;
    }

    public List<String> getAllPhonesByName(String name) throws SQLException {
        List<String> allPhones = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT Phones.phoneNumber FROM Phones, Owners, OwnersPhones "
                                + "WHERE Phones.id = OwnersPhones.phoneId "
                                + "AND Owners.id = OwnersPhones.ownerId "
                                + "AND Owners.name = '" + name + "'")) {
                    while (resultSet.next()) {
                        allPhones.add(resultSet.getString("phoneNumber"));
                    }
                }
            }
        }
        return allPhones;
    }

    public void addRecord(String ownerName, String phoneNumber) throws SQLException {
        addNameIfNotExists(ownerName);
        addPhoneIfNotExists(phoneNumber);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("INSERT OR IGNORE INTO OwnersPhones(ownerId, phoneId)"
                        + "VALUES ((SELECT id FROM Owners WHERE name = '" + ownerName + "'),"
                        + " (SELECT id FROM Phones WHERE phoneNumber = '" + phoneNumber +"'))"
                        );
            }
        }
    }

    public void deleteRecord(String ownerName, String phoneNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM OwnersPhones "
                        + "WHERE ownerId = (SELECT id FROM Owners WHERE name = '" + ownerName + "') "
                        + "AND phoneId = (SELECT id FROM Phones WHERE phoneNumber = '" + phoneNumber + "')");
            }
        }
    }

    public void replaceNameByPair(String oldName, String phoneNumber, String newName) throws SQLException {
        addNameIfNotExists(newName);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE OwnersPhones "
                        + "SET ownerId = (SELECT id FROM Owners WHERE name = '" + newName +"') "
                        + "WHERE ownerId = "
                        + "(SELECT id FROM Owners WHERE name = '" + oldName +"') "
                        + "AND phoneId = "
                        + "(SELECT id FROM Phones WHERE phoneNumber = '" + phoneNumber +"') ");
            }
        }
    }

    public void replacePhoneByPair(String name, String oldPhoneNumber, String newPhoneNumber) throws SQLException {
        addPhoneIfNotExists(newPhoneNumber);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE OwnersPhones "
                        + "SET phoneId = (SELECT id FROM Phones WHERE phoneNumber = '" + newPhoneNumber +"') "
                        + "WHERE ownerId = "
                        + "(SELECT id FROM Owners WHERE name = '" + name +"') "
                        + "AND phoneId = "
                        + "(SELECT id FROM Phones WHERE phoneNumber = '" + oldPhoneNumber +"') ");
            }
        }
    }

    private void addNameIfNotExists(String name) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("INSERT OR IGNORE INTO Owners(name) VALUES('"+ name + "')");
            }
        }
    }

    private void addPhoneIfNotExists(String phoneNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("INSERT OR IGNORE INTO Phones(phoneNumber) VALUES('"+ phoneNumber + "')");
            }
        }
    }
}
