package com.example.VideoclubFCM

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
//adaptador personalizado para establecer un hint que no sea seleccionable en los spinenr
class HintAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun isEnabled(position: Int): Boolean {
        // Deshabilitar el hint para que no sea seleccionable
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView
        if (position == 0) {
            // Establecer el color del hint
            textView.setTextColor(ContextCompat.getColor(context, R.color.gray))
        } else {
            // Restablecer el color de los elementos reales
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }
        return view
    }
}