package pl.kskowronski.views.admin.component;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import pl.kskowronski.data.entity.report.ParamType;
import pl.kskowronski.data.entity.report.Report;
import pl.kskowronski.data.entity.report.ReportDetail;
import pl.kskowronski.data.service.admin.report.ReportRunService;
import pl.kskowronski.data.service.admin.report.ReportService;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReportDialog extends Dialog {

    private ReportService reportService;
    private ReportRunService reportRunService;
    private ReportDetailService reportDetailService;

    private Grid<Map<String, String>> grid = new Grid<>();
    private TextArea textSql = new TextArea();
    private Report report;

    private HorizontalLayout h01 = new HorizontalLayout();

    public List<ReportDetail> paramList = new ArrayList<>();


    public ReportDialog(ReportRunService reportRunService, ReportService reportService, ReportDetailService reportDetailService) {
        this.reportDetailService = reportDetailService;
        this.reportRunService = reportRunService;
        this.reportService = reportService;
        setDraggable(true);
        setWidth("1000px");
        setHeight("600px");
    }

    public void open(Report report ) {
        this.report = report;
        int charLimit = 1400000;

        textSql.setLabel("Sql");
        textSql.setWidth("100%");
        textSql.setHeight("200px");
        textSql.setValue(report.getRapSql());
        textSql.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
        });

        var buttonRun = new Button("Run");
        buttonRun.addClickListener( e -> runSql(textSql.getValue()));
        var buttonSave = new Button("Save");
        buttonSave.addClickListener( e -> saveReport());
        var buttonParams = new Button("Params");
        buttonParams.addClickListener( e -> openDialogAddParams());

        h01.add(buttonRun, buttonParams, buttonSave);
        add(textSql, h01);
        setupParameters();
        add(grid);
        open();
    }

    private void saveReport() {
        report.setRapSql(textSql.getValue());
        reportService.update(report);
        Notification.show("Zapisano");
    }

    private void runSql( String sqlQuery ){
        grid.removeAllColumns();
        Gson gson = new Gson(); // Creates new instance of Gson
        List<Map<String, String>> items = new ArrayList<>();
        var response = reportRunService.getDataFromSqlQuery(sqlQuery, paramList);

        AtomicInteger j = new AtomicInteger();
        response.forEach( row -> {
            JsonElement element = gson.fromJson (row, JsonElement.class); //Converts the json string to JsonElement without POJO
            JsonObject jsonObj = element.getAsJsonObject(); //Converting JsonElement to JsonObject

            if (j.get() == 0) {
                jsonObj.keySet().stream().forEach( i -> {
                    grid.addColumn(map -> map.get(i)).setHeader(i);
                });
            }

            final Map<String, String> values = new HashMap<>();
            jsonObj.keySet().stream().forEach( i -> {
                values.put(i, jsonObj.get(i).getAsString() );
            });
            items.add(values);
            j.getAndIncrement();
        });

        grid.setItems(items);

    }

    private void openDialogAddParams() {
        var dialogAddParams = new AddParamsDialog(reportDetailService, report.getId());
        dialogAddParams.open(report.getId());
    }



    private void setupParameters() {
        paramList  = reportDetailService.findReportDetailBySrpRapId(report.getId());
        paramList.stream().forEach( item -> {
            addParamToReport(item);
        });

    }

    private void updateStringValueForParam(BigDecimal paramId, String value){
        paramList.stream().filter(item -> item.getSrpId().equals(paramId)).collect(Collectors.toList()).get(0).setStringValue(value);
    }

    private void updateDateValueForParam(BigDecimal paramId, LocalDate value){
        paramList.stream().filter(item -> item.getSrpId().equals(paramId)).collect(Collectors.toList()).get(0).setDateValue(value);
    }

    private void addParamToReport(ReportDetail detail) {

        if ( detail.getSrpTyp().equals(ParamType.NAPIS.name()) ) {
            TextField t = new TextField();
            t.setLabel(detail.getSrpName());
            t.setValueChangeMode(ValueChangeMode.EAGER);
            t.setId(detail.getSrpId().toString());
            t.addValueChangeListener(event -> {
                updateStringValueForParam(BigDecimal.valueOf(Long.valueOf(t.getId().get())), t.getValue());
            });
            add(t);
        }

        if ( detail.getSrpTyp().equals(ParamType.DATA.name()) ) {
            DatePicker d = new DatePicker();
            d.setLabel(detail.getSrpName());
            d.setId(detail.getSrpId().toString());
            d.addValueChangeListener(event ->
                        updateDateValueForParam(BigDecimal.valueOf(Long.valueOf(d.getId().get())), d.getValue())
            );
            add(d);
        }

    }


}
