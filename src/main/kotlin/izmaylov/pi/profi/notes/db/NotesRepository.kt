package izmaylov.pi.profi.notes.db

import izmaylov.pi.profi.notes.model.Note
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface NotesRepository : CrudRepository<Note, Long> {
    @Query("""
        select n from Note n where
            lower(n.content) like lower(concat('%', :query,'%')) or
            lower(n.title) like lower(concat('%', :query,'%')) 
        """)
    fun findAllByQuery(@Param("query") query: String): List<Note>
}
