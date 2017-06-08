package com.lee.justdoit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CustomKeyboard extends InputMethodService implements OnKeyboardActionListener {
	private KeyboardView keyboardView;
	private Keyboard keyboard;
	private boolean isShowing = false;
	private List<EditText> edts = new ArrayList<EditText>();
	private Activity aty;
	private boolean isPreparead = false;
	private static int mIndex = -1;
	private String beforeText = "";
	private int scrollDis = 0;
	private EditText extraEditText = null;
	private FrameLayout frameLayout = null;

	public CustomKeyboard(Activity aty) {
		this.aty = aty;
		keyboard = new Keyboard(aty, R.xml.symbols);
		keyboardView = (KeyboardView) aty.getLayoutInflater().inflate(
				R.layout.symbols_layout, null);
		keyboardView.setKeyboard(keyboard);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);
		keyboardView.setVisibility(View.GONE);
		keyboardView.setOnKeyboardActionListener(this);
		frameLayout = new FrameLayout(aty);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		frameLayout.setLayoutParams(lp);
		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params2.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		keyboardView.setLayoutParams(params2);
		frameLayout.addView(keyboardView, params2);
		((ViewGroup) aty.getWindow().getDecorView()).addView(frameLayout);
//		aty.getWindow().setFlags(LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//				LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	}

	public CustomKeyboard addEditText(List<EditText> edits){
		edts.addAll(edits);
		return this;
	}

	public CustomKeyboard addAllVisibleEditTExtFromView(View parent){
		int i=1;
		for(View view : getAllChildViews(parent)){
			if(view instanceof EditText){
//				if(isViewVisible(view)){
					Log.e(" *---*-*-*- ",""+ i++);
					edts.add((EditText)view);
//				}
			}
		}
		return this;
	}

	public CustomKeyboard removeAllEditText(){
		edts.clear();
		return this;
	}

	public CustomKeyboard addEditText(EditText edt) {
//		for(EditText e : edts){
//			if((Integer)e.getTag() == (Integer) edt.getTag()) {
//				Log.e(" ---- ","tag is same =  "+(Integer) edt.getTag());
//				return this;
//			}
//		}
		edts.add(edt);
		return this;
	}

	public CustomKeyboard addEditText(EditText edt, int position) {
		Log.e(" ---- ","  child position =  "+position);
        boolean canAdd = true;
		edt.setTag(position);
		for(EditText e : edts){
            Log.e(" ---- ","  child tag =  "+(Integer)e.getTag());
			if((Integer)e.getTag() == position) canAdd = false;
		}
		if(canAdd )
		    edts.add(edt);
//		edts.add(position,edt);
		return this;
	}

	public CustomKeyboard removeEditText(EditText edt) {
		edts.remove(edt);
		return this;
	}

	public CustomKeyboard addExtraEditText(EditText edt) {
		if (edt.isEnabled() && (edt.getVisibility() == View.VISIBLE)) {
			extraEditText = edt;
		} else {
			extraEditText = null;
		}
		return this;
	}

	public boolean isShowing() {
		return isShowing;
	}


	private ViewGroup getRootView() {
		ViewGroup parent = (ViewGroup) ((ViewGroup) aty
				.findViewById(android.R.id.content)).getChildAt(0);
		if (parent instanceof ScrollView) {
			return (ViewGroup) parent.getChildAt(0);
		}
		return parent;
	}

	//
	private List<View> getAllChildViews(View view) {
		List<View> allchildren = new ArrayList<View>();
		if(!(view instanceof ViewGroup)){
			allchildren.add(view);
		}else{
			ViewGroup vp = (ViewGroup)view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				Log.e(" ---- ","  child count =  "+i);
				View viewchild = vp.getChildAt(i);
				if(!(viewchild instanceof ViewGroup))allchildren.add(viewchild);
				else allchildren.addAll(getAllChildViews(viewchild));
			}
		}
		return allchildren;
	}

	//该view是否在屏幕上可见
	private boolean isViewVisible(View view){
		Point p = new Point();
		aty.getWindowManager().getDefaultDisplay().getSize(p);
		int screenWidth = p.x;
		int screenHeight = p.y;
		Rect rect = new Rect(0, 0, screenWidth, screenHeight);
		int[] location = new int[2];
		view.getLocationInWindow(location);
		if (view.getLocalVisibleRect(rect)) {
			return true;
		}
		return false;
	}

	public CustomKeyboard prepare() {
		if(edts.size() == 0) return this;
		for (int i = 0; i < edts.size(); i++) {
			EditText edt = edts.get(i);
			if(edt.getTag() == null)
				edt.setTag(i);
			edt.setOnTouchListener(new CustomListener());
//			edt.setOnFocusChangeListener(new FocusChangeListener());
		}
		outTouchCancelPrepare(getRootView());
		animateHideKeyboard(false);
		isPreparead = true;
		hideSystemInputMethod(edts.get(0));
		return this;
	}

	private class FocusChangeListener implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			EditText edt = (EditText)v;
			if(hasFocus){
//				beforeText = edt.getText().toString();
//				if (!beforeText.equals("")) {
//					if(edt.isSelected()){
//						edt.setSelected(false);
//						edt.setSelection(beforeText.length());
//						edt.setSelectAllOnFocus(false);
//					}else{
//						edt.setSelectAllOnFocus(true);
//						edt.setSelection(0,beforeText.length());
//						edt.setSelected(true);
//					}
//				}
			}else{
				edt.setSelected(false);
				edt.setSelectAllOnFocus(false);
			}

		}

	}


	public void show() {

		EditText edt = edts.get(0);
		if (!edt.isEnabled())
			return;
		mIndex = 0;
		hideSystemInputMethod(edt);
//		int inputType = edt.getInputType();
//		edt.setInputType(InputType.TYPE_NULL);
		animateShowKeyboard(true);
//		edt.setInputType(inputType);
		// edt.requestFocus();
		beforeText = edt.getText().toString();
		if (!beforeText.equals("")) {
			edt.setSelection(beforeText.length());
		}
		hideSystemInputMethod(edt);
	}

	public void hide(boolean animate) {
		animateHideKeyboard(animate);
	}

//	public void reset() {
//		edts.clear();
//		frameLayout.removeView(keyboardView);
//		((ViewGroup) aty.getWindow().getDecorView()).removeView(frameLayout);
//		keyboard = null;
//		keyboardView = null;
//		frameLayout = null;
//		scrollDis = 0;
//		isPreparead = false;
//		mIndex = -1;
//		extraEditText = null;
//		beforeText = "";
//		aty = null;
//	}

	@SuppressLint("NewApi")
	private void animateShowKeyboard(boolean animate) {
		setLabelState(getkey(-4));
		for(int i=0; i< edts.size() ; i++){
			Log.e(" *-*--* ","index = "+ i + " ,tag = "+edts.get(i).getTag());
		}
		int visibility = keyboardView.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			if (animate) {
				ObjectAnimator show = ObjectAnimator.ofFloat(keyboardView,
						"translationY", keyboardView.getMeasuredHeight(), 0)
						.setDuration(200);
				show.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						super.onAnimationStart(animation);
						keyboardView.setVisibility(View.VISIBLE);
						isShowing = true;
					}
				});
				show.start();
			} else {
				keyboardView.setVisibility(View.VISIBLE);
				isShowing = true;
			}
		}
		caculateToScroll();
	}

	@SuppressLint("NewApi")
	private void animateHideKeyboard(boolean animate) {
		Log.e(" --- "," --- animateHideKeyboard ---");
		int visibility = keyboardView.getVisibility();
		if (visibility == View.VISIBLE) {
			if (animate) {
				ObjectAnimator hide = ObjectAnimator.ofFloat(keyboardView,
						"translationY", 0, keyboardView.getMeasuredHeight())
						.setDuration(200);
				hide.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						keyboardView.setVisibility(View.GONE);
						isShowing = false;
					}
				});
				hide.start();
			} else {
				keyboardView.setVisibility(View.GONE);
				isShowing = false;
			}
		}
		resetScroll();
	}

	private boolean outTouchCancelKeyboard(View view) {
		if (!(view instanceof EditText)) {
			return true;
		} else {
			for (int i = 0; i < edts.size(); i++) {
				if ((EditText) view == edts.get(i)) {
					return false;
				}
			}
		}
		return true;
	}

	private void outTouchCancelPrepare(ViewGroup parent) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			if (child instanceof ViewGroup) {
				outTouchCancelPrepare((ViewGroup) child);
			}
			if ((child instanceof EditText)) {
				if (child.getTag() == null) {
					child.setOnTouchListener(new CustomListener());
				}
			}
		}
	}

	private void hideSystemInputMethod(EditText edt) {
		aty.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		InputMethodManager im = (InputMethodManager) aty
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// im.hideSoftInputFromWindow(aty.getCurrentFocus().getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
		im.hideSoftInputFromWindow(edt.getWindowToken(), 0);

		Class<EditText> cls = EditText.class;
		Method method;
		try {
			method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
			method.setAccessible(true);
			method.invoke(edt, false);
		} catch (Exception e) {
		}

		try {
			method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
			method.setAccessible(true);
			method.invoke(edt, false);
		} catch (Exception e) {
		}
	}

	// 根据当前获取焦点的EditText的位置来滚动布局
	private void caculateToScroll() {
		int H = getScrenHeight();
		int keyboardHeight = getViewHeight(keyboardView);
		int[] location = new int[2];
//		EditText edt = edts.get(mIndex);
		EditText edt = null;
		for(EditText editText : edts){
			if((Integer)editText.getTag() == mIndex)
				edt = editText;
		}
		edt.getLocationOnScreen(location);
		final int y = location[1];
		final int height = getViewHeight(edt);
		// 当前EditText下边框在屏幕中的y坐标
		final int Y = y + height;
		if (getRootView().getParent() != null
				&& (getRootView().getParent() instanceof ScrollView)) {
			final ScrollView scrollView = (ScrollView) getRootView()
					.getParent();
			final int d = keyboardHeight - (H - Y) + scrollView.getScrollY() + 20;
			 Log.e("CustomKeyboard",
			 "y = "+y+", height = "+height+", keyboardH = "+keyboardHeight+", d = "+d);
			if (isScrollAtBottom(scrollView)) {// 滚动到底部，

				if (Y > (H - keyboardHeight)) {
					getRootView().scrollBy(0, d);
					scrollDis += d;
				}
			} else {// 还没滚动到底部
				if (Y > (H - keyboardHeight)) {
					scrollView.post(new Runnable() {
						@Override
						public void run() {
							scrollView.setScrollY(d);
						}
					});
				}
			}
		} /*else if(hasListView()){
			final ListView listview = getListView();
			if(listview == null) return;
			final int d = keyboardHeight - (H - Y) + 20;
			Log.e("CustomKeyboard",
					"y = "+y+", height = "+height+", keyboardH = "+keyboardHeight+", d = "+d);
			if(isScrollAtBottom(listview)){
				getRootView().scrollBy(0, d);
				scrollDis += d;
			}else{
				if (Y > (H - keyboardHeight)) {
					listview.post(new Runnable() {
						@Override
						public void run() {
							listview.setScrollY(d);
						}
					});
				}
			}

		}*/ else{// 不在ScrollView布局中
			ViewGroup root = getRootView();
			int d = keyboardHeight - (H - Y);
            Log.e("CustomKeyboard",
                    "y = "+y+", height = "+height+", keyboardH = "+keyboardHeight+", d = "+d);
			if (Y > (H - keyboardHeight)) {
				getRootView().scrollBy(0, d);
				scrollDis += d;
			}
		}
	}

	private boolean hasListView(){
		for(int i=0; i< getRootView().getChildCount(); i++){
			if(getRootView().getChildAt(i) instanceof ListView){
				return true;
			}
		}
		return false;
	}

	private ListView getListView(){
		ListView listview = null;
		ViewGroup group = getRootView();
		for(int i=0; i< group.getChildCount(); i++){
			if(group.getChildAt(i) instanceof ListView){
				listview = (ListView) group.getChildAt(i);
			}
		}
		return listview;
	}


	private void resetScroll() {
		if (Math.abs(scrollDis) > 0) {
//			if (getRootView().getParent() != null
//					&& (getRootView().getParent() instanceof ScrollView)) {
//				getRootView().scrollBy(0, -scrollDis);
//			} else if(hasListView()){
//                final ListView listview = getListView();
//                if(listview == null) return;
//                listview.scrollBy(0,-scrollDis);
//            }else{
//				getRootView().scrollBy(0, -scrollDis);
//			}
			getRootView().scrollBy(0, -scrollDis);
			scrollDis = 0;
		}
	}

	// 是否滚动到底部，或者ScrollView不能滚动
	private boolean isScrollAtBottom(ScrollView scrollView) {
		int scrollHeight = getViewHeight(scrollView.getChildAt(0));
		if (Math.abs(scrollView.getScrollY()) == (scrollHeight - scrollView
				.getHeight())) {// 滚动到底部
			// Log.e("CustomKeyboard",">>>scroll at button scrollY = "+scrollView.getScrollY()+", scrollHeight = "
			// +scrollHeight+", scrollViewH = "+scrollView.getHeight());
			return true;
		} else {// 还没滚动到底部，但是ScrollView高度不足以滚动显示软键盘

			int[] location = new int[2];
			edts.get(edts.size() - 1).getLocationOnScreen(location);
			int lY = location[1];
			int keyboardHeight = getViewHeight(keyboardView);
			// Log.e("CustomKeyboard",">>>cant scroll lY = "+lY+", keyboardH = "+keyboardHeight+", scrollH = "+scrollHeight);
			if ((lY + keyboardHeight) > scrollHeight) {
				return true;
			}
		}
		return false;
	}

	private boolean isScrollAtBottom(ListView listView){
		if(listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1){//显示到最后一个item
			int[] location = new int[2];
			int H = getScrenHeight();
			EditText edt = edts.get(edts.size() - 1);
			edt.getLocationOnScreen(location);
			int lY = location[1];
			int keyboardHeight = getViewHeight(keyboardView);
			final int height = getViewHeight(edt);
			// 当前EditText下边框在屏幕中的y坐标
			final int Y = lY + height;
			// Log.e("CustomKeyboard",">>>cant scroll lY = "+lY+", keyboardH = "+keyboardHeight+", scrollH = "+scrollHeight);
			if (Y > (H - keyboardHeight)) {
				return true;
			}
		}
		return false;
	}

	private void setLabelState(Key key) {
		if (key == null)
			return;
		if (mIndex == edts.size() - 1) {
			key.label = "完成";
		} else {
			key.label = "下一个";
		}
		keyboardView.setKeyboard(keyboard);
	}

	private void dealWithLabelState() {
		Key key = getkey(-4);
		if (key == null)
			return;
		EditText edit = null;
		for(EditText editText : edts){
			if((Integer)editText.getTag() == mIndex)
				edit = editText;
		}
		if(edit != null) {
			edit.clearFocus();
			edit.clearComposingText();
		}
//		edts.get(mIndex).clearFocus();
//		edts.get(mIndex).clearComposingText();
		if (key.label.equals("完成")) {
			animateHideKeyboard(true);
			if (extraEditText != null && extraEditText.isEnabled()) {
				extraEditText.requestFocus();
				InputMethodManager imm = (InputMethodManager) aty
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(extraEditText, InputMethodManager.SHOW_FORCED);
			}
//			else {
//				EditText edt = edts.get(mIndex);
//				edt.requestFocus();
//				int x = edt.getText().toString().length();
//				edt.setSelection(x, x);
//			}
		} else {
//			EditText before = edts.get(mIndex);
			EditText before = edit;
			if(before != null)before.clearFocus();
			Log.e("-- "," cur index = "+ mIndex);

//			if (mIndex < edts.size()) {
			if (mIndex != (Integer) edts.get(edts.size() - 1).getTag()) {
				mIndex++;
				Log.e("-- "," next index = "+ mIndex);
//				EditText edt = edts.get(mIndex);
				EditText edt = null;
				for(EditText editText : edts){
					if((Integer)editText.getTag() == mIndex) {
						edt = editText;
						break;
					}
				}
				if(edt == null){
					Log.e("-- "," next is null = "+ mIndex);
					mIndex = edts.size() - 1;
					setLabelState(key);
					dealWithLabelState();
				}else if (edt.isEnabled()) {
					Log.e("-- "," next in not null = "+ mIndex);
//					int inputType = edt.getInputType();
//					edt.setInputType(InputType.TYPE_NULL);
//					edt.setInputType(inputType);
					edt.setFocusable(true);
					edt.setLayoutParams(edt.getLayoutParams());
					edt.requestFocus();
					edt.requestFocusFromTouch();
					caculateToScroll();

					beforeText = edt.getText().toString();
					if (!beforeText.equals("")) {
						edt.setSelection(beforeText.length());
					}
//					edt.setSelected(true);
//					edt.selectAll();
				} else {
					dealWithLabelState();
				}
			} else {
				mIndex = edts.size() - 1;
				setLabelState(key);
				dealWithLabelState();
				return;
			}
		}
		setLabelState(key);
	}

	// 根据KeyCode获取键盘按键（-4：完成键）
	private Key getkey(int keyCode) {
		List<Key> keys = keyboard.getKeys();
		for (Key k : keys) {
			if ((k.codes)[0] == keyCode) {
				return k;
			}
		}
		return null;
	}

	private int getScrenHeight() {
		WindowManager wm = (WindowManager) aty.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	private int getViewHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return view.getMeasuredHeight();
	}

	private class CustomListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent arg1) {
			if(arg1.getAction() == MotionEvent.ACTION_DOWN){
				if (!isPreparead || outTouchCancelKeyboard(view)) {
					animateHideKeyboard(true);
					return false;
				}
//				mIndex = edts.indexOf((EditText) view);
				mIndex = (Integer) view.getTag();
				Log.e(" ----- "," --- size = "+edts.size());
				EditText edt = ((EditText) view);
				edt.setLayoutParams(edt.getLayoutParams());
				hideSystemInputMethod(edt);
				animateShowKeyboard(true);
				edt.requestFocus();
				beforeText = edt.getText().toString();
				if (!beforeText.equals("")) {
					edt.setSelection(beforeText.length());
//					if(edt.isSelected()){
//						edt.setSelected(false);
//						edt.setSelection(beforeText.length());
//						edt.setSelectAllOnFocus(false);
//					}else{
//						edt.setSelectAllOnFocus(true);
//						edt.setSelection(0,beforeText.length());
//						edt.setSelected(true);
//					}
				}
				return true;
			}
			return false;
		}
	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		if (primaryCode == -4) {// 完成
			// animateHideKeyboard(true);
			dealWithLabelState();
			return;
		} else {
//			EditText editText = edts.get(mIndex);
			EditText editText = null;
			for(EditText e : edts){
				if((Integer)e.getTag() == mIndex)
					editText = e;
			}
			Editable et = editText.getText();
			if (!beforeText.equals("") &&
					primaryCode != Keyboard.KEYCODE_DELETE &&
					editText.isSelected()) {
				et.clear();
				beforeText = "";
			}
			int start = editText.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_DELETE) {
				if (et != null && et.length() > 0) {
					// editText.setSelectAllOnFocus(false);
					editText.setSelection(et.length());
					if (start > 0) {
						et.delete(start - 1, start);
					}
				}
			} else if (primaryCode == 4896) {// 清空
				et.clear();
			} else {
				if (editText.isEnabled()) {
					//通过反射获取变量值
                    char[] mAccepted = (char[])getValue((DigitsKeyListener)editText.getKeyListener(),"mAccepted");
                    for(char ch : mAccepted){
                        if(Character.toString((char) primaryCode).equals(String.valueOf(ch))){
							//如果遇到点击后不能输入的问题，就换成下面注释的语句
							et.insert(start, Character.toString((char) primaryCode));
//							StringBuffer stringBuffer = new StringBuffer(editText.getText().toString());
//							stringBuffer.insert(start, Character.toString((char) primaryCode)).toString();
//                            editText.setText(stringBuffer.toString());
//							if(start < editText.getText().toString().length())
//								editText.setSelection(start +1);
                        }
                    }
				}
			}
		}
	}


    public static Object getValue(Object instance, String fieldName) {
        Field field = null;
        try {
            field = getField(instance.getClass(), fieldName);
            // 参数值为true，禁用访问控制检查
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Field getField(Class thisClass, String fieldName) throws NoSuchFieldException {
        if (fieldName == null) {
            throw new NoSuchFieldException("Error field !");
        }
        Field field = thisClass.getDeclaredField(fieldName);
        return field;
    }


    @Override
	public void onPress(int arg0) {

	}

	@Override
	public void onRelease(int arg0) {

	}

	@Override
	public void onText(CharSequence arg0) {

	}

	@Override
	public void swipeDown() {

	}

	@Override
	public void swipeLeft() {

	}

	@Override
	public void swipeRight() {

	}

	@Override
	public void swipeUp() {

	}

}
