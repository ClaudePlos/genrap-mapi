package pl.kskowronski.data.service.admin.reportDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import pl.kskowronski.data.entity.report.ReportDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReportDetailRepo extends JpaRepository<ReportDetail, BigDecimal> {

    Optional<List<ReportDetail>> findReportDetailBySrpRapId(@Param("rapId") BigDecimal rapId);

}
