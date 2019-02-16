package ru.spbhse.smirnov.database;

class NamePhonePair {
    private final String phoneNumber;
    private final String ownerName;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    NamePhonePair(String ownerName, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.ownerName = ownerName;
    }
}
