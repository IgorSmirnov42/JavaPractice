package ru.spbhse.smirnov.database;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        var db = new PhoneDatabase("TEST");
        db.addRecord("a", "a");
        db.addRecord("a", "b");
        db.replacePhoneByPair("a", "b", "a");

        var gg = db.getAllNamePhonePairs();
        for (var t : gg) {
            System.out.println(t.getOwnerName() + " " + t.getPhoneNumber());
        }
    }
}
