package org.acme.quickstart;

/**
 * OldFashionPound
 */
public class OldFashionPound {

    public int sterline;

    public int scellini;

    public int pence;

    public OldFashionPound() {
        super();
    }

    public OldFashionPound(int sterline, int scellini, int pence) {
        super();
        this.sterline = sterline;
        this.scellini = scellini;
        this.pence = pence;
    }

    @Override
    public String toString() {
        return String.format("%sp %ss %sd", this.sterline, this.scellini, this.pence);
    }
}
