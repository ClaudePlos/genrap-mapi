package pl.kskowronski.data.service.admin;

import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.ReportDetail;

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

    public ReportDetail add(ReportDetail report) {
        return repo.save(report);
    }

    public ReportDetail update(ReportDetail report) {
        return repo.save(report);
    }

    public void delete(ReportDetail report) {
        repo.delete(report);
    }

}
