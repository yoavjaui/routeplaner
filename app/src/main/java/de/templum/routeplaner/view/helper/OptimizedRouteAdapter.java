package de.templum.routeplaner.view.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.templum.routeplaner.R;
import de.templum.routeplaner.model.RoutePoint;

/**
 * An adapter which displays the optimized route.
 */

public class OptimizedRouteAdapter extends RecyclerView.Adapter<OptimizedRouteAdapter.RouteItem> {

    private List<RoutePoint> mData = new ArrayList<>();

    public OptimizedRouteAdapter() {
    }

    @Override
    public OptimizedRouteAdapter.RouteItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OptimizedRouteAdapter.RouteItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.calculated_route_item, parent, false));
    }

    @Override
    public void onBindViewHolder(OptimizedRouteAdapter.RouteItem holder, int position) {
        RoutePoint routePoint = mData.get(position);

        holder.setText(routePoint.toString());
        holder.setPosition(position + 1); // Only it guys can handle a list which start at 0
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @UiThread
    public void addAll(List<RoutePoint> data) {
        int size = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(size, size + data.size());
    }

    class RouteItem extends RecyclerView.ViewHolder {

        @BindView(R.id.route_item_address)
        TextView mText;

        @BindView(R.id.icon_position)
        TextView mPosition;

        RouteItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setText(final String text) {
            mText.setText(text);
        }

        public void setPosition(final Integer position) {
            setPosition(position.toString());
        }

        private void setPosition(final String position) {
            mPosition.setText(position);
        }
    }
}
