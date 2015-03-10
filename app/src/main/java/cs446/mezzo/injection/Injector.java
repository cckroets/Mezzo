package cs446.mezzo.injection;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cs446.mezzo.sources.MusicSource;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;
import roboguice.inject.Nullable;

/**
 * @author curtiskroetsch
 */
public final class Injector {

    private static final String EXCEPTION_STATIC_VIEW = "Views can't be statically assigned.";
    private static final String EXCEPTION_WRONG_TYPE = "Need view type to assign";
    private static final String EXCEPTION_NULLABLE = "Can't inject null value into %s.%s when field is not @Nullable";

    private Injector() {

    }

    private static void injectField(View container, Field field, Object target) {
        try {
            final InjectView injectView = field.getAnnotation(InjectView.class);
            final int id = injectView.value();
            final View view = container.findViewById(id);
            if ((view == null) && Nullable.notNullable(field)) {
                throw new NullPointerException(String.format(EXCEPTION_NULLABLE,
                        field.getDeclaringClass(), field.getName()));
            }

            field.setAccessible(true);
            if (field.get(target) == null) {
                field.set(target, view);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void injectViews(Object target, View view) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectView.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new UnsupportedOperationException(EXCEPTION_STATIC_VIEW);
                } else {
                    if (View.class.isAssignableFrom(field.getType())) {
                        injectField(view, field, target);
                    } else {
                        throw new UnsupportedOperationException(EXCEPTION_WRONG_TYPE);
                    }
                }
            }
        }
    }

    public static <T> T getObject(Context context, String className) {
        Object object;
        try {
            final Class sourceClass = Class.forName(className);
            object = RoboGuice.getInjector(context).getInstance(sourceClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return (T) object;
    }
}
