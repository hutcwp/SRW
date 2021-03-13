package com.example.srw.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 *  author : kevin
 *  date : 2021/2/21 11:20 PM
 *  description :
 */
class GameMap : RelativeLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    //constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initMap()
    }

    private fun initMap() {
        
    }


}