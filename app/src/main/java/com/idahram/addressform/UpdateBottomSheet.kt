package com.idahram.addressform

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.idahram.addressform.databinding.LayoutAddBinding

class UpdateBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bi: LayoutAddBinding
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var id: String
    private lateinit var city: City

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.layout_add, container, false)
        bi = LayoutAddBinding.bind(view)

        bi.view.setOnClickListener { dismiss() }
        if (!TextUtils.isEmpty(id)) {
            bi.btnAdd.text = "Update City Info"
        } else {
            bi.btnAdd.text = "Add New City"
        }
        bi.etAddress.setText(city.address)
        bi.etCity.setText(city.city)
        bi.etPostalCode.setText(city.postalCode)
        bi.btnAdd.setOnClickListener {
            city.address = bi.etAddress.text.toString()
            city.city = (bi.etCity.text.toString())
            city.postalCode = (bi.etPostalCode.text.toString())
            //db.collection("cities").document("LA").set(city);
            if (!TextUtils.isEmpty(id)) {
                db.collection("cities").document(id)
                        .set(city)
                Toast.makeText(context, "Address Updated", Toast.LENGTH_SHORT).show()
            } else {
                db.collection("cities").add(city)
                Toast.makeText(context, "New Address Added", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }

        //BottomSheetBehavior.from(bi.bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val parent = view?.parent as View

        parent.setBackgroundColor(Color.TRANSPARENT)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val layoutParams: CoordinatorLayout.LayoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(48, 0, 48, 0)
            parent.layoutParams = layoutParams
        }

        val bottomSheetBehavior = BottomSheetBehavior.from<View>(parent)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    fun setProcess(city: City, id: String) {
        this.city = city
        this.id = id
    }
}