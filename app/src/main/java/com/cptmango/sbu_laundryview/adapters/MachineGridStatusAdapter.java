package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.Room;
import com.cptmango.sbu_laundryview.ui.Animations;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;


public class MachineGridStatusAdapter extends BaseAdapter {

    private Room roomData;
    private ArrayList<Machine> machines;
    private Activity context;
    private View machineMenu;
    private AdapterType type;

    public MachineGridStatusAdapter(Activity context, AdapterType type, Room roomData){

        this.context = context;
        this.machineMenu = context.findViewById(R.id.machine_menu);
        this.type = type;
        this.roomData = roomData;
        machineMenu.setAlpha(0f);

        updateMachineData();

    }

    @Override
    public void notifyDataSetChanged() {
        updateMachineData();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return machines.size();

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

            holder.container = (CardView) convertView.findViewById(R.id.Card);
            holder.machineNumber = (TextView) convertView.findViewById(R.id.Machine_txt_MachineNumber);
            holder.machineStatus = (TextView) convertView.findViewById(R.id.Machine_txt_Status);
            holder.timeLeft = (TextView) convertView.findViewById(R.id.Machine_txt_TimeLeft);
            holder.statusIcon = (CardView) convertView.findViewById(R.id.Machine_cardView_StatusIndicator);
            holder.machineIcon = (ImageView) convertView.findViewById(R.id.Machine_image_MachineIcon);
            holder.progressBar = (CircularProgressBar) convertView.findViewById(R.id.Machine_progress_ProgressBar);
            holder.machineIconLittle = (ImageView) convertView.findViewById(R.id.Machine_image_StatusIcon);

            convertView.setTag(holder);

        }else{

            holder = (ViewHolder) convertView.getTag();

        }


        //Setup View
        setupView(holder, machines.get(position));

        return convertView;
    }

    private void setupView(ViewHolder holder, Machine machine){

        int statusColor;
        int statusIcon;
        int progress = 100;
        String timeLeft = "";
        boolean inProgress = false;

        switch(machine.status()){

            case AVAILABLE:
                statusColor = ContextCompat.getColor(context, R.color.Green);
                statusIcon = R.drawable.icon_check;
                holder.progressBar.setProgress(progress);
            break;

            case IN_PROGRESS:
                statusColor = ContextCompat.getColor(context, R.color.Red);
                timeLeft = Integer.toString(machine.timeLeft());
                statusIcon = (machine.getType() == Machine.Type.WASHER) ? R.drawable.icon_water : R.drawable.icon_drying;
                holder.progressBar.enableIndeterminateMode(true);
            break;

            case DONE_DOOR_CLOSED:
                statusColor = ContextCompat.getColor(context, R.color.Yellow);
                statusIcon = R.drawable.icon_waiting;
                holder.progressBar.setProgress(progress);
            break;

            default:
                statusColor = ContextCompat.getColor(context, R.color.Grey);
                statusIcon = R.drawable.icon_close;
                holder.progressBar.setProgress(progress);
            break;

        }

        holder.machineNumber.setText(machine.machineNumber() + "");
        holder.timeLeft.setText(timeLeft);
        holder.machineStatus.setText(machine.status().description());
        holder.machineIconLittle.setImageResource(statusIcon);
        holder.machineIconLittle.setColorFilter(statusColor);
        holder.machineIcon.setColorFilter(statusColor);
        holder.progressBar.setColor(statusColor);
        holder.statusIcon.setCardBackgroundColor(statusColor);

        holder.container.setOnClickListener(v -> showMachineMenu(machine));

    }

    private void showMachineMenu(Machine machine){

        // Finding all the views.
        TextView pos = machineMenu.findViewById(R.id.txt_machineNumber);
        TextView type = machineMenu.findViewById(R.id.txt_machineType);
        CardView cardNumberContainer = machineMenu.findViewById(R.id.card_machineNumber);
        ImageView topBar = machineMenu.findViewById(R.id.img_topBar);
        TextView timeLeft = machineMenu.findViewById(R.id.txt_timeLeft);
        TextView timeExtraText = machineMenu.findViewById(R.id.txt_timeExtraText);
        ProgressBar progressBar = (ProgressBar) machineMenu.findViewById(R.id.progressBar);

        // Retrieving quad colors.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String quadColor = prefs.getString("quadColor", "000000");

        boolean showNotifyBtn = false;
        int progress = 100;
        String statusExtraText = "";
        String machineTimeLeft = "";
        float textSize;
        int statusColor;

        pos.setText(Integer.toString(machine.machineNumber()));
        type.setText(machine.getType().getDescription());
        timeLeft.setPadding(0,25, 0, 0);

        switch(machine.status()){

            case AVAILABLE:
                statusColor = R.color.Green;
                textSize = 45;
                machineTimeLeft = machine.status().description();
            break;

            case IN_PROGRESS:
                showNotifyBtn = true;
                statusColor = R.color.Red;
                textSize = 45;
                machineTimeLeft = Integer.toString(machine.timeLeft());
                statusExtraText = "mins remaining";
                timeLeft.setPadding(0, 0, 0, 0);
            break;

            case DONE_DOOR_CLOSED:
                showNotifyBtn = false;
                machineTimeLeft = machine.status().description();
                statusColor = R.color.Yellow;
                textSize = 30;
            break;

            default:
                statusColor = R.color.Grey;
                textSize = 45;
                machineTimeLeft = machine.status().description();
            break;

        }

        progressBar.setProgress(progress);
        progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, statusColor), PorterDuff.Mode.SRC_IN);
        machineMenu.findViewById(R.id.btn_notify).setVisibility( (showNotifyBtn) ? View.VISIBLE : View.GONE );
        timeLeft.setText(machineTimeLeft);
        timeLeft.setTextSize(textSize);
        timeExtraText.setText(statusExtraText);

        // Change color to quad/theme color.
        cardNumberContainer.setCardBackgroundColor(Color.parseColor(quadColor));
        topBar.setColorFilter(Color.parseColor(quadColor));

        // Change Icon color if user has favorited the machine.
        ImageView star = context.findViewById(R.id.star);
        if(machine.isFavorite()){
            star.setColorFilter(ContextCompat.getColor(context, R.color.Yellow));
        }
        else{
            star.setColorFilter(ContextCompat.getColor(context, R.color.Grey));
        }

        // Show the machine menu.
        Animations.show(context.findViewById(R.id.bg), 0.6f);
        Animations.showUp(machineMenu);

    }

    private void updateMachineData() {

        if(type == AdapterType.FAVORITES_ADAPTER){
            machines = roomData.getFavorites();
        }
        else {

            Machine.Type machineType;

            switch(type){
                case DRYER_ADAPTER: machineType = Machine.Type.DRYER; break;
                case WASHER_ADAPTER: machineType = Machine.Type.WASHER; break;
                default: machineType = Machine.Type.OTHER; break;
            }

            machines = roomData.getMachinesOfType(machineType);

        }

    }

    protected class ViewHolder{

        CardView container;
        TextView machineNumber;
        TextView timeLeft;
        TextView machineStatus;
        CardView statusIcon;
        ImageView machineIcon;
        ImageView machineIconLittle;
        CircularProgressBar progressBar;

    }

    public enum AdapterType {
        WASHER_ADAPTER,
        DRYER_ADAPTER,
        FAVORITES_ADAPTER,
        DEBUG
    }

}
