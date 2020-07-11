package com.lengyue.common_views.iosdialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;


/**
 * Created by leo on 2016/12/27.
 */
public final class DialogUtil {

    private DialogUtil() {
    }


    //需要no的点击监听，那么再加个接口。实现。
    public interface DialogAlertListener {
        void yes() ;
    }

    public interface UnitDialogAlertListener {
        void yes(String unitName, Dialog dialog) ;
    }

    public interface MaterialTypeDialogAlertListener {
        void yes(String sortName, String typeName, Dialog dialog) ;
    }

    /*
    * 仿ios dialog
    * */
    public static void alertIosDialog(Activity act, String message, String confirmMessage, String cancleMessage, final DialogAlertListener listener) {
        IosAlertDialog dialog = new IosAlertDialog(act).builder();
        dialog.setMsg(message);
        dialog.setConfirmMsg(confirmMessage);
        dialog.setConcleMsg(cancleMessage);
        if (listener != null) {
            dialog.setConfirmButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.yes();
                }
            });
        }
        if (!act.isFinishing()) {
            dialog.show();
        }
    }

    /*
     * 仿ios dialog
     * */
    public static void alertIosDialog(Activity act, String message, String confirmMessage,final DialogAlertListener listener) {
        IosAlertDialog dialog = new IosAlertDialog(act).oneButtonBuilder();
        dialog.setMsg(message);
        dialog.setConfirmMsg(confirmMessage);
        if (listener != null) {
            dialog.setConfirmButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.yes();
                }
            });
        }
        if (!act.isFinishing()) {
            dialog.show();
        }
    }

}
