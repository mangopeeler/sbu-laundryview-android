package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.MachineStatus;
import com.cptmango.sbu_laundryview.data.model.Room;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

/**
 * Created by mango on 4/18/18.
 */

public class MachineGridStatusAdapter extends BaseAdapter {

    Room room;
    Activity context;
    boolean isWasher;

    public MachineGridStatusAdapter(Activity context, Room roomData, boolean isWasher){

        this.context = context;
        this.room = roomData;
        this.isWasher = isWasher;

    }

    @Override
    public int getCount() {
        return isWasher ? room.washers() : room.dryers();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.machine_status, null, false);
            holder = new ViewHolder();

            holder.machineNumber = (TextView) convertView.findViewById(R.id.list_Machine_txt_MachineNumber);
            holder.machineStatus = (TextView) convertView.findViewById(R.id.list_Machine_txt_Status);
            holder.timeLeft = (TextView) convertView.findViewById(R.id.list_Machine_txt_TimeLeft);
            holder.statusIcon = (CardView) convertView.findViewById(R.id.littleIcon);
            holder.machineIcon = (ImageView) convertView.findViewById(R.id.machineIcon);
            holder.progressBar = (CircularProgressBar) convertView.findViewById(R.id.list_Machine_progress_ProgressBar);

            convertView.setTag(holder);


        }else{

            holder = (ViewHolder) convertView.getTag();

        }

        Machine machine = room.getMachine(position);

        holder.machineStatus.setText(machine.machineStatus().getDescription());
        holder.machineNumber.setText(position + 1 + "");
        setupCard(holder, machine);

        return convertView;
    }


    public void setupCard(ViewHolder holder, Machine machine){

        if(machine.machineStatus() == MachineStatus.IN_PROGRESS){
            holder.timeLeft.setText(machine.timeLeft() + "");
            holder.progressBar.setProgress(0);
            double progress = (1- (machine.timeLeft() / 60.0)) * 100.0;
            holder.progressBar.setProgressWithAnimation((int) progress, 800);

            holder.machineIcon.setColorFilter(ContextCompat.getColor(context, R.color.Red));
            holder.progressBar.setColor(ContextCompat.getColor(context, R.color.Red));
            holder.statusIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Red));

        }
        else{
            holder.progressBar.setProgress(100);
            holder.machineIcon.setColorFilter(ContextCompat.getColor(context, R.color.Green));
            holder.progressBar.setColor(ContextCompat.getColor(context, R.color.Green));
            holder.statusIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Green));

            holder.timeLeft.setText("");
        }

    }

    private class ViewHolder{

        TextView machineNumber;
        TextView timeLeft;
        TextView machineStatus;
        CardView statusIcon;
        ImageView machineIcon;
        CircularProgressBar progressBar;

    }

}
