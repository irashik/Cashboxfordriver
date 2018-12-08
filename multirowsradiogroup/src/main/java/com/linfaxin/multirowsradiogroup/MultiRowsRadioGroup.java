package com.linfaxin.multirowsradiogroup;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

/**
 * 可以多行布局的RadioGroup，但是会用掉子RadioButton的OnCheckedChangeListener
 * A RadioGroup allow multiple rows layout, will use the RadioButton's OnCheckedChangeListener
 *
 * https://github.com/linfaxin/MultiRowsRadioGroup.git
 *
 * Apache License
 Version 2.0, January 2004
 http://www.apache.org/licenses/


 APPENDIX: How to apply the Apache License to your work.

 To apply the Apache License to your work, attach the following
 boilerplate notice, with the fields enclosed by brackets "{}"
 replaced with your own identifying information. (Don't include
 the brackets!)  The text should be enclosed in the appropriate
 comment syntax for the file format. We also recommend that a
 file or class name and description of purpose be included on the
 same "printed page" as the copyright notice for easier
 identification within third-party archives.

 Copyright linfaxin.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 *
 *
 *
 */


public class MultiRowsRadioGroup extends RadioGroup {

	public MultiRowsRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MultiRowsRadioGroup(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			public void onChildViewRemoved(View parent, View child) {
				if (parent == MultiRowsRadioGroup.this && child instanceof ViewGroup) {
					for(RadioButton radioButton:getRadioButtonFromGroup((ViewGroup) child)){
						radioButton.setOnCheckedChangeListener(null);
					}
	            }
			}
			@Override
			public void onChildViewAdded(View parent, View child) {
				if (parent == MultiRowsRadioGroup.this && child instanceof ViewGroup) {
					for(final RadioButton radioButton : getRadioButtonFromGroup((ViewGroup) child)){
		                int id = radioButton.getId();
		                // generates an id if it's missing
		                if (id == View.NO_ID) {
		                	if(VERSION.SDK_INT>=17) id = View.generateViewId();
		                	else id = radioButton.hashCode();
		                	radioButton.setId(id);
		                }
		                if(radioButton.isChecked()){
		                	check(id);
		                }
		                
						radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								if(isChecked){
									radioButton.setOnCheckedChangeListener(null);
                                    check(buttonView.getId());
									radioButton.setOnCheckedChangeListener(this);
								}
							}
						});
						
					}
	            }
			}
		});
	}

	private boolean checking=false;
	@Override
	public void check(int id) {
		if(checking) return;
		checking=true;
		super.check(id);
		checking=false;
	}

	private ArrayList<RadioButton> getRadioButtonFromGroup(ViewGroup group){
		if(group==null) return new ArrayList<>();
		ArrayList<RadioButton> list=new ArrayList<>();
		getRadioButtonFromGroup(group, list);
		return list;
	}
	private void getRadioButtonFromGroup(ViewGroup group, ArrayList<RadioButton> list){
		for(int i=0,count=group.getChildCount();i<count;i++){
			View child = group.getChildAt(i);
			if(child instanceof RadioButton){
				list.add((RadioButton) child);

			}else if(child instanceof ViewGroup){
                getRadioButtonFromGroup((ViewGroup) child, list);
            }
		}
	}
	
}
