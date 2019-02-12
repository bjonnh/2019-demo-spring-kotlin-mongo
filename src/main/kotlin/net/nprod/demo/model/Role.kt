package net.nprod.demo.model


import org.springframework.data.annotation.Id

/*
 * Each User has roles, each roles have a list of privileges.
 */

class Role(@Id private val id: String,
           var name: String,
           val privileges: Collection<Privilege>)