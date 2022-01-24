package pl.kskowronski.data.service.admin.reportDetail;

import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.report.ReportDetail;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportDetailService {

    private final ReportDetailRepo repo;

    public ReportDetailService(ReportDetailRepo repo) {
        this.repo = repo;
    }

    public List<ReportDetail> findAll() {
        return repo.findAll();
    }

    public List<ReportDetail> findReportDetailBySrpRapId( BigDecimal rapId) {
        return repo.findReportDetailBySrpRapIdOrderBySrpLp(rapId).get();
    }

    public ReportDetail save(ReportDetail report) {
        return repo.save(report);
    }

    public ReportDetail update(ReportDetail report) {
        return repo.save(report);
    }

    public void delete(ReportDetail report) {
        repo.delete(report);
    }

    public void deleteById(BigDecimal id) {
        repo.deleteById(id);
    }

}
