package lk.archmage.scopecinemas.Interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

import lk.archmage.scopecinemas.Models.ConcessionData;

public interface ConcessionInterface {

    void setConcessionObject(ConcessionData object);

    void removeConcessionObject(ConcessionData object);
}
