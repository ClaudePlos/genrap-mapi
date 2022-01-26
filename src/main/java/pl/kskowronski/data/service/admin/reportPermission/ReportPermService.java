package pl.kskowronski.data.service.admin.reportPermission;

import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.report.ReportDetail;
import pl.kskowronski.data.entity.report.ReportPermission;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportPermService {

    private final ReportPermRepo repo;

    public ReportPermService(ReportPermRepo repo) {
        this.repo = repo;
    }

    public List<ReportPermission> findAll() {
        return repo.findAll();
    }

    public BigDecimal findMaxId(){
        BigDecimal id = repo.findMaxId();
        return id;
    }

    public List<ReportPermission> findReportPermissionByPermRapIdOrderByPermId(BigDecimal rapId) {
        return repo.findReportPermissionByPermRapIdOrderByPermId(rapId).get();
    }

    public ReportPermission save(ReportPermission perm) {
        return repo.save(perm);
    }

    public ReportPermission update(ReportPermission perm) {
        return repo.save(perm);
    }

    public void delete(ReportPermission perm ) {
        repo.delete(perm);
    }

    public void deleteById(BigDecimal id) {
        repo.deleteById(id);
    }

}
