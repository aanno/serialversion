package com.github.aanno.serialversion;

import com.google.common.base.Objects;

public class DiffResultConfig {

    private boolean sort = true;
    private boolean serializableOnly = true;
    private boolean sameClasses = false;

    public DiffResultConfig() {
    }

    public DiffResultConfig setSort(boolean sort) {
        this.sort = sort;
        return this;
    }

    public boolean isSort() {
        return sort;
    }

    public boolean isSerializableOnly() {
        return serializableOnly;
    }

    public boolean isSameClasses() {
        return sameClasses;
    }

    public DiffResultConfig setSerializableOnly(boolean serializableOnly) {
        this.serializableOnly = serializableOnly;
        return this;
    }

    public DiffResultConfig setSameClasses(boolean sameClasses) {
        this.sameClasses = sameClasses;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DiffResultConfig that = (DiffResultConfig) o;
        return sort == that.sort && serializableOnly == that.serializableOnly && sameClasses == that.sameClasses;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sort, serializableOnly, sameClasses);
    }

    @Override
    public String toString() {
        return "DiffResultFormatterConfig{" +
                "sort=" + sort +
                ", serializableOnly=" + serializableOnly +
                ", sameClasses=" + sameClasses +
                '}';
    }
}
