/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <p>
 * <gerviba@gerviba.hu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.       Szabó Gergely
 */
package hu.gerviba.authsch.struct;

import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * @author Gerviba
 */
public class PersonEntitlement implements Serializable {

    private static final long serialVersionUID = -2904767686389619156L;

    private final long id;
    private final String name;
    private final String status;
    private final String start;
    private final String end;

    public PersonEntitlement(long id, String name, String status, String start, String end) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.start = start;
        this.end = end;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Date Format: YYYY-MM-DD
     */
    public String getStart() {
        return start;
    }

    /**
     * Date Format: YYYY-MM-DD
     * @warning It can be null!
     */
    @Nullable
    public String getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "PersonEntitlement [id=" + id + ", name=" + name + ", status=" + status + ", start=" + start + ", end="
                + end + "]";
    }

}
