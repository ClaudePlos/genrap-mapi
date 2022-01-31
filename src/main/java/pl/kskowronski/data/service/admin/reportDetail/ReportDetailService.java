package pl.kskowronski.data.service.admin.reportDetail;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.report.ParamType;
import pl.kskowronski.data.entity.report.ReportDetail;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
public class ReportDetailService {

    private final ReportDetailRepo repo;

    public ReportDetailService(ReportDetailRepo repo) {
        this.repo = repo;
    }

    // main GRID for both Admin and User
    private GridListDataView<Map<String, String>> dataView;
    public Grid<Map<String, String>> gridData = new Grid<>();

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

        if ( detail.getSrpTyp().equals(ParamType.EXCEL.name()) ) {
            Button a = new Button("EXCEL");
            a.addClickListener( e -> {
                Stream<Map<String, String>> data = gridData.getGenericDataView().getItems();
                data.forEach( i -> {
                    System.out.println(i);
                });
            });
            return a;
        }

        if ( detail.getSrpTyp().equals(ParamType.CSV.name()) ) {
            var streamResource = new StreamResource("dane.csv",
                    () -> {
                        try {
                            AtomicReference<String> dataCsv = new AtomicReference<>("");
                            Stream<Map<String, String>> data = gridData.getGenericDataView().getItems();
                            AtomicReference<String> firstRow = new AtomicReference<>("");

                            AtomicInteger j = new AtomicInteger();
                            data.forEach( i -> {
                                if ( j.get() == 0 ){ // Header
                                    AtomicReference<String> key = new AtomicReference<>("");
                                    i.entrySet().stream().forEach( e -> {
                                        key.set(e.getKey());
                                        dataCsv.set(dataCsv.get() + key.get() + ";");
                                        firstRow.set(firstRow.get() + e.getValue() + ";" );
                                    });
                                } else { // Data
                                    if ( j.get() == 1) {
                                        dataCsv.set(dataCsv.get() + "\n" +  firstRow.get());
                                    }
                                    dataCsv.set(dataCsv.get() + "\n");
                                    AtomicReference<String> value = new AtomicReference<>("");
                                    i.entrySet().stream().forEach( e -> {
                                        value.set(e.getValue());
                                        dataCsv.set(dataCsv.get() + value.get() + ";");
                                    });
                                }
                                j.getAndIncrement();
                            });

                            return new ByteArrayInputStream(dataCsv.get().getBytes());
                        } catch (Exception e) {
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



    private String export(Grid<Map<String, String>> grid) {
        // Fetch all data from the grid in the current sorted order
        Stream<Map<String, String>> persons = null;


        Set<Map<String, String>> selection = (Set<Map<String, String>>) grid.getDataProvider();
        if (selection != null && selection.size() > 0) {
            persons = selection.stream();
        } else {
            persons = dataView.getItems();
            // Alternative approach without DataView
            // persons = ((DataProvider<Person, String>) grid.getDataProvider()).fetch(createQuery(grid));
        }

        StringWriter output = new StringWriter();
        StatefulBeanToCsv<Map<String, String>> writer = new StatefulBeanToCsvBuilder<Map<String, String>>(output).build();
        try {
            writer.write(persons);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            output.write("An error occured during writing: " + e.getMessage());
        }

        return output.toString();
    }

}
