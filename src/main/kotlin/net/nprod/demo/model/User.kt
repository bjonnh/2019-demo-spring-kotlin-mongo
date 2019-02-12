package net.nprod.demo.model

import org.springframework.data.annotation.Id

class User(@Id val id: String,
           var login: String,
           var firstName: String?,
           var lastName: String?,
           var password: String?,
           var roles: Collection<Role> = listOf())