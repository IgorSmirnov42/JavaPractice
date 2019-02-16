package ru.spbhse.smirnov.database;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class DatabaseClient {
    public static void main(String[] args) throws SQLException, IOException {
        execute(new BufferedReader(new InputStreamReader(System.in)), new BufferedWriter(new OutputStreamWriter(System.out)));
    }

    public static void execute(BufferedReader reader, BufferedWriter writer) throws IOException, SQLException {
        var database = new PhoneDatabase("mainDatabase");
        boolean executing = true;
        while (executing) {
            writer.write("Write command name (8 for help): ");
            writer.flush();
            String argument;
            if ((argument = reader.readLine()) == null) {
                break;
            }
            String name;
            String phone;
            try {
                switch (argument) {
                    case "0":
                        executing = false;
                        break;
                    case "1":
                        writer.write("Write owner name: ");
                        writer.flush();
                        name = reader.readLine();
                        writer.write("Write phone number: ");
                        writer.flush();
                        phone = reader.readLine();
                        database.addRecord(name, phone);
                        break;
                    case "2":
                        writer.write("Write owner name: ");
                        writer.flush();
                        name = reader.readLine();
                        List<String> phones = database.getAllPhonesByName(name);
                        for (String currentPhone : phones) {
                            writer.write(currentPhone);
                            writer.newLine();
                        }
                        writer.flush();
                        break;
                    case "3":
                        writer.write("Write phone number: ");
                        writer.flush();
                        phone = reader.readLine();
                        List<String> names = database.getAllNamesByPhone(phone);
                        for (String currentName : names) {
                            writer.write(currentName);
                            writer.newLine();
                        }
                        writer.flush();
                        break;
                    case "4":
                        writer.write("Write owner name: ");
                        writer.flush();
                        name = reader.readLine();
                        writer.write("Write phone number: ");
                        writer.flush();
                        phone = reader.readLine();
                        database.deleteRecord(name, phone);
                        break;
                    case "5":
                        writer.write("Write owner name: ");
                        writer.flush();
                        name = reader.readLine();
                        writer.write("Write phone number: ");
                        writer.flush();
                        phone = reader.readLine();
                        writer.write("Write new owner name: ");
                        writer.flush();
                        String newName = reader.readLine();
                        database.replaceNameByPair(name, phone, newName);
                        break;
                    case "6":
                        writer.write("Write owner name: ");
                        writer.flush();
                        name = reader.readLine();
                        writer.write("Write phone number: ");
                        writer.flush();
                        phone = reader.readLine();
                        writer.write("Write new phone number: ");
                        writer.flush();
                        String newPhone = reader.readLine();
                        database.replacePhoneByPair(name, phone, newPhone);
                        break;
                    case "7":
                        List<NamePhonePair> pairs = database.getAllNamePhonePairs();
                        for (NamePhonePair pair : pairs) {
                            writer.write(pair.getOwnerName() + " " + pair.getPhoneNumber());
                            writer.newLine();
                        }
                        writer.flush();
                        break;
                    case "8":
                        writer.write("0 to exit\n" +
                                "1 to add new record\n" +
                                "2 to get all phones numbers of given person\n" +
                                "3 to get all owners of given phone number\n" +
                                "4 to delete record\n" +
                                "5 to replace owner's name in pair\n" +
                                "6 to replace owner's phone in pair\n" +
                                "7 to print all pairs in database\n");
                        writer.flush();
                        break;
                    default:
                        writer.write("Unknown command name");
                        writer.newLine();
                        writer.flush();
                }
            } catch (IllegalArgumentException e) {
                writer.write(e.getMessage());
                writer.flush();
            }
        }
    }
}
