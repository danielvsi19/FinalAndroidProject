package com.example.couplesnight.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.couplesnight.databinding.ItemPreferenceBinding
import com.example.couplesnight.model.Preference

class PreferencesAdapter(
    private val preferences: List<Preference>,
    private val onChecked: (Preference, Boolean) -> Unit
) : RecyclerView.Adapter<PreferencesAdapter.PrefViewHolder>() {

    inner class PrefViewHolder(val binding: ItemPreferenceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(preference: Preference) {
            binding.checkbox.text = preference.name
            binding.checkbox.isChecked = preference.isSelected
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                onChecked(preference, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefViewHolder {
        val binding = ItemPreferenceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PrefViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrefViewHolder, position: Int) {
        holder.bind(preferences[position])
    }

    override fun getItemCount(): Int = preferences.size
}