package com.paypad.vuk507.square;


/*import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;*/
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Phrase {
    private static final int EOF = 0;
    private char curChar;
    private int curCharIndex;
    private CharSequence formatted;
    private Token head;
    private final Set<String> keys = new HashSet();
    private final Map<String, CharSequence> keysToValues = new HashMap();
    private final CharSequence pattern;
    private boolean spanSupportedEnabled = true;

    private static class KeyToken extends Token {
        private final String key;
        private CharSequence value;

        KeyToken(Token token, String str) {
            super(token);
            this.key = str;
        }

        /* access modifiers changed from: 0000 */
        public void expand(Editable editable, Map<String, CharSequence> map) {
            this.value = (CharSequence) map.get(this.key);
            int formattedStart = getFormattedStart();
            editable.replace(formattedStart, this.key.length() + formattedStart + 2, this.value);
        }

        /* access modifiers changed from: 0000 */
        public int getFormattedLength() {
            return this.value.length();
        }
    }

    private static class LeftCurlyBracketToken extends Token {
        /* access modifiers changed from: 0000 */
        public int getFormattedLength() {
            return 1;
        }

        LeftCurlyBracketToken(Token token) {
            super(token);
        }

        /* access modifiers changed from: 0000 */
        public void expand(Editable editable, Map<String, CharSequence> map) {
            int formattedStart = getFormattedStart();
            editable.replace(formattedStart, formattedStart + 2, "{");
        }
    }

    private static class TextToken extends Token {
        private final int textLength;

        /* access modifiers changed from: 0000 */
        public void expand(Editable editable, Map<String, CharSequence> map) {
        }

        TextToken(Token token, int i) {
            super(token);
            this.textLength = i;
        }

        /* access modifiers changed from: 0000 */
        public int getFormattedLength() {
            return this.textLength;
        }
    }

    private static abstract class Token {
        /* access modifiers changed from: private */
        public Token next;
        private final Token prev;

        /* access modifiers changed from: 0000 */
        public abstract void expand(Editable editable, Map<String, CharSequence> map);

        /* access modifiers changed from: 0000 */
        public abstract int getFormattedLength();

        protected Token(Token token) {
            this.prev = token;
            if (token != null) {
                token.next = this;
            }
        }

        /* access modifiers changed from: 0000 */
        public final int getFormattedStart() {
            Token token = this.prev;
            if (token == null) {
                return 0;
            }
            return token.getFormattedStart() + this.prev.getFormattedLength();
        }
    }

    public static Phrase from(Fragment fragment, int i) {
        return from(fragment.getResources(), i);
    }

    public static Phrase from(View view, int i) {
        return from(view.getResources(), i);
    }

    public static Phrase from(Context context, int i) {
        return from(context.getResources(), i);
    }

    public static Phrase from(Resources resources, int i) {
        return from(resources.getText(i));
    }

    public static Phrase fromPlural(View view, int i, int i2) {
        return fromPlural(view.getResources(), i, i2);
    }

    public static Phrase fromPlural(Context context, int i, int i2) {
        return fromPlural(context.getResources(), i, i2);
    }

    public static Phrase fromPlural(Resources resources, int i, int i2) {
        return from(resources.getQuantityText(i, i2));
    }

    public static Phrase from(CharSequence charSequence) {
        return new Phrase(charSequence);
    }

    public Phrase put(String str, CharSequence charSequence) {
        if (!this.keys.contains(str)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid key: ");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        } else if (charSequence != null) {
            this.keysToValues.put(str, charSequence);
            this.formatted = null;
            return this;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Null value for '");
            sb2.append(str);
            sb2.append("'");
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public Phrase put(String str, int i) {
        return put(str, (CharSequence) Integer.toString(i));
    }

    public Phrase putOptional(String str, CharSequence charSequence) {
        return this.keys.contains(str) ? put(str, charSequence) : this;
    }

    public Phrase putOptional(String str, int i) {
        return this.keys.contains(str) ? put(str, i) : this;
    }

    public CharSequence format() {
        if (this.formatted == null) {
            if (this.keysToValues.keySet().containsAll(this.keys)) {
                Editable createEditable = createEditable(this.pattern);
                for (Token token = this.head; token != null; token = token.next) {
                    token.expand(createEditable, this.keysToValues);
                }
                this.formatted = createEditable;
            } else {
                HashSet hashSet = new HashSet(this.keys);
                hashSet.removeAll(this.keysToValues.keySet());
                StringBuilder sb = new StringBuilder();
                sb.append("Missing keys: ");
                sb.append(hashSet);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        return this.formatted;
    }

    public void setSpanSupportEnabled(boolean z) {
        this.spanSupportedEnabled = z;
    }

    public void into(TextView textView) {
        if (textView != null) {
            textView.setText(format());
            return;
        }
        throw new IllegalArgumentException("TextView must not be null.");
    }

    public String toString() {
        return this.pattern.toString();
    }

    private Editable createEditable(CharSequence charSequence) {
        if (this.spanSupportedEnabled) {
            return new SpannableStringBuilder(charSequence);
        }
        return new SimpleEditable(charSequence);
    }

    private Phrase(CharSequence charSequence) {
        char c = 0;
        if (charSequence.length() > 0) {
            c = charSequence.charAt(0);
        }
        this.curChar = c;
        this.pattern = charSequence;
        Token token = null;
        while (true) {
            token = token(token);
            if (token == null) {
                return;
            }
            if (this.head == null) {
                this.head = token;
            }
        }
    }

    private Token token(Token token) {
        char c = this.curChar;
        if (c == 0) {
            return null;
        }
        if (c != '{') {
            return text(token);
        }
        char lookahead = lookahead();
        if (lookahead == '{') {
            return leftCurlyBracket(token);
        }
        if (lookahead >= 'a' && lookahead <= 'z') {
            return key(token);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected character '");
        sb.append(lookahead);
        sb.append("'; expected key.");
        throw new IllegalArgumentException(sb.toString());
    }

    private KeyToken key(Token token) {
        char c;
        StringBuilder sb = new StringBuilder();
        consume();
        while (true) {
            char c2 = this.curChar;
            if (c2 < 'a' || c2 > 'z') {
                c = this.curChar;
                if (c != '_') {
                    break;
                }
            }
            sb.append(this.curChar);
            consume();
        }
        if (c == '}') {
            consume();
            if (sb.length() != 0) {
                String sb2 = sb.toString();
                this.keys.add(sb2);
                return new KeyToken(token, sb2);
            }
            throw new IllegalArgumentException("Empty key: {}");
        }
        throw new IllegalArgumentException("Missing closing brace: }");
    }

    private TextToken text(Token token) {
        int i = this.curCharIndex;
        while (true) {
            char c = this.curChar;
            if (c != '{' && c != 0) {
                consume();
            }
        }
    }

    private LeftCurlyBracketToken leftCurlyBracket(Token token) {
        consume();
        consume();
        return new LeftCurlyBracketToken(token);
    }

    private char lookahead() {
        if (this.curCharIndex < this.pattern.length() - 1) {
            return this.pattern.charAt(this.curCharIndex + 1);
        }
        return 0;
    }

    private void consume() {
        int i = this.curCharIndex + 1;
        this.curCharIndex = i;
        this.curChar = i == this.pattern.length() ? 0 : this.pattern.charAt(this.curCharIndex);
    }
}
