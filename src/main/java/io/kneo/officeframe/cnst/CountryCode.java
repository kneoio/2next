package io.kneo.officeframe.cnst;

import io.kneo.core.localization.LanguageCode;
import lombok.Getter;

/**
 * ISO 3166-1 alpha-2 country codes
 * ...
 *
 * @author Kayra created 27-12-2015
 */
@Getter
public enum CountryCode {
    UNKNOWN(0, "Unknown", LanguageCode.unknown),

    KZ(777, "Kazakhstan", LanguageCode.kk), RU(778, "Russia", LanguageCode.ru), BY(779, "Belarus", LanguageCode.be), UA(780, "Ukraine", LanguageCode.uk),
    DE(781, "Germany", LanguageCode.de), FR(782, "France", LanguageCode.fr), TR(783, "Turkey", LanguageCode.tr), US(784, "United States", LanguageCode.en),
    CN(785, "China", LanguageCode.zh), BG(786, "Bulgaria", LanguageCode.bg), GB(787, "United Kingdom", LanguageCode.en), JP(788, "Japan", LanguageCode.ja),
    ES(789, "Spain", LanguageCode.es), PT(790, "Portugal", LanguageCode.pt),

    AF(791, "Afghanistan", LanguageCode.ps), AL(792, "Albania", LanguageCode.sq), DZ(793, "Algeria", LanguageCode.ar), AD(794, "Andorra", LanguageCode.ca),
    AO(795, "Angola", LanguageCode.pt), AG(796, "Antigua and Barbuda", LanguageCode.en), AR(797, "Argentina", LanguageCode.es),
    AM(798, "Armenia", LanguageCode.hy), AU(799, "Australia", LanguageCode.en), AT(800, "Austria", LanguageCode.de), AZ(801, "Azerbaijan", LanguageCode.az),
    BS(802, "Bahamas", LanguageCode.en), BH(803, "Bahrain", LanguageCode.ar), BD(804, "Bangladesh", LanguageCode.bn), BB(805, "Barbados", LanguageCode.en),
    BE(806, "Belgium", LanguageCode.nl), BZ(807, "Belize", LanguageCode.en), BJ(808, "Benin", LanguageCode.fr), BT(809, "Bhutan", LanguageCode.en),
    BO(810, "Bolivia", LanguageCode.es), BA(811, "Bosnia and Herzegovina", LanguageCode.bs), BW(812, "Botswana", LanguageCode.en),
    BR(813, "Brazil", LanguageCode.pt), BN(814, "Brunei Darussalam", LanguageCode.ms), BF(815, "Burkina Faso", LanguageCode.fr),
    BI(816, "Burundi", LanguageCode.fr), CV(817, "Cabo Verde", LanguageCode.pt), KH(818, "Cambodia", LanguageCode.km), CM(819, "Cameroon", LanguageCode.fr),
    CA(820, "Canada", LanguageCode.en), CF(821, "Central African Republic", LanguageCode.fr), TD(822, "Chad", LanguageCode.fr),
    CL(823, "Chile", LanguageCode.es), CO(824, "Colombia", LanguageCode.es), KM(825, "Comoros", LanguageCode.ar), CG(826, "Congo", LanguageCode.fr),
    CR(827, "Costa Rica", LanguageCode.es), HR(828, "Croatia", LanguageCode.hr), CU(829, "Cuba", LanguageCode.es), CY(830, "Cyprus", LanguageCode.el),
    CZ(831, "Czech Republic", LanguageCode.cs), DK(832, "Denmark", LanguageCode.da), DJ(833, "Djibouti", LanguageCode.fr),
    DM(834, "Dominica", LanguageCode.en), DO(835, "Dominican Republic", LanguageCode.es), EC(836, "Ecuador", LanguageCode.es),
    EG(837, "Egypt", LanguageCode.ar), SV(838, "El Salvador", LanguageCode.es), GQ(839, "Equatorial Guinea", LanguageCode.es),
    ER(840, "Eritrea", LanguageCode.ar), EE(841, "Estonia", LanguageCode.et), ET(842, "Ethiopia", LanguageCode.am), FJ(843, "Fiji", LanguageCode.en),
    FI(844, "Finland", LanguageCode.fi), GA(845, "Gabon", LanguageCode.fr), GM(846, "Gambia", LanguageCode.en), GE(847, "Georgia", LanguageCode.ka),
    GH(848, "Ghana", LanguageCode.en), GR(849, "Greece", LanguageCode.el), GD(850, "Grenada", LanguageCode.en), GT(851, "Guatemala", LanguageCode.es),
    GN(852, "Guinea", LanguageCode.fr), GW(853, "Guinea-Bissau", LanguageCode.pt), GY(854, "Guyana", LanguageCode.en), HT(855, "Haiti", LanguageCode.fr),
    HN(856, "Honduras", LanguageCode.es), HU(857, "Hungary", LanguageCode.hu), IS(858, "Iceland", LanguageCode.is), IN(859, "India", LanguageCode.hi),
    ID(860, "Indonesia", LanguageCode.id), IR(861, "Iran", LanguageCode.fa), IQ(862, "Iraq", LanguageCode.ar), IE(863, "Ireland", LanguageCode.en),
    IL(864, "Israel", LanguageCode.he), IT(865, "Italy", LanguageCode.it), JM(866, "Jamaica", LanguageCode.en), JO(867, "Jordan", LanguageCode.ar),
    KE(868, "Kenya", LanguageCode.sw), KI(869, "Kiribati", LanguageCode.en), KR(870, "South Korea", LanguageCode.ko), KW(871, "Kuwait", LanguageCode.ar),
    KG(872, "Kyrgyzstan", LanguageCode.ky), LA(873, "Laos", LanguageCode.lo), LV(874, "Latvia", LanguageCode.lv), LB(875, "Lebanon", LanguageCode.ar),
    LS(876, "Lesotho", LanguageCode.en), LR(877, "Liberia", LanguageCode.en), LY(878, "Libya", LanguageCode.ar), LI(879, "Liechtenstein", LanguageCode.de),
    LT(880, "Lithuania", LanguageCode.lt), LU(881, "Luxembourg", LanguageCode.fr), MG(882, "Madagascar", LanguageCode.fr),
    MW(883, "Malawi", LanguageCode.en), MY(884, "Malaysia", LanguageCode.ms), MV(885, "Maldives", LanguageCode.en), ML(886, "Mali", LanguageCode.fr),
    MT(887, "Malta", LanguageCode.mt), MR(888, "Mauritania", LanguageCode.ar), MU(889, "Mauritius", LanguageCode.en), MX(890, "Mexico", LanguageCode.es),
    MD(891, "Moldova", LanguageCode.ro), MC(892, "Monaco", LanguageCode.fr), MN(893, "Mongolia", LanguageCode.mn), ME(894, "Montenegro", LanguageCode.sr),
    MA(895, "Morocco", LanguageCode.ar), MZ(896, "Mozambique", LanguageCode.pt), MM(897, "Myanmar", LanguageCode.my), NA(898, "Namibia", LanguageCode.en),
    NR(899, "Nauru", LanguageCode.en), NP(900, "Nepal", LanguageCode.ne), NL(901, "Netherlands", LanguageCode.nl), NZ(902, "New Zealand", LanguageCode.en),
    NI(903, "Nicaragua", LanguageCode.es), NE(904, "Niger", LanguageCode.fr), NG(905, "Nigeria", LanguageCode.en), NO(906, "Norway", LanguageCode.no),
    OM(907, "Oman", LanguageCode.ar), PK(908, "Pakistan", LanguageCode.ur), PW(909, "Palau", LanguageCode.en), PA(910, "Panama", LanguageCode.es),
    PG(911, "Papua New Guinea", LanguageCode.en), PY(912, "Paraguay", LanguageCode.es), PE(913, "Peru", LanguageCode.es),
    PH(914, "Philippines", LanguageCode.tl), PL(915, "Poland", LanguageCode.pl), QA(916, "Qatar", LanguageCode.ar), RO(917, "Romania", LanguageCode.ro),
    RW(918, "Rwanda", LanguageCode.en), KN(919, "Saint Kitts and Nevis", LanguageCode.en), LC(920, "Saint Lucia", LanguageCode.en),
    VC(921, "Saint Vincent and the Grenadines", LanguageCode.en), WS(922, "Samoa", LanguageCode.en), SM(923, "San Marino", LanguageCode.it),
    ST(924, "Sao Tome and Principe", LanguageCode.pt), SA(925, "Saudi Arabia", LanguageCode.ar), SN(926, "Senegal", LanguageCode.fr),
    RS(927, "Serbia", LanguageCode.sr), SC(928, "Seychelles", LanguageCode.en), SL(929, "Sierra Leone", LanguageCode.en),
    SG(930, "Singapore", LanguageCode.en), SK(931, "Slovakia", LanguageCode.sk), SI(932, "Slovenia", LanguageCode.sl),
    SB(933, "Solomon Islands", LanguageCode.en), ZA(934, "South Africa", LanguageCode.en), LK(935, "Sri Lanka", LanguageCode.si),
    SD(936, "Sudan", LanguageCode.ar), SR(937, "Suriname", LanguageCode.nl), SE(938, "Sweden", LanguageCode.sv), CH(939, "Switzerland", LanguageCode.de),
    SY(940, "Syria", LanguageCode.ar), TW(941, "Taiwan", LanguageCode.zh), TJ(942, "Tajikistan", LanguageCode.tg), TZ(943, "Tanzania", LanguageCode.sw),
    TH(944, "Thailand", LanguageCode.th), TL(945, "Timor-Leste", LanguageCode.pt), TG(946, "Togo", LanguageCode.fr), TO(947, "Tonga", LanguageCode.en),
    TT(948, "Trinidad and Tobago", LanguageCode.en), TN(949, "Tunisia", LanguageCode.ar), TM(950, "Turkmenistan", LanguageCode.tk),
    UG(951, "Uganda", LanguageCode.en), AE(952, "United Arab Emirates", LanguageCode.ar), UY(953, "Uruguay", LanguageCode.es),
    UZ(954, "Uzbekistan", LanguageCode.uz), VU(955, "Vanuatu", LanguageCode.en), VA(956, "Vatican City", LanguageCode.it),
    VE(957, "Venezuela", LanguageCode.es), VN(958, "Vietnam", LanguageCode.vi), YE(959, "Yemen", LanguageCode.ar), ZM(960, "Zambia", LanguageCode.en),
    ZW(961, "Zimbabwe", LanguageCode.en);

    private final int code;
    private final String countryName;
    private final LanguageCode preferredLanguage;

    CountryCode(int code, String countryName, LanguageCode preferredLanguage) {
        this.code = code;
        this.countryName = countryName;
        this.preferredLanguage = preferredLanguage;
    }

    public static CountryCode fromString(String code) {
        if (code == null || code.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return CountryCode.valueOf(code.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static CountryCode getType(int code) {
        for (CountryCode type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public boolean isValid() {
        return this != UNKNOWN;
    }

    public String getIsoCode() {
        return this == UNKNOWN ? "??" : this.name();
    }

}