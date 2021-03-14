package izmaylov.pi.profi.notes.model

import javax.persistence.*

@Entity
data class Note(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    @Column(nullable = true)
    var title: String?,
    @Column(nullable = false)
    var content: String
) {
    constructor(title: String?, content: String): this(0, title, content)
}

class NoteUpdate(
    val title: String?,
    val content: String?
)
