package ru.itis.glabplugin.utils;

import org.jetbrains.annotations.Nullable;
import ru.itis.glabplugin.api.models.AbstractModel;

import java.time.*;
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

    public static String timeDiff(String datetime) {
        Instant before = LocalDateTime.parse(datetime.substring(0, datetime.length() - 1)).atZone(ZoneId.systemDefault()).toInstant();
        Instant now = Instant.now();
        String duration = Duration.between(before, now).toString();
        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < duration.length(); i++) {
            char a = duration.charAt(i);
            if (Character.isDigit(a)) {
                builder.append(a);
            } else {
                builder.append(a);
                break;
            }
        }
        String res = builder.toString();
        int ind = res.length() - 1;
        if (res.charAt(ind) == '.' || res.charAt(ind) == 'S') {
            return res.substring(0, ind) + " seconds ago";
        } else if (res.charAt(ind) == 'M') {
            return res.substring(0, ind) + " minutes ago";
        } else if (res.charAt(ind) == 'H') {
            int num = Integer.parseInt(res.substring(0, ind));
            if (num < 24) {
                return num + " hours ago";
            } else if (num < 8760) {
                return num / 24 + " days ago";
            } else {
                return num / 8760 + " years ago";
            }
        }
        return "";
    }


}
