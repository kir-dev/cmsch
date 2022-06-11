/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch.response;

import hu.gerviba.authsch.struct.BMEUnitScope;
import hu.gerviba.authsch.struct.Entrant;
import hu.gerviba.authsch.struct.PersonEntitlement;

import java.io.Serializable;
import java.util.*;

/**
 * @author Gerviba
 * @see #newBuilder()
 */
@SuppressWarnings({"java:S107", "java:S1874", "java:S1123" // backward compatiblity
})
public final class ProfileDataResponse implements Serializable {

    private static final long serialVersionUID = 216528239480860425L;

    public static ProfileDataResponseBuilder newBuilder() {
        return new ProfileDataResponseBuilder();
    }

    public static final class ProfileDataResponseBuilder {
        private UUID internalId = null;
        private String displayName = null;
        private String surname = null;
        private String givenName = null;
        private String mail = null;
        private String neptun = null;
        private Map<String, String> linkedAccounts = new HashMap<>();
        private List<PersonEntitlement> eduPersonEntitlement = new ArrayList<>();
        @Deprecated
        private String roomNumber = null;
        private String mobile = null;
        private List<String> courses = new ArrayList<>();
        private List<Entrant> entrants = new ArrayList<>();
        private List<String> admembership = new ArrayList<>();
        private List<BMEUnitScope> bmeunitscope = new ArrayList<>();

        private ProfileDataResponseBuilder() {
        }

        public ProfileDataResponseBuilder setInternalId(UUID internalId) {
            this.internalId = internalId;
            return this;
        }

        public ProfileDataResponseBuilder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public ProfileDataResponseBuilder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public ProfileDataResponseBuilder setGivenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        public ProfileDataResponseBuilder setMail(String mail) {
            this.mail = mail;
            return this;
        }

        public ProfileDataResponseBuilder setNeptun(String neptun) {
            this.neptun = neptun;
            return this;
        }

        public ProfileDataResponseBuilder addLinkedAccount(String account, String value) {
            this.linkedAccounts.put(account, value);
            return this;
        }

        public ProfileDataResponseBuilder addEduPersonEntitlement(PersonEntitlement entitlement) {
            this.eduPersonEntitlement.add(entitlement);
            return this;
        }

        public ProfileDataResponseBuilder setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public ProfileDataResponseBuilder setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public ProfileDataResponseBuilder addCourse(String course) {
            this.courses.add(course);
            return this;
        }

        public ProfileDataResponseBuilder addEntrant(Entrant entrant) {
            this.entrants.add(entrant);
            return this;
        }

        public ProfileDataResponseBuilder addADMembership(String admembership) {
            this.admembership.add(admembership);
            return this;
        }

        public ProfileDataResponseBuilder addBmeUnitScope(BMEUnitScope bmeunitscope) {
            this.bmeunitscope.add(bmeunitscope);
            return this;
        }

        public ProfileDataResponse build() {
            return new ProfileDataResponse(internalId, displayName, surname, givenName, mail, neptun, linkedAccounts,
                    eduPersonEntitlement, roomNumber, mobile, courses, entrants, admembership, bmeunitscope);
        }

    }

    private final UUID internalId;
    private final String displayName;
    private final String surname;
    private final String givenName;
    private final String mail;
    private final String neptun;
    private final Map<String, String> linkedAccounts;
    private final List<PersonEntitlement> eduPersonEntitlement;
    private final String roomNumber;
    private final String mobile;
    private final List<String> courses;
    private final List<Entrant> entrants;
    private final List<String> admembership;
    private final List<BMEUnitScope> bmeunitscope;

    private ProfileDataResponse(UUID internalId, String displayName, String surname, String givenName, String mail,
                                String neptun, Map<String, String> linkedAccounts, List<PersonEntitlement> eduPersonEntitlement,
                                String roomNumber, String mobile, List<String> courses, List<Entrant> entrants,
                                List<String> admembership, List<BMEUnitScope> bmeunitscope) {

        this.internalId = internalId;
        this.displayName = displayName;
        this.surname = surname;
        this.givenName = givenName;
        this.mail = mail;
        this.neptun = neptun;
        this.linkedAccounts = linkedAccounts;
        this.eduPersonEntitlement = eduPersonEntitlement;
        this.roomNumber = roomNumber;
        this.mobile = mobile;
        this.courses = courses;
        this.entrants = entrants;
        this.admembership = admembership;
        this.bmeunitscope = bmeunitscope;
    }

    public UUID getInternalId() {
        return internalId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSurname() {
        return surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getMail() {
        return mail;
    }

    public String getNeptun() {
        return neptun;
    }

    public Map<String, String> getLinkedAccounts() {
        return linkedAccounts;
    }

    public List<PersonEntitlement> getEduPersonEntitlements() {
        return eduPersonEntitlement;
    }

    /**
     * Room number
     * @deprecated Due to legal reasons.
     */
    @Deprecated
    public String getRoomNumber() {
        return roomNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public List<String> getCourses() {
        return courses;
    }

    public List<Entrant> getEntrants() {
        return entrants;
    }

    public List<String> getADMemberships() {
        return admembership;
    }

    public List<BMEUnitScope> getBmeUnitScopes() {
        return bmeunitscope;
    }

    @Override
    public String toString() {
        return "ProfileDataResponse [internalId=" + internalId + ", displayName=" + displayName
                + ", surname=" + surname + ", givenName=" + givenName + ", mail=" + mail + ", neptun="
                + neptun + ", linkedAccounts=" + linkedAccounts + ", eduPersonEntitlement="
                + eduPersonEntitlement + ", roomNumber=" + roomNumber + ", mobile=" + mobile
                + ", courses=" + courses + ", entrants=" + entrants + ", admembership="
                + admembership + ", bmeunitscope=" + bmeunitscope + "]";
    }

}
