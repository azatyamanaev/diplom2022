package ru.itis.glabplugin.components;

import java.util.function.Function;

/**
 * 21.05.2022
 *
 * @author Azat Yamanaev
 */
public class TableRowDefinition<T> {

    public String title;
    public Function<T, Object> tableModelRowFunction;

    public TableRowDefinition(String title, Function<T, Object> tableModelRowFunction) {
        this.title = title;
        this.tableModelRowFunction = tableModelRowFunction;
    }
}
