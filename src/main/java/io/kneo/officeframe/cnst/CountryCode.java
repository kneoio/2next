package io.kneo.officeframe.cnst;

import lombok.Getter;

/**
 * ISO 3166-1 alpha-2 country codes
 * ...
 *
 * @author Kayra created 27-12-2015
 */
@Getter
public enum CountryCode {
    UNKNOWN(0, "Unknown"),

    KZ(777, "Kazakhstan"), RU(778, "Russia"), BY(779, "Belarus"), UA(780, "Ukraine"),
    DE(781, "Germany"), FR(782, "France"), TR(783, "Turkey"), US(784, "United States"),
    CN(785, "China"), BG(786, "Bulgaria"), GB(787, "United Kingdom"), JP(788, "Japan"),
    ES(789, "Spain"), PT(790, "Portugal"),

    AF(791, "Afghanistan"), AL(792, "Albania"), DZ(793, "Algeria"), AD(794, "Andorra"),
    AO(795, "Angola"), AG(796, "Antigua and Barbuda"), AR(797, "Argentina"),
    AM(798, "Armenia"), AU(799, "Australia"), AT(800, "Austria"), AZ(801, "Azerbaijan"),
    BS(802, "Bahamas"), BH(803, "Bahrain"), BD(804, "Bangladesh"), BB(805, "Barbados"),
    BE(806, "Belgium"), BZ(807, "Belize"), BJ(808, "Benin"), BT(809, "Bhutan"),
    BO(810, "Bolivia"), BA(811, "Bosnia and Herzegovina"), BW(812, "Botswana"),
    BR(813, "Brazil"), BN(814, "Brunei Darussalam"), BF(815, "Burkina Faso"),
    BI(816, "Burundi"), CV(817, "Cabo Verde"), KH(818, "Cambodia"), CM(819, "Cameroon"),
    CA(820, "Canada"), CF(821, "Central African Republic"), TD(822, "Chad"),
    CL(823, "Chile"), CO(824, "Colombia"), KM(825, "Comoros"), CG(826, "Congo"),
    CR(827, "Costa Rica"), HR(828, "Croatia"), CU(829, "Cuba"), CY(830, "Cyprus"),
    CZ(831, "Czech Republic"), DK(832, "Denmark"), DJ(833, "Djibouti"),
    DM(834, "Dominica"), DO(835, "Dominican Republic"), EC(836, "Ecuador"),
    EG(837, "Egypt"), SV(838, "El Salvador"), GQ(839, "Equatorial Guinea"),
    ER(840, "Eritrea"), EE(841, "Estonia"), ET(842, "Ethiopia"), FJ(843, "Fiji"),
    FI(844, "Finland"), GA(845, "Gabon"), GM(846, "Gambia"), GE(847, "Georgia"),
    GH(848, "Ghana"), GR(849, "Greece"), GD(850, "Grenada"), GT(851, "Guatemala"),
    GN(852, "Guinea"), GW(853, "Guinea-Bissau"), GY(854, "Guyana"), HT(855, "Haiti"),
    HN(856, "Honduras"), HU(857, "Hungary"), IS(858, "Iceland"), IN(859, "India"),
    ID(860, "Indonesia"), IR(861, "Iran"), IQ(862, "Iraq"), IE(863, "Ireland"),
    IL(864, "Israel"), IT(865, "Italy"), JM(866, "Jamaica"), JO(867, "Jordan"),
    KE(868, "Kenya"), KI(869, "Kiribati"), KR(870, "South Korea"), KW(871, "Kuwait"),
    KG(872, "Kyrgyzstan"), LA(873, "Laos"), LV(874, "Latvia"), LB(875, "Lebanon"),
    LS(876, "Lesotho"), LR(877, "Liberia"), LY(878, "Libya"), LI(879, "Liechtenstein"),
    LT(880, "Lithuania"), LU(881, "Luxembourg"), MG(882, "Madagascar"),
    MW(883, "Malawi"), MY(884, "Malaysia"), MV(885, "Maldives"), ML(886, "Mali"),
    MT(887, "Malta"), MR(888, "Mauritania"), MU(889, "Mauritius"), MX(890, "Mexico"),
    MD(891, "Moldova"), MC(892, "Monaco"), MN(893, "Mongolia"), ME(894, "Montenegro"),
    MA(895, "Morocco"), MZ(896, "Mozambique"), MM(897, "Myanmar"), NA(898, "Namibia"),
    NR(899, "Nauru"), NP(900, "Nepal"), NL(901, "Netherlands"), NZ(902, "New Zealand"),
    NI(903, "Nicaragua"), NE(904, "Niger"), NG(905, "Nigeria"), NO(906, "Norway"),
    OM(907, "Oman"), PK(908, "Pakistan"), PW(909, "Palau"), PA(910, "Panama"),
    PG(911, "Papua New Guinea"), PY(912, "Paraguay"), PE(913, "Peru"),
    PH(914, "Philippines"), PL(915, "Poland"), QA(916, "Qatar"), RO(917, "Romania"),
    RW(918, "Rwanda"), KN(919, "Saint Kitts and Nevis"), LC(920, "Saint Lucia"),
    VC(921, "Saint Vincent and the Grenadines"), WS(922, "Samoa"), SM(923, "San Marino"),
    ST(924, "Sao Tome and Principe"), SA(925, "Saudi Arabia"), SN(926, "Senegal"),
    RS(927, "Serbia"), SC(928, "Seychelles"), SL(929, "Sierra Leone"),
    SG(930, "Singapore"), SK(931, "Slovakia"), SI(932, "Slovenia"),
    SB(933, "Solomon Islands"), ZA(934, "South Africa"), LK(935, "Sri Lanka"),
    SD(936, "Sudan"), SR(937, "Suriname"), SE(938, "Sweden"), CH(939, "Switzerland"),
    SY(940, "Syria"), TW(941, "Taiwan"), TJ(942, "Tajikistan"), TZ(943, "Tanzania"),
    TH(944, "Thailand"), TL(945, "Timor-Leste"), TG(946, "Togo"), TO(947, "Tonga"),
    TT(948, "Trinidad and Tobago"), TN(949, "Tunisia"), TM(950, "Turkmenistan"),
    UG(951, "Uganda"), AE(952, "United Arab Emirates"), UY(953, "Uruguay"),
    UZ(954, "Uzbekistan"), VU(955, "Vanuatu"), VA(956, "Vatican City"),
    VE(957, "Venezuela"), VN(958, "Vietnam"), YE(959, "Yemen"), ZM(960, "Zambia"),
    ZW(961, "Zimbabwe");

    private final int code;
    private final String countryName;

    CountryCode(int code, String countryName) {
        this.code = code;
        this.countryName = countryName;
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