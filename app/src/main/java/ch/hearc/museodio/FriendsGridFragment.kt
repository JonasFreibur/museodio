package ch.hearc.museodio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.friends_fragment_grid.view.*


class FriendsGridFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment with the ProductGrid theme
        val view = inflater.inflate(R.layout.friends_fragment_grid, container, false)



        // Set up the RecyclerView
        //view.recycler_view.setHasFixedSize(true)
        //view.recycler_view.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
       // val adapter = FriendsCardViewHolder(lis)
        //view.recycler_view.adapter = adapter
       // val largePadding = resources.getDimensionPixelSize(R.dimen.shr_product_grid_spacing)
        //val smallPadding = resources.getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small)
        //view.recycler_view.addItemDecoration(ProductGridItemDecoration(largePadding, smallPadding))

        return view;
    }

   /* override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }*/
}