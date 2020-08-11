package com.paypad.vuk507.square;

import android.text.Editable;
import android.text.InputFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SimpleEditable implements Editable {
    private String text;

    public void clear() {
    }

    public void clearSpans() {
    }

    public void getChars(int i, int i2, char[] cArr, int i3) {
    }

    public InputFilter[] getFilters() {
        return new InputFilter[0];
    }

    public int getSpanEnd(Object obj) {
        return 0;
    }

    public int getSpanFlags(Object obj) {
        return 0;
    }

    public int getSpanStart(Object obj) {
        return 0;
    }

    public <T> T[] getSpans(int i, int i2, Class<T> cls) {
        return null;
    }

    public int nextSpanTransition(int i, int i2, Class cls) {
        return 0;
    }

    public void removeSpan(Object obj) {
    }

    public void setFilters(InputFilter[] inputFilterArr) {
    }

    public void setSpan(Object obj, int i, int i2, int i3) {
    }

    public SimpleEditable(CharSequence charSequence) {
        this(charSequence.toString());
    }

    public SimpleEditable(String str) {
        this.text = str;
    }

    public Editable replace(int i, int i2, CharSequence charSequence, int i3, int i4) {
        String str = this.text;
        this.text = str.replaceFirst(Pattern.quote(str.substring(i, i2)), Matcher.quoteReplacement(charSequence.toString()));
        return this;
    }

    public Editable replace(int i, int i2, CharSequence charSequence) {
        return replace(i, i2, charSequence, 0, charSequence.length());
    }

    public Editable insert(int i, CharSequence charSequence, int i2, int i3) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public Editable insert(int i, CharSequence charSequence) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public Editable delete(int i, int i2) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public Editable append(CharSequence charSequence) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public Editable append(CharSequence charSequence, int i, int i2) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public Editable append(char c) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public int length() {
        return this.text.length();
    }

    public char charAt(int i) {
        return this.text.charAt(i);
    }

    public CharSequence subSequence(int i, int i2) {
        return this.text.subSequence(i, i2);
    }

    public String toString() {
        return this.text;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == String.class) {
            return this.text.equals(obj);
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.text.equals(((SimpleEditable) obj).text);
    }

    public int hashCode() {
        return this.text.hashCode();
    }
}
