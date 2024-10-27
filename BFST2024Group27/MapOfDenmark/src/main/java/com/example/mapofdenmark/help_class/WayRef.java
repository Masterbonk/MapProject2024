package com.example.mapofdenmark.help_class;

import com.example.mapofdenmark.Way;

import java.io.Serializable;

import static com.example.mapofdenmark.Model.id2way;

public class WayRef implements Serializable {
    int ref;
    private boolean reverse = false;
    /**
     * The reference is recorded
     * @param ref The reference to the way
     */
    public WayRef(int ref){
        this.ref = ref;
    }

    /**
     * Puts the boolean reverse to true
     */
    public void setReverse(boolean setTo){
        reverse = setTo;
    }

    /**
     * Gets the reference to the way
     * @return Returns reference
     */
    public int getReference() {
        return ref;
    }

    /**
     * Gets the way that the reference is referring to
     * @return Returns the way
     */
    public Way getRefWay(){

        return id2way.get(ref);
    }

    /**
     * Checks whether the boolean reversed is true
     * @return Returns the reverse boolean
     */
    public boolean isReverse() {
        return reverse;
    }
}
