package pl.kskowronski.views.admin.component;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import pl.kskowronski.data.entity.Report;
import pl.kskowronski.data.service.admin.ReportRunService;
import pl.kskowronski.data.service.admin.ReportService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DialogReportView extends Dialog {

    private ReportService reportService;
    private ReportRunService reportRunService;

    private Grid<Map<String, String>> grid = new Grid<>();
    private TextArea textSql = new TextArea();
    private Report report;

    private HorizontalLayout h01 = new HorizontalLayout();


    public DialogReportView(ReportRunService reportRunService, ReportService reportService) {
        this.reportRunService = reportRunService;
        this.reportService = reportService;
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
        buttonRun.addClickListener( e -> saveReport());
        var buttonParams = new Button("Params");
        buttonParams.addClickListener( e -> saveReport());

        h01.add(buttonRun, buttonSave);
        add(textSql, buttonRun, buttonParams, buttonSave);
        add(grid);
        open();
    }

    private void saveReport() {
        report.setRapSql(textSql.getValue());
        reportService.update(report);
    }

    private void runSql( String sqlQuery ){
        grid.removeAllColumns();
        Gson gson = new Gson(); // Creates new instance of Gson
        List<Map<String, String>> items = new ArrayList<>();
        var response = reportRunService.getDataFromSqlQuery(sqlQuery);

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
                values.put(i, jsonObj.get(i).toString() );
            });
            items.add(values);
            j.getAndIncrement();
        });

        grid.setItems(items);

    }

    private void openDialogAddParams() {
        var dialogAddParams = new DialogAddParams();
        dialogAddParams.open();
    }


}
