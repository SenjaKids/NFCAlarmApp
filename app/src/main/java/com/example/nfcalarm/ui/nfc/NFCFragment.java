package com.example.nfcalarm.ui.nfc;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nfcalarm.databinding.FragmentNfcBinding;

public class NFCFragment extends Fragment {

    private FragmentNfcBinding binding;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY = "tagID";
    public static final String KEY_TIME = "alarmTime";
    NfcAdapter mNfcAdapter;

    NFCViewModel nfcViewModel;

    private String TAG = "DEBUG";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        NFCViewModel nfcViewModel =
//                new ViewModelProvider(this).get(NFCViewModel.class);


        binding = FragmentNfcBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button registerNFCTagBtn = binding.scanToRegisterBtn;
        final Button removeNFCTagBtn = binding.removeNfcTag;
        final LinearLayout layout0 = binding.tagStatus0View;
        final LinearLayout layout1 = binding.tagStatus1View;
        final LinearLayout layout2 = binding.tagStatus2View;

        nfcViewModel = new NFCViewModel(layout0, layout1, layout2);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(super.getActivity());
        registerNFCTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: setNFCTagClicked");
                nfcViewModel.changePageStatus(1);
                nfcViewModel.refreshView();
            }
        });

        removeNFCTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTagID();
            }
        });

        loadTagID();

//        nfcViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mNfcAdapter!= null) {
            Bundle options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            mNfcAdapter.enableReaderMode(super.getActivity(),
                    this::onTagDiscovered,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mNfcAdapter!= null) {
            mNfcAdapter.disableReaderMode(super.getActivity());
            mNfcAdapter.disableForegroundDispatch(super.getActivity());
            Log.i("DEBUG", "onPause: disabled");
        }
    }

    public void onTagDiscovered(Tag tag) {
        Log.i("DEBUG", "onTagDiscovered: "+tag);
        Log.i("DEBUG [tag ID]", bytesToHexString(tag.getId()));
        if (nfcViewModel != null){
            if (nfcViewModel.getPageStatus() == 1){
                saveTagID(bytesToHexString(tag.getId()));
//                Toast.makeText(super.getContext(), "NFC Tag Registered", Toast.LENGTH_LONG).show();
                nfcViewModel.changePageStatus(2);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nfcViewModel.refreshView();
                    }
                });
            }
        }
    }



    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length == 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
//            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }


    void saveTagID(String tagID){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY, tagID);
        editor.apply();

        Log.i("DEBUG", "saveTagID: "+tagID);
    }


    void deleteTagID(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        nfcViewModel.changePageStatus(0);
        nfcViewModel.refreshView();
        editor.apply();
    }

    void loadTagID(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String tagID = sharedPreferences.getString(KEY, null);
        if (tagID == null){
            Log.i("DEBUG", "loadTagID: null value");
            nfcViewModel.changePageStatus(0);
            nfcViewModel.refreshView();
        } else {
            Log.i("DEBUG", "loadTagID: have value");
            nfcViewModel.changePageStatus(2);
            nfcViewModel.refreshView();
        }
    }


}