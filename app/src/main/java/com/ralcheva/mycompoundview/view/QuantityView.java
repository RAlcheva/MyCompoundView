package com.ralcheva.mycompoundview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ralcheva.mycompoundview.R;

/**
 * Class representing a custom value selector.
 */
public class QuantityView extends FrameLayout {

    //Views
    private ImageView subtract;
    private EditText quantity;
    private ImageView add;

    //Attributes
    private int minQuantity;
    private int maxQuantity;
    private int startQuantity;
    private int currentQuantity;
    private int deltaQuantity;
    private int textColorRes;
    private int colorRes;
    private boolean isOutlined;

    /**
     * Constructor.
     *
     * @param context the context.
     */
    public QuantityView(@NonNull Context context) {
        super(context);
        obtainStyledAttributes(context, null, 0);
        init();
    }

    /**
     * Constructor.
     *
     * @param context the context.
     * @param attrs   the attributes from the layout.
     */
    public QuantityView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        obtainStyledAttributes(context, attrs, 0);
        init();
    }

    /**
     * Constructor.
     *
     * @param context      the context.
     * @param attrs        the attributes from the layout.
     * @param defStyleAttr the attributes from the default style.
     */
    public QuantityView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttributes(context, attrs, defStyleAttr);
        init();
    }

    private void obtainStyledAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.QuantityView, defStyleAttr, 0);
            minQuantity = typedArray.getInteger(R.styleable.QuantityView_minQuantity, 0);
            maxQuantity = typedArray.getInteger(R.styleable.QuantityView_maxQuantity, 100);
            startQuantity = typedArray.getInteger(R.styleable.QuantityView_startQuantity, 0);
            deltaQuantity = typedArray.getInteger(R.styleable.QuantityView_deltaQuantity, 10);
            colorRes = typedArray.getResourceId(R.styleable.QuantityView_colorOfQuantity, R.color.colorBlack);
            textColorRes = typedArray.getResourceId(R.styleable.QuantityView_colorOfText, R.color.colorBlack);
            isOutlined = typedArray.getBoolean(R.styleable.QuantityView_isOutlined, true);
            return;
        }
        minQuantity = 0;
        maxQuantity = 100;
        startQuantity = 0;
        deltaQuantity = 10;
        colorRes = R.color.colorBlack;
        textColorRes = R.color.colorBlack;
        isOutlined = true;
    }

    private void init() {
        inflate(getContext(), R.layout.view_quantity, this);
        subtract = findViewById(R.id.subtract);
        quantity = findViewById(R.id.quantity);
        add = findViewById(R.id.add);
        subtract.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQuantity(true);
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQuantity(false);
            }
        });
        setupView();
    }

    private void setupView() {
        if (!isOutlined) {
            add.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add));
            subtract.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_subtract));
        } else {
            add.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_outline));
            subtract.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_subtract_outline));
        }
        if (startQuantity <= minQuantity) {
            currentQuantity = minQuantity;
            subtract.setEnabled(false);
            add.setEnabled(true);
            setupColors(false, true);
        } else if (startQuantity >= maxQuantity) {
            currentQuantity = maxQuantity;
            subtract.setEnabled(true);
            add.setEnabled(false);
            setupColors(true, false);
        } else {
            currentQuantity = startQuantity;
            subtract.setEnabled(true);
            add.setEnabled(true);
            setupColors(true, true);
        }
        quantity.setText(Integer.toString(currentQuantity));
    }

    private void setupColors(boolean isSubtractEnabled, boolean isAddEnabled) {
        int disabledColor = ContextCompat.getColor(getContext(), R.color.colorDisabled);
        int color = ContextCompat.getColor(getContext(), colorRes);
        if (color != ContextCompat.getColor(getContext(), R.color.colorBlack)) {
            add.getDrawable().setColorFilter(isAddEnabled ? color : disabledColor, PorterDuff.Mode.SRC_ATOP);
            quantity.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            subtract.getDrawable().setColorFilter(isSubtractEnabled ? color : disabledColor, PorterDuff.Mode.SRC_ATOP);
        }
        int textColor = ContextCompat.getColor(getContext(), textColorRes);
        if (textColor != ContextCompat.getColor(getContext(), R.color.colorBlack)) {
            quantity.setTextColor(textColor);
        }
    }

    private void setQuantity(boolean isSubtract) {
        currentQuantity = isSubtract ? subtractQuantity(currentQuantity) : addQuantity(currentQuantity);
        quantity.setText(Integer.toString(currentQuantity));
    }

    private int subtractQuantity(int currentQuantity) {
        if ((currentQuantity - deltaQuantity) <= minQuantity) {
            currentQuantity = minQuantity;
            modifyViewClickable(false, true);
        } else {
            currentQuantity -= deltaQuantity;
            modifyViewClickable(true, false);
        }
        return currentQuantity;
    }

    private int addQuantity(int currentQuantity) {
        if ((currentQuantity + deltaQuantity) >= maxQuantity) {
            currentQuantity = maxQuantity;
            modifyViewClickable(false, false);
        } else {
            currentQuantity += deltaQuantity;
            modifyViewClickable(true, true);
        }
        return currentQuantity;
    }

    private void modifyViewClickable(boolean isEnabled, boolean isSubtract) {
        int defColor = isEnabled ? colorRes : R.color.colorDisabled;
        int color = ContextCompat.getColor(getContext(), defColor);
        if (isSubtract) {
            subtract.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            subtract.setEnabled(isEnabled);
            return;
        }
        add.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        add.setEnabled(isEnabled);
    }

    /**
     * Method that gets the currently set value.
     *
     * @return the current value.
     */
    public int getQuantity() {
        return currentQuantity;
    }
}