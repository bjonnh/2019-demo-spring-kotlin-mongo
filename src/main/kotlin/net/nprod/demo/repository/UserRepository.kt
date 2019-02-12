package net.nprod.demo.repository

import net.nprod.demo.model.User

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
interface UserRepository : MongoRepository<User, String> {
    fun findByLogin(@Param("login") login: String): User?
}