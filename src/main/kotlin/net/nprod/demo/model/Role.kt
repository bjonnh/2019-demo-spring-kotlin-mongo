package net.nprod.demo.model


import org.springframework.data.annotation.Id

class Role(@Id private val id: String,
           var name: String,
           val privileges: Collection<Privilege>)