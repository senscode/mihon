package tachiyomi.domain.manga.interactor

import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.model.MangaUpdate
import tachiyomi.domain.manga.model.toMangaUpdate
import tachiyomi.domain.manga.repository.MangaRepository

class NetworkToLocalManga(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(manga: Manga): Manga {
        val localDbManga = getManga(manga.url, manga.source)

        if (localDbManga != null && localDbManga.dirLastModifiedAt != manga.dirLastModifiedAt) {
            updateManga(manga.copy(id = localDbManga.id, favorite = localDbManga.favorite).toMangaUpdate())
        }

        return when {
            localDbManga == null -> {
                val id = insertManga(manga)
                manga.copy(id = id!!)
            }
            !localDbManga.favorite -> {
                // if the manga isn't a favorite, set its display title from source
                // if it later becomes a favorite, updated title will go to db
                localDbManga.copy(title = manga.title)
            }
            else -> {
                localDbManga
            }
        }
    }

    private suspend fun getManga(url: String, sourceId: Long): Manga? {
        return mangaRepository.getMangaByUrlAndSourceId(url, sourceId)
    }

    private suspend fun insertManga(manga: Manga): Long? {
        return mangaRepository.insert(manga)
    }

    private suspend fun updateManga(mangaUpdate: MangaUpdate) {
        mangaRepository.update(mangaUpdate)
    }
}
