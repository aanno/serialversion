package com.github.aanno.serialversion;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DiffResult {

    private Set<String> onlyInA;
    private Set<String> onlyInB;
    private Set<String> sameInAandB;
    private Map<String, DiffSvu> svuDiff = new HashMap<>();

    public DiffResult(Set<String> onlyInA, Set<String> onlyInB, Set<String> sameInAandB, Map<String, DiffSvu> svuDiff) {
        this.onlyInA = onlyInA;
        this.onlyInB = onlyInB;
        this.sameInAandB = sameInAandB;
        this.svuDiff = svuDiff;
    }

    public DiffResult(Set<String> onlyInA, Set<String> onlyInB) {
        this.onlyInA = onlyInA;
        this.onlyInB = onlyInB;
    }

    public Set<String> getOnlyInA() {
        return onlyInA;
    }

    public Set<String> getOnlyInB() {
        return onlyInB;
    }

    public Set<String> getSameInAandB() {
        return sameInAandB;
    }

    public Map<String, DiffSvu> getSvuDiff() {
        return svuDiff;
    }

    @Override
    public String toString() {
        return "DiffResult{" +
                "onlyInA=" + onlyInA +
                ", onlyInB=" + onlyInB +
                ", sameInAandB=" + sameInAandB +
                ", svuDiff=" + svuDiff +
                '}';
    }
}
