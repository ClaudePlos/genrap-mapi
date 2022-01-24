package pl.kskowronski.data.service.admin.report;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;
import pl.kskowronski.data.entity.report.ParamType;
import pl.kskowronski.data.entity.report.ReportDetail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ReportRunService {

    @PersistenceContext
    private EntityManager em;

    public JsonArray getDataFromSqlQuery(String sqlQuery, List<ReportDetail> paramList) {
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
