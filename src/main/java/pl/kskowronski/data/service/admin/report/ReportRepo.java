package pl.kskowronski.data.service.admin.report;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kskowronski.data.entity.report.Report;


import java.math.BigDecimal;

public interface ReportRepo extends JpaRepository<Report, BigDecimal> {

}
