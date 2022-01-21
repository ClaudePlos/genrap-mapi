package pl.kskowronski.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "nap_reports_details")
public class ReportDetail {

    @Id
    @Column(name="SRP_ID")
    private BigDecimal srpId;

    @Column(name="SRP_RAP_ID")
    private BigDecimal srpRapId;

    @Column(name="SRP_TYP")
    private String srpTyp;

    @Column(name="SRP_NAME")
    private String srpName;

    @Column(name="SRP_SQL")
    private String srpSql;

    @Column(name="SRP_OUTER")
    private String srpOuter;

    @Column(name="SRP_FORMULA")
    private String srpFormula;

    public ReportDetail() {
    }

    public BigDecimal getSrpId() {
        return srpId;
    }

    public void setSrpId(BigDecimal srpId) {
        this.srpId = srpId;
    }

    public BigDecimal getSrpRapId() {
        return srpRapId;
    }

    public void setSrpRapId(BigDecimal srpRapId) {
        this.srpRapId = srpRapId;
    }

    public String getSrpTyp() {
        return srpTyp;
    }

    public void setSrpTyp(String srpTyp) {
        this.srpTyp = srpTyp;
    }

    public String getSrpName() {
        return srpName;
    }

    public void setSrpName(String srpName) {
        this.srpName = srpName;
    }

    public String getSrpSql() {
        return srpSql;
    }

    public void setSrpSql(String srpSql) {
        this.srpSql = srpSql;
    }

    public String getSrpOuter() {
        return srpOuter;
    }

    public void setSrpOuter(String srpOuter) {
        this.srpOuter = srpOuter;
    }

    public String getSrpFormula() {
        return srpFormula;
    }

    public void setSrpFormula(String srpFormula) {
        this.srpFormula = srpFormula;
    }
}
