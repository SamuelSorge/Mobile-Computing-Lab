package common;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Created by SebastianHesse on 30.06.2015.
 */
public class SearchResponseMessage extends BaseMessage implements Serializable {

    private static final long serialVersionUID = -4368275527286140566L;

    public Rectangle2D targetLA;
    public String sourceId;
    public String targetId;
}
