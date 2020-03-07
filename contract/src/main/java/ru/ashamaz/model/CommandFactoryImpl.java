package ru.ashamaz.model;

import com.google.gson.Gson;

/**
 * An implementation of CommandFactory with logic of creating Commands of different types
 */
public class CommandFactoryImpl implements CommandFactory {
    private Gson gson = new Gson();

    @Override
    public Command getRegistrationCommand(AbstractClient client) {
        return new Command(Commands.REGISTER, gson.toJson(new RegistrationRequest(client.getName())));
    }

    @Override
    public Command getUnregister(AbstractClient client) {
        return new Command(Commands.UNREGISTER, gson.toJson(new RegistrationRequest(client.getName())));
    }

    @Override
    public Command getRequestCommand(RqCommand request) {
        return new Command(Commands.REQUEST, gson.toJson(request));
    }

    @Override
    public Command getMessageCommand(Message message) {
        return new Command(Commands.MESSAGE, gson.toJson(message));
    }
}
