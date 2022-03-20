package org.lf.calendar.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.list.ListEditor
import org.lf.calendar.list.ListList

/**
 * The class is tab of main activity
 */
class List : Fragment() {
    
    private lateinit var list: ListList
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        list = childFragmentManager.findFragmentById(R.id.listList) as ListList
        
        view.findViewById<FloatingActionButton>(R.id.list_add_group).setOnClickListener {
            if(activity != null && activity is MainActivity) {
                (activity as MainActivity).setFragmentToOther(ListEditor.newInstance())
            }
        }

    }
    
    fun refreshList() {
        list.refreshList(false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            List().apply {
                arguments = Bundle().apply { }
            }
    }
}