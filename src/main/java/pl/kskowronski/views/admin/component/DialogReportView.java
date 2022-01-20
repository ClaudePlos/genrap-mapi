package pl.kskowronski.views.admin.component;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextArea;
import pl.kskowronski.data.entity.Report;
import pl.kskowronski.data.service.admin.ReportRunService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DialogReportView extends Dialog {

    private ReportRunService reportRunService;

    private Grid<Map<String, String>> grid = new Grid<>();

    public DialogReportView(ReportRunService reportRunService) {
        this.reportRunService = reportRunService;
        setWidth("1000px");
        setHeight("600px");
    }

    public void open(Report report ) {
        int charLimit = 1400000;
        var textSql = new TextArea();
        textSql.setLabel("Sql");
        textSql.setValue(report.getRapSql());
        textSql.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
        });

        var buttonRun = new Button("Run");
        buttonRun.addClickListener( e -> runSql(textSql.getValue()));
        var buttonSave = new Button("Save");

        add(textSql, buttonRun, buttonSave);
        add(grid);
        open();
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


}
