package ru.spbhse.smirnov.database;

import org.jetbrains.annotations.NotNull;

/** Stores immutable pair of phone number and its owner's name */
class NamePhonePair {
    @NotNull private final String phoneNumber;
    @NotNull private final String ownerName;

    @NotNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @NotNull
    public String getOwnerName() {
        return ownerName;
    }

    NamePhonePair(@NotNull String ownerName, @NotNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.ownerName = ownerName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof NamePhonePair) {
            var otherPair = (NamePhonePair) other;
            return phoneNumber.equals(otherPair.phoneNumber) &&
                    ownerName.equals(otherPair.ownerName);
        }
        return false;
    }
}
