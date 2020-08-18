package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lk.archmage.scopecinemas.R;

public class MovieCastingAdapter extends ArrayAdapter {

    Context context;
    ArrayList castingArray;

    public MovieCastingAdapter(Context context, ArrayList castingArray) {
        super(context, android.R.layout.simple_list_item_1, castingArray);
        this.context = context;
        this.castingArray = castingArray;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layout_casting, null);


        TextView castNameTv = convertView.findViewById(R.id.castNameTv);
        TextView acterNameTv = convertView.findViewById(R.id.acterNameTv);

        try {

            JSONObject getCastOBJ = new JSONObject(castingArray.get(position).toString());

            castNameTv.setText(getCastOBJ.getString("actor"));
            acterNameTv.setText(getCastOBJ.getString("character"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }
}
