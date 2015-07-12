package de.mobilecomputing.task4.communication;

import java.io.Serializable;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class Pair<C, S> implements Serializable {

    private C a;
    private S b;

    public Pair(C a, S b) {
        this.a = a;
        this.b = b;
    }

    public C getA() {
        return a;
    }

    public S getB() {
        return b;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
