package com.cnsky1103.utils;

public class Pair<L, R> {
    public L l;
    public R r;

    public Pair(L l, R r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Pair<L, R> other = (Pair<L, R>) obj;
        
        return other.l.equals(this.l) && other.r.equals(this.r);
    }

    @Override
    public int hashCode() {
        return l.hashCode() * 19 + r.hashCode();
    }
}
