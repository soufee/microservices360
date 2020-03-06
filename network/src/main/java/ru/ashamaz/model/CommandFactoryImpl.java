package ru.ashamaz.model;

import com.google.gson.Gson;

public class CommandFactoryImpl implements CommandFactory {
    private Gson gson = new Gson();
    @Override
    public Command getRegistrationCommand(AbstractClient client) {
        return new Command(Commands.REGISTRATION, gson.toJson(new RegistrationRequest(client.getName(), client.getConnection().toString())));
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
