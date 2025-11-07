package kr.co.ideait.platform.gaiacairos.core.type;

public enum PlatformType {

    GAIA("gaia"), PGAIA("pgaia"), CAIROS("cairos"), WBSGEN("wbsgen"), ETC("etc");

    private final String name;

    PlatformType(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public static PlatformType from(String name) {
        return switch (name.toLowerCase()) {
            case "gaia" -> GAIA;
            case "cairos" -> CAIROS;
            case "wbsgen" -> WBSGEN;
            default -> ETC;
        };
    }
}
