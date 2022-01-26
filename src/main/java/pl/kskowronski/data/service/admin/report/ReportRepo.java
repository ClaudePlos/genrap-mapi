package pl.kskowronski.data.service.admin.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kskowronski.data.entity.report.Report;


import java.math.BigDecimal;

public interface ReportRepo extends JpaRepository<Report, BigDecimal> {

    @Query("select r from Report r where r.id = :rapId")
    Report getForRepId(@Param("rapId") BigDecimal rapId);

}
