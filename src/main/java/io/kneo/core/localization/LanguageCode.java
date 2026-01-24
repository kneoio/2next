package io.kneo.core.localization;

import lombok.Getter;

public enum LanguageCode {
    unknown(0, "??"),
    en(45, "en"),
    zh(315, "zh"),
    hi(327, "hi"),
    es(230, "es"),
    fr(317, "fr"),
    ar(328, "ar"),
    ru(570, "ru"),
    pt(545, "pt"),
    de(316, "de"),
    ja(326, "ja"),
    ko(325, "ko"),
    it(324, "it"),
    tr(323, "tr"),
    pl(318, "pl"),
    nl(330, "nl"),
    sv(331, "sv"),
    da(332, "da"),
    no(333, "no"),
    fi(334, "fi"),
    he(335, "he"),
    th(336, "th"),
    vi(337, "vi"),
    id(338, "id"),
    ms(339, "ms"),
    tl(340, "tl"),
    sw(341, "sw"),
    am(342, "am"),
    yo(343, "yo"),
    ig(344, "ig"),
    ha(345, "ha"),
    zu(346, "zu"),
    xh(347, "xh"),
    af(348, "af"),
    bn(349, "bn"),
    ur(350, "ur"),
    fa(351, "fa"),
    ps(352, "ps"),
    ta(353, "ta"),
    te(354, "te"),
    ml(355, "ml"),
    kn(356, "kn"),
    gu(357, "gu"),
    pa(358, "pa"),
    or(359, "or"),
    as(360, "as"),
    mr(361, "mr"),
    ne(362, "ne"),
    si(363, "si"),
    my(364, "my"),
    km(365, "km"),
    lo(366, "lo"),
    ka(367, "ka"),
    hy(368, "hy"),
    az(369, "az"),
    kk(255, "kk"),
    ky(370, "ky"),
    uz(371, "uz"),
    tg(372, "tg"),
    tk(373, "tk"),
    mn(374, "mn"),
    bg(115, "bg"),
    hr(375, "hr"),
    sr(376, "sr"),
    bs(377, "bs"),
    mk(378, "mk"),
    sq(379, "sq"),
    sl(380, "sl"),
    sk(381, "sk"),
    cs(320, "cs"),
    hu(382, "hu"),
    ro(383, "ro"),
    lv(329, "lv"),
    lt(384, "lt"),
    et(385, "et"),
    mt(386, "mt"),
    is(387, "is"),
    ga(388, "ga"),
    cy(389, "cy"),
    br(390, "br"),
    eu(391, "eu"),
    ca(392, "ca"),
    gl(393, "gl"),
    oc(394, "oc"),
    co(395, "co"),
    sc(396, "sc"),
    rm(397, "rm"),
    la(398, "la"),
    el(321, "el"),
    uk(322, "uk"),
    be(319, "be"),
    tt(399, "tt"),
    cv(400, "cv"),
    ba(401, "ba"),
    ce(402, "ce"),
    av(403, "av"),
    lez(404, "lez"),
    kbd(405, "kbd"),
    ady(406, "ady"),
    fil(406, "fil");

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