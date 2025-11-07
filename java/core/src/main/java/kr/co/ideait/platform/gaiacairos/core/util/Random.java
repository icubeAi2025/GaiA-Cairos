package kr.co.ideait.platform.gaiacairos.core.util;

import java.util.UUID;

public class Random {

    public static String id() {
        return UUID.randomUUID().toString();
    }
}
