package ru.spbhse.smirnov.database;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Interaction with database.
 * To interact from console run main
 */
public class DatabaseInteractor {
    public static void main(String[] args) throws SQLException, IOException {
        execute(new BufferedReader(new InputStreamReader(System.in)),
                /* You told not to use BufferedWriter because I always have to to flush()
                   But, as I found, most java Writers (PrintWriter, OutputStreamWriter) don't print result at once
                   So, a code below, as I think, is the simplest way to work with console as writer
                */
                new Writer() {
                    @Override
                    public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
                        StringBuilder string = new StringBuilder();
                        for (int position = off; position < off + len; ++position) {
                            string.append(cbuf[position]);
                        }
                        System.out.print(string);
                    }

                    @Override
                    public void flush() throws IOException {}

                    @Override
                    public void close() throws IOException {}
                },
                "mainDatabase");
    }

    /**
     * Starts interaction with database
     * After exit saves database
     */
    public static void execute(@NotNull BufferedReader reader, @NotNull Writer writer,
                               @NotNull String databaseName) throws IOException, SQLException {
        var database = new PhoneDatabase(databaseName);
        boolean executing = true;
        while (executing) {
            writer.write("Write command name (8 for help): ");
            String argument;
            if ((argument = reader.readLine()) == null) {
                break;
            }
            try {
                switch (argument) {
                    case "0":
                        executing = false;
                        break;
                    case "1":
                        writer.write("Write owner name: ");
                        String name = reader.readLine();
                        writer.write("Write phone number: ");
                        String phone = reader.readLine();
                        database.addRecord(name, phone);
                        break;
                    case "2":
                        writer.write("Write owner name: ");
                        name = reader.readLine();
                        List<String> phones = database.getAllPhonesByName(name);
                        for (String currentPhone : phones) {
                            writer.write(currentPhone);
                            writer.write("\n");
                        }
                        break;
                    case "3":
                        writer.write("Write phone number: ");
                        phone = reader.readLine();
                        List<String> names = database.getAllNamesByPhone(phone);
                        for (String currentName : names) {
                            writer.write(currentName);
                            writer.write("\n");
                        }
                        break;
                    case "4":
                        writer.write("Write owner name: ");
                        name = reader.readLine();
                        phone = askForPhoneNumberIfNeeded(writer, reader, name, database);
                        database.deleteRecord(name, phone);
                        break;
                    case "5":
                        writer.write("Write owner name: ");
                        name = reader.readLine();
                        phone = askForPhoneNumberIfNeeded(writer, reader, name, database);
                        writer.write("Write new owner name: ");
                        String newName = reader.readLine();
                        database.replaceNameByPair(name, phone, newName);
                        break;
                    case "6":
                        writer.write("Write owner name: ");
                        name = reader.readLine();
                        phone = askForPhoneNumberIfNeeded(writer, reader, name, database);
                        writer.write("Write new phone number: ");
                        String newPhone = reader.readLine();
                        database.replacePhoneByPair(name, phone, newPhone);
                        break;
                    case "7":
                        List<NamePhonePair> pairs = database.getAllNamePhonePairs();
                        for (NamePhonePair pair : pairs) {
                            writer.write(pair.getOwnerName() + " " + pair.getPhoneNumber());
                            writer.write("\n");
                        }
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
                        break;
                    default:
                        writer.write("Unknown command name");
                        writer.write("\n");
                }
            } catch (IllegalArgumentException | SQLException e) {
                writer.write(e.getMessage());
                writer.write("\n");
            }
        }
    }

    private static String askForPhoneNumberIfNeeded(@NotNull Writer writer,
                                                    @NotNull BufferedReader reader,
                                                    String name,
                                                    PhoneDatabase database) throws IOException, SQLException {
        String phone;
        List<String> phoneNumbers = database.getAllPhonesByName(name);
        if (phoneNumbers.size() != 1) {
            writer.write("Write phone number: ");
            phone = reader.readLine();
        } else {
            phone = phoneNumbers.get(0);
        }
        return phone;
    }
}
