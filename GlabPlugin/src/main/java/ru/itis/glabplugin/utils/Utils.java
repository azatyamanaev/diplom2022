package ru.itis.glabplugin.utils;

import org.jetbrains.annotations.Nullable;
import ru.itis.glabplugin.api.models.AbstractModel;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 21.05.2022
 *
 * @author Azat Yamanaev
 */
public class Utils {

    @Nullable
    public static LinkedHashMap<Integer, ? extends AbstractModel> map(@Nullable List<? extends AbstractModel> models) {
        if (models == null) return null;
        LinkedHashMap<Integer, AbstractModel> map = new LinkedHashMap<>();
        models.stream().filter(model -> model.getId() != null).forEach(model -> map.put((Integer) model.getId(), model));
        return map;
    }

}
