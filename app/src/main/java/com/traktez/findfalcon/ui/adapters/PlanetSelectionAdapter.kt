package com.traktez.findfalcon.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.traktez.findfalcon.R
import com.traktez.findfalcon.data.entity.PlanetEntity
import com.traktez.findfalcon.databinding.PlanetItemBinding
import javax.inject.Inject


class PlanetSelectionAdapter @Inject constructor() : BaseAdapter() {
    var planetList: ArrayList<PlanetEntity> = arrayListOf()
    lateinit var planetChoiceHandler: PlanetChoiceHandler


    var isSearchQuotaFull = false

    override fun getItem(p0: Int): Any = planetList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = planetList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: PlanetViewHolder
        if (convertView == null) {
            val itemBinding: PlanetItemBinding =
                PlanetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            holder = PlanetViewHolder(itemBinding)
            holder.view = itemBinding.root
            holder.view.tag = holder
        } else {
            holder = convertView.tag as PlanetViewHolder
        }
        val currentPlanet = planetList[position]
        holder.binding.tvPlanetName.text = currentPlanet.planetName
        holder.binding.tvDistance.text = currentPlanet.distance.toString()
        holder.binding.imgCheck.visibility =
            if (currentPlanet.planetSearching) View.VISIBLE else View.INVISIBLE

        holder.binding.btnSpaceShip.text =
            if (!currentPlanet.vehicleName.isNullOrEmpty()) currentPlanet.vehicleName else holder.view.context.getString(
                R.string.select_space_ship
            )
        holder.view.setOnClickListener {
            if (!isSearchQuotaFull) {
                planetChoiceHandler.selectedPlanet(currentPlanet)
            } else {
                planetChoiceHandler.sendNoMoreResAlert()
            }
        }

        holder.binding.imgCheck.setOnClickListener {
            holder.binding.imgCheck.visibility = View.GONE
            planetChoiceHandler.deselectedPlanet(currentPlanet)
            currentPlanet.planetSearching = false
            isSearchQuotaFull = false
            currentPlanet.vehicleName = ""
            notifyDataSetChanged()
        }

        return holder.view
    }


    internal class PlanetViewHolder(binding: PlanetItemBinding) {
        var view: View
        val binding: PlanetItemBinding

        init {
            view = binding.root
            this.binding = binding
        }
    }

    interface PlanetChoiceHandler {
        fun selectedPlanet(planet: PlanetEntity)
        fun deselectedPlanet(planet: PlanetEntity)
        fun findFalcone()
        fun sendNoMoreResAlert()
    }
}