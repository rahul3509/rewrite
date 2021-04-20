package org.openrewrite.java.format;

import org.openrewrite.Cursor;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.style.IntelliJ;
import org.openrewrite.java.style.TabsAndIndentsStyle;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Space;

import java.util.Objects;
import java.util.Optional;

/**
 * Less commonly used than {@link AutoFormat}, but useful in cases when a block of code is being
 * moved definitively a certain number of indentation levels left or right, such as when unwrapping
 * a block or conditional statement.
 */
public class ShiftFormat {
    private ShiftFormat() {
    }

    public static <J2 extends J> J2 indent(J j, Cursor cursor, int shift) {
        J.CompilationUnit cu = cursor.firstEnclosingOrThrow(J.CompilationUnit.class);
        TabsAndIndentsStyle tabsAndIndents = Optional.ofNullable(cu.getStyle(TabsAndIndentsStyle.class))
                .orElse(IntelliJ.tabsAndIndents());

        //noinspection unchecked
        return (J2) Objects.requireNonNull(new JavaIsoVisitor<Integer>() {
            @Override
            public Space visitSpace(Space space, Space.Location loc, Integer integer) {
                return space.withWhitespace(shift(space.getWhitespace(), shift))
                        .withComments(ListUtils.map(space.getComments(), comment -> comment
                                .withSuffix(shift(comment.getSuffix(), shift))));
            }

            private String shift(String whitespace, int shift) {
                if (!whitespace.contains("\n")) {
                    return whitespace;
                }
                return shift < 0 ?
                        shiftLeft(tabsAndIndents, whitespace, -1 * shift) :
                        shiftRight(tabsAndIndents, whitespace, shift);
            }
        }.visit(j, 0));
    }

    static String shiftLeft(TabsAndIndentsStyle tabsAndIndents, String whitespace, int shift) {
        char[] chars = whitespace.toCharArray();

        int erase = 0;
        int shifted = shift * (tabsAndIndents.getUseTabCharacter() ?
                tabsAndIndents.getTabSize() :
                tabsAndIndents.getIndentSize());

        shiftLoop:
        for (int i = chars.length - 1; i >= 0 && shifted > 0; i--) {
            switch (chars[i]) {
                case '\n':
                case '\r':
                    break shiftLoop;
                case '\t':
                    erase += 1;
                    shifted -= tabsAndIndents.getTabSize();
                    break;
                default:
                    if (Character.isWhitespace(chars[i])) {
                        erase += 1;
                        shifted--;
                    }
            }
        }

        String w = whitespace.substring(0, whitespace.length() - erase);
        for (int i = shifted; i < 0; i++) {
            //noinspection StringConcatenationInLoop
            w += ' ';
        }
        return w;
    }

    static String shiftRight(TabsAndIndentsStyle tabsAndIndents, String whitespace, int shift) {
        StringBuilder w = new StringBuilder(whitespace);
        for (int i = 0; i < shift; i++) {
            if (tabsAndIndents.getUseTabCharacter()) {
                w.append('\t');
            } else {
                for (int j = 0; j < tabsAndIndents.getIndentSize(); j++) {
                    w.append(' ');
                }
            }
        }

        return w.toString();
    }
}
