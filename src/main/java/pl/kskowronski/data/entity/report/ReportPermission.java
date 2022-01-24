package pl.kskowronski.data.entity.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "nap_reports")
public class ReportPermission {

    @Id
    @Column(name="PERM_ID")
    private BigDecimal permId;

    @Column(name="PERM_RAP_ID")
    private BigDecimal permRapId;

    @Column(name="PERM_USER_ID")
    private BigDecimal permUserId;

    @Column(name="PERM_USERNAME")
    private String permUSername;

    @Column(name="PERM_RAP_NAME")
    private String permRapName;

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

    public String getPermUSername() {
        return permUSername;
    }

    public void setPermUSername(String permUSername) {
        this.permUSername = permUSername;
    }

    public String getPermRapName() {
        return permRapName;
    }

    public void setPermRapName(String permRapName) {
        this.permRapName = permRapName;
    }
}
