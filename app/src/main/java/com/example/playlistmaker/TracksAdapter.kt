package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class TracksAdapter(private val tracksList: ArrayList<Track>) :
    RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {


    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackPicture: ImageView = itemView.findViewById(R.id.track_picture)
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_timing)
        private val artistName: TextView = itemView.findViewById(R.id.artist)
        private val dipToPixelRadius = dpToPx(2.0f, itemView.context)

        fun bind(track: Track) {

            Glide // подключил Глайд, загружаю картинку из трэка приходящего, дай плейсхолд при ошибках, скругляю радиус, выравниваю масштаб и подгружаю в Вьюху внутри холдера всё
                .with(itemView)
                .load(track.artworkUrl100)
                .fallback(R.drawable.no_image_placehold)
                .placeholder(R.drawable.no_image_placehold)
                .error(R.drawable.no_image_placehold)
                .transform(RoundedCorners(dipToPixelRadius))
                .centerCrop()
                .into(trackPicture)

            val formatOfTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime) // формат в мин и сек
            trackName.text = track.trackName
            trackTime.text = formatOfTime
            artistName.text = track.artistName
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        ) // создал в констр-е холдера лейаут вьюхи
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracksList[position]) // привязываю данные из актуального трека в отображаемый холдер на данный момент
    }

    override fun getItemCount(): Int {
        return tracksList.size
    }

    companion object {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }

}