package pl.kskowronski.data.service.admin.report;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.vaadin.crudui.crud.CrudListener;
import pl.kskowronski.data.entity.report.Report;
import pl.kskowronski.data.entity.report.ReportDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReportService implements CrudListener<Report> {

    private final ReportRepo repo;

    public ReportService(ReportRepo repo) {
        this.repo = repo;
    }

    public Report getReportById( BigDecimal repId) {
        Report rep = repo.getForRepId(repId);
        return rep;
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
