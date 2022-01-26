package pl.kskowronski.data.entity.report;

import pl.kskowronski.data.entity.User;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "nap_reports_perm")
public class ReportPermission {

    @Id
    @Column(name="PERM_ID")
    private BigDecimal permId;

    @Column(name="PERM_RAP_ID")
    private BigDecimal permRapId;

    @Column(name="PERM_USER_ID")
    private BigDecimal permUserId;

    @Column(name="PERM_USERNAME")
    private String permUsername;

    @Transient
    private User user;



    public BigDecimal getPermId() {
        return permId;
    }

    public void setPermId(BigDecimal permId) {
        this.permId = permId;
    }

    public BigDecimal getPermRapId() {
        return permRapId;
    }

    public void setPermRapId(BigDecimal permRapId) {
        this.permRapId = permRapId;
    }

    public BigDecimal getPermUserId() {
        return permUserId;
    }

    public void setPermUserId(BigDecimal permUserId) {
        this.permUserId = permUserId;
    }

    public String getPermUsername() {
        return permUsername;
    }

    public void setPermUsername(String permUsername) {
        this.permUsername = permUsername;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.permUserId = user.getId();
        if (this.permUsername == null) {
            this.permUsername = user.getUsername();
        }
    }

}
