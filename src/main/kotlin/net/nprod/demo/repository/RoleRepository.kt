import net.nprod.demo.model.Role
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource


interface RoleRepository : MongoRepository<Role, String> {
    fun findByName(@Param("name") name: String): Role
}