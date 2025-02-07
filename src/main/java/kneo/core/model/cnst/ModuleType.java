package kneo.core.model.cnst;

import lombok.Getter;

@Getter
public enum ModuleType {
    UNKNOWN(0, "unknown", "???"),
    OFFICE_FRAME(101, "kneo/officeframe", "of"),
    PROJECTS(102,"projects" ,"prj");

    private final int code;
    private final String name;
    private final String shortName;

    ModuleType(int code, String name, String shortName) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
    }

    public static ModuleType getType(int code) {
        for (ModuleType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
