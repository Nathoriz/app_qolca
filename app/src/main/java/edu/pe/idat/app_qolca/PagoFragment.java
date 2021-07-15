package edu.pe.idat.app_qolca;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.pe.idat.app_qolca.databinding.FragmentPagoBinding;

public class PagoFragment extends Fragment {
    FragmentPagoBinding binding;

    public PagoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPagoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}