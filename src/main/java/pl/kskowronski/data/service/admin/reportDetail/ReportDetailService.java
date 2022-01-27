package pl.kskowronski.data.service.admin.reportDetail;

import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.report.ParamType;
import pl.kskowronski.data.entity.report.ReportDetail;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ReportDetailService {

    private final ReportDetailRepo repo;

    public ReportDetailService(ReportDetailRepo repo) {
        this.repo = repo;
    }

    // main GRID for both Admin and User
    public Grid<Map<String, String>> gridData;

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

    public BigDecimal findMaxId(){
        BigDecimal id = repo.findMaxId();
        return id;
    }


    public Component getComponentForParameter(ReportDetail detail) {

        if ( detail.getSrpTyp().equals(ParamType.NAPIS.name()) ) {
            TextField t = new TextField();
            t.setClassName("param");
            t.setLabel(detail.getSrpName());
            t.setValueChangeMode(ValueChangeMode.EAGER);
            t.setId(detail.getSrpId().toString());
            return t;
        }

        if ( detail.getSrpTyp().equals(ParamType.DATA.name()) ) {
            DatePicker d = new DatePicker();
            d.setClassName("param");
            d.setLabel(detail.getSrpName());
            d.setId(detail.getSrpId().toString());
           return d;
        }

        if ( detail.getSrpTyp().equals(ParamType.CSV.name()) ) {
            var streamResource = new StreamResource("dane.csv",
                    () -> {
                        try {
                            Stream<Map<String, String>> data = gridData.getGenericDataView().getItems();
                            StringWriter output = new StringWriter();
                            var beanToCsv = new StatefulBeanToCsvBuilder<Map<String, String>>(output).build();
                            beanToCsv.write(data);
                            var contents = output.toString();
                            return new ByteArrayInputStream(contents.getBytes());
                        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                            e.printStackTrace();
                            return null;
                        }
                    });
            Anchor a = new Anchor(streamResource, "CSV");
            a.setClassName("param");
            a.setId(detail.getSrpId().toString());
            return a;
        }

        return null;
    }


}
