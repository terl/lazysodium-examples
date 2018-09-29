package com.goterl.lazycode.lazysodium.example.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.goterl.lazycode.lazysodium.example.R;
import com.goterl.lazycode.lazysodium.example.activities.*;
import com.goterl.lazycode.lazysodium.example.adapters.MultiAdapter;
import com.goterl.lazycode.lazysodium.example.models.Operation;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends BaseFragment {

    private MultiAdapter adapter;

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        return v;
    }

}