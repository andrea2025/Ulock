// Generated by view binder compiler. Do not edit!
package com.example.user.bluetooth_communication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.user.bluetooth_communication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ContentNewDeviceHomeBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CardView cardView;

  @NonNull
  public final RecyclerView deleteList;

  @NonNull
  public final EditText searchEdit;

  @NonNull
  public final TextView textVisible;

  private ContentNewDeviceHomeBinding(@NonNull ConstraintLayout rootView,
      @NonNull CardView cardView, @NonNull RecyclerView deleteList, @NonNull EditText searchEdit,
      @NonNull TextView textVisible) {
    this.rootView = rootView;
    this.cardView = cardView;
    this.deleteList = deleteList;
    this.searchEdit = searchEdit;
    this.textVisible = textVisible;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ContentNewDeviceHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ContentNewDeviceHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.content_new_device_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ContentNewDeviceHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardView;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      id = R.id.deleteList;
      RecyclerView deleteList = ViewBindings.findChildViewById(rootView, id);
      if (deleteList == null) {
        break missingId;
      }

      id = R.id.searchEdit;
      EditText searchEdit = ViewBindings.findChildViewById(rootView, id);
      if (searchEdit == null) {
        break missingId;
      }

      id = R.id.textVisible;
      TextView textVisible = ViewBindings.findChildViewById(rootView, id);
      if (textVisible == null) {
        break missingId;
      }

      return new ContentNewDeviceHomeBinding((ConstraintLayout) rootView, cardView, deleteList,
          searchEdit, textVisible);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}