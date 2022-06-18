// Generated by view binder compiler. Do not edit!
package com.example.beaux_arts.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.beaux_arts.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentItinBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final RecyclerView list;

  @NonNull
  public final Spinner spinner1;

  @NonNull
  public final Spinner spinner2;

  private FragmentItinBinding(@NonNull FrameLayout rootView, @NonNull RecyclerView list,
      @NonNull Spinner spinner1, @NonNull Spinner spinner2) {
    this.rootView = rootView;
    this.list = list;
    this.spinner1 = spinner1;
    this.spinner2 = spinner2;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentItinBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentItinBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_itin, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentItinBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.list;
      RecyclerView list = ViewBindings.findChildViewById(rootView, id);
      if (list == null) {
        break missingId;
      }

      id = R.id.spinner1;
      Spinner spinner1 = ViewBindings.findChildViewById(rootView, id);
      if (spinner1 == null) {
        break missingId;
      }

      id = R.id.spinner2;
      Spinner spinner2 = ViewBindings.findChildViewById(rootView, id);
      if (spinner2 == null) {
        break missingId;
      }

      return new FragmentItinBinding((FrameLayout) rootView, list, spinner1, spinner2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
