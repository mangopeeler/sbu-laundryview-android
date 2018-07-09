package com.cptmango.sbu_laundryview.data;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.MachineStatus;
import com.cptmango.sbu_laundryview.data.model.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataManager {

    private String quad;
    private String building;
    private String dataURL;
    private int timeout = 0;

    Context context;
    Room room;
    RequestQueue queue;

    public DataManager(Context context, String quad, String building){
        this.quad = quad;
        this.building = building;
        this.context = context;
        queue = Volley.newRequestQueue(context);




        String url = context.getResources().getString(R.string.url);
        dataURL = url + "/" + getURLName(quad) + "/" + getURLName(building);
    }

    public void getData(){

        if(timeout != 0) {
//            Toast.makeText(context, "Retrying refresh request " + timeout, Toast.LENGTH_SHORT).show();
            System.out.println("Retrying request " + timeout);
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, dataURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if(!parseData(response)){
                    if(timeout == 3) return;
                    // Retry request.
                    timeout++;
                    getData();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "An error occurred while retrieving data. Try again later.", Toast.LENGTH_LONG).show();
                System.out.println("An error has occurred retrieving the data. Retrying.");
            }
        });

        queue.add(request);

    }

    private boolean parseData(JSONObject data){

        try{

            JSONArray machines = data.getJSONArray("machines");

            // Check whether the data retrieval was successful.
            if(machines.length() == 0){
                System.out.println("Data was empty.");
                return false;
            }

            if(room == null){
                room = new Room(
                        quad,
                        building,
                        data.getInt("totalWashers"),
                        data.getInt("totalDryers"));

            }

            Machine[] newMachineData = new Machine[room.totalMachines()];

            // Going through the JSON array to parse the data and pass the data into the Room object.
            int washers_available = 0;
            int dryers_available = 0;

            for(int i = 0; i < room.totalMachines(); i++){

                String machineStatusSummary = machines.getJSONObject(i).getString("status");

                MachineStatus statusCode;
                int machineTimeLeft;

                boolean isWasher = i <= room.totalWashers() - 1;

                // Setting machine status.
                switch(machines.getJSONObject(i).getInt("statusCode")){

                    case 0: statusCode = MachineStatus.AVAILABLE;
                            if(isWasher) washers_available++; else dryers_available++;
                    break;

                    case 1: statusCode = MachineStatus.IN_PROGRESS;
                    break;

                    case 2: statusCode = MachineStatus.DONE_DOOR_CLOSED;
                    break;

                    case -1: statusCode = MachineStatus.OUT_OF_ORDER;
                    break;

                    default: return false;

                }

                if(statusCode == MachineStatus.IN_PROGRESS) {

                    machineStatusSummary = machineStatusSummary.substring(machineStatusSummary.indexOf("remaining ") + 10);
                    machineTimeLeft = Integer.parseInt(machineStatusSummary.substring(0, machineStatusSummary.indexOf(" ")));

                }
                else { machineTimeLeft = -1; }

                newMachineData[i] = new Machine(i+1, machineTimeLeft, statusCode, isWasher);

            }


            room.setMachineData(newMachineData);
            room.setDryers_available(dryers_available);
            room.setWashers_available(washers_available);

            // Reset timeout. Retrieval and parse was successful.
            timeout = 0;
            return true;

        } catch (JSONException e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public Room getRoomData(){ return room; }

    public RequestQueue getQueue() {
        return queue;
    }

    /**
     * Changes the spaces of the names within building to underscores.
     * Example --> "Greeley A" to "Greeley_A"
     * @param string The name of the building/quad to be converted to be URL compliant.
     * @return URL compliant name of the building/quad.
     */
    private String getURLName(String string){


        if(string.contains(" ")){

            return string.replace(" ", "_");

        }
        else if(string.contains("'")){

            return "Oneill";
        }
        else return string;


    }
}
