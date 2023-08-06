package se.insektionen.songbook.services

import androidx.annotation.StringRes
import se.insektionen.songbook.model.Songbook

class RepositoryException(@StringRes val errorResId: Int) : Exception() {
    fun asResult(): Result<Songbook> = Result.failure(this)
}
