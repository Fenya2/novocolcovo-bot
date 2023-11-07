package core.service_handlers.handlers;

import models.Message;

public abstract class ServiceHandler {
    public abstract int handle(Message message);
}
