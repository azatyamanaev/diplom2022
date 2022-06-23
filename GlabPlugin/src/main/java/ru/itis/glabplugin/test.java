package ru.itis.glabplugin;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 13.06.2022
 *
 * @author Azat Yamanaev
 */
public class test {

    public static void main(String[] args) {
//        String s = "2022-06-09T12:50:00.000Z";
//        String s1 = "2022-06-12T13:55:40.000Z";
//
//        Instant now = LocalDateTime.parse(s1.substring(0, s.length() - 1)).atZone(ZoneId.systemDefault()).toInstant();
////        Instant now = Instant.now();
//        Instant dateTime = LocalDateTime.parse(s.substring(0, s.length() - 1)).atZone(ZoneId.systemDefault()).toInstant();
//        Duration duration = Duration.between(dateTime, now);
//        String res = duration.toString();
//        System.out.println(res);
//        StringBuilder builder = new StringBuilder();
//        for (int i = 2; i < res.length(); i++) {
//            char a = res.charAt(i);
//            if (Character.isDigit(a)) {
//                builder.append(a);
//            } else {
//                builder.append(a);
//                break;
//            }
//        }
//        System.out.println(builder);


        System.out.println("COMPILATION ERROR : \n" +
                "/builds/technaxis/mkusa-backend/src/main/java/com/technaxis/mkusa/services/chatbot/impl/ChatBotServiceImpl.java:[10,47] cannot find symbol\n" +
                "symbol:   class SessionInsightForm\n" +
                "location: package com.technaxis.mkusa.dto.forms.community\n" +
                "/builds/technaxis/mkusa-backend/src/main/java/com/technaxis/mkusa/services/chatbot/ChatBotService.java:[7,47] cannot find symbol\n" +
                "symbol:   class SessionInsightForm\n" +
                "location: package com.technaxis.mkusa.dto.forms.community\n" +
                "/builds/technaxis/mkusa-backend/src/main/java/com/technaxis/mkusa/services/chatbot/impl/ChatBotServiceImpl.java:[137,68] cannot find symbol\n" +
                "symbol:   class SessionInsightForm\n" +
                "location: class com.technaxis.mkusa.services.chatbot.impl.ChatBotServiceImpl\n" +
                "/builds/technaxis/mkusa-backend/src/main/java/com/technaxis/mkusa/services/chatbot/ChatBotService.java:[81,61] cannot find symbol\n" +
                "symbol:   class SessionInsightForm\n" +
                "location: interface com.technaxis.mkusa.services.chatbot.ChatBotService\n" +
                "/builds/technaxis/mkusa-backend/src/main/java/com/technaxis/mkusa/web/controllers/client/ChatBotController.java:[11,47] cannot find symbol\n" +
                "symbol:   class SessionInsightForm\n" +
                "location: package com.technaxis.mkusa.dto.forms.community\n" +
                "/builds/technaxis/mkusa-backend/src/main/java/com/technaxis/mkusa/web/controllers/client/ChatBotController.java:[98,71] cannot find symbol\n" +
                "symbol:   class SessionInsightForm\n" +
                "location: class com.technaxis.mkusa.web.controllers.client.ChatBotController");
    }
}
