package io.kneo.officeframe.cnst;

import lombok.Getter;

/**
 * ISO 3166-1 alpha-2 country codes
 * <a href="https://en.wikipedia.org/wiki/ISO_3166-2">...</a>
 *
 * @author Kayra created 27-12-2015
 */
@Getter
public enum CountryCode {
    UNKNOWN(0),

    KZ(777), RU(778), BY(779), UA(780), DE(781), FR(782), TR(783),
    US(784), CN(785), BG(786), GB(787), JP(788), ES(789), PT(790),

    AF(791), AL(792), DZ(793), AD(794), AO(795), AG(796), AR(797), AM(798), AU(799), AT(800),
    AZ(801), BS(802), BH(803), BD(804), BB(805), BE(806), BZ(807), BJ(808), BT(809), BO(810),
    BA(811), BW(812), BR(813), BN(814), BF(815), BI(816), CV(817), KH(818), CM(819), CA(820),
    CF(821), TD(822), CL(823), CO(824), KM(825), CG(826), CR(827), HR(828), CU(829), CY(830),
    CZ(831), DK(832), DJ(833), DM(834), DO(835), EC(836), EG(837), SV(838), GQ(839), ER(840),
    EE(841), ET(842), FJ(843), FI(844), GA(845), GM(846), GE(847), GH(848), GR(849), GD(850),
    GT(851), GN(852), GW(853), GY(854), HT(855), HN(856), HU(857), IS(858), IN(859), ID(860),
    IR(861), IQ(862), IE(863), IL(864), IT(865), JM(866), JO(867), KE(868), KI(869), KR(870),
    KW(871), KG(872), LA(873), LV(874), LB(875), LS(876), LR(877), LY(878), LI(879), LT(880),
    LU(881), MG(882), MW(883), MY(884), MV(885), ML(886), MT(887), MR(888), MU(889), MX(890),
    MD(891), MC(892), MN(893), ME(894), MA(895), MZ(896), MM(897), NA(898), NR(899), NP(900),
    NL(901), NZ(902), NI(903), NE(904), NG(905), NO(906), OM(907), PK(908), PW(909), PA(910),
    PG(911), PY(912), PE(913), PH(914), PL(915), QA(916), RO(917), RW(918), KN(919), LC(920),
    VC(921), WS(922), SM(923), ST(924), SA(925), SN(926), RS(927), SC(928), SL(929), SG(930),
    SK(931), SI(932), SB(933), ZA(934), LK(935), SD(936), SR(937), SE(938), CH(939), SY(940),
    TW(941), TJ(942), TZ(943), TH(944), TL(945), TG(946), TO(947), TT(948), TN(949), TM(950),
    UG(951), AE(952), UY(953), UZ(954), VU(955), VA(956), VE(957), VN(958), YE(959), ZM(960),
    ZW(961);

    private final int code;

    CountryCode(int code) {
        this.code = code;
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