//package com.icarus.unzip.adapter
//
//import android.content.Context
//import android.graphics.drawable.ColorDrawable
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.icarus.unzip.R
//import com.icarus.unzip.inter.Observer
//import com.icarus.unzip.util.*
//import java.io.File
//import java.util.*
//import kotlin.collections.ArrayList
//import kotlin.collections.HashMap
//
//class MediaIndexAdapter(
//    private val index: List<String>,
//    private val map: HashMap<String, ArrayList<File>>,
//    private val editPlug: AdapterEditPlug
//) :
//    RecyclerView.Adapter<MediaIndexAdapter.ViewHolder>(), Observer {
//
//    class ViewHolder(view: View) :
//        RecyclerView.ViewHolder(view) {
//        val title: CheckBox = view.findViewById(R.id.index_title)
//        val recyclerView: RecyclerView = view.findViewById(R.id.medias)
//    }
//
//    private var context: Context? = null
//    private var viewPool = RecyclerView.RecycledViewPool()
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        if (context == null) {
//            context = parent.context
//        }
//        val inflate =
//            LayoutInflater.from(parent.context).inflate(R.layout.item_media_index, parent, false)
//        return ViewHolder(inflate)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val arrayList = map[index[position]] ?: return
//        holder.title.text = index[position].split('/').last()
//        val mediaAdapter = MediaAdapter(this, arrayList, editPlug)
//        holder.title.setOnCheckedChangeListener { _, isChecked ->
//            mediaAdapter.b = isChecked
//            mediaAdapter.notifyDataSetChanged()
//        }
//        holder.title.isChecked = false
//        holder.recyclerView.adapter = mediaAdapter
//        holder.recyclerView.setRecycledViewPool(viewPool)
//        holder.recyclerView.isNestedScrollingEnabled = false
//        holder.recyclerView.layoutManager = GridLayoutManager(context, 3)
//        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
//        if (editPlug.editMode && position == itemCount - 1) {
//            layoutParams.bottomMargin = 75.toPx()
//        } else {
//            layoutParams.bottomMargin = 0
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return index.size
//    }
//
//    override fun notifyDataChanged() {
//        notifyDataSetChanged()
//    }
//}
//
//class MediaAdapter(
//    private val parentAdapter: MediaIndexAdapter,
//    private val medias: List<File>,
//    private val editPlug: AdapterEditPlug
//) :
//    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
//    class ViewHolder(view: View) :
//        RecyclerView.ViewHolder(view) {
//        val image: ImageView = view.findViewById(R.id.media_pic)
//        val box: CheckBox = view.findViewById(R.id.box)
//    }
//
//    var b = false
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflate =
//            LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
//        return ViewHolder(inflate)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        position.log()
//        val item = medias[position]
//        holder.image.setImageDrawable(ColorDrawable(0))
//        Glide.with(getAppContext()).load(item).into(holder.image)
//        holder.box.visible(editPlug.editMode)
//        holder.box.isChecked = editPlug.isFileSelected(item)
//        holder.itemView.setOnClickListener {
//            if (editPlug.editMode) {
//                editPlug.toggleFile(item)
//                notifyItemChanged(position)
//            } else {
//                item.open()
//            }
//        }
//        holder.itemView.setOnLongClickListener {
//            if (editPlug.editMode) {
//                false
//            } else {
//                editPlug.editMode = true
//                editPlug.toggleFile(item)
//                parentAdapter.notifyDataSetChanged()
//                true
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return if (b) medias.size else 3.coerceAtMost(medias.size)
//    }
//}
