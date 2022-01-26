package pl.kskowronski.data.service.admin.report;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.report.ParamType;
import pl.kskowronski.data.entity.report.ReportDetail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReportRunService {

    @PersistenceContext
    private EntityManager em;


    public Grid<Map<String, String>> runSqlForGrid(String sqlQuery, List<ReportDetail>  paramList, Grid<Map<String, String>> grid ) {

        grid.removeAllColumns();
        Gson gson = new Gson(); // Creates new instance of Gson
        List<Map<String, String>> items = new ArrayList<>();
        var response = getDataFromSqlQuery(sqlQuery, paramList);

        AtomicInteger j = new AtomicInteger();
        response.forEach(row -> {
            JsonElement element = gson.fromJson(row, JsonElement.class); //Converts the json string to JsonElement without POJO
            JsonObject jsonObj = element.getAsJsonObject(); //Converting JsonElement to JsonObject

            if (j.get() == 0) {
                jsonObj.keySet().stream().forEach(i -> {
                    grid.addColumn(map -> map.get(i)).setHeader(i);
                });
            }

            final Map<String, String> values = new HashMap<>();
            jsonObj.keySet().stream().forEach(i -> {
                values.put(i, jsonObj.get(i).getAsString());
            });
            items.add(values);
            j.getAndIncrement();
        });

        grid.setItems(items);
        return grid;
    }


        private JsonArray getDataFromSqlQuery(String sqlQuery, List<ReportDetail> paramList) {
        Gson gson = new Gson();
        String[] cellName = sqlQuery.split(" ");
        String sql= sqlQuery;

        //put parameters to sql
        for ( ReportDetail param : paramList ) {
            if (param.getSrpTyp().equals(ParamType.NAPIS.name()))
             sql = sql.replaceAll(":"+param.getSrpName(), "'"+param.getStringValue()+"'");

            if (param.getSrpTyp().equals(ParamType.DATA.name()))
                sql = sql.replaceAll(":"+param.getSrpName(), "to_date('"+param.getDateValue()+"','YYYY-MM-DD')");
        }

        try {
            List<Object[]> result = em.createNativeQuery(sql).getResultList();

            JsonArray jsonArray = new Gson().fromJson(gson.toJson(result), JsonArray.class);
            JsonArray jsonEnd = new JsonArray();
            jsonArray.forEach(item -> {
                JsonObject j = new JsonObject();
                for (int i=0; i<jsonArray.get(0).getAsJsonArray().size(); i++) {
                    j.add(cellName[i+1].replace(",",""), item.getAsJsonArray().get(i));
                }
                jsonEnd.add(j);
            });

            return jsonEnd;

        } catch (Exception ex) {
            Notification.show(ex.getMessage());
        }

        return null;

    }



}
