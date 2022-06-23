// Generated by view binder compiler. Do not edit!
package com.example.user.bluetooth_communication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.user.bluetooth_communication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DeviceAdapterViewBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final TextView tvDeviceAddress;

  @NonNull
  public final TextView tvDeviceName;

  private DeviceAdapterViewBinding(@NonNull CardView rootView, @NonNull TextView tvDeviceAddress,
      @NonNull TextView tvDeviceName) {
    this.rootView = rootView;
    this.tvDeviceAddress = tvDeviceAddress;
    this.tvDeviceName = tvDeviceName;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static DeviceAdapterViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DeviceAdapterViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.device_adapter_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DeviceAdapterViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.tvDeviceAddress;
      TextView tvDeviceAddress = ViewBindings.findChildViewById(rootView, id);
      if (tvDeviceAddress == null) {
        break missingId;
      }

      id = R.id.tvDeviceName;
      TextView tvDeviceName = ViewBindings.findChildViewById(rootView, id);
      if (tvDeviceName == null) {
        break missingId;
      }

      return new DeviceAdapterViewBinding((CardView) rootView, tvDeviceAddress, tvDeviceName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
