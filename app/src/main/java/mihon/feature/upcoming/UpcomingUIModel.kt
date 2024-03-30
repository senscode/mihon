package mihon.feature.upcoming

import tachiyomi.domain.manga.model.Manga
import java.time.LocalDate

sealed interface UpcomingUIModel {
    data class Header(val date: LocalDate) : UpcomingUIModel
    data class Item(val manga: Manga) : UpcomingUIModel
}
