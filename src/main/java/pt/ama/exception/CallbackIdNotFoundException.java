package pt.ama.exception;

import jakarta.ws.rs.WebApplicationException;

public class CallbackIdNotFoundException extends WebApplicationException {

    public CallbackIdNotFoundException() {
        super("CallbackID not found.");
    }

}
