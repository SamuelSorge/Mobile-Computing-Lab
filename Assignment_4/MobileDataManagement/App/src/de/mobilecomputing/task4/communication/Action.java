package de.mobilecomputing.task4.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class Action implements Serializable {

    private static final long serialVersionUID = 4591229014903472154L;

    private ActionId actionId;
    private List<Message> messages;

    public Action(ActionId actionId, List<Message> messages) {
        this.actionId = actionId;
        if (messages != null) {
            this.messages = messages;
        } else {
            this.messages = new ArrayList<>();
        }
    }

    public ActionId getActionId() {
        return actionId;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
