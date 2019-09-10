package com.example.dylbo.RecordingBuddy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dylbo.RecordingBuddy.R;
import com.example.dylbo.RecordingBuddy.database.BandEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordingGroupAdapter extends RecyclerView.Adapter<RecordingGroupAdapter.SongViewHolder> {

    private static final String TAG = RecordingGroupAdapter.class.getSimpleName();
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ItemClickListener mItemClickListener;
    final private ItemLongClickListener mItemLongClickListener;


    private Context mContext;
    // Class variables for the List that holds task data and the Context
    private List<BandEntry> mBandEntries;

    /**
     * Constructor for GreenAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public RecordingGroupAdapter(Context context, ItemClickListener listener, ItemLongClickListener longClickListener) {
        mContext = context;
        mItemClickListener = listener;
        mItemLongClickListener = longClickListener;

    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recycler_item_groups;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        SongViewHolder viewHolder = new SongViewHolder(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {

        // Determine the values of the wanted data
        BandEntry bandEntry = mBandEntries.get(position);
        String title = bandEntry.getBandName();
        Date date =bandEntry.getCreatedAt();
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        holder.titleTextView.setText(title);



    }
    public List<BandEntry> getBands() {
        return mBandEntries;
    }
    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mBandEntries == null) {
            return 0;
        }
        return mBandEntries.size();
    }
    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setBands(List<BandEntry> bandEntries) {
        mBandEntries = bandEntries;
        Log.d(TAG, "setBands: " + bandEntries);
        notifyDataSetChanged();
    }


    /**
     * The interface that receives onClick messages.
     */
    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public interface ItemLongClickListener {
        void onItemLongClicked(int itemId);
    }
    // COMPLETED (5) Implement OnClickListener in the NumberViewHolder class
    /**
     * Cache of the children views for a list item.
     */
    class SongViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener, View.OnLongClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView titleTextView;
        ImageView bandLogoImageView;










        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         *
         * @param itemView The View that you inflated in
         *                 {@link com.example.dylbo.RecordingBuddy.ui.RecordingGroupActivity onCreateViewHolder(ViewGroup, int)}
         */
        public SongViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.band_title);
            bandLogoImageView = itemView.findViewById(R.id.band_Logo);


            // COMPLETED (7) Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        // COMPLETED (6) Override onClick, passing the clicked item's position (getAdapterPosition()) to mOnClickListener via its onListItemClick method

        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int elementId = mBandEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }

        @Override
        public boolean onLongClick(View v) {
            int elementId = getAdapterPosition();
            mItemLongClickListener.onItemLongClicked(elementId);
            return true;

        }
    }
}
