package common;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Created by SebastianHesse on 30.06.2015.
 */
public class LocationUpdateMessage extends BaseMessage implements Serializable {

    private static final long serialVersionUID = 8834682851114365174L;
    public String id;
    public Rectangle2D currentLA;
    public Rectangle2D previousLA;
}
