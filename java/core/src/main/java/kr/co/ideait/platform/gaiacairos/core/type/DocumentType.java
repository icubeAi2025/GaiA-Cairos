package kr.co.ideait.platform.gaiacairos.core.type;

public enum DocumentType {

    FOLDER("FOLDR"), FILE("FILE"), ITEM("ITEM"), ETC("ETC");

    String typeString;

    DocumentType(String typeString) {
        this.typeString = typeString;
    }

    public static DocumentType from(String documentTypeString) {
        return switch (documentTypeString.toUpperCase()) {
            case "FOLDR" -> FOLDER;
            case "FILE" -> FILE;
            case "ITEM" -> ITEM;
            default -> ETC;
        };
    }

    public String toString() {
        return typeString;
    }

}
