package ru.ashamaz.model;

import java.io.Serializable;

/**
 * enum of command types
 */
public enum Commands implements Serializable {
    REGISTER,
    UNREGISTER,
    REQUEST,
    MESSAGE
}
