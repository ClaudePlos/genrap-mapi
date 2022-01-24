package pl.kskowronski.data.service.admin.reportPermission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import pl.kskowronski.data.entity.report.ReportDetail;
import pl.kskowronski.data.entity.report.ReportPermission;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReportPermRepo extends JpaRepository<ReportPermission, BigDecimal> {

    Optional<List<ReportPermission>> findReportPermissionByPermRapIdOrderById(@Param("rapId") BigDecimal rapId);

}
