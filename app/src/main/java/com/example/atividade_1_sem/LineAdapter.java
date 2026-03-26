package com.example.atividade_1_sem;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LineAdapter extends BaseAdapter {

    Context context;
    List<Line> list;

    public LineAdapter(Context context, List<Line> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.line_list_item, parent, false);
        }

        Line line = list.get(i);

        TextView title = view.findViewById(R.id.title);
        TextView subtitle = view.findViewById(R.id.subtitle);

        title.setText(line.origin + " - " + line.target);

        String displayStart = line.start;
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdfInput.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdfInput.parse(line.start);

            SimpleDateFormat sdfOutput = new SimpleDateFormat("HH:mm", Locale.getDefault());
            displayStart = sdfOutput.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        subtitle.setText("Início: " + displayStart +
                "    Intervalo: " + line.interval);

        return view;
    }
}
