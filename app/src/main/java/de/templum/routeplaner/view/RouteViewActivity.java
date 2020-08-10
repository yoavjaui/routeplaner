package de.templum.routeplaner.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.templum.routeplaner.R;
import de.templum.routeplaner.computing.RouteFactory;
import de.templum.routeplaner.model.RoutePoint;
import de.templum.routeplaner.util.Helper;
import de.templum.routeplaner.view.helper.OptimizedRouteAdapter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RouteViewActivity extends AppCompatActivity implements Observer<List<RoutePoint>> {
    public static final String ROUTE_LIST = "Route_List";
    private final String TAG = RouteViewActivity.class.getCanonicalName();

    //@BindView(R.id.route_view_list)
//    RecyclerView mList;
    @BindView(R.id.route_view_footer)
    RelativeLayout mFooter;
    @BindView(R.id.route_footer_value)
    TextView mValue;
    @BindView(R.id.route_view_progress)
    SpinKitView mProgress;

    private List<String> mData = null;
    private List<RoutePoint> mInitialRoute = new ArrayList<>();
    private List<RoutePoint> mOptimizedRoute = new ArrayList<>();
    private RouteFactory mFactory = new RouteFactory(this);
    private Disposable mSubscription;
    private OptimizedRouteAdapter mAdapter;

    /**
     * Lifecycle
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_view);
        ButterKnife.bind(this);


        if (getIntent() != null) {
            mData = Objects.requireNonNull(getIntent().getExtras()).getStringArrayList(ROUTE_LIST);
        }

        if (savedInstanceState != null) {
            mData = savedInstanceState.getStringArrayList(ROUTE_LIST);
        }

        if(mData == null || mData.isEmpty())
            super.onBackPressed();

        //initialiseCalculatedRouteList();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mSubscription != null) mSubscription.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mData != null && mOptimizedRoute.isEmpty()) {
            initializeOptimization();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (mData != null) {
            outState.putStringArrayList(ROUTE_LIST, (ArrayList<String>) mData);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSubscribe(Disposable d) {
        if(mSubscription != null) mSubscription.dispose();
        mSubscription = d;
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNext(List<RoutePoint> value) {
        if (value.isEmpty()) return;
        Log.i(TAG, "Found an route");

        if (mOptimizedRoute.isEmpty()) {
            mOptimizedRoute.addAll(value);
            Log.i(TAG, "Found an better route");
        }
        if (Helper.calculateRouteLength(value) < Helper.calculateRouteLength(mOptimizedRoute)) {
            mOptimizedRoute.clear();
            mOptimizedRoute.addAll(value);
            Log.i(TAG, "Found an better route");
        }
    }

    @Override
    public void onError(Throwable e) {
        Snackbar.make(mFooter, e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {
        if (mSubscription != null) mSubscription.dispose();
        mAdapter.addAll(mOptimizedRoute);
        mProgress.setVisibility(View.GONE);
        //mList.setVisibility(View.VISIBLE);
        calculateDifference();
    }
/*
    private void initialiseCalculatedRouteList() {
        mAdapter = new OptimizedRouteAdapter();
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(mAdapter);
        mList.setHasFixedSize(true);
    }


 */
    private void initializeOptimization() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mInitialRoute = Helper.searchBy(RouteViewActivity.this, mData);
            }
        }).start();

        mFactory.optimizeGivenRoute(mData)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    private void calculateDifference() {
        Double lengthOriginal = Helper.calculateRouteLength(mInitialRoute);
        Double lengthCurrent = Helper.calculateRouteLength(mOptimizedRoute);

        Boolean isBetter = lengthCurrent < lengthOriginal;
        Double dif = Math.abs(lengthOriginal - lengthCurrent) / 1000; //Convert to Kilometer

        Log.i(TAG, "Original: " + lengthOriginal + " Current: " + lengthCurrent);

        if (isBetter) {
            mValue.setTextColor(Color.GREEN);
            mValue.setText(String.format(Locale.getDefault(), "%1$,.2f KM", dif));
        } else {
            mValue.setTextColor(Color.RED);
            mValue.setText(String.format(Locale.getDefault(), "%1$,.2f KM", dif));
        }
    }
}
