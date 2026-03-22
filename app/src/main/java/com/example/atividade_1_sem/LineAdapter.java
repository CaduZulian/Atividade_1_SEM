package com.example.atividade_1_sem;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.List;

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
        subtitle.setText("Início: " + line.start +
                "    Intervalo: " + line.interval);

        return view;
    }
}
