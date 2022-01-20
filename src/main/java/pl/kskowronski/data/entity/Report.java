package pl.kskowronski.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "nap_reports")
public class Report {

    @Id
    @Column(name="RAP_ID")
    private BigDecimal id;

    @Column(name="RAP_NAME")
    private String rapName;

    @Column(name="RAP_OP_ID")
    private BigDecimal rapOpId;

    @Column(name="RAP_DESC")
    private String rapDesc;

    @Column(name="RAP_SQL")
    private String rapSql;

    public Report() {
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getRapName() {
        return rapName;
    }

    public void setRapName(String rapName) {
        this.rapName = rapName;
    }

    public BigDecimal getRapOpId() {
        return rapOpId;
    }

    public void setRapOpId(BigDecimal rapOpId) {
        this.rapOpId = rapOpId;
    }

    public String getRapDesc() {
        return rapDesc;
    }

    public void setRapDesc(String rapDesc) {
        this.rapDesc = rapDesc;
    }

    public String getRapSql() {
        return rapSql;
    }

    public void setRapSql(String rapSql) {
        this.rapSql = rapSql;
    }
}
