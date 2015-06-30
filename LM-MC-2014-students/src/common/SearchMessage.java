package common;

import java.io.Serializable;

/**
 * Created by SebastianHesse on 30.06.2015.
 */
public class SearchMessage extends BaseMessage implements Serializable {
    private static final long serialVersionUID = -1782946196255935187L;

    public String sourceId;
    public String targetId;
}
