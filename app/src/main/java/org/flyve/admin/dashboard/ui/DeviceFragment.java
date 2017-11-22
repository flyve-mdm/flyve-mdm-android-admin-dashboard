package org.flyve.admin.dashboard.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.flyve.admin.dashboard.R;
import org.flyve.admin.dashboard.adapter.DeviceAdapter;
import org.flyve.admin.dashboard.adapter.DeviceTouchHelper;
import org.flyve.admin.dashboard.utils.FlyveLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceFragment extends Fragment {

    private ProgressBar pb;
    private RecyclerView lst;
    private List<HashMap<String, String>> data;
    private DeviceAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device, container, false);

        pb = v.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);

        lst = v.findViewById(R.id.lst);

        LinearLayoutManager llm = new LinearLayoutManager(DeviceFragment.this.getActivity());
        lst.setLayoutManager(llm);

        lst.setItemAnimator(new DefaultItemAnimator());
        lst.addItemDecoration(new DividerItemDecoration(DeviceFragment.this.getContext(), DividerItemDecoration.VERTICAL));

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new DeviceTouchHelper(0, ItemTouchHelper.LEFT, new DeviceTouchHelper.RecyclerItemTouchHelperListener() {

            /**
             * callback when recycler view is swiped
             * item will be removed on swiped
             * undo option will be provided in snackbar to restore the item
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                if (viewHolder instanceof DeviceAdapter.DataViewHolder) {
                    // get the removed item name to display it in snack bar
                    String name = data.get(viewHolder.getAdapterPosition()).get("UserRealName");

                    // backup of removed item for undo purpose
                    final HashMap<String, String> deletedItem = data.get(viewHolder.getAdapterPosition());
                    final int deletedIndex = viewHolder.getAdapterPosition();

                    // remove the item from recycler view
                    mAdapter.removeItem(viewHolder.getAdapterPosition());

                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar
                            .make(DeviceFragment.this.getView(), name + " removed from list", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // undo is selected, restore the deleted item
                            mAdapter.restoreItem(deletedItem, deletedIndex);
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(lst);


        load(loadJSONFromAsset());

        return v;
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getActivity().getAssets().open("json/agents.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            FlyveLog.e(ex.getMessage());
            return null;
        }
        return json;
    }

    public void load(String jsonStr) {

        data = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(jsonStr);

            JSONArray items = json.getJSONArray("data");
            for (int y = 0; y < items.length(); y++) {

                JSONObject obj = items.getJSONObject(y);
                HashMap<String, String> c = new HashMap<>();

                c.put("type", "data"); // clasify the item if data or header
                c.put("id", obj.getString("PluginFlyvemdmAgent.id"));
                c.put("email", obj.getString("PluginFlyvemdmAgent.name"));
                c.put("fleetName", obj.getString("PluginFlyvemdmAgent.PluginFlyvemdmFleet.name"));
                c.put("ComputerId", obj.getString("PluginFlyvemdmAgent.Computer.id"));
                c.put("ComputerSerial", obj.getString("PluginFlyvemdmAgent.Computer.serial"));
                c.put("UserId", obj.getString("PluginFlyvemdmAgent.Computer.User.id"));
                c.put("FleetId", obj.getString("PluginFlyvemdmAgent.PluginFlyvemdmFleet.id"));
                c.put("lastContact", obj.getString("PluginFlyvemdmAgent.last_contact"));
                c.put("UserRealName", obj.getString("PluginFlyvemdmAgent.Computer.User.realname"));
                c.put("AgentVersion", obj.getString("PluginFlyvemdmAgent.version"));
                c.put("isOnline", obj.getString("PluginFlyvemdmAgent.is_online"));

                data.add(c);
            }

            pb.setVisibility(View.GONE);

            mAdapter = new DeviceAdapter(data, new DeviceAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(HashMap<String, String> item) {
                    openDetail(item)
;                }
            });
            lst.setAdapter(mAdapter);

        } catch (Exception ex) {
            pb.setVisibility(View.GONE);
            FlyveLog.e(ex.getMessage());
        }
    }

    private void openDetail(HashMap<String, String> item) {
        Intent miIntent = new Intent(DeviceFragment.this.getActivity(), DeviceDetailActivity.class);
        DeviceFragment.this.startActivity(miIntent);
    }

}
