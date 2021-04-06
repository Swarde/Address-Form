package com.idahram.addressform

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.idahram.addressform.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    var db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "MainActivity"

        private lateinit var cityList: List<City>
        private lateinit var cityListId: ArrayList<String>
        private val mScrollY = 0

        private val bottomSheetFragment = UpdateBottomSheet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //DocumentReference docRef = db.collection("cities").document("SF");

        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.CACHE
        db.collection("cities")[source]
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        //cityList = new ArrayList<>();
                        //cityList = task.getResult().toObjects(City.class);
                        val dataSting: MutableList<String> = ArrayList()
                        cityListId = ArrayList()
                        for (document in task.result!!) {
                            cityListId.add(document.id)
                            Log.d(TAG, document.id + " => " + document.data)
                            val city = document.toObject(City::class.java)
                            dataSting.add(city.address + "," + city.city + ", " + city.postalCode)
                            //cityList.add(document.toObject(City.class));
                        }
                        val arrayAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, dataSting)
                        mBinding.lvListAddress.adapter = arrayAdapter
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }

        mBinding.lvListAddress.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val colors = arrayOf<CharSequence>("Ubah", "Hapus")
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setItems(colors) { dialog: DialogInterface?, which: Int ->
                when (which) {
                    0 -> {
                        bottomSheetFragment.setProcess(cityList[position], cityListId[position])
                        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                    }
                    1 -> db.collection("cities").document(cityListId[position])
                            .delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                Toast.makeText(this@MainActivity, "Address Deleted", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener(OnFailureListener { e -> Log.w(TAG, "Error deleting document", e) })
                }
            }
            builder.show()
        }

        /*binding.lvListAddress.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (mScrollY > binding.lvListAddress.getScrollY()) {
                binding.fabAddAddress.show();
            } else {
                binding.fabAddAddress.hide();
            }
            mScrollY = binding.lvListAddress.getScrollY();
        });*/

        mBinding.fabAddAddress.setOnClickListener {
            bottomSheetFragment.setProcess(City(), "")
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    override fun onStart() {
        super.onStart()
        db.collection("cities").addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
            //cityList = new ArrayList<>();
            //cityList = task.getResult().toObjects(City.class);
            val dataSting: MutableList<String> = ArrayList()
            cityList = queryDocumentSnapshots!!.toObjects(City::class.java)
            cityListId = ArrayList()
            for (document in queryDocumentSnapshots) {
                cityListId.add(document.id)
                Log.d(TAG, document.id + " => " + document.data)
                val city = document.toObject(City::class.java)
                dataSting.add(city.address + "," + city.city + ", " + city.postalCode)
                //cityList.add(document.toObject(City.class));
            }
            val arrayAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, dataSting)
            mBinding.lvListAddress.adapter = arrayAdapter
        }
    }

    fun bottomSheet(isEdit: Boolean) {
        // using BottomSheetDialog
        /*View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialog.show();*/

        // using BottomSheetDialogFragment
        /*UpdateBottomSheet bottomSheetFragment = new UpdateBottomSheet();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());*/
    }
}