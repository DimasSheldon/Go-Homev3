package sheldon.com.android.gohomev2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sheldon.com.android.gohomev2.R;
import sheldon.com.android.gohomev2.activities.MainActivity;
import sheldon.com.android.gohomev2.adapters.DigitalInputAdapter;
import sheldon.com.android.gohomev2.content.WidgetMonitor;
import sheldon.com.android.gohomev2.adapters.AnalogInputAdapter;
import sheldon.com.android.gohomev2.helper.ExpandableButton;

public class MonitorFragment extends Fragment {

    private ImageButton mExpandButtonAI, mExpandButtonDI;
    private RecyclerView mRecyclerViewAI, mRecyclerViewDI;
    private static ShimmerFrameLayout analogInputShimmer, digitalInputShimmer;
    private static ArrayList<WidgetMonitor> widgetsAI, widgetsDI;
    private static AnalogInputAdapter analogInputAdapter;
    private static DigitalInputAdapter digitalInputAdapter;
    private static int positionAI = 0;
    private static int positionDI = 0;

    public MonitorFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        widgetsAI = new ArrayList<>();
        widgetsDI = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_monitor, container, false);

        mRecyclerViewAI = (RecyclerView) rootView.findViewById(R.id.rv_analog_input);
        mRecyclerViewDI = (RecyclerView) rootView.findViewById(R.id.rv_digital_input);
        mExpandButtonAI = (ImageButton) rootView.findViewById(R.id.expand_button_ai);
        mExpandButtonDI = (ImageButton) rootView.findViewById(R.id.expand_button_di);
        analogInputShimmer = (ShimmerFrameLayout) rootView.findViewById(R.id.rv_container_ai);
        digitalInputShimmer = (ShimmerFrameLayout) rootView.findViewById(R.id.rv_container_di);

        initiateEmptyWidgets();
        analogInputShimmer.startShimmerAnimation();
        digitalInputShimmer.startShimmerAnimation();

        analogInputAdapter = new AnalogInputAdapter(widgetsAI);
        digitalInputAdapter = new DigitalInputAdapter(widgetsDI);
        LinearLayoutManager llmAI = new LinearLayoutManager(getActivity());
        LinearLayoutManager llmDI = new LinearLayoutManager(getActivity());

        mRecyclerViewAI.setHasFixedSize(true);
        mRecyclerViewDI.setHasFixedSize(true);
        mRecyclerViewAI.setAdapter(analogInputAdapter);
        mRecyclerViewDI.setAdapter(digitalInputAdapter);
        mRecyclerViewAI.setLayoutManager(llmAI);
        mRecyclerViewDI.setLayoutManager(llmDI);

        ExpandableButton.giveActionOnClick(mExpandButtonAI, mRecyclerViewAI);
        ExpandableButton.giveActionOnClick(mExpandButtonDI, mRecyclerViewDI);

        return rootView;
    }

    private static void initiateEmptyWidgets() {
        widgetsAI.add(new WidgetMonitor("default", "Monitoring", "bg-gray", "NA", MainActivity.starText));
        widgetsDI.add(new WidgetMonitor("default", "Monitoring", "bg-gray", "NA", MainActivity.starText));
    }

    public static void updateDataAI(JSONObject jsonObject) {
        update(widgetsAI, positionAI, jsonObject);

        analogInputAdapter.notifyDataSetChanged();

        positionAI++;
    }

    public static void updateDataDI(JSONObject jsonObject) {
        update(widgetsDI, positionDI, jsonObject);

        digitalInputAdapter.notifyDataSetChanged();

        positionDI++;
    }

    private static void update(ArrayList<WidgetMonitor> widgets, int position, JSONObject jsonObject) {
        if (!(position >= widgets.size())) {
            Log.d("MONITOR_FRAGMENT", "UPDATING WIDGETS");

            try {
                widgets.get(position).setIcon(jsonObject.getString("icon"));
                widgets.get(position).setLabel(jsonObject.getString("label"));
                widgets.get(position).setColor(jsonObject.getString("color"));
                widgets.get(position).setValue(jsonObject.getString("value"));
                widgets.get(position).setUpdateIndicator(MainActivity.starText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Log.d("MONITOR_FRAGMENT", "ADDED NEW ELEMENTS");
                widgets.add(position, new WidgetMonitor(jsonObject.getString("icon"),
                        jsonObject.getString("label"),
                        jsonObject.getString("color"),
                        jsonObject.getString("value"),
                        MainActivity.starText));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetPosition() {
        positionAI = 0;
        positionDI = 0;
        analogInputShimmer.stopShimmerAnimation();
        digitalInputShimmer.stopShimmerAnimation();
    }

    public static void removeUnusedAIWidgets() {
        for (int i = MainActivity.countUpdateAI; i < widgetsAI.size(); i++) {
            widgetsAI.remove(i);
        }

        analogInputAdapter.notifyItemRemoved(positionAI);
        analogInputAdapter.notifyItemRangeRemoved(positionAI, analogInputAdapter.getItemCount());
    }

    public static void removeUnusedDIWidgets() {
        for (int i = MainActivity.countUpdateDI; i < widgetsDI.size(); i++) {
            widgetsDI.remove(i);
        }

        digitalInputAdapter.notifyItemRemoved(positionDI);
        digitalInputAdapter.notifyItemRangeRemoved(positionDI, digitalInputAdapter.getItemCount());
    }
}