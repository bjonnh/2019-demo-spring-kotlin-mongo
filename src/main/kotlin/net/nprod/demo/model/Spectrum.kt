package net.nprod.demo.model

import org.springframework.data.annotation.Id

class Spectrum(@Id val id: String,
               var name: String? = null,
               var public: Boolean = false,
               var owner: String? = null)