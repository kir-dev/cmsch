/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch.struct;

import java.io.Serializable;

/**
 * @author Gerviba
 */
public class Entrant implements Serializable {

    private static final long serialVersionUID = 461763126385555164L;

    private final int groupId;
    private final String groupName;
    private final String entrantType;

    public Entrant(int groupId, String groupName, String entrantType) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.entrantType = entrantType;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getEntrantType() {
        return entrantType;
    }

    @Override
    public String toString() {
        return "Entrant [groupId=" + groupId + ", groupName=" + groupName + ", entrantType=" + entrantType + "]";
    }

}
