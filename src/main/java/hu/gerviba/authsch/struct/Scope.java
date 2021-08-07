/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch.struct;

import com.fasterxml.jackson.databind.JsonNode;
import hu.gerviba.authsch.response.ProfileDataResponse.ProfileDataResponseBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Access Scope
 * @author Gerviba
 * @see https://git.sch.bme.hu/kszk/hu.gerviba.authsch/wikis/api
 */
public enum Scope {
    /**
     * AuthSCH-s azonosító (varchar, maximum 24 karakter). 
     * Belépéskor a kiadásához nem szükséges a felhasználó jóváhagyása.
     */
    BASIC("basic") {
        @Override
        public boolean canApply(JsonNode obj) {
            return true;
        }

        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setInternalId(UUID.fromString(obj.get("internal_id").asText()));
        }
    },
    /**
     * Név
     */
    DISPLAY_NAME("displayName") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setDisplayName(obj.get(getScope()).asText());
        }
    },
    /**
     * Vezetéknév
     */
    SURNAME("sn") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setSurname(obj.get(getScope()).asText());
        }
    },
    /**
     * Keresztnév
     */
    GIVEN_NAME("givenName") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setGivenName(obj.get(getScope()).asText());
        }
    },
    /**
     * E-mail cím
     */
    MAIL("mail") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setMail(obj.get(getScope()).asText());
        }
    },
    /**
     * Neptun kód (csak abban az esetben, ha a felhasználónak be van kötve a BME címtár 
     * azonosítója is, egyébként null-t ad vissza). Fokozottan védett információ, 
     * ami azt jelenti, hogy alapból nem kérhető le (invalid scope hibával kerül 
     * visszatérésre az ezt tartalmazó engedélykérés), csak indokolt esetben, központi 
     * engedélyezés után használható (ehhez adj fel egy ticketet a support.sch.bme.hu 
     * oldalon, amelyben leírod hogy mihez és miért van rá szükséged.
     * @warning Külön engedélyeztetni kell.
     */
    NEPTUN_CODE("niifPersonOrgID") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setNeptun(obj.get(getScope()).asText());
        }
    },
    /**
     * Kapcsolt accountok, kulcs - érték párokban. Lehetséges kulcsok:
     * <li> bme: szám@bme.hu
     * <li> schacc: schacc username
     * <li> vir: vir id (integer)
     * <li> virUid: vir username
     */
    LINKED_ACCOUNTS("linkedAccounts") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            obj.path(getScope()).fields()
                    .forEachRemaining(x -> response.addLinkedAccount(x.getKey(), x.getValue().asText()));
        }
    },
    /**
     * Körtagságok (itt az adott körnél a status csak egy értéket vehet fel, 
     * mégpedig a körvezető / tag / öregtag közül valamelyiket, ebben a prioritási sorrendben)
     * @see PersonEntilement
     */
    EDU_PERSON_ENTILEMENT("eduPersonEntitlement") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            obj.path(getScope()).elements().forEachRemaining(entrant ->
                response.addEduPersonEntitlement(new PersonEntitlement(
                        entrant.get("id").asInt(),
                        entrant.get("name").asText(),
                        entrant.get("status").asText(),
                        entrant.get("start").asText(),
                        entrant.get("end").isNull() ? null : entrant.get("end").asText()))
            );
        }
    },
    /**
     * Felhasználó szobaszáma (ha kollégista, akkor a kollégium neve és a szobaszám található 
     * meg benne, ha nem kollégista, akkor pedig null-t ad vissza). Amennyiben a felhasználó 
     * nem rendelkezik SCH Accounttal, szintén null-t ad eredményül. 
     * @deprecated Határozatlan ideig nem elérhető jogi okokból.
     */
    @Deprecated
    ROOM_NUMBER("roomNumber") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setRoomNumber(obj.get(getScope()).asText());
        }
    },
    /**
     * Mobilszám a VIR-ből
     */
    MOBILE("mobile") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            response.setMobile(obj.get(getScope()).asText());
        }
    },
    /**
     * Az adott félévben hallgatott tárgyak
     */
    COURSES("niifEduPersonAttendedCourse") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            for (String course : obj.get(Scope.COURSES.getScope()).asText().split(";"))
                response.addCourse(course);
        }
    },
    /**
     * Közösségi belépők a VIR-ről, február és július között az őszi, egyébként (tehát 
     * augusztustól januárig) a tavaszi belépők
     * @see Entrant
     */
    ENTRANTS("entrants") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            obj.path(Scope.ENTRANTS.getScope()).elements().forEachRemaining(entrant ->
                response.addEntrant(new Entrant(
                        entrant.get("groupId").asInt(),
                        entrant.get("groupName").asText(),
                        entrant.get("entrantType").asText()))
            );
        }
    },
    /**
     * Csoporttagságok a KSZK-s Active Directoryban
     */
    ACTIVE_DIRECTORY_MEMBERSHIP("admembership") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            obj.path(Scope.ACTIVE_DIRECTORY_MEMBERSHIP.getScope()).elements()
                    .forEachRemaining(x -> response.addADMembership(x.asText()));
        }
    },
    /**
     * Egyetemi jogviszony, jelenlegi lehetséges értékek: 
     * BME, BME_VIK, BME_VIK_ACTIVE, BME_VIK_NEWBIE
     * @see BMEUnitScope
     */
    BME_UNIT_SCOPE("bmeunitscope") {
        @Override
        public void apply(ProfileDataResponseBuilder response, JsonNode obj) {
            obj.path(Scope.BME_UNIT_SCOPE.getScope()).elements()
                    .forEachRemaining(x -> response.addBmeUnitScope(BMEUnitScope.valueOf(x.asText())));
        }
    };

    private final String scope;

    private Scope(String scope) {
        this.scope = scope;
    }

    public static Scope byScope(String scope) {
        for (Scope s : values())
            if (s.getScope().equals(scope))
                return s;
        return BASIC;
    }

    public String getScope() {
        return scope;
    }

    public static String buildForUrl(List<Scope> scopes) {
        return String.join("+", scopes.stream()
                .map(x -> x.scope).collect(Collectors.toList()));
    }

    public static String buildForUrl(Scope... scopes) {
        return String.join("+", Arrays.asList(scopes).stream()
                .map(x -> x.scope).collect(Collectors.toList()));
    }

    public static List<Scope> listFromString(String delimiter, String scopes) {
        return Arrays.asList(scopes.split(delimiter)).stream()
                .map(Scope::byScope)
                .collect(Collectors.toList());
    }

    public boolean canApply(JsonNode obj) {
        return obj.get(getScope()) != null
                && !obj.get(getScope()).isNull();
    }

    public abstract void apply(ProfileDataResponseBuilder builder, JsonNode obj);

}
