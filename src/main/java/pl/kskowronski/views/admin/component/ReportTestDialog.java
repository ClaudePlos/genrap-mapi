package pl.kskowronski.views.admin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import pl.kskowronski.data.entity.report.Report;
import pl.kskowronski.data.entity.report.ReportDetail;
import pl.kskowronski.data.service.admin.report.ReportRunService;
import pl.kskowronski.data.service.admin.report.ReportService;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportTestDialog extends Dialog {

    private ReportService reportService;
    private ReportRunService reportRunService;
    private ReportDetailService reportDetailService;

    //private Grid<Map<String, String>> grid = new Grid<>();
    private TextArea textSql = new TextArea();
    private Report report;

    private HorizontalLayout h01 = new HorizontalLayout();

    public List<ReportDetail> paramList = new ArrayList<>();


    public ReportTestDialog(ReportRunService reportRunService, ReportService reportService, ReportDetailService reportDetailService) {
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
        add(reportDetailService.gridData);
        open();
    }

    private void saveReport() {
        report.setRapSql(textSql.getValue());
        reportService.update(report);
        Notification.show("Zapisano");
    }

    private void runSql( String sqlQuery ){
        reportDetailService.gridData= reportRunService.runSqlForGrid(sqlQuery, paramList, reportDetailService.gridData);
    }

    private void openDialogAddParams() {
        var dialogAddParams = new AddParamsDialog(reportDetailService, report.getId());
        dialogAddParams.open(report.getId());
    }



    private void setupParameters() {
        var h01 = new HorizontalLayout();
        h01.setClassName("h01param");
        paramList  = reportDetailService.findReportDetailBySrpRapId(report.getId());
        paramList.stream().forEach( item -> {
            Component component = reportDetailService.getComponentForParameter( item );
            addEvenListenerChangeToParameter(component);
            h01.add(component);
            //addParamToReport(item);
        });

        add(h01);

    }

    /** manage components **/
    private void addEvenListenerChangeToParameter( Component component ) {
        if (component instanceof TextField) {
            TextField t = (TextField) component;
            t.addValueChangeListener(event -> {
                updateStringValueForParam(BigDecimal.valueOf(Long.valueOf(t.getId().get())), t.getValue());
            });
        } else if (component instanceof DatePicker) {
            DatePicker d = (DatePicker) component;
            d.addValueChangeListener(event ->
                    updateDateValueForParam(BigDecimal.valueOf(Long.valueOf(d.getId().get())), d.getValue())
            );
        }
    }

    private void updateStringValueForParam(BigDecimal paramId, String value){
        paramList.stream().filter(item -> item.getSrpId().equals(paramId)).collect(Collectors.toList()).get(0).setStringValue(value);
    }

    private void updateDateValueForParam(BigDecimal paramId, LocalDate value){
        paramList.stream().filter(item -> item.getSrpId().equals(paramId)).collect(Collectors.toList()).get(0).setDateValue(value);
    }




}
