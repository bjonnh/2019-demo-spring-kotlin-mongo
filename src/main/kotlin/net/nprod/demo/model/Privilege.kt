package net.nprod.demo.model

import org.springframework.data.annotation.Id

/*
 * These are the modular rights that can be given to specific roles
 */

class Privilege(@Id private val id: String,
                var name: String)