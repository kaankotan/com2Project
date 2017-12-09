package org.kaan.morsecodetranslator;

import android.app.Dialog;
import android.view.View;
import android.widget.RelativeLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

/**
 * Created by orhan on 09.12.2017.
 */

public class MorseLayoutFragment extends AAH_FabulousFragment {

    public static MorseLayoutFragment newInstance() {
        MorseLayoutFragment f = new MorseLayoutFragment();
        return f;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.morse_table_layout, null);
        RelativeLayout relativeLayout = (RelativeLayout) contentView.findViewById(R.id.table_layout);

        setAnimationDuration(600);
        setPeekHeight(450);
        setViewMain(relativeLayout);
        setMainContentView(contentView);
        super.setupDialog(dialog, style);
    }

}
