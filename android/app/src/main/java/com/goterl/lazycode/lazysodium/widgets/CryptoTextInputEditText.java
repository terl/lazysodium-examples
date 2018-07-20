package com.goterl.lazycode.lazysodium.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import com.goterl.lazycode.lazysodium.R;

public class CryptoTextInputEditText extends TextInputEditText {

    public CryptoTextInputEditText(Context context) {
        super(context);
        init();
    }

    public CryptoTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CryptoTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ColorStateList hintColor = ContextCompat.getColorStateList(getContext(), R.color.snowD1);
        int topPadding = getContext().getResources().getDimensionPixelSize(R.dimen.edit_text_hint_space);
        setTextColor(hintColor);
        setTextSize(16);
        setBackground(null);
        setPadding(getPaddingLeft(), topPadding, getPaddingRight(), getPaddingBottom());
    }

}
