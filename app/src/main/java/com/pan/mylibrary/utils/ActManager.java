package com.pan.mylibrary.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by panchenhuan on 2018/6/19 14:13.
 * <p>
 * panchenhuan@mirahome.me
 */
public class ActManager {

    private static Stack<Activity> actStack;
    private static ActManager actManager;

    public static ActManager get() {
        if (actManager == null) {
            synchronized (ActManager.class) {
                if (actManager == null) {
                    actManager = new ActManager();
                }
            }
        }

        if (actStack == null) actStack = new Stack<>();
        return actManager;
    }

    public void addActivity(Activity act) {
        if (act != null && actStack != null) {
            actStack.add(act);
        }
    }

    public void removeActivity(Activity act) {
        if (act != null && actStack != null) {
            actStack.remove(act);
        }
    }

    public Activity findActivity(Class<?> cls) {
        if (actStack == null || actStack.empty()) return null;
        for (Activity act : actStack) {
            if (act.getClass().equals(cls)) {
                return act;
            }
        }
        return null;
    }

    public boolean existActivity(Class<?> cls) {
        if (actStack == null || actStack.empty()) return false;
        for (Activity act : actStack) {
            if (act.getClass().equals(cls)) return true;
        }
        return false;
    }

    public int existActivityNum(Class<?> cls) {
        if (actStack == null || actStack.empty()) return 0;
        int num = 0;
        for (Activity act : actStack) {
            if (act.getClass().equals(cls)) num++;
        }
        return num;
    }

    /**
     * 获取顶部/最后一个Activity
     */
    public Activity getTopActivity() {
        if (actStack == null || actStack.empty()) return null;
        return actStack.lastElement();
    }

    /**
     * 回到第一个Activity
     */
    public void redirectToBottomActivity() {
        if (actStack == null || actStack.empty()) return;
        for (int i = 1; i < actStack.size(); i++) {
            if (actStack.get(i) != null && !actStack.get(i).isFinishing()) {
                KLog.d("pkgName  -- " + actStack.get(i).getPackageName()
                        + "\n actName  --  " + actStack.get(i).getComponentName().getClassName());
                actStack.get(i).finish();
            }
        }
    }

    /**
     * 结束全部当前栈中
     */
    public void finishAll() {
        if (actStack == null || actStack.empty()) return;
        for (Activity act : actStack) {
            if (act != null && !act.isFinishing()) {
                KLog.d("pkgName  -- " + act.getPackageName() + "\n actName  --  " + act.getComponentName().getClassName());
                act.finish();
            }
        }
        actStack.clear();
    }

    /**
     * 结束当前以上的所有Activity
     */
    public void finishAbove(Class<?> cls) {
        if (actStack == null || actStack.empty()) return;
        for (int i = 0; i < actStack.size(); i++) {
            if (actStack.get(i).getClass().equals(cls)) {
                if (i == actStack.size() - 1) return;
                for (int j = i + 1; j < actStack.size(); j++) {
                    Activity act = actStack.get(j);
                    if (act != null && !act.isFinishing()) act.finish();
                }
                return;
            }
        }
    }

    public void finishExcept(Class<?> cls) {
        if (actStack == null || actStack.empty()) return;
        for (int i = 0; i < actStack.size(); i++) {
            if (!actStack.get(i).getClass().equals(cls)) {
                Activity act = actStack.get(i);
                if (act != null && !act.isFinishing()) act.finish();
            }
        }
    }
}