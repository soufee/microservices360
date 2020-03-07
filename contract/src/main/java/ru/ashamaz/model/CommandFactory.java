package ru.ashamaz.model;

public interface CommandFactory {

    Command getRegistrationCommand(AbstractClient client);

    Command getUnregister(AbstractClient client);

    Command getRequestCommand(RqCommand request);

    Command getMessageCommand(Message message);
}
