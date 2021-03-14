package izmaylov.pi.profi.notes.service

import izmaylov.pi.profi.notes.db.NotesRepository
import izmaylov.pi.profi.notes.model.Note
import izmaylov.pi.profi.notes.model.NoteUpdate
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class NoteService(
    private val repository: NotesRepository,
    @Value("\${notes.defaultTitleSize}")
    private val titleFallback: Int
) {
    fun getAllNotes(): List<Note> {
        return repository.findAll().map(this::recoverTitle)
    }

    fun filterNotes(query: String): List<Note> {
        return repository.findAllByQuery(query)
            .map(this::recoverTitle)
    }

    fun createNote(noteCreate: NoteUpdate): Note {
        val newNote = Note(noteCreate.title?.trim(), noteCreate.content ?: "")
        return repository.save(newNote)
    }

    fun findNote(id: Long): Note? {
        return repository.findByIdOrNull(id)?.let { recoverTitle(it) }
    }

    fun updateNote(id: Long, noteUpdate: NoteUpdate): Note {
        return repository.findByIdOrNull(id)?.let { note ->
            if (noteUpdate.title != null) {
                note.title = noteUpdate.title.trim()
            }
            if (noteUpdate.content != null) {
                note.content = noteUpdate.content
            }
            repository.save(note)
        } ?: throw NoNoteException("Cannot find note by id=$id")
    }

    fun deleteNote(id: Long) {
        repository.deleteById(id)
    }

    class NoNoteException(message: String) : RuntimeException(message)

    private fun recoverTitle(note: Note): Note {
        if (note.title.isNullOrEmpty()) {
            note.title = note.content.substring(
                0,
                min(
                    note.content.length,
                    titleFallback
                )
            )
        }
        return note
    }
}
