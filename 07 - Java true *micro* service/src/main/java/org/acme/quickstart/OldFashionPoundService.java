package org.acme.quickstart;

import javax.enterprise.context.ApplicationScoped;

import org.graalvm.collections.Pair;

/**
 * OldFashionPoundService
 */
@ApplicationScoped
public class OldFashionPoundService {

    public OldFashionPound sum(final OldFashionPound amount1, final OldFashionPound amount2) {

        OldFashionPound result = new OldFashionPound();
        result.sterline = amount1.sterline + amount2.sterline;
        result.scellini = amount1.scellini + amount2.scellini;
        result.pence = amount1.pence + amount2.pence;

        if (result.pence > 12) {
            int initialPence = result.pence;

            result.scellini = result.scellini + initialPence / 12;
            result.pence = initialPence % 12;
        }

        if (result.scellini > 20) {
            int initialScellini = result.scellini;

            result.sterline = result.sterline + initialScellini / 20;
            result.scellini = initialScellini % 20;
        }

        return result;
    }

    public OldFashionPound difference(final OldFashionPound amount1, final OldFashionPound amount2) {

        OldFashionPound result = new OldFashionPound();
        result.sterline = amount1.sterline - amount2.sterline;
        result.scellini = amount1.scellini - amount2.scellini;
        result.pence = amount1.pence - amount2.pence;

        if (result.pence < 0) {
            result.scellini--;
            result.pence = result.pence + 12;
        }

        if (result.pence < 0) {
            result.sterline--;
            result.scellini = result.scellini + 20;
        }

        return result;
    }

    public OldFashionPound multiply(final OldFashionPound amount, final int factor) {
        OldFashionPound result = new OldFashionPound();
        result.sterline = amount.sterline * factor;
        result.scellini = amount.scellini * factor;
        result.pence = amount.pence * factor;

        if (result.pence > 12) {
            int initialPence = result.pence;

            result.scellini = result.scellini + initialPence / 12;
            result.pence = initialPence % 12;
        }

        if (result.scellini > 20) {
            int initialScellini = result.scellini;

            result.sterline = result.sterline + initialScellini / 20;
            result.scellini = initialScellini % 20;
        }

        return result;
    }

    public Pair<OldFashionPound, OldFashionPound> divide(final OldFashionPound amount, final int factor) {
        OldFashionPound result = new OldFashionPound();
        result.sterline = amount.sterline / factor;
        result.scellini = (amount.scellini + (amount.sterline % factor) * 20) / factor;

        int restScellini = (amount.scellini + (amount.sterline % factor) * 20) % factor;
        result.pence = (amount.pence + restScellini * 12) / factor;

        OldFashionPound rest = new OldFashionPound();
        rest.pence = (amount.pence + restScellini * 12) % factor;

        if (rest.pence > 12) {
            rest.scellini = rest.pence % 12;
            rest.pence = rest.pence - rest.scellini * 12;
        }

        return Pair.create(result, rest);
    }
}
