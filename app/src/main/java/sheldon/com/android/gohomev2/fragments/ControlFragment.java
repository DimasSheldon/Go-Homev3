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
import sheldon.com.android.gohomev2.adapters.DigitalOutputAdapter;
import sheldon.com.android.gohomev2.content.WidgetControl;
import sheldon.com.android.gohomev2.helper.ExpandableButton;

public class ControlFragment extends Fragment {

    private ImageButton mExpandButtonDO;
    private RecyclerView mRecyclerViewDO;
    private static ShimmerFrameLayout digitalOutputShimmer;
    private static DigitalOutputAdapter digitalOutputAdapter;
    private static int positionDO = 0;

    private static ArrayList<WidgetControl> widgetsDO;

    public ControlFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        widgetsDO = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);

        mRecyclerViewDO = (RecyclerView) rootView.findViewById(R.id.rv_control);
        mExpandButtonDO = (ImageButton) rootView.findViewById(R.id.expand_button_do);
        digitalOutputShimmer = (ShimmerFrameLayout) rootView.findViewById((R.id.rv_container_do));

        initiateEmptyWidgets();
        digitalOutputShimmer.startShimmerAnimation();

        digitalOutputAdapter = new DigitalOutputAdapter(widgetsDO);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        mRecyclerViewDO.setHasFixedSize(true);
        mRecyclerViewDO.setAdapter(digitalOutputAdapter);
        mRecyclerViewDO.setLayoutManager(llm);

        ExpandableButton.giveActionOnClick(mExpandButtonDO, mRecyclerViewDO);

        return rootView;
    }

    private static void initiateEmptyWidgets() {
        widgetsDO.add(new WidgetControl("Control", "bg-gray", "NA", MainActivity.starText));
    }

    public static void updateDataDO(JSONObject jsonObject) {
        update(widgetsDO, positionDO, jsonObject);

        digitalOutputAdapter.notifyDataSetChanged();

        positionDO++;
    }

    private static void update(ArrayList<WidgetControl> widgets, int position, JSONObject jsonObject) {
        if (!(position >= widgets.size())) {
            Log.d("CONTROL_FRAGMENT", "UPDATE");
            try {
                widgets.get(position).setLabel(jsonObject.getString("label"));
                widgets.get(position).setColor(jsonObject.getString("color"));
                widgets.get(position).setValue(jsonObject.getString("value"));
                widgets.get(position).setUpdateIndicator(MainActivity.starText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Log.d("CONTROL_FRAGMENT", "ADD");
                widgets.add(position, new WidgetControl(jsonObject.getString("label"),
                        jsonObject.getString("color"),
                        jsonObject.getString("value"),
                        MainActivity.starText));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetPosition() {
        positionDO = 0;
        digitalOutputShimmer.stopShimmerAnimation();
    }

    public static void removeUnusedD0Widgets() {
        for (int i = MainActivity.countUpdateDO; i < widgetsDO.size(); i++) {
            widgetsDO.remove(i);
        }

        digitalOutputAdapter.notifyItemRemoved(positionDO);
        digitalOutputAdapter.notifyItemRangeRemoved(positionDO, digitalOutputAdapter.getItemCount());
    }
}