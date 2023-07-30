package codeemoji.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static codeemoji.core.CESymbol.COLOR_BACKGROUND;

public class CEUtil {

    public static boolean isPreviewEditor(@NotNull Editor editor) {
        return editor.getEditorKind().name().equalsIgnoreCase("UNTYPED");
    }

    public static @NotNull String generateEmoji(int codePoint, int modifier, boolean addColor) {
        char[] codePointChar = Character.toChars(codePoint);
        char[] withoutColor = codePointChar;
        if (modifier > 0) {
            char[] modifierChar = Character.toChars(modifier);
            withoutColor = Arrays.copyOf(codePointChar, codePointChar.length + modifierChar.length);
            System.arraycopy(modifierChar, 0, withoutColor, codePointChar.length, modifierChar.length);
        }
        if (addColor) {
            char[] addColorChar = Character.toChars(COLOR_BACKGROUND.getValue());
            char[] withColor = Arrays.copyOf(withoutColor, withoutColor.length + addColorChar.length);
            System.arraycopy(addColorChar, 0, withColor, withoutColor.length, addColorChar.length);
            return new String(withColor);
        }
        return new String(withoutColor);
    }

    public static boolean isIterableType(@Nullable PsiTypeElement typeElement) {
        try {
            PsiType fieldType = Objects.requireNonNull(typeElement).getType();
            if (fieldType instanceof PsiClassType psiType) {
                PsiClass psiTypeClass = Objects.requireNonNull(psiType.resolve());
                String qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
                try {
                    Class<?> typeClass = Class.forName(qualifiedName);
                    return Iterable.class.isAssignableFrom(typeClass);
                } catch (RuntimeException | ClassNotFoundException ignored) {
                    Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
                    for (Project proj : openProjects) {
                        Project project = typeElement.getProject();
                        GlobalSearchScope scope = psiTypeClass.getResolveScope();
                        PsiClass psiUserClass = JavaPsiFacade.getInstance(project).findClass(qualifiedName, scope);
                        PsiClassType iteratorType = JavaPsiFacade.getElementFactory(project).createTypeByFQClassName("java.lang.Iterable", scope);
                        PsiClass iteratorClass = iteratorType.resolve();
                        if (iteratorClass != null && psiUserClass != null && psiUserClass.isInheritor(iteratorClass, true)) {
                            return true;
                        }
                    }
                }
            }
        } catch (RuntimeException ignored) {
        }
        return false;
    }

    public static boolean isArrayType(@Nullable PsiTypeElement typeElement) {
        try {
            String returnClassSimpleName = Objects.requireNonNull(typeElement).getText();
            return returnClassSimpleName.contains("[]");
        } catch (RuntimeException ignored) {
        }
        return false;
    }

    public static boolean sameNameAsType(@Nullable PsiTypeElement typeElement, @Nullable String fieldName) {
        if (fieldName != null) {
            try {
                String typeName = Objects.requireNonNull(typeElement).getType().getPresentableText();
                int index = typeName.indexOf("<");
                if (index > 0) {
                    typeName = typeName.substring(0, index);
                }
                return fieldName.equalsIgnoreCase(typeName);
            } catch (RuntimeException ignored) {
            }
        }
        return false;
    }

    public static boolean isPluralForm(@Nullable String name) {
        if (name != null) {
            String word = getLastWordWithUpperCase(name);
            if (isIrregularPluralForm(word)) {
                return true;
            } else return isCommonPluralForm(word);
        }
        return false;
    }

    private static String getLastWordWithUpperCase(@NotNull String name) {
        String result = null;
        Pattern pattern = Pattern.compile("\\b[A-Z][a-zA-Z]*\\b");
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            result = matcher.group();
        }
        return (result != null) ? result : name;
    }

    private static boolean isIrregularPluralForm(@NotNull String word) {
        ClassLoader classLoader = CEUtil.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("irregular_plural.json")) {
            if (is != null) {
                Reader reader = new InputStreamReader(is);
                JsonElement je = new Gson().fromJson(reader, JsonObject.class).get(word.trim().toLowerCase());
                if (je != null) {
                    return je.getAsString() != null;
                }
            }
        } catch (RuntimeException | IOException ignored) {
        }
        return false;
    }

    private static boolean isCommonPluralForm(@NotNull String word) {
        String[] pluralPatterns = {
                ".*s$", ".*[aeiou]ys$", ".*[^s]ses$", ".*[^z]zes$", ".*[^i]xes$",
                ".*[cs]hes$", ".*[^aeiou]ies$", ".*[^aeiou]ices$", ".*[aeiou]es$",
                ".*[^aeiou]ves$", ".*[^aeiou]a$", ".*[^aeiou]i$", ".*[^aeiou]ae$"
        };
        for (String pattern : pluralPatterns) {
            Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(word);
            return mat.matches();
        }
        return false;
    }

    public static boolean containsOnlySpecialCharacters(@NotNull String name) {
        String alphaNumericChars = "^[^a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(alphaNumericChars);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isNotGenericType(@Nullable PsiTypeElement typeElement) {
        /*if (typeElement != null) {
            PsiType type = typeElement.getType();
            if (type instanceof PsiPrimitiveType) {
                return true;
            } else if (type.getDeepComponentType() instanceof PsiClassType classType) {
                return !classType.hasParameters();
            }
        }*/
        //TODO: Implement
        return true;
    }
}
