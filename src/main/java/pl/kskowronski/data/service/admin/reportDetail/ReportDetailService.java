package pl.kskowronski.data.service.admin.reportDetail;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.report.ParamType;
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


    public Component getComponentForParameter(ReportDetail detail) {

        if ( detail.getSrpTyp().equals(ParamType.NAPIS.name()) ) {
            TextField t = new TextField();
            t.setLabel(detail.getSrpName());
            t.setValueChangeMode(ValueChangeMode.EAGER);
            t.setId(detail.getSrpId().toString());
            return t;
        }

        if ( detail.getSrpTyp().equals(ParamType.DATA.name()) ) {
            DatePicker d = new DatePicker();
            d.setLabel(detail.getSrpName());
            d.setId(detail.getSrpId().toString());
           return d;
        }

        return null;
    }


}
