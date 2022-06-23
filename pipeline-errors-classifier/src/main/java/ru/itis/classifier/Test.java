package ru.itis.classifier;

import ru.itis.classifier.api.TemplateGenerator;
import ru.itis.classifier.utils.Utils;

/**
 * 23.06.2022
 *
 * @author Azat Yamanaev
 */
public class Test {

    public static void main(String[] args) {

        String s1 = "Removing intermediate container a05dff05ffcd";
        String s2 = "Removing intermediate container 123a40d9078b";
        System.out.println(Utils.calculate(s1, s2));
        System.out.println(TemplateGenerator.overlap(s1, s2));
    }
}
