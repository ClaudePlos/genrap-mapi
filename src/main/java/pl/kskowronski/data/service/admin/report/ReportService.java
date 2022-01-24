package pl.kskowronski.data.service.admin.report;

import org.springframework.stereotype.Service;
import org.vaadin.crudui.crud.CrudListener;
import pl.kskowronski.data.entity.report.Report;

import java.util.List;

@Service
public class ReportService implements CrudListener<Report> {

    private final ReportRepo repo;

    public ReportService(ReportRepo repo) {
        this.repo = repo;
    }

    @Override
    public List<Report> findAll() {
        return repo.findAll();
    }

    @Override
    public Report add(Report report) {
        return repo.save(report);
    }

    @Override
    public Report update(Report report) {
        return repo.save(report);
    }

    @Override
    public void delete(Report report) {
        repo.delete(report);
    }

}
