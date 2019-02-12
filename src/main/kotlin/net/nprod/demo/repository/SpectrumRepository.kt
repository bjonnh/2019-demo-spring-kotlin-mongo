package net.nprod.demo.repository

import net.nprod.demo.model.Spectrum
import net.nprod.demo.model.User

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource


interface SpectrumRepository : MongoRepository<Spectrum, String>