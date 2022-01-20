package pl.kskowronski.data.service.admin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ReportRunService {

    @PersistenceContext
    private EntityManager em;

    public JsonArray getDataFromSqlQuery( String sqlQuery ) {
        Gson gson = new Gson();
        String[] cellName = sqlQuery.split(" ");
        String sql = sqlQuery;
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
    }
}
