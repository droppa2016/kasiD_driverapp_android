package co.za.kasi.adapters

import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import co.za.kasi.R
import kotlin.io.encoding.ExperimentalEncodingApi

class CapturedImageAdapter(
    private val images: List<String>,
    private val selectedIndex: Int?,
    private val onImageClick: (String, Int) -> Unit
) : RecyclerView.Adapter<CapturedImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val tickOverlay: ImageView = view.findViewById(R.id.tickOverlay)
        init {
            imageView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onImageClick(images[position], position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_preview, parent, false)
        return ImageViewHolder(view)
        }


    override fun getItemCount(): Int = images.size

    @OptIn(ExperimentalEncodingApi::class)
    override fun onBindViewHolder(holder: CapturedImageAdapter.ImageViewHolder, position: Int) {
        val base64 = images[position]
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        holder.imageView.setImageBitmap(bitmap)
        holder.tickOverlay.visibility = if (position == selectedIndex) View.VISIBLE else View.GONE
    }


}