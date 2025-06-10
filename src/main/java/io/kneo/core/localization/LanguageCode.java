package io.kneo.core.localization;

import lombok.Getter;

public enum LanguageCode {
    unknown(0, "??"),
    en(45, "en"),
    ru(570, "ru"),
    kk(255, "kk"),
    bg(115, "bg"),
    pt(545, "pt"),
    es(230, "es"),
    zh(315, "zh"),
    de(316, "de"),
    fr(317, "fr"),
    pl(318, "pl"),
    be(319, "be"),
    cs(320, "cs"),
    el(321, "el"),
    uk(322, "uk"),
    tr(323, "tr"),
    it(324, "it"),
    ko(325, "ko"),
    ja(326, "ja"),
    hi(327, "hi"),
    ar(328, "ar"),
    lv(329, "lv");

    @Getter
    private final int code;
    @Getter
    private final String altCode;

    LanguageCode(int code, String altCode) {
        this.code = code;
        this.altCode = altCode;
    }

    public static LanguageCode getType(int code) {
        for (LanguageCode type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return unknown;
    }

    public static LanguageCode getByAltCode(String altCode) {
        for (LanguageCode type : values()) {
            if (type.altCode.equalsIgnoreCase(altCode)) {
                return type;
            }
        }
        return unknown;
    }
}