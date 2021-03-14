package izmaylov.pi.profi.notes.controller

import izmaylov.pi.profi.notes.model.Note
import izmaylov.pi.profi.notes.model.NoteUpdate
import izmaylov.pi.profi.notes.service.NoteService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
class NotesController(
    private val noteService: NoteService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllNotes(@RequestParam(required = false) query: String?): ResponseEntity<List<Note>> {
        val result = if (query != null) {
            noteService.filterNotes(query)
        } else {
            noteService.getAllNotes()
        }
        return wrapToResponse(result)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createNote(@RequestBody note: NoteUpdate): Note {
        return noteService.createNote(note)
    }

    @GetMapping("/{id:[0-9]+}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getNote(@PathVariable id: Long): ResponseEntity<Note> {
        return wrapToResponse(noteService.findNote(id))
    }

    @PutMapping("/{id:[0-9]+}", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateNote(@PathVariable id: Long, @RequestBody note: NoteUpdate): ResponseEntity<Note>? {
        return kotlin.runCatching { noteService.updateNote(id, note) }
            .map { ResponseEntity.ok(it) }
            .recoverCatching {
                if (it is NoteService.NoNoteException) {
                    ResponseEntity.notFound().build()
                } else {
                    throw it
                }
            }.getOrThrow()
    }

    @DeleteMapping("/{id:[0-9]+}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteNote(@PathVariable id: Long): String {
        noteService.deleteNote(id)
        return """{"id": $id}"""
    }

    private fun wrapToResponse(note: Note?) = note?.let { ResponseEntity.ok(note) } ?: ResponseEntity.noContent().build()

    private fun wrapToResponse(notes: List<Note>) = if (notes.isEmpty()) {
        ResponseEntity.noContent().build()
    } else {
        ResponseEntity.ok(notes)
    }
}
