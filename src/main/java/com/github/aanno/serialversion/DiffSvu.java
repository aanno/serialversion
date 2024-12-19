package com.github.aanno.serialversion;

import com.google.common.base.Objects;

public class DiffSvu {

    private long suv1;

    private long suv2;

    public DiffSvu(long suv1, long suv2) {
        this.suv1 = suv1;
        this.suv2 = suv2;
    }

    public long getSuv1() {
        return suv1;
    }

    public long getSuv2() {
        return suv2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DiffSvu diffSvu = (DiffSvu) o;
        return suv1 == diffSvu.suv1 && suv2 == diffSvu.suv2;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(suv1, suv2);
    }

    @Override
    public String toString() {
        return "SvuDiff{" +
                "suv1=" + suv1 +
                ", suv2=" + suv2 +
                '}';
    }
}
