package project.leo.com.baidumapproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class BottomSheetDialogFragment extends AppCompatDialogFragment {

    protected Activity mActivity;

    private BottomSheetBehavior mBehavior;
    private ViewGroup coordinator;
    private FrameLayout bottomSheet;
    private View contentView;
    private int mDefaultHeight = -1;
    boolean mCancelable = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        coordinator = (ViewGroup) inflater.inflate(R.layout.dialog_bottom_sheet, container);
        bottomSheet = coordinator.findViewById(R.id.design_bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        mBehavior.setBottomSheetCallback(mBottomSheetCallback);
        mBehavior.setHideable(mCancelable);

        contentView = getContentView(inflater, coordinator);
        bottomSheet.addView(contentView);

        if (mDefaultHeight != -1) {
            setDefaultHeight(mDefaultHeight);
        }

        // 设置 dialog 位于屏幕底部，并且设置出入动画
        setBottomLayout();
        setPeekHeight();
        initBackAction();
        return coordinator;
    }

    private void initBackAction() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 设置默认的高度
     */
    public void setDefaultHeight(int defaultHeight) {
        mDefaultHeight = defaultHeight;
        if (bottomSheet != null) {
            ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
            layoutParams.height = defaultHeight;
            bottomSheet.setLayoutParams(layoutParams);
        }
    }

    private void setPeekHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口高度
        int screenHeight = dm.heightPixels;
        mBehavior.setPeekHeight(screenHeight);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (mCancelable != cancelable) {
            mCancelable = cancelable;
            if (mBehavior != null) {
                mBehavior.setHideable(cancelable);
            }
        }
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
            = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet,
                                   @BottomSheetBehavior.State int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
            if (bottomSheetCallback != null) {
                bottomSheetCallback.onStateChanged(bottomSheet, newState);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (bottomSheetCallback != null) {
                bottomSheetCallback.onSlide(bottomSheet, slideOffset);
            }
        }
    };

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    public void setBottomSheetCallback(BottomSheetBehavior.BottomSheetCallback bottomSheetCallback) {
        this.bottomSheetCallback = bottomSheetCallback;
    }

    protected abstract View getContentView(LayoutInflater inflater, ViewGroup container);

    /**
     * 设置 dialog 位于屏幕底部，并且设置出入动画
     */
    private void setBottomLayout() {
        Window win = getDialog().getWindow();
        if (win != null) {
            win.setBackgroundDrawableResource(R.drawable.transparent);
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            // dialog 布局位于底部
            win.setGravity(Gravity.BOTTOM);
            // 设置进出场动画
            win.setWindowAnimations(R.style.Animation_Bottom);
        }
    }

    @Override
    public void dismiss() {
        if (mDialogFragmentStateListener != null) {
            mDialogFragmentStateListener.dismiss();
        }
        super.dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (mDialogFragmentStateListener != null) {
            mDialogFragmentStateListener.show();
        }
        super.show(manager, tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        if (mDialogFragmentStateListener != null) {
            mDialogFragmentStateListener.show();
        }
        return super.show(transaction, tag);
    }

    private DialogFragmentStateListener mDialogFragmentStateListener;

    public void setDialogFragmentStateListener(DialogFragmentStateListener dialogFragmentStateListener) {
        this.mDialogFragmentStateListener = dialogFragmentStateListener;
    }

    public interface DialogFragmentStateListener {
        void show();

        void dismiss();
    }
}
