package com.internshala.echo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.Fragments.AboutUsFragment
import com.internshala.echo.Fragments.FavoriteFragment
import com.internshala.echo.Fragments.MainFrameFragment
import com.internshala.echo.Fragments.SettingsFragment
import com.internshala.echo.activity.MainActivity
import com.internshala.echo.R

class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImage: IntArray, _context: Context) : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    var contentList: ArrayList<String>? = null
    var get_image: IntArray? = null
    var mContext: Context? = null

    init {
        this.contentList = _contentList
        this.get_image = _getImage
        this.mContext = _context

    }

    override fun onBindViewHolder(p0: NavViewHolder, p1: Int) {
        p0?.icon_Get?.setBackgroundResource(get_image?.get(p1) as Int)
        p0?.text_Get?.setText(contentList?.get(p1))
        p0?.contentHolder?.setOnClickListener({
            if (p1 == 0) {
                val mainFrameFragment = MainFrameFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment, mainFrameFragment)
                        .commit()


            } else if (p1 == 1) {
                val favoriteFragment = FavoriteFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment, favoriteFragment)
                        .commit()

            } else if (p1 == 2) {
                val settingsFragment = SettingsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment, settingsFragment)
                        .commit()

            } else {
                val aboutUsFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.detail_fragment, aboutUsFragment)
                        .commit()
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        })
    }

    override fun getItemCount(): Int {
        return (contentList as ArrayList).size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavViewHolder {
        var itemView = LayoutInflater.from(p0?.context)
                .inflate(R.layout.row_custom_navdrawer, p0, false)
        var returnThis = NavViewHolder(itemView)
        return returnThis

    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView as View) {
        var icon_Get: ImageView? = null
        var text_Get: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            icon_Get = itemView?.findViewById(R.id.icon_navdrawer)
            text_Get = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}