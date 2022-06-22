// Generated by view binder compiler. Do not edit!
package com.example.beaux_arts.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.beaux_arts.R;
import com.fengmap.android.map.FMMapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMapBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final FloatingActionButton btnMyLocation;

  @NonNull
  public final LinearLayout layoutMode;

  @NonNull
  public final FMMapView mapView;

  private ActivityMapBinding(@NonNull LinearLayout rootView,
      @NonNull FloatingActionButton btnMyLocation, @NonNull LinearLayout layoutMode,
      @NonNull FMMapView mapView) {
    this.rootView = rootView;
    this.btnMyLocation = btnMyLocation;
    this.layoutMode = layoutMode;
    this.mapView = mapView;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMapBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMapBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_map, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMapBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_my_location;
      FloatingActionButton btnMyLocation = ViewBindings.findChildViewById(rootView, id);
      if (btnMyLocation == null) {
        break missingId;
      }

      id = R.id.layout_mode;
      LinearLayout layoutMode = ViewBindings.findChildViewById(rootView, id);
      if (layoutMode == null) {
        break missingId;
      }

      id = R.id.mapView;
      FMMapView mapView = ViewBindings.findChildViewById(rootView, id);
      if (mapView == null) {
        break missingId;
      }

      return new ActivityMapBinding((LinearLayout) rootView, btnMyLocation, layoutMode, mapView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}